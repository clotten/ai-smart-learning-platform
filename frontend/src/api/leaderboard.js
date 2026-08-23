import request from './request'

export function getTop(n = 10) {
    return request.get('/leaderboard/top', { params: { n } })
}
export function getMyRank() {
    return request.get('/leaderboard/me')
}