package main

import (
	"fmt"
	"math/rand"
	"net"
	"os"
	"os/exec"
	"projetoesr/etapa5/pkgs"
	"regexp"
	"strconv"
	"strings"
	"sync"
	"time"
)

var node_id string
var node_address string
var rp_address string

type Metrics struct {
	Status     bool
	Latency    int
	Packetloss float32
	Jitter     int
}

type DirectConnection struct {
	Label      string
	IP         string
	RP_gateway bool
	Metrics    Metrics
}

type NeighbourNodes struct {
	Nodes []DirectConnection
}

func (node *DirectConnection) measureMetrics() {

	if node.Metrics.Status {

		cmd := exec.Command("ping", "-c", "10", node.IP)

		// Run the command and capture its output
		output, err := cmd.CombinedOutput()
		fmt.Println("Host: ", node.IP)
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

		fmt.Printf("[Metrics of %s] Packetloss-%.2f, Latency-%d, Jitter-%d\n",
			node.Label, node.Metrics.Packetloss, node.Metrics.Latency, node.Metrics.Jitter)
		if err != nil {
			panic(err)
		}

	} else {
		fmt.Print("Couldn't Measure Metrics with ", node.Label, "| Not connected to the Overlay")
	}
}

func (nn *NeighbourNodes) AddNode(node DirectConnection) {
	nn.Nodes = append(nn.Nodes, node)
}

func (nn *NeighbourNodes) findRPgateway() *DirectConnection {
	for i := range nn.Nodes {
		nd := &nn.Nodes[i]
		if nd.RP_gateway {
			return nd
		}
	}
	return nil
}

func (nodes *NeighbourNodes) storeNeighbours(data string) {

	nodesINFO := strings.Split(data, ",")

	for _, val := range nodesINFO {
		nodeinf := strings.Split(val, "|")
		if nodeinf[0] == node_id {
			node_address = nodeinf[1]
		} else {
			node := DirectConnection{
				Label:      nodeinf[0],
				IP:         nodeinf[1],
				RP_gateway: false,
				Metrics: Metrics{
					Status:     false,
					Latency:    0,
					Packetloss: 0,
					Jitter:     0,
				},
			}
			nodes.AddNode(node)
		}
	}
}

func (nodes *NeighbourNodes) bootstraper() {
	conn, err := net.Dial("tcp", rp_address)
	if err != nil {
		panic(err)
	}

	clientAddr := conn.RemoteAddr()
	defer conn.Close()

	message := pkgs.MessageBuilder("control", "bootstraper", []byte(node_id))
	conn.Write(message)
	fmt.Printf("[TCP | %s] Sending Message (control|bootstraper|%s)\n", clientAddr, string(message))

	buffer := make([]byte, 1024)
	n, err := conn.Read(buffer)
	if err != nil {
		fmt.Println("Error reading from server:", err)
		return
	}
	fmt.Printf("[TCP | %s] Received Message : %s\n", clientAddr, string(buffer[:n]))
	Type, Subtype, Data, err := pkgs.MessageDecoder(buffer, n)

	if err != nil {
		fmt.Println("Error on Received Message", err)
		return
	}
	if Type == "control" && Subtype == "bootstraper" {
		nodes.storeNeighbours(Data)
	}

}

func (nodes *NeighbourNodes) connect() {

	for i := range nodes.Nodes {
		target := &nodes.Nodes[i]
		ip_add := target.IP + ":" + "9000"
		fmt.Println("Connecting to", target.IP, target.Label)
		conn, err := net.Dial("tcp", ip_add)
		if err != nil {
			fmt.Println("Couldn't Connect to ", ip_add)
			target.Metrics = Metrics{Status: false, Latency: 0, Packetloss: 0, Jitter: 0}
		} else {
			defer conn.Close()
			message := pkgs.MessageBuilder("control", "connect", []byte(node_id))
			conn.Write(message)
			buffer := make([]byte, 1024)
			n, err := conn.Read(buffer)
			if err != nil {
				fmt.Println("Error reading connection:", err)
				return
			}
			Type, Subtype, Data, err := pkgs.MessageDecoder(buffer, n)
			if Type == "control" && Subtype == "connect" {
				target.Metrics.Status = true
				fmt.Printf("[TCP | %s] Received Message : %s\n", target.IP, Data)
				// change for metrics functions
				target.measureMetrics()
			}

			fmt.Printf("[TCP] Connection Succeded with %s - %s\n", target.Label, target.IP)
		}
	}
}

func (nn *NeighbourNodes) updateMetrics() {
	for i := range nn.Nodes {
		node := &nn.Nodes[i]
		node.measureMetrics()
	}
}

func rp_path_finder_worker(node *DirectConnection,
	returnChan chan<- struct {
		Label string
		Value float32
	}, wg *sync.WaitGroup) {
	defer wg.Done()

	id_add := node.IP + ":" + pkgs.TCP_PORT
	conn, err := net.Dial("tcp", id_add)
	if err != nil {
		fmt.Println("\n2: Couldn't Connect to ", id_add)
		node.Metrics = Metrics{Status: false, Latency: 0, Packetloss: 0, Jitter: 0}
	} else {
		//manda msg com control|rp_path|node_id
		defer conn.Close()
		mes := pkgs.MessageBuilder("control", "rp_path", []byte(node_id))
		conn.Write(mes)
		buffer := make([]byte, 1024)
		n, _ := conn.Read(buffer)
		//recebe score das metricas
		Type, Subtype, Data, err := pkgs.MessageDecoder(buffer, n)
		if err != nil {
			fmt.Println("Error in message decoder:", err)
			return
		}
		//calcula final score media entre score e metricas entre nodos
		if Type == "control" && Subtype == "rp_path" {
			branch_score, err := strconv.ParseFloat(Data, 32)
			if err != nil {
				fmt.Println("Wrong Data Score Branch:", err)
				return
			}
			final_score := (float32(branch_score) + pkgs.MetricsScoring(node.Metrics.Latency, node.Metrics.Jitter, float32(node.Metrics.Packetloss))) / 2
			chanmsg := struct {
				Label string
				Value float32
			}{Label: node.Label, Value: final_score}
			returnChan <- chanmsg
		}

	}
}

func (nodes *NeighbourNodes) rp_path_finder(dados string) string {

	var wg sync.WaitGroup
	returnChan := make(chan struct {
		Label string
		Value float32
	}) // Buffered channel to return worker values

	// Launch worker goroutines
	for i := range nodes.Nodes {
		target := &nodes.Nodes[i]
		if target.Label != dados && target.Metrics.Status {
			wg.Add(1)
			go rp_path_finder_worker(target, returnChan, &wg)
		}
	}

	responses := make([]struct {
		Label string
		Value float32
	}, 0)

	done := make(chan struct{})
	go func() {
		defer close(done) // Signal that data collection is done when this goroutine exits
		for data := range returnChan {
			// Populate responses while collecting data from returnChan
			responses = append(responses, data)
		}
	}()

	wg.Wait()

	close(returnChan)
	<-done

	lowest_score := struct {
		Label string
		Value float32
	}{Label: "", Value: 10000}
	for _, result := range responses {
		if result.Value < lowest_score.Value {
			lowest_score = result
		}
	}

	for i := range nodes.Nodes {
		nd := &nodes.Nodes[i]
		if nd.Label == lowest_score.Label {
			nd.RP_gateway = true
		} else {
			nd.RP_gateway = false
		}
	}
	return strconv.FormatFloat(float64(lowest_score.Value), 'f', -1, 32)
}

// --------------------------------------------------------------------------
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
	fmt.Print("Reading stream")
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
	fmt.Print("Writing stream to ", client.Label)
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

func handleTCPclientMessages(conn net.Conn, nn *NeighbourNodes, streams *Stream) {
	defer conn.Close()
	buffer := make([]byte, 1024)
	clientAddr := conn.RemoteAddr()
	for {
		n, err := conn.Read(buffer)
		if err != nil || n == 0 {
			fmt.Printf("[TCP | %v]  Connection Closed ", clientAddr)
			return
		}
		Type, Subtype, Data, err := pkgs.MessageDecoder(buffer, n)

		switch {
		case Type == "control": //control messages
			fmt.Printf("[TCP | %v] Control | ", clientAddr)
			if Subtype == "connect" { //connect (testa conecção)
				fmt.Printf("Connect | %s \n", Data)

				for i := range nn.Nodes {
					nd := &nn.Nodes[i]
					//se o nodo a conectar-se já estiver nos vizinhos
					if Data == nd.Label {
						nd.Metrics.Status = true
					}
				}
				msg := pkgs.MessageBuilder("control", "connect", []byte("active"))
				conn.Write(msg)
				conn.Close()

			}
			if Subtype == "rp_path" { //connect (testa conecção)
				fmt.Printf("RP_PATH | %s \n", Data)
				score := nn.rp_path_finder(Data)
				msg := pkgs.MessageBuilder("control", "rp_path", []byte(score))
				conn.Write(msg)
				conn.Close()
			}
		case Type == "content": //content related messages
			fmt.Printf("[TCP | %v] Content | ", clientAddr)
			if Subtype == "content_sub" { //client askes for content

				fmt.Printf("ContentSub | %s \n", Data)
				fmt.Println("Streams:", streams)

				if streams.HasStreams() {
					fmt.Printf("Has Streams")
					client_ip := strings.Split(string(clientAddr.String()), ":")[0]
					client := streams.AddClient(nn, Data, client_ip) //data -> media
					fmt.Print("Client ADDED: ", client)

					msg := pkgs.MessageBuilder("content", "content_sub", []byte("OK"+node_id))
					conn.Write(msg)

					go streams.streamtoclient(client)

				} else {
					fmt.Println("No Streams")
					rp_gateway := nn.findRPgateway()
					ip_add := rp_gateway.IP + ":" + "9000"
					fmt.Println("Connecting to", rp_gateway.IP, rp_gateway.Label)
					RP_gateway_conn, err := net.Dial("tcp", ip_add)
					if err != nil {
						fmt.Printf("[TCP | %v]  Couldn't Connect ", clientAddr)
						return
					}

					msg := pkgs.MessageBuilder("content", "content_sub", []byte(node_id))

					RP_gateway_conn.Write(msg)
					clientLabel := Data
					n, err := RP_gateway_conn.Read(buffer)
					RP_gateway_conn.Close()
					if err != nil || n == 0 {
						fmt.Printf("[TCP | %v]  Connection Closed ", clientAddr)
						return
					}
					Type, Subtype, Data, err = pkgs.MessageDecoder(buffer, n)
					ok_sign := Data[:2]
					if Type == "content" && Subtype == "content_sub" && ok_sign == "OK" {
						streams.AddSource(rp_gateway)
						client_ip := strings.Split(string(clientAddr.String()), ":")[0]
						fmt.Print("SOURCE ADDED: ", streams)
						go streams.getStreamfromSource()
						client := streams.AddClient(nn, clientLabel, client_ip) //data -> media
						fmt.Print("\nCLIENT ADDED: ", client)
						msg = pkgs.MessageBuilder("content", "content_sub", []byte("OK"+node_id))
						conn.Write(msg)
						go streams.streamtoclient(client)
					}

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
					connection, err := net.Dial("tcp", streams.Source.IP+":"+pkgs.TCP_PORT)
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
	fmt.Println("Server Running...")
	rp_address = os.Args[1] + ":" + pkgs.TCP_PORT
	node_id = os.Args[2]
	server, err := net.Listen("tcp", node_address+":"+pkgs.TCP_PORT)
	if err != nil {
		fmt.Println("Error listening:", err.Error())
		os.Exit(1)
	}
	defer server.Close()
	fmt.Println("Listening on " + node_address + ":" + pkgs.TCP_PORT)

	var neighbourNodes NeighbourNodes
	stream := &Stream{}

	neighbourNodes.bootstraper()
	fmt.Print(neighbourNodes)
	go func() {
		for {
			neighbourNodes.connect()
			neighbourNodes.rp_path_finder(node_id)
			fmt.Print("Nodes:", neighbourNodes)
			interval := rand.Intn(11) + 10
			time.Sleep(time.Duration(interval) * time.Second)
		}
	}()

	for {
		connection, err := server.Accept()
		if err != nil {
			fmt.Println("Error accepting: ", err.Error())
			os.Exit(1)
		}
		fmt.Printf("Client %v connected \n", connection.RemoteAddr())

		go handleTCPclientMessages(connection, &neighbourNodes, stream)

	}

}
