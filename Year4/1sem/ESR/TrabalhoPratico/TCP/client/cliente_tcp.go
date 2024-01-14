// socket-client project main.go
package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
)
const (
        SERVER_HOST = "localhost"
        SERVER_PORT = "9988"
        SERVER_TYPE = "tcp"
)

func handleServerMessages(connection net.Conn){
    serverReader := bufio.NewReader(connection)
	for {
		message, err := serverReader.ReadString('\n')
		if err != nil {
			fmt.Println("Server Disconnected")
			return
		}
		fmt.Printf("\n[Server %v]: %vClient: ",  connection.RemoteAddr(),message)
	}
}

func main() {
        //establish connection
        connection, err := net.Dial(SERVER_TYPE, SERVER_HOST+":"+SERVER_PORT)
        if err != nil {
                panic(err)
        }

        defer connection.Close()

        fmt.Println("Connection with Server Established")

        go handleServerMessages(connection)

        ///send some data
        clientReader :=  bufio.NewReader(os.Stdin);
        for {
            fmt.Printf("Client: ")
            clientMessage, inputError := clientReader.ReadString('\n')
                if inputError != nil {
                        fmt.Println("Error reading Server input: ", inputError.Error())
                        os.Exit(1)  
                }
            if clientMessage == "exit"{
                fmt.Println("Closing Client")
                os.Exit(1)  
            }else {
                connection.Write([]byte( clientMessage))
            }
            
        }
       
       
}