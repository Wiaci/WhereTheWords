import {useState} from "react";
import {ColorRing} from "react-loader-spinner";
import authData from "../store/authData";
import {$api} from "../http";

export const SuggestText = () => {

    const [text, setText] = useState("")
    const [src, setSrc] = useState("")
    const [status, setStatus] = useState("")
    const [loading, setLoading] = useState(false)

    const suggestText = async () => {
        console.log("authData", authData)
        await $api.post("/suggestText", {
            id: 0,
            text: text,
            src: src,
            topics: []
        }).then(() => {
            setStatus("Текст добавлен")
            setText("")
        }).catch(() => {
            setStatus("Ошибка")
        }).finally(() => setLoading(false))
    }

    return (
        <>
            <span>Поле для текста:</span><br/>
            <textarea value={text} onChange={e => {
                setText(e.target.value)
                setStatus("")
            }}/> <br/>

            <span>Источник текста</span>
            <input className="source" type="text" onChange={e => setSrc(e.target.value)}/><br/>

            <span>{status}</span><br/>
            <button onClick={() => {
                if (text !== "") {
                    suggestText()
                    setLoading(true)
                } else setStatus("Текстовое поле пусто")
            }}>Отправить</button>  {loading ? <ColorRing/> : <span/>} <br/>

        </>
    )
}