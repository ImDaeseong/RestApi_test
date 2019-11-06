// main
package main

import (
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"time"
)

func getAllList() bool {

	defaulturl := "http://127.0.0.1:8080/api/AllList"
	sUrl := fmt.Sprintf("%s", defaulturl)

	res, err := http.Get(sUrl)
	if err != nil {
		log.Println(err)
		return false
	}
	defer res.Body.Close()

	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		log.Println(err)
		return false
	}

	fmt.Println(string(body))

	return true
}

func main() {

	go func() {
		for {
			fmt.Println("--------------------첫번째 요청-----------------------")
			getAllList()
			time.Sleep(100 * time.Millisecond)
		}
	}()

	go func() {
		for {
			fmt.Println("--------------------두번째 요청-----------------------")
			getAllList()
			time.Sleep(500 * time.Millisecond)
		}
	}()

	go func() {
		for {
			fmt.Println("--------------------세번째 요청-----------------------")
			getAllList()
			time.Sleep(100 * time.Millisecond)
		}
	}()

	go func() {
		for {
			fmt.Println("--------------------네번째 요청-----------------------")
			getAllList()
			time.Sleep(500 * time.Millisecond)
		}
	}()

	fmt.Scanln() // 엔터누르면 종료
}
