import {makeAutoObservable} from "mobx";

class SavedTexts {

    texts = []
    selectedText = {
        words: [],
        firstSentence: '',
        level: '',
        wordEntries: 0
    }

    setTexts(texts) {
        this.texts = texts
    }

    setSelectedText(text) {
        this.selectedText.words = text.words
        this.selectedText.firstSentence = text.firstSentence
        this.selectedText.level = text.level
        this.selectedText.wordEntries = text.wordEntries
    }


    constructor() {
        makeAutoObservable(this)
    }
    
}

export default new SavedTexts();