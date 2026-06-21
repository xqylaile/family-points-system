package com.family.points.controller;

import com.family.points.common.Result;
import com.family.points.entity.FamilyMember;
import com.family.points.entity.PointRecord;
import com.family.points.entity.PointRule;
import com.family.points.service.FamilyMemberService;
import com.family.points.service.PointRecordService;
import com.family.points.service.PointRuleService;
import com.family.points.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

/**
 * 积分变更控制器
 */
@Controller
@RequestMapping("/record")
public class PointRecordController {

    @Autowired
    private PointRecordService pointRecordService;

    @Autowired
    private FamilyMemberService familyMemberService;

    @Autowired
    private PointRuleService pointRuleService;

    /**
     * 积分变更页面
     */
    @GetMapping("/add")
    public String add(Model model) {
        List<FamilyMember> members = familyMemberService.listEnabled();
        model.addAttribute("members", members);
        return "record/add";
    }

    /**
     * 根据成员ID和规则类型获取适用的规则
     */
    @GetMapping("/getRulesByMember")
    @ResponseBody
    public Result<List<PointRule>> getRulesByMember(@RequestParam Long memberId, @RequestParam String ruleType) {
        try {
            FamilyMember member = familyMemberService.getById(memberId);
            if (member == null) {
                return Result.error("成员不存在");
            }

            List<PointRule> rules = pointRuleService.listByMemberType(member.getMemberType(), ruleType);
            return Result.success(rules);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 保存积分变更
     */
    @PostMapping("/save")
    @ResponseBody
    public Result<Void> save(PointRecord record, @RequestParam(required = false) MultipartFile imageFile) {
        try {
            // 上传凭证图片
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = FileUploadUtil.upload(imageFile, "record");
                record.setImagePath(imagePath);
            }

            pointRecordService.addPointRecord(record);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 积分记录列表
     */
    @GetMapping("/list")
    public String list(@RequestParam(required = false) Long memberId,
                      @RequestParam(required = false) String startDate,
                      @RequestParam(required = false) String endDate,
                      Model model) {
        List<Map<String, Object>> records = pointRecordService.listRecordsWithDetails(memberId, startDate, endDate);
        List<FamilyMember> members = familyMemberService.listEnabled();

        model.addAttribute("records", records);
        model.addAttribute("members", members);
        model.addAttribute("memberId", memberId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "record/list";
    }

    /**
     * 撤销记录
     */
    @PostMapping("/cancel/{id}")
    @ResponseBody
    public Result<Void> cancel(@PathVariable Long id) {
        try {
            pointRecordService.cancelRecord(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
