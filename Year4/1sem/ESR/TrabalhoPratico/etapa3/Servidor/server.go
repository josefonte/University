package main

import (
	"log"
	"net"

	//"fmt"
	"os"
	"os/exec"
)

func Stream(conn net.Conn) {
	file, err := os.Open("movie.Mjpeg") // Open the video file
	if err != nil {
		log.Fatal("open video: ", err)
	}
	defer file.Close()

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

	buffer := make([]byte, 180*10)
	for {
		size, err := file.Read(buffer) // Read video file content into buffer
		if err != nil {
			break // Break loop at the end of the file
		}

		_, err = ffplayStdin.Write(buffer[:size]) // Send video data to ffplay
		if err != nil {
			break // Break loop on error
		}

		_, err = conn.Write(buffer[:size]) // Send video data over UDP
		if err != nil {
			//log.Fatal("udp: ", err)
			continue
		}
	}
}

func main() {

	conn, err := net.Dial("udp", "10.0.1.20:9000") // Establish UDP connection
	if err != nil {
		log.Fatal("dial UDP: ", err)
	}
	defer conn.Close()

	Stream(conn) // Start streaming the video over UDP
}
