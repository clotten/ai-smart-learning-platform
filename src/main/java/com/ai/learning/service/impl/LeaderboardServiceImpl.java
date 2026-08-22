package com.ai.learning.service.impl;


import com.ai.learning.VO.LeaderboardVO;
import com.ai.learning.entity.SysUser;
import com.ai.learning.mapper.SysUserMapper;
import com.ai.learning.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private static final String LEADERBOARD_KEY = "learn:leaderboard";

    private final StringRedisTemplate redisTemplate;
    private final SysUserMapper sysUserMapper;

    @Override
    public List<LeaderboardVO> top(int n){
        //ZREVRANGE:从高到低取前 n 个（member + score）
        Set<ZSetOperations.TypedTuple<String>> tuples
                =redisTemplate.opsForZSet().reverseRangeWithScores(LEADERBOARD_KEY, 0, n - 1);
        List<LeaderboardVO> list = new ArrayList<>();
        long rank  = 0;
        if(tuples != null){
            for(ZSetOperations.TypedTuple<String> t : tuples){
                String member = t.getValue(); //先取出来
                if(member == null) continue;  //防御：null 就跳过这条
                LeaderboardVO vo = new LeaderboardVO();
                vo.setUserId(Long.valueOf(member));
                vo.setCount(t.getScore() == null ? 0L : t.getScore().longValue());
                vo.setRank(rank++);
                //查用户名（从数据库补全展示信息）
                SysUser user = sysUserMapper.selectById(t.getValue());
                vo.setUsername(user != null ? user.getUsername() : "未知用户");
                list.add(vo);
            }
        }
        return list;
    }

    @Override
    public LeaderboardVO myRank(Long userId){
        LeaderboardVO vo = new LeaderboardVO();
        vo.setUserId(userId);
        //ZREVRANK:我的排名（0=第一）
        Long rank = redisTemplate.opsForZSet().reverseRank(LEADERBOARD_KEY,userId.toString());
        vo.setRank(rank == null ? -1 : rank);//没上榜返回-1
        //ZSCORE:我的分数
        Double score = redisTemplate.opsForZSet().score(LEADERBOARD_KEY, userId.toString());
        vo.setCount(score == null ? 0L :score.longValue());
        SysUser user =sysUserMapper.selectById(userId);
        vo.setUsername(user != null ? user.getUsername() : "未知用户");
        return vo;

    }
}
