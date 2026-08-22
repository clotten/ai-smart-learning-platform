package com.ai.learning.VO;


import lombok.Data;

/**
 * 排行榜VO
 */
@Data
public class LeaderboardVO {
    private Long userId;        //用户id
    private String username;    //用户名
    private Long count;         //刷题数
    private Long rank;          //排名(0=第一名）
}
