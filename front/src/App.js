import {Route, Routes} from "react-router";
import {observer} from "mobx-react-lite";
import {Link} from "react-router-dom";
import {AddTextForm} from "./components/AddTextForm";
import authData from "./store/authData";
import {LoginForm} from "./components/LoginForm";
import {NewUser} from "./components/NewUser";
import {HomePage} from "./components/HomePage";
import {ProfilePage} from "./components/ProfilePage";

import "./css/style.css"
import {SuggestText} from "./components/SuggestText";

const App = observer(() => {
    return (
        <div className="App">
            {!authData.loggedIn ?
                <div>
                    <Routes>
                        <Route path="/*" element={<LoginForm/>}/>
                        <Route path="/newuser" element={<NewUser/>}/>
                    </Routes>
                </div> :
                authData.role === 'USER' ?
                    <div>
                        <header>
                            <div className="links">
                                <Link className="link" to="/">Запрос</Link>
                                <Link className="link" to="/profile">Профиль</Link>
                                <Link className="link" to="/suggest">Предложить</Link>
                                <Link className="link" to="/" onClick={() => authData.logout()}>Выйти</Link>
                            </div>
                        </header>
                        <div className="main">
                            <Routes>
                                <Route path="/" element={<HomePage/>}/>
                                <Route path="/profile" element={<ProfilePage/>}/>
                                <Route path="/suggest" element={<SuggestText/>}/>
                            </Routes>
                        </div>
                    </div> :
                    <div>
                        <header>
                            <Link className="link" to="/addtext">Add Text</Link>
                            <Link className="link" to="/" onClick={() => {
                                authData.logout()
                                console.log(authData)
                            }
                            }>Logout</Link>
                        </header>
                        <Routes>
                            <Route path="/*" element={<AddTextForm/>}/>
                        </Routes>
                    </div>
            }
        </div>
    );
})

export default App;
