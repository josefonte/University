package main

import (
	"encoding/json"
	"errors"
	"fmt"
	"math/rand"
	"net"
	"os"
	"projetoesr/etapa2/pkgs"
	"strconv"
	"strings"
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
					Latency:    rand.Intn(100) + 1,
					Packetloss: rand.Float32(),
					Jitter:     rand.Intn(100) + 1,
				},
			}
			nn.AddNode(node)
		}

	}
}

/*
func handleUDPrequests(conn *net.UDPConn, network *NetworkGraph, nn *NeighbourNodes) {
	buf := make([]byte, 1024)
	for {
		n, addr, err := conn.ReadFromUDP(buf)
		if err != nil || n == 0 {
			fmt.Println("Error reading from UDP:", err)
			return
		}
		fmt.Print(addr.String() + string(buf[:n]))
	}
}*/

func handleTCPrequests(conn net.Conn, network *NetworkGraph, nn *NeighbourNodes) {
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
					}
				}
				msg := pkgs.MessageBuilder("control", "connect", []byte("active"))
				conn.Write(msg)

			}
			if Subtype == "rp_path" { //connect (testa conecção)
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

	var neighbourNodes NeighbourNodes

	neighbourNodes.storeNeighbours(network)
	fmt.Println("Store", neighbourNodes)
	/*
		//Openning UDP socket
		udpAddr, err := net.ResolveUDPAddr("udp", fmt.Sprintf("%s:%s", node_address, pkgs.UDP_PORT))
		if err != nil {
			fmt.Println("Error resolving UDP address:", err)
			os.Exit(1)
		}

		udpConn, err := net.ListenUDP("udp", udpAddr)
		if err != nil {
			fmt.Println("Error creating UDP listener:", err)
			os.Exit(1)
		}
		defer udpConn.Close()

		fmt.Printf("[UDP | localhost] Server listening on %s:%s\n", node_address, pkgs.UDP_PORT)
	*/
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

	// handle UDP and TCP connections
	//go handleUDPrequests(udpConn, network, &neighbourNodes)
	for {
		tcpConn, err := tcpListener.Accept()
		if err != nil {
			fmt.Println("Error accepting TCP connection:", err)
		} else {
			go handleTCPrequests(tcpConn, network, &neighbourNodes)
		}
	}
}
