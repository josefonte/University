package main

import (
//	"bufio"
	"fmt"
	"net"
	"strings"
	"bytes"
	"encoding/binary"
	"os/exec"
	"regexp"
	"strconv"
)    

type Metrics struct {
    Status     bool
    Latency    int
    Packetloss float64
    Jitter     int
}

type DirectConnection struct {
    Label      string
    IP         string
    RP_gateway bool
    Metrics    Metrics
}

const port = "9000"
const node_address = "10.0.2.1"

func messageBuilder(Type string, SubType string, data []byte) []byte {
	var buffer bytes.Buffer
	typeBytes := []byte{0b00000000,0b00000000}
	if strings.ToLower(Type) == "control" {
			typeBytes[0] = 0b00000001
			if strings.ToLower(SubType) == "bootstraper" {
				typeBytes[1] = 0b00000001
			}
	} else if strings.ToLower(Type) == "avaliable_data" {
			typeBytes[0] = 0b00000011
			if strings.ToLower(SubType) == "videos" {
				typeBytes[1] = 0b00000001
			}
	}
	err := binary.Write(&buffer, binary.BigEndian, typeBytes)
	if err != nil {
		fmt.Println("Error writing first three bits:", err)
		return nil
	}
	_, err = buffer.Write(data)
	if err != nil {
		fmt.Println("Error writing string:", err)
		return nil
	}

	finalMessage := buffer.Bytes()
	return finalMessage

}

func connectToServer() net.Conn {
	server, err := net.Listen("tcp", node_address+":"+port)
	if err != nil {
			fmt.Println("Error listening to TCP:", err.Error())
			return nil
	}
	defer server.Close()
	connection, err := server.Accept()
	fmt.Printf("Connected to %v \n", node_address)
	return connection
}

func (node *DirectConnection) getContentInServer() []string{
	connection := connectToServer()
	connection.Write(messageBuilder("avaliable_data", "videos", []byte{}))
    readBuffer := make([]byte, 64)
    _, err := connection.Read(readBuffer)
    if err != nil {
            fmt.Println("Error reading from TCP:", err)
            return nil
    }
    length := binary.BigEndian.Uint64(readBuffer[:8])
    videos := make([]string, length)
    bits_read := uint64(8)
    for i := uint64(0); i < length; i++ {
            size := binary.BigEndian.Uint64(readBuffer[bits_read:8+bits_read])
            bits_read += 8
            videos[i] = string(readBuffer[bits_read:size+bits_read])
            bits_read += size
    }
    return videos
}

func (node *DirectConnection) measureMetrics() {

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

	node.Metrics.Packetloss, _ = strconv.ParseFloat(lossMatch[1], 32)
	node.Metrics.Packetloss = node.Metrics.Packetloss/100
	node.Metrics.Latency, err = strconv.Atoi(latency_jitter[1])
   	if err != nil {
    	    // ... handle error
    	    panic(err)
    	}
	node.Metrics.Jitter, err = strconv.Atoi(latency_jitter[2])
   	if err != nil {
    	    // ... handle error
    	    panic(err)
    	}
	// Print the output
	
	fmt.Println("Package Loss: ", node.Metrics.Packetloss)
	fmt.Println("Latency: ", node.Metrics.Latency)
	fmt.Println("Jitter: ", node.Metrics.Jitter)
	fmt.Println(lines[len(lines)-2])
	fmt.Println(lines[len(lines)-3])
}

func main() {
	Metrics := Metrics{Status: true, Latency: 0, Packetloss: 0, Jitter: 0}
	DirectConnection := DirectConnection{Label: "RP", IP: "10.0.2.10", RP_gateway: true, Metrics: Metrics}
	DirectConnection.measureMetrics()
	video := DirectConnection.getContentInServer()
	fmt.Printf("Videos : %v \n", video)
}