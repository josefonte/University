package pkgs

import (
	"bytes"
	"errors"
	"fmt"
	"strings"
)

const (
	TCP_PORT = "9000"
	UDP_PORT = "8000"
)

// ---------------------------------------

func MetricsScoring(latency int, jitter int, packet_loss float32) float32 {
	var wlatency, wjitter, wpacket float32 = 0.2, 0.5, 0.3
	return float32(latency)*wlatency + float32(jitter)*wjitter + wpacket*(1-packet_loss)
}

func MessageBuilder(Type string, SubType string, data []byte) []byte {
	var buffer bytes.Buffer
	typeBytes := []byte{0b00000000, 0b00000000}
	if strings.ToLower(Type) == "control" {
		typeBytes[0] = 0b00000001
		if strings.ToLower(SubType) == "bootstraper" {
			typeBytes[1] = 0b0000001
		} else if strings.ToLower(SubType) == "connect" {
			typeBytes[1] = 0b0000010
		} else if strings.ToLower(SubType) == "rp_path" {
			typeBytes[1] = 0b0000011
		}

	} else if strings.ToLower(Type) == "metric" {
		typeBytes[0] = 0b00000010
		if strings.ToLower(SubType) == "latency" {
			typeBytes[1] = 0b00000001
		} else if strings.ToLower(SubType) == "package_loss" {
			typeBytes[1] = 0b00000010
		}
	}

	_, err := buffer.Write(typeBytes)
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

func MessageDecoder(buf []byte, n int) (Type string, Subtype string, Data string, err error) {
	switch {
	case (buf[0]) == 0b00000001: //control messages
		Type = "control"
		if (buf[1]) == 0b00000001 {
			Subtype = "bootstraper"
		} else if (buf[1]) == 0b00000010 {
			Subtype = "connect"
		} else if (buf[1]) == 0b00000011 {
			Subtype = "rp_path"
		}
	case (buf[0]) == 0b00000010: //control messages
		Type = "metric"
		if (buf[1]) == 0b00000001 {
			Subtype = "latency"
		} else if (buf[1]) == 0b00000010 {
			Subtype = "package_loss"
		}
	default:
		err = errors.New("Invalid message type")
		return "", "", "", err
	}
	Data = string(buf[2:n])
	return Type, Subtype, Data, nil
}
