package main

import (
	"fmt"
	"math/rand"
	"net"
	"os"
	"projetoesr/etapa2/pkgs"
	"strconv"
	"strings"
	"sync"
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

func (nn *NeighbourNodes) AddNode(node DirectConnection) {
	nn.Nodes = append(nn.Nodes, node)
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
				target.Metrics.Latency = rand.Intn(100) + 1
				target.Metrics.Jitter = rand.Intn(100) + 1
				target.Metrics.Packetloss = rand.Float32()
			}
			fmt.Printf("[TCP] Connection Succeded with %s - %s\n", target.Label, target.IP)
		}
	}
}

// timeout de resposta
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

// ao subscrever a rede - connect()
// ao pedir conteudo()
// dados = node de quem veio a msg
// trigger node => dados = node_id
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
	fmt.Println("Responses:", responses)

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
	fmt.Println("\nPath Finder Nodes:", nodes)
	fmt.Println("Path Finder lowest Score:", lowest_score)
	return strconv.FormatFloat(float64(lowest_score.Value), 'f', -1, 32)

}

func handleTCPclientMessages(conn net.Conn, nn *NeighbourNodes) {
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

			}
			if Subtype == "rp_path" { //connect (testa conecção)
				fmt.Printf("RP_PATH | %s \n", Data)
				score := nn.rp_path_finder(Data)
				msg := pkgs.MessageBuilder("control", "rp_path", []byte(score))
				conn.Write(msg)
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

	neighbourNodes.bootstraper()
	fmt.Print(neighbourNodes)
	go func() {
		neighbourNodes.connect()
		neighbourNodes.rp_path_finder(node_id)
		fmt.Print("Nodes [connect + path]:", neighbourNodes)
	}()

	for {
		connection, err := server.Accept()
		if err != nil {
			fmt.Println("Error accepting: ", err.Error())
			os.Exit(1)
		}
		fmt.Printf("Client %v connected \n", connection.RemoteAddr())

		go handleTCPclientMessages(connection, &neighbourNodes)

	}

}
