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

	fmt.Println("--------------------Call getAllList-----------------------")

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

func getItem1List() bool {

	fmt.Println("--------------------Call getItem1List-----------------------")

	defaulturl := "http://127.0.0.1:8080/api/item/1"
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

func getItem2List() bool {

	fmt.Println("--------------------Call getItem2List-----------------------")

	defaulturl := "http://127.0.0.1:8080/api/item/2"
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

func getItem3List() bool {

	fmt.Println("--------------------Call getItem3List-----------------------")

	defaulturl := "http://127.0.0.1:8080/api/item/3"
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
			getAllList()
			time.Sleep(100 * time.Millisecond)
		}
	}()

	go func() {
		for {
			getItem1List()
			time.Sleep(500 * time.Millisecond)
		}
	}()

	go func() {
		for {
			getItem2List()
			time.Sleep(100 * time.Millisecond)
		}
	}()

	go func() {
		for {
			getItem3List()
			time.Sleep(500 * time.Millisecond)
		}
	}()

	fmt.Scanln() // 엔터누르면 종료
}
