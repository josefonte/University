package main

import (
	"fmt"
	"log"
	"net"
	"os"

	//"os/exec"
	"projetoesr/etapa5/pkgs"
	"time"
)

var node_address string
var node_id string
var rp_address string

func streamContent(conn net.Conn, stopSignal chan struct{}) {
	fmt.Println("Start Streaming on ", conn.RemoteAddr())
	file, err := os.Open("movie.Mjpeg") // Open the video file
	if err != nil {
		log.Fatal("open video: ", err)
	}
	defer file.Close()

	/*
		ffplayCmd := exec.Command("ffplay", "-af", "volume=0.0", "-f", "mjpeg", "-")
		ffplayStdin, err := ffplayCmd.StdinPipe()
		if err != nil {
			panic(err)
		}

		// Start ffplay process
		err = ffplayCmd.Start()
		if err != nil {
			panic(err)
		}
	*/
	time.Sleep(1 * time.Second)

	buffer := make([]byte, 180*10)
	for {
		select {
		case <-stopSignal:
			fmt.Println("Stop signal received. Closing connection.")
			conn.Close()
			file.Close()
			return
		default:
			size, err := file.Read(buffer)
			if err != nil {
				// Handle errors, including reaching the end of the file
				if err.Error() == "EOF" {
					// If end of file is reached, rewind to the beginning
					file.Seek(0, 0)
				} else {
					fmt.Println("Error reading file:", err)
					break
				}
			}

			_, err = conn.Write(buffer[:size]) // Send video data over UDP
			if err != nil {
				//log.Fatal("udp: ", err)
				continue
			}

			time.Sleep(8 * time.Millisecond)
		}
	}

}

func handleTCPclientMessages(conn net.Conn, stopSignal chan struct{}) {
	defer conn.Close()
	buffer := make([]byte, 1024)
	clientAddr := conn.RemoteAddr()
	for {
		n, err := conn.Read(buffer)
		if err != nil || n == 0 {
			fmt.Printf("[TCP | %v]  Connection Closed ", clientAddr)
			return
		}
		Type, Subtype, data, err := pkgs.MessageDecoder(buffer, n)

		switch {
		case Type == "content": //control messages
			fmt.Printf("[TCP | %v] Content | ", clientAddr)
			if Subtype == "content_sub" { //connect (testa conecção)
				fmt.Print("[Content Sub | ", data)
				fmt.Println("Streaming to", rp_address)
				UDPconn, err := net.Dial("udp", rp_address+":"+pkgs.UDP_PORT) // Establish UDP connection with rpServer
				if err != nil {
					log.Fatal("dial UDP: ", err)
				}
				conn.Close()

				go streamContent(UDPconn, stopSignal)
			} else if Subtype == "content_term" { //connect (testa conecção)
				stopSignal <- struct{}{}
			}
		default:
			fmt.Printf("[TCP | %s] Received Message : %s\n", clientAddr, string(buffer[:n]))
			fmt.Printf("[TCP | localhost ] No Protocol iniciated ")
		}
	}
}

func main() {
	//abrir socket tcp e handle tcp
	fmt.Println("Server Running...")
	node_address = ":" + pkgs.TCP_PORT
	rp_address = os.Args[1]
	server, err := net.Listen("tcp", node_address)
	if err != nil {
		fmt.Println("Error listening:", err.Error())
		os.Exit(1)
	}
	defer server.Close()
	fmt.Println("Listening on " + node_address)

	for {
		connection, err := server.Accept()
		if err != nil {
			fmt.Println("Error accepting: ", err.Error())
			os.Exit(1)
		}

		stopSignal := make(chan struct{})
		fmt.Printf("Client %v connected \n", connection.RemoteAddr())

		go handleTCPclientMessages(connection, stopSignal)

	}

}
