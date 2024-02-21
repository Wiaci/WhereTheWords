import {observer} from "mobx-react-lite";
import {useEffect, useState} from "react";
import {ColorRing} from "react-loader-spinner";
import {$api} from "../http";
import authData from "../store/authData";
import Select from "react-select";


export const AddTextForm = observer(() => {
    const [text, setText] = useState("")
    const [src, setSrc] = useState("")
    const [status, setStatus] = useState("")
    const [loading, setLoading] = useState(false)
    const [topics, setTopics] = useState([])
    const [options, setOptions] = useState([])
    const [textId, setTextId] = useState(0)

    useEffect(() => {
        $api.get("/topics")
            .then(resp => {
                setOptions(optionsByTopics(resp.data))
            }).catch(e => console.log(e))
        loadNext()
    }, [])

    const optionsByTopics = (topics) => {
        let options = []
        topics.forEach(l => options.push({
            label: l, value: l}))
        return options;
    }

    const sendText = async () => {
        console.log("authData", authData)
        await $api.post("/addtext", {
            id: textId,
            text: text,
            src: src,
            topics: topics
        }).then(() => {
            setStatus("Текст добавлен")
        }).catch(() => {
            setStatus("Ошибка")
        }).finally(() => {
            setLoading(false)
            loadNext()
        })
    }

    const loadNext = () => {
        $api.get("/suggestText")
            .then(resp => {
                setTextId(resp.data.id)
                setText(resp.data.text)
                setSrc(resp.data.src)
                setTopics(resp.data.topics)
            })
            .catch(e => console.log(e))
    }

    return (
        <div>
            {text === '' ? <span>Предложенных текстов нет</span> :
                <div>
                    <span>Поле для текста:</span><br/>
                    <textarea onChange={e => {
                        setText(e.target.value)
                        setStatus("")
                    }} value={text}/> <br/>

                    <span>Источник текста</span>
                    <input type="text" value={src} onChange={e => setSrc(e.target.value)}/><br/>

                    <span>{status}</span> <br/>
                    <div>
                        <Select isMulti options={options} defaultValue={optionsByTopics(topics)}
                                value={optionsByTopics(topics)}
                                onChange={values => setTopics(values.map(v => v.value))}/>
                        <span>{topics.length === 0 ? "Выберите хотя бы один элемент из списка" : ""}</span><br/>
                        <button onClick={() => {
                            if (text !== "") {
                                sendText()
                                setLoading(true)
                            } else setStatus("Текстовое поле пусто")
                        }} disabled={topics.length === 0}>Отправить</button>  {loading ? <ColorRing/> : <span/>}
                        <button>Убрать</button><br/>
                    </div>
                </div>}
        </div>
    )
})