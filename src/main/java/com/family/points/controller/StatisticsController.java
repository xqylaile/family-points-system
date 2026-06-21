package com.family.points.controller;

import com.family.points.entity.ExchangeRecord;
import com.family.points.entity.FamilyMember;
import com.family.points.entity.PointRecord;
import com.family.points.entity.RewardItem;
import com.family.points.service.ExchangeRecordService;
import com.family.points.service.FamilyMemberService;
import com.family.points.service.PointRecordService;
import com.family.points.service.RewardItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计报表控制器
 */
@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @Autowired
    private PointRecordService pointRecordService;

    @Autowired
    private ExchangeRecordService exchangeRecordService;

    @Autowired
    private RewardItemService rewardItemService;

    /**
     * 统计报表首页
     */
    @GetMapping("/index")
    public String index(Model model) {
        // 成员积分排行
        List<FamilyMember> memberRanking = familyMemberService.getRanking();
        model.addAttribute("memberRanking", memberRanking);

        // 最近积分记录
        List<PointRecord> recentRecords = pointRecordService.listByCondition(null, null, null, null, null)
                .stream().limit(10).collect(Collectors.toList());
        model.addAttribute("recentRecords", recentRecords);

        // 热门奖品（按兑换次数排序）
        List<RewardItem> popularRewards = rewardItemService.list().stream()
                .sorted((r1, r2) -> r2.getExchangeCount() - r1.getExchangeCount())
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("popularRewards", popularRewards);

        // 最近兑换记录
        List<ExchangeRecord> recentExchanges = exchangeRecordService.listByCondition(null, null, null)
                .stream().limit(10).collect(Collectors.toList());
        model.addAttribute("recentExchanges", recentExchanges);

        // 统计数据
        List<FamilyMember> allMembers = familyMemberService.list();
        int totalMembers = allMembers.size();
        int totalPoints = allMembers.stream().mapToInt(FamilyMember::getCurrentPoints).sum();
        int totalEarned = allMembers.stream().mapToInt(FamilyMember::getTotalEarnedPoints).sum();
        int totalSpent = allMembers.stream().mapToInt(FamilyMember::getTotalSpentPoints).sum();

        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("totalEarned", totalEarned);
        model.addAttribute("totalSpent", totalSpent);

        return "statistics/index";
    }
}
