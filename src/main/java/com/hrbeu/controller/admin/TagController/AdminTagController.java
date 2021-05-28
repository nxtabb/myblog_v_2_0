package com.hrbeu.controller.admin.TagController;

import com.hrbeu.pojo.Tag;
import com.hrbeu.service.admin.AdminDocumentTagService;
import com.hrbeu.service.admin.AdminTagService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * @Classname AdminTagController
 * @Description TODO
 * @Date 2021/5/13 22:16
 * @Created by nxt
 */
@Controller
@RequestMapping("/admin")
public class AdminTagController {
    @Autowired
    private AdminTagService adminTagService;
    @Autowired
    private AdminDocumentTagService adminDocumentTagService;
    //删除指定标签
    @GetMapping("/tags/deleteById/{tagId}")
    public String deleteTageById(@PathVariable("tagId")Long tagId, RedirectAttributes attributes){
        //查询与tagId相关的数量，如果为0，才能删除
        Integer count = adminDocumentTagService.queryTagInDocumentsCountById(tagId);
        //能够删除
        if(count==null||count.equals(0)){
            adminTagService.deleteTagById(tagId);
            attributes.addAttribute("errMsg","操作成功");
            return "redirect:/admin/tags/1";
        }else {
            //不能删除
            attributes.addAttribute("errMsg","该标签有关联文档，不能删除");
            return "redirect:/admin/tags/1";
        }
    }
    //修改指定标签
    @PostMapping("/tags/updateTag/{tagId}")
    public String updateTag(@PathVariable("tagId")Long tagId, @RequestParam("name")String tagName, RedirectAttributes attributes){
        //检查要修改的tagName是否已经存在了
        List<Tag> tagList = adminTagService.queryAllTags();
        for (Tag tag : tagList) {
            if(tagName.equals(tag.getTagName())){
                attributes.addAttribute("errMsg","要修改的tagName已经存在");
                return "redirect:/admin/tags/1";
            }
        }
        adminTagService.updateTagName(tagId,tagName);
        attributes.addAttribute("errMsg","修改成功");
        return "redirect:/admin/tags/1";
    }
    //增加指定标签
    @PostMapping("/tags/addtag")
    public String addtag(@Param("name")String name, RedirectAttributes attributes){
        //查询全部的tagList
        List<Tag> tagList = adminTagService.queryAllTags();
        //如果tagName已经存在
        for (Tag tag : tagList) {
            if(name.equals(tag.getTagName())){
                //返回错误信息
                attributes.addFlashAttribute("errMsg","tagName已存在");
                return "redirect:/admin/tags/1";
            }
        }
        //添加tag
        adminTagService.addTag(name);
        attributes.addFlashAttribute("errMsg","操作成功");
        return "redirect:/admin/tags/1";
    }
}
