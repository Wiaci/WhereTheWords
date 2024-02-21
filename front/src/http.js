import axios from "axios"
import authData from "./store/authData";

export const API_URL = 'http://localhost:8081/'

export const $api = axios.create({
    baseURL: API_URL,
    headers: {
        "Authentication": "Basic " + authData.token,
    }
})

export const refreshHeaders = () => {
    $api.defaults.headers.common["Authorization"] = "Basic " + authData.token
}

