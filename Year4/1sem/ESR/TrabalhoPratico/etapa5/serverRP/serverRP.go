package main

import (
	"encoding/binary"
	"encoding/json"
	"errors"
	"fmt"
	"math/rand"
	"net"
	"os"
	"os/exec"
	"projetoesr/etapa5/pkgs"
	"regexp"
	"strconv"
	"strings"
	"time"
)

var node_id string
var node_address string

//-------------------------------------------------------------------------

type Node struct {
	Label string `json:"label"`
	IP    string `json:"ip"`
}

type Connection struct {
	EdgeA string `json:"edgeA"`
	EdgeB string `json:"edgeB"`
}

type NetworkGraph struct {
	Nodes       []Node       `json:"nodes"`
	Connections []Connection `json:"connections"`
}

func (network *NetworkGraph) AddNode(node Node) {
	network.Nodes = append(network.Nodes, node)
}

func (network *NetworkGraph) GetConnectedNodesIPs(nd *Node) []string {
	var connectedNodes []string
	var err error
	var nodo *Node
	for _, connection := range network.Connections {
		if connection.EdgeA == nd.Label {
			nodo, err = network.FindNodebyName(connection.EdgeB)
			nodoInfo := nodo.Label + "|" + nodo.IP
			connectedNodes = append(connectedNodes, nodoInfo)
		} else if connection.EdgeB == nd.Label {
			nodo, err = network.FindNodebyName(connection.EdgeA)
			nodoInfo := nodo.Label + "|" + nodo.IP
			connectedNodes = append(connectedNodes, nodoInfo)
		}
	}
	if err != nil {
		return nil
	} else {
		return connectedNodes
	}

}

func (network *NetworkGraph) FindNodebyName(name string) (*Node, error) {
	var err error

	for _, node := range network.Nodes {
		if node.Label == name {
			return &node, err
		}
	}
	err = errors.New("404 : Node not found")
	return nil, err
}

func initNetworkfromJSON(fileName string) (*NetworkGraph, error) {
	//Opening file descriptor
	jsonFile, err := os.Open(fileName)
	if err != nil {
		return nil, err
	}
	defer jsonFile.Close()

	var network NetworkGraph

	// Decode the JSON data from the file into network.Nodes
	decoder := json.NewDecoder(jsonFile)
	if err := decoder.Decode(&network); err != nil {
		return nil, err
	}

	return &network, err
}

//-------------------------------------------------------------------------

type Metrics struct {
	Status     bool
	Latency    int
	Packetloss float32
	Jitter     int
}

type DirectConnection struct {
	Label   string
	IP      string
	Metrics Metrics
}

type NeighbourNodes struct {
	Nodes []DirectConnection
}

func (nn *NeighbourNodes) AddNode(node DirectConnection) {
	nn.Nodes = append(nn.Nodes, node)
}

func (node *DirectConnection) measureMetrics() {

	if node.Metrics.Status {

		cmd := exec.Command("ping", "-c", "10", node.IP)

		// Run the command and capture its output
		output, err := cmd.CombinedOutput()

		lines := strings.Split(string(output), "\n")
		if err != nil {
			fmt.Println("Error Ping:", err)
			return
		}
		lossRegex := regexp.MustCompile(`(\d+)%`)

		latency_jitterRegex := regexp.MustCompile(`\d+.\d*\/(\d+).\d*\/\d+.\d*\/(\d+).\d*`)

		lossMatch := lossRegex.FindStringSubmatch(lines[len(lines)-3])
		latency_jitter := latency_jitterRegex.FindStringSubmatch(lines[len(lines)-2])

		parsedValue, err := strconv.ParseFloat(lossMatch[1], 32)
		node.Metrics.Packetloss = float32(parsedValue) / 100
		node.Metrics.Latency, err = strconv.Atoi(latency_jitter[1])
		node.Metrics.Jitter, err = strconv.Atoi(latency_jitter[2])
		if err != nil {
			panic(err)
		}
		fmt.Printf("[Metrics of %s] Packetloss-%.2f, Latency-%d, Jitter-%d\n",
			node.Label, node.Metrics.Packetloss, node.Metrics.Latency, node.Metrics.Jitter)

	} else {
		fmt.Println("Couldn't Measure Metrics with ", node.Label, "| Not connected to the Overlay")
	}
}

func (nn *NeighbourNodes) FindNodebyLabel(name string) (*DirectConnection, error) {
	var err error

	for _, node := range nn.Nodes {
		if node.Label == name {
			return &node, err
		}
	}
	err = errors.New("404 : Neighbour Node not found")
	return nil, err
}

func (nn *NeighbourNodes) storeNeighbours(network *NetworkGraph) {

	var nodeinf *Node
	var err error
	for _, val := range network.Connections {
		nodeinf = nil
		if val.EdgeA == node_id {
			nodeinf, err = network.FindNodebyName(val.EdgeB)
		} else if val.EdgeB == node_id {
			nodeinf, err = network.FindNodebyName(val.EdgeA)
		}

		if err != nil {
			return
		}
		//replace vals with metrics functions
		if nodeinf != nil {
			node := DirectConnection{
				Label: nodeinf.Label,
				IP:    nodeinf.IP,
				Metrics: Metrics{
					Status:     false,
					Latency:    0,
					Packetloss: 0,
					Jitter:     0,
				},
			}
			nn.AddNode(node)
		}

	}
}

func (node *DirectConnection) getContentInServer() []string {
	server, err := net.Listen("tcp", node_address+":"+pkgs.TCP_PORT)
	if err != nil {
		fmt.Println("Error listening to TCP:", err.Error())
		return nil
	}
	defer server.Close()
	connection, err := server.Accept()
	fmt.Printf("Connected to %v \n", node_address)

	connection.Write(pkgs.MessageBuilder("content", "videos_catalog", []byte{}))
	readBuffer := make([]byte, 64)
	_, err = connection.Read(readBuffer)
	if err != nil {
		fmt.Println("Error reading from TCP:", err)
		return nil
	}
	length := binary.BigEndian.Uint64(readBuffer[:8])
	videos := make([]string, length)
	bits_read := uint64(8)
	for i := uint64(0); i < length; i++ {
		size := binary.BigEndian.Uint64(readBuffer[bits_read : 8+bits_read])
		bits_read += 8
		videos[i] = string(readBuffer[bits_read : size+bits_read])
		bits_read += size
	}
	return videos
}

func (nn *NeighbourNodes) updateMetrics() {
	for i := range nn.Nodes {
		node := &nn.Nodes[i]
		node.measureMetrics()
	}
}

// _-----------------------------------------------

type Stream struct {
	Source  *DirectConnection
	Clients []struct {
		*DirectConnection
		Pipe chan []byte
	}
}

func (s *Stream) HasStreams() bool {
	return len(s.Clients) > 0
}

func (s *Stream) AddSource(nd *DirectConnection) {
	s.Source = nd
}

func (s *Stream) RemoveSource() {
	s.Source = nil
}

func (s *Stream) AddClient(nn *NeighbourNodes, label string, ip string) *DirectConnection {
	for i := range nn.Nodes {
		nd := &nn.Nodes[i]
		if nd.Label == label {
			newClient := struct {
				*DirectConnection
				Pipe chan []byte
			}{nd, make(chan []byte, 1800)}
			s.Clients = append(s.Clients, newClient)
			return nd
		}
	}

	newClient := struct {
		*DirectConnection
		Pipe chan []byte
	}{
		&DirectConnection{
			Label:   label,
			IP:      ip,
			Metrics: Metrics{},
		},
		make(chan []byte, 1800),
	}

	s.Clients = append(s.Clients, newClient)

	return newClient.DirectConnection
}

func (s *Stream) RemoveClient(label string) {
	for i, client := range s.Clients {
		if client.Label == label {
			s.Clients = append(s.Clients[:i], s.Clients[i+1:]...)
			break
		}
	}
}

func (st *Stream) getStreamfromSource() {

	source_add, err := net.ResolveUDPAddr("udp", node_address+":"+pkgs.UDP_PORT)
	if err != nil {
		panic(err)
	}

	source_conn, err := net.ListenUDP("udp", source_add)
	if err != nil {
		panic(err)
	}
	defer source_conn.Close()

	buffer := make([]byte, 1800)
	for {
		n, _, err := source_conn.ReadFromUDP(buffer)
		if err != nil {
			panic(err)
		}

		for i := range st.Clients {
			client := &st.Clients[i]
			client.Pipe <- buffer[:n]
		}

	}

}

func (st *Stream) streamtoclient(client *DirectConnection) {

	conn, err := net.Dial("udp", client.IP+":"+pkgs.UDP_PORT)
	if err != nil {
		panic(err)
	}
	defer conn.Close()
	var cl *struct {
		*DirectConnection
		Pipe chan []byte
	}
	for i := range st.Clients {
		cl = &st.Clients[i]
		if cl.DirectConnection.Label == client.Label {
			break
		}
	}

	for {
		data, ok := <-cl.Pipe
		if !ok {
			fmt.Println("Queue closed, exiting client handler")
			return
		}

		_, err := conn.Write(data)
		if err != nil {
			fmt.Println("Error writing to client:", err)
			return
		}
	}
}

//--------------------------------

func handleTCPrequests(conn net.Conn, network *NetworkGraph, nn *NeighbourNodes, streams *Stream) {
	defer conn.Close()
	buffer := make([]byte, 1024)
	clientAddr := conn.RemoteAddr()
	for {

		n, err := conn.Read(buffer)
		if err != nil {
			fmt.Printf("[TCP | %v]  Connection Closed \n", clientAddr)
			return
		}
		Type, Subtype, Data, err := pkgs.MessageDecoder(buffer, n)

		if err != nil {
			fmt.Println("Error on Received Message", err)
			return
		}

		switch {
		case Type == "control": //control messages
			fmt.Printf("[TCP | %v] Control | ", clientAddr)
			if Subtype == "bootstraper" {
				fmt.Printf("Bootstraper | %s \n", Data)
				nodo, err := network.FindNodebyName(Data)

				nodelist := network.GetConnectedNodesIPs(nodo)
				atach := nodo.Label + "|" + nodo.IP
				nodelist = append(nodelist, atach)

				if err != nil {
					fmt.Println(err)
				}
				message := strings.Join(nodelist, ",")

				msg := pkgs.MessageBuilder("control", "bootstraper", []byte(message))
				conn.Write([]byte(msg))
				fmt.Printf("[TCP | localhost] Sending Message: %v\n", message)
			}

			if Subtype == "connect" { //connect (testa conecção)
				fmt.Printf("Connect | %s \n", Data)

				for i := range nn.Nodes {
					nd := &nn.Nodes[i]
					if Data == nd.Label {
						nd.Metrics.Status = true
						nd.measureMetrics()
					}

				}
				msg := pkgs.MessageBuilder("control", "connect", []byte("active"))
				conn.Write(msg)

			}
			if Subtype == "rp_path" { //rp (testa conecção)
				fmt.Printf("rp_path | %s \n", Data)
				node, err := nn.FindNodebyLabel(Data)
				if err != nil {
					fmt.Println("Error finding node", err)
					return
				}

				branch_score := pkgs.MetricsScoring(node.Metrics.Latency, node.Metrics.Jitter, float32(node.Metrics.Packetloss))
				msg := pkgs.MessageBuilder("control", "rp_path", []byte(strconv.FormatFloat(float64(branch_score), 'f', -1, 32)))
				conn.Write(msg)
				fmt.Printf("[TCP | localhost] Sending Message: %v\n", string(msg))
			}
		case Type == "content": //control messages
			fmt.Printf("[TCP | %v] Content | ", clientAddr)
			if Subtype == "content_sub" {
				fmt.Println("[Content Sub | ", Data)
				if streams.HasStreams() {
					client_ip := strings.Split(string(clientAddr.String()), ":")[0]
					client := streams.AddClient(nn, Data, client_ip) //data -> media
					fmt.Print("Client ADDED: ", streams)
					msg := pkgs.MessageBuilder("content", "content_sub", []byte("OK"+node_id))
					conn.Write(msg)
					fmt.Print(client)
					go streams.streamtoclient(client)

				} else {
					contentServer, err := nn.FindNodebyLabel("n16")
					fmt.Println("No Stream: ", contentServer)
					ip_add := contentServer.IP + ":" + "9000"
					fmt.Println("Connecting to", contentServer.IP, contentServer.Label)
					serverContconn, err := net.Dial("tcp", ip_add)
					if err != nil {
						fmt.Printf("[TCP | %v]  Couldn't Connect ", clientAddr)
						return
					}

					msg := pkgs.MessageBuilder("content", "content_sub", []byte(node_id))

					serverContconn.Write(msg)
					serverContconn.Close()

					clientLabel := Data
					streams.AddSource(contentServer)
					go streams.getStreamfromSource()
					fmt.Print("SOURCE ADDED: ", streams)

					client_ip := strings.Split(string(clientAddr.String()), ":")[0]
					client := streams.AddClient(nn, clientLabel, client_ip) //data -> media

					fmt.Print("\nCLIENT ADDED: ", client)

					msg = pkgs.MessageBuilder("content", "content_sub", []byte("OK"+node_id))
					conn.Write(msg)

					go streams.streamtoclient(client)

				}
				conn.Close()
			} else if Subtype == "content_term" { //client terminates streaming content
				/*
					if node has no other clients streaming
						close port
						send server_term to server streaming to it

				*/
				streams.RemoveClient(Data)
				if !streams.HasStreams() { //if no clients, initiate term protocol
					contentServer, err := nn.FindNodebyLabel("n16")
					fmt.Println("No Stream: ", contentServer)
					ip_add := contentServer.IP + ":" + "9000"
					connection, err := net.Dial("tcp", ip_add)
					if err != nil {
						panic(err)
					}

					msg := pkgs.MessageBuilder("content", "content_term", []byte(node_id))
					connection.Write(msg)
					connection.Close()

					streams.RemoveSource()
				}
			}

		default:
			fmt.Printf("[TCP | %s] Received Message : %s\n", clientAddr, string(buffer[:n]))
			fmt.Printf("[TCP | localhost ] No Protocol iniciated ")
		}
	}

}

func main() {

	// Creating network graph from json file
	network, err := initNetworkfromJSON("overlay.json")
	if err != nil {
		fmt.Println("Error creating overlay topology", err)
		os.Exit(1)
	}
	node_id = os.Args[1]
	nodo, err := network.FindNodebyName(node_id)
	if err != nil {
		fmt.Println("Node not present in config file", err)
		os.Exit(1)
	}
	node_address = nodo.IP

	stream := &Stream{}

	var neighbourNodes NeighbourNodes

	neighbourNodes.storeNeighbours(network)
	go func() {
		for {
			neighbourNodes.updateMetrics()
			fmt.Println("Nodes: ", neighbourNodes)
			interval := rand.Intn(11) + 10
			time.Sleep(time.Duration(interval) * time.Second)
		}
	}()

	// Opening TCP Listener

	tcpAddr, err := net.ResolveTCPAddr("tcp", fmt.Sprintf("%s:%s", node_address, pkgs.TCP_PORT))
	if err != nil {
		fmt.Println("Error resolving TCP address:", err)
		os.Exit(1)
	}

	tcpListener, err := net.Listen("tcp", tcpAddr.String())
	if err != nil {
		fmt.Println("Error creating TCP listener:", err)
		os.Exit(1)
	}
	defer tcpListener.Close()

	fmt.Printf("[TCP | localhost] Server listening on %s:%s\n", node_address, pkgs.TCP_PORT)

	for {
		tcpConn, err := tcpListener.Accept()
		if err != nil {
			fmt.Println("Error accepting TCP connection:", err)
		} else {
			go handleTCPrequests(tcpConn, network, &neighbourNodes, stream)
		}
	}
}
