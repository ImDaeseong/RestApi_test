// main
package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"

	"github.com/gorilla/mux"
)

type Game struct {
	ID          string    `json:"id"`
	PackageName string    `json:"packagename"`
	GameTitle   string    `json:"gametitle"`
	GameDesc    *GameDesc `json:"gamedesc"`
}

type GameDesc struct {
	Details1 string `json:"details1"`
	Details2 string `json:"details2"`
}

var gamedata []Game

//AllList
func getGames(response http.ResponseWriter, request *http.Request) {
	response.Header().Set("Content-Type", "application/json")
	json.NewEncoder(response).Encode(gamedata)
}

//item
func getGame(response http.ResponseWriter, request *http.Request) {
	response.Header().Set("COntent-Type", "application/json")
	param := mux.Vars(request)

	for _, items := range gamedata {
		if items.ID == param["id"] {
			json.NewEncoder(response).Encode(items)
			return
		}
	}

	//Not Found
	json.NewEncoder(response).Encode("")
}

func main() {

	gamedata = append(gamedata, Game{ID: "1", PackageName: "com.pearlabyss.blackdesertm", GameTitle: "검은사막 모바일", GameDesc: &GameDesc{Details1: "당신이 진짜로 원했던 모험의 시작", Details2: "월드클래스 MMORPG “검은사막 모바일”"}})
	gamedata = append(gamedata, Game{ID: "2", PackageName: "com.kakaogames.moonlight", GameTitle: "달빛조각사", GameDesc: &GameDesc{Details1: "500만 구독자의 게임 판타지 대작 '달빛조각사'", Details2: "- 5레벨만 달성해도 달빛조각사 이모티콘 100% 지급!"}})
	gamedata = append(gamedata, Game{ID: "3", PackageName: "com.ncsoft.lineagem19", GameTitle: "리니지M", GameDesc: &GameDesc{Details1: "PC의 향수! 리니지 본질 그대로 리니지M", Details2: "PC리니지와 동일한 아덴월드의 오픈 필드"}})
	gamedata = append(gamedata, Game{ID: "4", PackageName: "com.netmarble.bnsmkr", GameTitle: "블레이드&소울 레볼루션", GameDesc: &GameDesc{Details1: "원작 감성의 방대한 세계관과 복수 중심의 흥미진진한 스토리", Details2: "MMORPG의 필드를 제대로 즐길 수 있는 경공"}})
	gamedata = append(gamedata, Game{ID: "5", PackageName: "com.cjenm.sknights", GameTitle: "세븐나이츠", GameDesc: &GameDesc{Details1: "Netmarble롤플레잉", Details2: "세나의 재탄생, 세븐나이츠: 리부트"}})
	gamedata = append(gamedata, Game{ID: "6", PackageName: "com.google.android.youtube", GameTitle: "YouTube", GameDesc: &GameDesc{Details1: "Google LLC동영상 플레이어/편집기", Details2: "좋아하는 동영상 빠르게 검색하기"}})

	router := mux.NewRouter()

	//http://127.0.0.1:8080/api/AllList
	router.HandleFunc("/api/AllList", getGames).Methods("GET")

	//http://127.0.0.1:8080/api/item/1
	router.HandleFunc("/api/item/{id}", getGame).Methods("GET")

	fmt.Println("start server")
	log.Fatal(http.ListenAndServe(":8080", router))

}
