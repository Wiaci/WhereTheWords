import {makeAutoObservable} from "mobx";
import {refreshHeaders} from "../http";

class AuthData {

    loggedIn = false
    token = ''
    id = 0
    username = ''
    role = ''
    words = []
    topics = []

    constructor() {
        makeAutoObservable(this)
    }

    login() {
        this.loggedIn = true;
    }

    logout() {
        this.loggedIn = false;
        this.token = ''
    }

    setUsername(username) {
        this.username = username
    }

    setPassword(password) {
        this.password = password
    }

    setTopics(topics) {
        this.topics = topics
    }

    setWords(words) {
        this.words = words
    }

    setWordStatus(word, status) {
        this.words.forEach(w => {
            if (w.baseForm === word.baseForm && w.pos === word.pos) {
                word.status = status
            }
        })
    }

    deleteWord(word) {
        for (let i = 0; i < this.words.length; i++) {
            if ((this.words[i].baseForm === word.baseForm && this.words[i].pos === word.pos)) {
                this.words.splice(i, 1)
            }
        }
    }

    containsWord(word) {
        for (let i = 0; i < this.words.length; i++) {
            if (this.words[i].baseForm === word.wordInfo.baseForm && this.words[i].pos === word.wordInfo.pos) {
                return true
            }
        }
        return false;
    }

    setUserData(id, username, role, words, topics, token) {
        this.id = id
        this.username = username
        this.role = role
        this.words = words
        this.topics = topics
        this.token = token
        this.loggedIn = true
        refreshHeaders()
    }
}

export default new AuthData()