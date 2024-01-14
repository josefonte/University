package main

import (
	"bytes"
	"encoding/binary"
	"fmt"
	"net"
	"os"
	"strings"
)

type Metrics struct {
	Connected  bool
	Latency    int
	Packetloss int
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

func messageBuilder(Type string, Subtype string, data string) []byte {
	var buffer bytes.Buffer
	if strings.ToLower(Type) == "control" {
		typeBits := [1]byte{0b00000000}
		err := binary.Write(&buffer, binary.BigEndian, typeBits)
		if err != nil {
			fmt.Println("Error writing first three bits:", err)
			return nil
		}
		if strings.ToLower(Subtype) == "bootstraper" {
			subtypeBits := [1]byte{0b00000000}
			err := binary.Write(&buffer, binary.BigEndian, subtypeBits)
			if err != nil {
				fmt.Println("Error writing first three bits:", err)
				return nil
			}
		}
	}
	_, err := buffer.WriteString(data)
	if err != nil {
		fmt.Println("Error writing string:", err)
		return nil
	}

	finalMessage := buffer.Bytes()
	return finalMessage

}

func (nodes *NeighbourNodes) storeNeighbours(buf []byte) {
	data := string(buf)
	nodesINFO := strings.Split(data, ",")

	for _, val := range nodesINFO {
		nodeinf := strings.Split(val, "|")

		node := DirectConnection{
			Label: nodeinf[0],
			IP:    nodeinf[1],
			Metrics: Metrics{
				Connected:  false,
				Latency:    0,
				Packetloss: 0,
				Jitter:     0,
			},
		}
		nodes.AddNode(node)
	}
}

func main() {
	udpAddr, err := net.ResolveUDPAddr("udp", "localhost:8000")
	if err != nil {
		fmt.Println("Error resolving UDP address:", err)
		os.Exit(1)
	}
	conn, err := net.DialUDP("udp", nil, udpAddr)
	if err != nil {
		fmt.Println("Error creating UDP connection:", err)
		os.Exit(1)
	}
	defer conn.Close()

	message := messageBuilder("Control", "Bootstraper", "n1")

	conn.Write([]byte(message))

	buf := make([]byte, 1024)
	n, _, err := conn.ReadFrom(buf)

	if err != nil {
		fmt.Println("Error reading from connection:", err.Error())
		os.Exit(1)
	}

	fmt.Printf("Received data: %s \n", string(buf[:n]))

	var nodes NeighbourNodes
	nodes.storeNeighbours(buf[:n])

	fmt.Print(nodes)
}
