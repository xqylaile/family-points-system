package com.family.points.controller;

import com.family.points.common.Result;
import com.family.points.entity.FamilyMember;
import com.family.points.service.FamilyMemberService;
import com.family.points.service.MonthlySettlementService;
import com.family.points.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 家庭成员控制器
 */
@Controller
@RequestMapping("/member")
public class FamilyMemberController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @Autowired
    private MonthlySettlementService monthlySettlementService;

    /**
     * 成员列表页面
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<FamilyMember> members = familyMemberService.list();
        model.addAttribute("members", members);
        return "member/list";
    }

    /**
     * 添加成员页面
     */
    @GetMapping("/add")
    public String add() {
        return "member/add";
    }

    /**
     * 编辑成员页面
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        FamilyMember member = familyMemberService.getById(id);
        model.addAttribute("member", member);
        return "member/edit";
    }

    /**
     * 保存成员
     */
    @PostMapping("/save")
    @ResponseBody
    public Result<Void> save(FamilyMember member, @RequestParam(required = false) MultipartFile avatarFile) {
        try {
            // 上传头像
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarPath = FileUploadUtil.upload(avatarFile, "avatar");
                member.setAvatar(avatarPath);
            }

            familyMemberService.saveProfile(member);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除成员
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public Result<Void> delete(@PathVariable Long id) {
        try {
            familyMemberService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 启用/停用成员
     */
    @PostMapping("/updateStatus/{id}")
    @ResponseBody
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            familyMemberService.updateStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 积分排行榜页面
     */
    @GetMapping("/ranking")
    public String ranking(Model model) {
        List<FamilyMember> members = familyMemberService.getRanking();
        String month = monthlySettlementService.getSettlementMonth();
        model.addAttribute("members", members);
        model.addAttribute("settlementMonth", month);
        model.addAttribute("alreadySettled", monthlySettlementService.isSettled(month));
        return "member/ranking";
    }
}
