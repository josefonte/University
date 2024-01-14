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

func handleclientMessages(connection net.Conn){
        clientReader := bufio.NewReader(connection)
	for {
		message, err := clientReader.ReadString('\n')
		if err != nil {
			fmt.Println("Client disconnected")
			return
		}else if message == "exit" {
                        fmt.Println("Client disconnected")
			return
                }

                fmt.Printf("\n[Client %v]: %vServer: ",  connection.RemoteAddr(),message)
	}
}

func main() {
        fmt.Println("Server Running...")
        server, err := net.Listen(SERVER_TYPE, SERVER_HOST+":"+SERVER_PORT)
        if err != nil {
                fmt.Println("Error listening:", err.Error())
                os.Exit(1)
        }
        defer server.Close()
        fmt.Println("Listening on " + SERVER_HOST + ":" + SERVER_PORT)

        for {
                connection, err := server.Accept()
                if err != nil {
                        fmt.Println("Error accepting: ", err.Error())
                        os.Exit(1)
                }
                fmt.Printf("Client %v connected \n", connection.RemoteAddr())
                
                go handleclientMessages(connection)    
                
                
                serverReader :=  bufio.NewReader(os.Stdin);
	        for {
                        fmt.Printf("Server: ")
		        serverMessage, inputError := serverReader.ReadString('\n')
                        if inputError != nil {
                                fmt.Println("Error reading Server input: ", inputError.Error())
                                os.Exit(1)  
                        }
	        	connection.Write([]byte(serverMessage))
	        }
                
        }
}


func processClient(connection net.Conn) {
        buffer := make([]byte, 1024)
        mLen, err := connection.Read(buffer)
        if err != nil {
                fmt.Println("Error reading:", err.Error())
        }
        
        clientMessage := string(buffer)

        fmt.Printf("[Client %v]: %v ",  connection.RemoteAddr(), clientMessage)

        if clientMessage == "exit"{

        }
        _, err = connection.Write([]byte("Thanks! Great Convo:" + string(buffer[:mLen])))
        connection.Close()
}