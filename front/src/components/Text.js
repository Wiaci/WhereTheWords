import {useState} from "react";
import authData from "../store/authData";
import savedTexts from "../store/savedTexts";
import {observer} from "mobx-react";
import {$api} from "../http";

export const Text = observer(() => {

    const [selectedWord, setSelectedWord] = useState('')
    const [infoX, setInfoX] = useState(0)
    const [infoY, setInfoY] = useState(0)
    const [infoVisible, setInfoVisible] = useState(false)

    const getClass = (word) => {
        let c = "word"
        if (word.wordInfo.partOfSpeech !== 'ignore') c += " subst"
        if (authData.containsWord(word)) {
            c += " userword"
        }
        return c
    }

    const addWord = (word) => {
        $api.post("/addWord?id=" + authData.id, word.wordInfo)
            .then(resp => {
                authData.words.push(resp.data)
            })
            .catch(e => console.log(e))
    }

    return (
        <div className="textcontainer">
            {savedTexts.selectedText.text !== {} ? savedTexts.selectedText.words.map((word, i) => {
                return (
                    <div className="wordcontainer" key={i}>
                        {word.original !== '\n' ?
                        <span className={getClass(word)}
                              onMouseEnter={e => {
                                  setSelectedWord(word)
                                  setInfoX(e.pageX)
                                  setInfoY(e.pageY)
                                  setInfoVisible(true)
                              }}
                              onMouseLeave={() => {
                                  setSelectedWord('')
                                  setInfoVisible(false)
                              } }
                              onClick={() => addWord(word)}
                        >{word.original}</span> : <div className="break"><br/></div>}
                    </div>
                )
            }) : <></>} <br/>
            <div className="wordinfo" style={{
                top: (infoY + 10) + "px",
                left: (infoX + 10) + "px",
                visibility: infoVisible ? "visible" : "hidden"
            }}>
            {selectedWord === '' || selectedWord.wordInfo.partOfSpeech === 'ignore' ? <div/> :
                <div>
                    <span>Базовая форма: {selectedWord.wordInfo.baseForm}</span><br/>
                    <span>Часть речи: {selectedWord.wordInfo.partOfSpeech}</span><br/>
                    <span>Уровень: {selectedWord.wordInfo.level}</span>
                </div>
            }
            </div>
        </div>
    )
})