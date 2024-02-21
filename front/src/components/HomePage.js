import {observer} from "mobx-react";
import {$api} from "../http";
import authData from "../store/authData";
import {Text} from "./Text";
import savedTexts from "../store/savedTexts";
import {toJS} from "mobx";
import {ColorRing} from "react-loader-spinner";
import {useState} from "react";


export const HomePage = observer(() => {

    const [loading, setLoading] = useState(false)
    const [count, setCount] = useState("3")

    const getTexts = () => {
        setLoading(true)
        $api.post("/getTexts?id=" + authData.id + "&count=" + count, {id: authData.id})
            .then(resp => {
                savedTexts.setTexts(resp.data)
                console.log(savedTexts)
            })
            .catch(e => console.log(e))
            .finally(() => setLoading(false))
    }

    return (
        <div>
            <span>Количество текстов: </span>
            <select value={count} onChange={e => setCount(e.target.value)}>
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
                <option value="5">5</option>
            </select>
            <button onClick={() => getTexts()}>Запросить</button> <br/> {loading ? <ColorRing/> : <span/>} <br/>
            <ul>
                {savedTexts.texts.map((text, i) => {
                    return (
                        <li key={i}>
                            <span className="wordlist" onClick={() => {
                                console.log("text", toJS(text))
                                savedTexts.setSelectedText(toJS(text))
                                console.log("selectedText", savedTexts.selectedText)
                            }}>{text.firstSentence}</span>
                            {text.topics.map((topic, j) => {
                                return (<span key={j} className="topic">{topic}</span>)
                            })}
                        </li>
                    )
                })}
            </ul>
            <div>
                {savedTexts.selectedText.words.length === 0 ? <></> : <Text/>}
            </div>
        </div>
    )
})