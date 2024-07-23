from flask import Flask, Response, json
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

# JSON 응답에 대한 한글 인코딩 설정
app.config['JSON_AS_ASCII'] = False


class GameDesc:
    def __init__(self, details1, details2):
        self.details1 = details1
        self.details2 = details2


class Game:
    def __init__(self, id, package_name, game_title, game_desc):
        self.id = id
        self.package_name = package_name
        self.game_title = game_title
        self.game_desc = game_desc


gamedata = [
    Game("1", "com.pearlabyss.blackdesertm", "검은사막 모바일", GameDesc("당신이 진짜로 원했던 모험의 시작", "월드클래스 MMORPG “검은사막 모바일”")),
    Game("2", "com.kakaogames.moonlight", "달빛조각사",
         GameDesc("500만 구독자의 게임 판타지 대작 '달빛조각사'", "- 5레벨만 달성해도 달빛조각사 이모티콘 100% 지급!")),
    Game("3", "com.ncsoft.lineagem19", "리니지M", GameDesc("PC의 향수! 리니지 본질 그대로 리니지M", "PC리니지와 동일한 아덴월드의 오픈 필드")),
    Game("4", "com.netmarble.bnsmkr", "블레이드&소울 레볼루션",
         GameDesc("원작 감성의 방대한 세계관과 복수 중심의 흥미진진한 스토리", "MMORPG의 필드를 제대로 즐길 수 있는 경공")),
    Game("5", "com.cjenm.sknights", "세븐나이츠", GameDesc("Netmarble롤플레잉", "세나의 재탄생, 세븐나이츠: 리부트")),
    Game("6", "com.google.android.youtube", "YouTube", GameDesc("Google LLC동영상 플레이어/편집기", "좋아하는 동영상 빠르게 검색하기"))
]


@app.route('/api/AllList', methods=['GET'])
def get_games():
    result = []
    for game in gamedata:
        result.append({
            "id": game.id,
            "packagename": game.package_name,
            "gametitle": game.game_title,
            "gamedesc": {
                "details1": game.game_desc.details1,
                "details2": game.game_desc.details2
            }
        })
    response = Response(
        json.dumps(result, ensure_ascii=False),
        mimetype='application/json; charset=utf-8'
    )
    return response


@app.route('/api/item/<id>', methods=['GET'])
def get_game(id):
    for game in gamedata:
        if game.id == id:
            data = {
                "id": game.id,
                "packagename": game.package_name,
                "gametitle": game.game_title,
                "gamedesc": {
                    "details1": game.game_desc.details1,
                    "details2": game.game_desc.details2
                }
            }
            response = Response(
                json.dumps(data, ensure_ascii=False),
                mimetype='application/json; charset=utf-8'
            )
            return response
    return Response(
        json.dumps({}),
        mimetype='application/json; charset=utf-8'
    )


if __name__ == '__main__':
    app.run(debug=True, port=8080)
