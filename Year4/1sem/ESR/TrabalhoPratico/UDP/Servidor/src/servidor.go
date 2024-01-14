package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"strings"
)

func server() {
	socket, err := net.ListenPacket("udp", "127.0.0.1:3000")

	if err != nil {
		fmt.Println(err.Error())
		os.Exit(1)
	}

	defer socket.Close()

	buffer := make([]byte, 1024)

	terminal := bufio.NewReader(os.Stdin)

	fmt.Printf("Abri um socket e estou à escuta em 127.0.0.1:3000\n\n")

	for string(buffer) != "exit" {
		_, remetente, erroRead := socket.ReadFrom(buffer)

		if erroRead != nil {
			fmt.Println(err.Error())
			os.Exit(1)
		}

		fmt.Printf("Recebi uma mensagem do %s: %s\n\n", remetente.String(), string(buffer))

		message, _ := terminal.ReadString('\n')

		message = strings.Replace(message, "\n", "", -1)

		_, erroWrite := socket.WriteTo([]byte(message), remetente)

		if erroWrite != nil {
			fmt.Println(err.Error())
			os.Exit(1)
		}

		fmt.Printf("buffer: %s \n", string(buffer))
	}

}

func main() {
	server()
}
