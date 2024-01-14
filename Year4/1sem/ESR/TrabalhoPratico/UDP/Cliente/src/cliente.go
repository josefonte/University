package main

import (
	"fmt"
	"net"
	"os"
	"bufio"
	"strings"
)

func main() {
	socket, err := net.ListenPacket("udp", "")
	//var message string

	defer socket.Close()

	exitmsg := "exit"

	terminal := bufio.NewReader(os.Stdin)
	fmt.Println("Message to Server write \"exit\" to end conversation:")

	for {
		
		message, _ := terminal.ReadString('\n')
		// convert CRLF to LF
		message = strings.Replace(message, "\n", "", -1)
		
		if err != nil {
			fmt.Println(err.Error())
			os.Exit(1)
		}
		
		defer socket.Close()
		
		endereco, err := net.ResolveUDPAddr("udp", "127.0.0.1:3000")
		
		if err != nil {
			fmt.Println(err.Error())
			os.Exit(1)
		}
		
		_, err = socket.WriteTo([]byte(message), endereco)
		
		if err != nil {
			fmt.Println(err.Error())
			os.Exit(1)
		}
		
		buffer := make([]byte, 1024)
		
		_, servidor, err := socket.ReadFrom(buffer)
		
		if err != nil {
			fmt.Println(err.Error())
			os.Exit(1)
		}
		
		fmt.Printf("Recebi resposta do servidor %s: %s\n\n", servidor.String(), string(buffer))

		if exitmsg == message {
			break
		}
	}
}