import request from "./request"

export function pageQuestions(params){
    return request.get('/question/page', { params })
}

export function addQuestion(data){
    return request.post('/question', data)
}

export function deleteQuestion(id){
    return request.delete('/question/' + id)
}