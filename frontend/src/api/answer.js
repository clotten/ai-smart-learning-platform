import request from './request'

export function submitAnswer(data){
    return request.post('/answer/submit', data)
}

export function getWrongList(params) {
    return request.get('/answer/wrong-list', { params })
}

export function getStats() {
    return request.get('/answer/stats')
}