package com.family.points.controller;

import com.family.points.common.Constants;
import com.family.points.common.Result;
import com.family.points.entity.SystemConfig;
import com.family.points.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 首页和系统管理控制器
 */
@Controller
public class HomeController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model) {
        String notice = systemConfigService.getConfigValue(Constants.CONFIG_KEY_SYSTEM_NOTICE);
        model.addAttribute("notice", notice);
        return "index";
    }

    /**
     * 系统设置页面
     */
    @GetMapping("/system/settings")
    public String settings(Model model) {
        List<SystemConfig> configs = systemConfigService.list();
        model.addAttribute("configs", configs);
        return "system/settings";
    }

    /**
     * 更新系统配置
     */
    @PostMapping("/system/updateConfig")
    @ResponseBody
    public Result<Void> updateConfig(@RequestParam String configKey, @RequestParam String configValue) {
        try {
            systemConfigService.updateConfigValue(configKey, configValue);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
