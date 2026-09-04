package com.ai.learning.answer.service;

import com.ai.learning.answer.vo.LeaderboardVO;

import java.util.List;

public interface LeaderboardService {

    /**
     * 刷题排行榜 TOP N
     */
    List<LeaderboardVO> top(int n);

    /**
     * 我的排名
     */
    LeaderboardVO myRank(Long userId);
}
