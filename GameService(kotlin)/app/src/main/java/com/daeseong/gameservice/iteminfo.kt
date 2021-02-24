package com.daeseong.gameservice

class iteminfo {

    private val tag: String = iteminfo::class.java.simpleName

    companion object {
        private var instance: iteminfo? = null
        fun getInstance(): iteminfo {
            if (instance == null) {
                instance = iteminfo()
            }
            return instance as iteminfo
        }
    }

    private val gameMap: HashMap<String, String> = HashMap()

    init {
        gameMap.clear()
    }

    fun setGameItem(packageName: String) {

        if (!gameMap.containsKey(packageName)) {
            gameMap[packageName] = packageName
        }
    }

    fun isGameItem(packageName: String): Boolean {

        var bfind = false
        if (gameMap.containsKey(packageName)) {
            bfind = true
        }
        return bfind
    }

    fun clearGameItem() {
        gameMap.clear()
    }
}