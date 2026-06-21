package com.family.points.controller;

import com.family.points.common.Constants;
import com.family.points.common.Result;
import com.family.points.entity.PointRule;
import com.family.points.service.PointRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 积分规则控制器
 */
@Controller
@RequestMapping("/rule")
public class PointRuleController {

    @Autowired
    private PointRuleService pointRuleService;

    /**
     * 规则列表页面
     */
    @GetMapping("/list")
    public String list(@RequestParam(required = false) String ruleType,
                      @RequestParam(required = false) String category,
                      Model model) {
        List<PointRule> rules = pointRuleService.list();
        model.addAttribute("rules", rules);
        model.addAttribute("ruleType", ruleType);
        model.addAttribute("category", category);
        return "rule/list";
    }

    /**
     * 添加规则页面
     */
    @GetMapping("/add")
    public String add() {
        return "rule/add";
    }

    /**
     * 编辑规则页面
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        PointRule rule = pointRuleService.getById(id);
        model.addAttribute("rule", rule);
        return "rule/edit";
    }

    /**
     * 保存规则
     */
    @PostMapping("/save")
    @ResponseBody
    public Result<Void> save(PointRule rule) {
        try {
            if (rule.getId() == null) {
                rule.setStatus(Constants.STATUS_ENABLED);
                rule.setUseCount(0);
            }
            pointRuleService.saveOrUpdate(rule);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除规则
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public Result<Void> delete(@PathVariable Long id) {
        try {
            pointRuleService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 启用/停用规则
     */
    @PostMapping("/updateStatus/{id}")
    @ResponseBody
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            PointRule rule = pointRuleService.getById(id);
            rule.setStatus(status);
            pointRuleService.updateById(rule);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 规则统计页面
     */
    @GetMapping("/statistics")
    public String statistics(Model model) {
        List<PointRule> rules = pointRuleService.list();
        model.addAttribute("rules", rules);
        return "rule/statistics";
    }
}
