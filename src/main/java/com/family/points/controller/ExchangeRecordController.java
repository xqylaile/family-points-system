package com.family.points.controller;

import com.family.points.common.Result;
import com.family.points.entity.FamilyMember;
import com.family.points.entity.RewardItem;
import com.family.points.service.ExchangeRecordService;
import com.family.points.service.FamilyMemberService;
import com.family.points.service.RewardItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 积分兑换控制器
 */
@Controller
@RequestMapping("/exchange")
public class ExchangeRecordController {

    @Autowired
    private ExchangeRecordService exchangeRecordService;

    @Autowired
    private FamilyMemberService familyMemberService;

    @Autowired
    private RewardItemService rewardItemService;

    /**
     * 兑换页面
     */
    @GetMapping("/add")
    public String add(@RequestParam(required = false) Long memberId, Model model) {
        List<FamilyMember> members = familyMemberService.listEnabled();
        model.addAttribute("members", members);

        if (memberId != null) {
            FamilyMember member = familyMemberService.getById(memberId);
            List<RewardItem> items = rewardItemService.listAvailableForMember(memberId);
            model.addAttribute("selectedMember", member);
            model.addAttribute("items", items);
        }

        return "exchange/add";
    }

    /**
     * 获取成员可兑换的奖品
     */
    @GetMapping("/getAvailableItems")
    @ResponseBody
    public Result<List<RewardItem>> getAvailableItems(@RequestParam Long memberId) {
        try {
            List<RewardItem> items = rewardItemService.listAvailableForMember(memberId);
            return Result.success(items);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 确认兑换
     */
    @PostMapping("/confirm")
    @ResponseBody
    public Result<Void> confirm(@RequestParam Long memberId,
                                @RequestParam Long itemId,
                                @RequestParam Integer quantity) {
        try {
            exchangeRecordService.exchangeReward(memberId, itemId, quantity);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 兑换记录列表
     */
    @GetMapping("/list")
    public String list(@RequestParam(required = false) Long memberId,
                      @RequestParam(required = false) String startDate,
                      @RequestParam(required = false) String endDate,
                      Model model) {
        List<Map<String, Object>> records = exchangeRecordService.listRecordsWithDetails(memberId, startDate, endDate);
        List<FamilyMember> members = familyMemberService.listEnabled();

        model.addAttribute("records", records);
        model.addAttribute("members", members);
        model.addAttribute("memberId", memberId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "exchange/list";
    }

    /**
     * 更新兑换状态
     */
    @PostMapping("/updateStatus/{id}")
    @ResponseBody
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            exchangeRecordService.updateExchangeStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 撤销兑换
     */
    @PostMapping("/cancel/{id}")
    @ResponseBody
    public Result<Void> cancel(@PathVariable Long id) {
        try {
            exchangeRecordService.cancelExchange(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
