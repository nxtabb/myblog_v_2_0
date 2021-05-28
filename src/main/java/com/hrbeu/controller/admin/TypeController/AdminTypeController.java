package com.hrbeu.controller.admin.TypeController;

import com.hrbeu.pojo.Type;
import com.hrbeu.service.admin.AdminTypeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Classname AdminTypeController
 * @Description TODO
 * @Date 2021/5/13 10:50
 * @Created by nxt
 */
@Controller
@RequestMapping("/admin")
public class AdminTypeController {
    @Autowired
    private AdminTypeService adminTypeService;

    //修改type的名称
    @PostMapping("/types/updateType/{typeId}")
    public String updateType(@PathVariable("typeId")Long typeId,
                             @RequestParam("name")String typeName){
        //将所有的type遍历出来
        List<Type> typeList = adminTypeService.queryAllTypes();
        //如果新起的名字在原数据中存在，则不进行修改
        for (Type type : typeList) {
            if(type.getTypeName().equals(typeName)){
                return "redirect:/admin/types/1";
            }
        }
        adminTypeService.updateTypeName(typeId,typeName);
        return "redirect:/admin/types/1";
    }

    //删除type
    @GetMapping("types/deleteType/{typeId}")
    public String deleteTypeById(@PathVariable("typeId")Long typeId){
        //查询当前type与document关联关系
        Integer count = adminTypeService.queryTypeCountById(typeId);
        //如果没有关联关系
        if(count==null||count.equals(0)){
            //可以删除
            adminTypeService.deleteTypeById(typeId);
            return "redirect:/admin/types/1";
        //如果有关联关系，则不能删除
        }else {
            return "redirect:/admin/types/1";
        }
    }

    //新增type
    @PostMapping("/types/addtype")
    public String addtype(@Param("name")String name, RedirectAttributes attributes) {
        if (name == null || name.equals("")) {
            //如果name为空或者为""
            attributes.addFlashAttribute("errMsg", "name不合法");
            return "redirect:/admin/types/1";
        }
        //如果name已经存在,则直接返回
        List<Type> types = adminTypeService.queryAllTypes();
        for (Type type : types) {
            if (type.getTypeName().equals(name)) {
                attributes.addAttribute("errMsg", "该name已存在");
                return "redirect:/admin/types/1";
            }
        }
        //存入该Type到tb_type中
        adminTypeService.addType(name);
        attributes.addFlashAttribute("errMsg", "操作成功");
        return "redirect:/admin/types/1";
    }
}
