import {useState} from "react";
import authData from "../store/authData";
import {$api} from "../http";
import Select from "react-select";
import {toJS} from "mobx";

export const NewUser = () => {

    const [stage, setStage] = useState(1)
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [confPassword, setConfPassword] = useState('')
    const [passwordsMatch, setPasswordsMatch] = useState(true)
    const [usernameExists, setUsernameExists] = useState(false)
    const [topics, setTopics] = useState([])
    const [options, setOptions] = useState([])
    const [startWords, setStartWords] = useState([])
    const [checkedWords, setCheckedWords] = useState(
        new Array(60).fill(false)
    )

    const loadTopics = () => {
        $api.get("/topics")
            .then(resp => {
                let options = []
                resp.data.forEach(l => options.push({
                    value: l.id,
                    label: l.beautifiedName
                }))
                setOptions(options)
            }).catch(e => console.log(e))
    }

    const checkUsername = () => {
        $api.post("/checkUsername", {username: username})
            .then(resp => {
                console.log("checkUsername resp.data", resp.data)
                setUsernameExists(resp.data)
                if (!usernameExists) setStage(2)
                authData.setUsername(username)
                authData.setPassword(password)
                loadTopics()
            }).catch(e => console.log(e))
    }

    const getStartWords = () => {
        $api.get("/startWords")
            .then(resp => {
                const options = []
                resp.data.forEach(w => {
                    options.push(w.baseForm + ", " + w.partOfSpeech)
                })
                setStartWords(options)
            })
            .catch(e => console.log(e))
    }

    const createUser = () => {
        const words = []
        for (let i = 0; i < 60; i++) {
            if (checkedWords[i]) {
                words.push({
                    baseForm: startWords[i].split(", ")[0],
                    partOfSpeech: startWords[i].split(", ")[1],
                    level: "C2"
                })
            }
        }
        authData.setWords(words)
        const userData = {
            id: 0,
            username: authData.username + ":" + authData.password,
            role: "ADMIN",
            words: toJS(authData.words),
            topics: toJS(authData.topics).map(el => el.label),
            authToken: ""
        }
        console.log(userData)
        $api.post("/newUser", userData).then(resp => {
            authData.setUserData(resp.data.id, resp.data.username, resp.data.role,
                resp.data.words, resp.data.topics, resp.data.authToken)
        }).catch(e => console.log())
    }

    return (
        <div>
            {stage === 1 ?
                <div className="login">
                    <input className="loginField" type="text" placeholder="username" onChange={e => setUsername(e.target.value)}/><br/>
                    <input className="loginField" type="password" placeholder="password" onChange={e => setPassword(e.target.value)}/><br/>
                    <input className="loginField" type="password"
                           placeholder="confirm password"
                           onChange={e => {
                               setConfPassword(e.target.value)
                               setPasswordsMatch(true)
                           }
                           }/><br/>
                    <span>{passwordsMatch ? "" : "Пароли не совпадают"}
                        {usernameExists ? "Такой пользователь уже есть" : ""}</span>
                    <button className="loginButton" onClick={() => {
                        if (password !== confPassword && password !== '') setPasswordsMatch(false)
                        else checkUsername()
                    }}>Подтвердить</button>
                </div> :
            stage === 2 ?
                <div>
                    <Select isMulti options={options} onChange={values => setTopics(values)}/>
                    <span>{topics.length === 0 ? "Выберите хотя бы один элемент из списка" : ""}</span><br/>
                    <button disabled={topics.length === 0} onClick={() => {
                        authData.setTopics(topics)
                        setStage(3)
                        getStartWords()
                    }}>Далее</button>
                </div> :
                <div>
                    {startWords.map((word, i) => {
                        return <div key={i}>
                            <input type="checkbox" value={word} key={i} checked={checkedWords[i]}
                                   id={word.replace(', ', '')} onClick={() => {
                                        const updatedCheckedState = checkedWords.map((item, index) =>
                                        index === i ? !item : item);
                                        setCheckedWords(updatedCheckedState);
                                   }
                            }/>
                            <label htmlFor={word}>{word}</label> <br/>

                        </div>
                    })}
                    <button onClick={() => createUser()}>Подтвердить</button>
                </div>}
        </div>
    )
}
