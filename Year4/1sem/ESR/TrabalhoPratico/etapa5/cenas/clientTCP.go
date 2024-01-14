package main

import (
	"net"
)

func main() {

	connection, err := net.Dial("tcp", "localhost:9000")
	if err != nil {
		panic(err)
	}

	defer connection.Close()

	clientMessage := ("HELLO from TCP client")
	connection.Write([]byte(clientMessage))
}
