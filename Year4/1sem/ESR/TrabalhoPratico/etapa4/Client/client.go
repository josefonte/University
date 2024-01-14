// socket-client project main.go
package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"encoding/binary"
	"bytes"
)

const (
        SERVER_HOST = "localhost"
        SERVER_PORT = "3000"
        SERVER_TYPE = "tcp"	
)

var videos = []string{"video1", "video2"}

func handleMessages(connection net.Conn){
	buffer := make([]byte, 1024)
	for {
		_, err := connection.Read(buffer)
		if err != nil{
			fmt.Println("Error reading from UDP:", err)
			return
		}
		switch {
		//case (buffer[0]) == 0b00000000: //control messages
		//	fmt.Printf("[TCP | %v] Control | ", addr)
		//	if (buffer[1]) == 0b00000000 {
		//		data := string(buffer[2:n])
		//		fmt.Printf("Bootstrapper |  %s \n", data)
		//		nodo, err := network.FindNodebyName(data)
		//		nodelist := network.GetConnectedNodesIPs(nodo)
	//
		//		if err != nil {
		//			fmt.Println(err)
		//		}
	//
		//		message := strings.Join(nodelist, ",")
		//		conn.WriteTo([]byte(message), addr)
		//		fmt.Printf("[TCP | localhost] Sending Message: %v\n", message)
		//	}

		case (buffer[0]) == 0b00000011: //control messages
			fmt.Printf("[TCP ] Control Messages | ")
			var writeBuffer bytes.Buffer
			if (buffer[1]) == 0b00000001 {
				fmt.Printf("Video Request | ")
				binary.Write(&writeBuffer, binary.BigEndian, uint64(len(videos)))
				for _, video := range videos {
					binary.Write(&writeBuffer, binary.BigEndian, uint64(len(video)))
					binary.Write(&writeBuffer, binary.BigEndian, []byte(video))
				}
			}
			connection.Write(writeBuffer.Bytes())	

		default:
			msgType, _ := binary.Varint(buffer[:0])
			fmt.Printf("[TCP | localhost ] Received Message : %s\n", fmt.Sprint(msgType))
			fmt.Printf("[TCP | localhost ] No Protocol iniciated ")
		}
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

        handleMessages(connection)

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