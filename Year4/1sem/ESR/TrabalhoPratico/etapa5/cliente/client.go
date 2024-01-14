package main

import (
	"fmt"
	"io"
	"net"
	"os"
	"os/exec"
	"projetoesr/etapa5/pkgs"
	"strings"
)

func main() {
	overlay_node := os.Args[1]
	node_id := os.Args[2]
	connection, err := net.Dial("tcp", overlay_node+":"+pkgs.TCP_PORT)
	if err != nil {
		panic(err)
	}
	defer connection.Close()
	node_address := connection.LocalAddr()
	node_ip := strings.Split(string(node_address.String()), ":")[0]

	msg := pkgs.MessageBuilder("content", "content_sub", []byte(node_id))
	fmt.Println("Msg:", msg)
	connection.Write(msg)

	buffer := make([]byte, 1024)
	n, err := connection.Read(buffer)
	if err != nil {
		fmt.Printf("[TCP ]  Connection Closed ")
		return
	}
	Type, Subtype, Data, err := pkgs.MessageDecoder(buffer, n)
	connection.Close()

	ok_sign := Data[:2]
	multicast_id := Data[3:]
	if Type == "content" && Subtype == "content_sub" && ok_sign == "OK" {
		//start streaming process
		fmt.Println("My addr:", node_ip)
		source_add, err := net.ResolveUDPAddr("udp", "224.0.0."+multicast_id+":"+pkgs.UDP_PORT) //st.Source.IP+
		if err != nil {
			panic(err)
		}

		conn, err := net.ListenMulticastUDP("udp", nil, source_add)
		if err != nil {
			panic(err)
		}
		defer conn.Close()

		ffplayCmd := exec.Command("ffplay", "-f", "mjpeg", "-")
		ffplayStdin, err := ffplayCmd.StdinPipe()
		if err != nil {
			panic(err)
		}
		ffplayCmd.Stdout = os.Stdout
		ffplayCmd.Stderr = os.Stderr
		// Start ffplay process
		err = ffplayCmd.Start()
		if err != nil {
			panic(err)
		}
		fmt.Println("Start Reading...")
		buffer := make([]byte, 180*10) // Buffer to read UDP data
		for {
			fmt.Println("Start for")
			n, _, err := conn.ReadFromUDP(buffer) // Read UDP data
			if err != nil {
				if err != io.EOF {
					panic(err)
				}
				break
			}
			fmt.Println("Buffer:", buffer)
			_, err = ffplayStdin.Write(buffer[:n]) // Write UDP data to ffplay
			if err != nil {
				panic(err)
			}
		}

		//mandar mens content_term

		// Wait for ffplay to finish

		err = ffplayCmd.Wait()
		if err != nil {
			panic(err)
		}
	}

	connection, err = net.Dial("tcp", overlay_node+":"+pkgs.TCP_PORT)
	if err != nil {
		panic(err)
	}

	msg = pkgs.MessageBuilder("content", "content_term", []byte(node_id))
	connection.Write(msg)
	connection.Close()

}
