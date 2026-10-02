package com.family.points.controller;

import com.family.points.common.Result;
import com.family.points.entity.MonthlySettlement;
import com.family.points.service.MonthlySettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 月度结算控制器
 */
@Controller
@RequestMapping("/settlement")
public class MonthlySettlementController {

    @Autowired
    private MonthlySettlementService monthlySettlementService;

    @PostMapping("/execute")
    @ResponseBody
    public Result<MonthlySettlement> execute(@RequestParam String month) {
        try {
            return Result.success(monthlySettlementService.settle(month));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) String month, Model model) {
        model.addAttribute("settlements", monthlySettlementService.listSettlements(month));
        model.addAttribute("month", month);
        return "settlement/list";
    }
}
