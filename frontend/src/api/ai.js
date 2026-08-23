import request from './request'

export function aiExplain(data) {
    return request.post('/ai/explain', data)
}