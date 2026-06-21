package com.family.points.controller;

import com.family.points.common.Constants;
import com.family.points.common.Result;
import com.family.points.entity.RewardItem;
import com.family.points.service.RewardItemService;
import com.family.points.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 奖品控制器
 */
@Controller
@RequestMapping("/reward")
public class RewardItemController {

    @Autowired
    private RewardItemService rewardItemService;

    /**
     * 奖品列表页面
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<RewardItem> items = rewardItemService.list();
        model.addAttribute("items", items);
        return "reward/list";
    }

    /**
     * 添加奖品页面
     */
    @GetMapping("/add")
    public String add() {
        return "reward/add";
    }

    /**
     * 编辑奖品页面
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        RewardItem item = rewardItemService.getById(id);
        model.addAttribute("item", item);
        return "reward/edit";
    }

    /**
     * 保存奖品
     */
    @PostMapping("/save")
    @ResponseBody
    public Result<Void> save(RewardItem item, @RequestParam(required = false) MultipartFile imageFile) {
        try {
            // 上传图片
            if (imageFile != null && !imageFile.isEmpty()) {
                String imagePath = FileUploadUtil.upload(imageFile, "reward");
                item.setImagePath(imagePath);
            }

            // 设置默认值
            if (item.getId() == null) {
                item.setStatus(Constants.STATUS_ENABLED);
                item.setExchangeCount(0);
            }

            rewardItemService.saveOrUpdate(item);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除奖品
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public Result<Void> delete(@PathVariable Long id) {
        try {
            rewardItemService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上架/下架奖品
     */
    @PostMapping("/updateStatus/{id}")
    @ResponseBody
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            RewardItem item = rewardItemService.getById(id);
            item.setStatus(status);
            rewardItemService.updateById(item);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新库存
     */
    @PostMapping("/updateStock/{id}")
    @ResponseBody
    public Result<Void> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        try {
            RewardItem item = rewardItemService.getById(id);
            item.setStock(stock);
            rewardItemService.updateById(item);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
