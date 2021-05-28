package com.hrbeu.controller.admin;

import com.hrbeu.dao.admin.AdminDocumentTagDao;
import com.hrbeu.dao.admin.AdminTypeDao;
import com.hrbeu.pojo.*;
import com.hrbeu.pojo.vo.File_Len;
import com.hrbeu.pojo.vo.Tag_Count;
import com.hrbeu.pojo.vo.Type_Count;
import com.hrbeu.service.admin.*;
import com.hrbeu.utils.PageUtil;
import com.hrbeu.utils.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
    @Autowired
    private AdminTypeService adminTypeService;
    @Autowired
    private AdminTagService adminTagService;
    @Autowired
    private AdminDocumentService adminDocumentService;
    @Autowired
    private AdminFileService adminFileService;
    @Autowired
    private AdminDocumentTagService adminDocumentTagService;

    //进入管理员登录或者直接进入管理员首页，不需要过滤器验证
    @GetMapping("")
    public String adminLoginOrIndex(HttpServletRequest request){
        User user =(User) request.getSession().getAttribute("user");
        if(user!=null&&user.getAuthority().equals(2)) {
            return "redirect:/admin/index";
        }
        else {
            request.setAttribute("errMsg","亲爱的用户，请登录管理员账号进入后台管理");
            return "admin/login";
        }
    }
    //后台管理首页，需要Admin过滤器验证
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String admin_index(){
        return "admin/index";
    }



    //登出
    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return "admin/login";
    }

    //进入后台管理所有文件列表+页码
    @GetMapping("/documentsIndex/{pageIndex}")
    public String documentListByPage(@PathVariable("pageIndex")int pageIndex, Model model){
        int pageSize = 7;
        //获取文档个数，测试通过
        int allDocumentCount = adminDocumentService.queryAllDocumentCount();
        //获取文档列表且按照文档的最后修改时间降序排列，测试通过
        List<Document> documentList = adminDocumentService.queryAllDocumentListByPage(pageIndex,pageSize);
        //获取全部的type对象，测试通过
        List<Type> types = adminTypeService.queryAllTypes();
        //分页，获取到当前页、下一页、上一页
        Map<String,Integer> pageInfo = PageUtil.page(pageIndex,pageSize,allDocumentCount);
        model.addAttribute("types",types);
        model.addAttribute("prePage",pageInfo.get("prePage"));
        model.addAttribute("nextPage",pageInfo.get("nextPage"));
        model.addAttribute("documentList",documentList);
        return "admin/document";
    }

    //进入新增文档的页面
    @GetMapping("/documents/adddocument")
    public String addDocument(Model model){
        //获取所有的type，需要在页面上显示
        List<Type> typeList = adminTypeService.queryAllTypes();
        //获取所有的tag，需要在页面上显示
        List<Tag> tagList = adminTagService.queryAllTags();
        model.addAttribute("typeList",typeList);
        model.addAttribute("tagList",tagList);
        return "admin/document-input";
    }

    //进入编辑文档界面
    @GetMapping("/documents/updatedocument/{documentId}")
    public String updateDocument(@PathVariable("documentId") Long documentId, Model model, HttpServletRequest request) {
        //获取document的超级详细信息，测试成功
        Document document = adminDocumentService.queryDocumentAndUserAndTagsById(documentId);
        User owner = document.getUser();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //判断当前文档的所属用户是否是登录的用户，如果不是则不能修改，直接返回文档列表页面
        if (!owner.getUserId().equals(user.getUserId())) {
            return "redirect:/admin/documentsIndex/1";
        }
        //查询所有的type
        List<Type> typeList= adminTypeService.queryAllTypes();
        //查询全部Tag
        List<Tag> tagList = adminTagService.queryAllTags();
        //将document中的tag传到前端
        List<Long> tagIdList = new LinkedList<>();
        //因为前端需要，因此需要将所有的tagId放入到一个List中
        for (Tag tag : document.getTagList()) {
            tagIdList.add(tag.getTagId());
        }
        StringBuilder sb = new StringBuilder();
        //将全部的tagId 做成 "1，2，3，4，"这样的字符串
        for (Long tagId : tagIdList) {
            sb.append(tagId);
            sb.append(",");
        }
        //删除最后一个逗号
        String finalTagIdList = sb.substring(0,sb.toString().lastIndexOf(","));
        //查询附属文件的大小等信息
        List<File> files = adminFileService.queryFilesByDocument(documentId);
        //将File的基本信息和长度信息都需要放入到List<File_Len>中
        List<File_Len> fileLenList = new ArrayList<>();
        if(files!=null){
            for(File file:files){
                //定义一个File_Len
                File_Len file_len = new File_Len();
                //获取物理磁盘上的文件
                java.io.File fileOfFile = new java.io.File(PathUtil.getBasePath()+ file.getFilePath());
                //查询其大小 以M为单位
                Double fileLength = fileOfFile.length()/1024.0/1024.0;
                String fileLengthStr = String.format("%.2f", fileLength);
                file_len.setLength(fileLengthStr);
                file_len.setFile(file);
                fileLenList.add(file_len);
            }
        }
        model.addAttribute("typeList",typeList);
        model.addAttribute("tagList",tagList);
        model.addAttribute("finalTagIdList",finalTagIdList);
        //将整个document对象放入到model中
        model.addAttribute("document",document);
        model.addAttribute("fileLenList",fileLenList);
        return "admin/document-update";
    }

    //进入查看所有分类的页面
    @GetMapping(value = "/types/{pageIndex}")
    public String types(@PathVariable("pageIndex")int pageIndex, Model model,HttpServletRequest request) {
        String errMsg = request.getParameter("errMsg");
        if(errMsg!=null&&!errMsg.equals("")){
            model.addAttribute("errMsg",errMsg);
        }
        //查询type的全部数量
        int typesCount = adminTypeService.queryAllTypeCount();
        int pageSize = 7;
        //查询每个Type的关联doc数目
        List<Type_Count> typeList = adminTypeService.queryTypeCountListByPage(pageIndex, pageSize);
        Map<String,Integer> pageInfo = PageUtil.page(pageIndex,pageSize,typesCount);
        model.addAttribute("nextPage",pageInfo.get("nextPage"));
        model.addAttribute("prePage",pageInfo.get("prePage"));
        model.addAttribute("maxPage",pageInfo.get("maxPage"));
        model.addAttribute("typeList", typeList);
        return "admin/types";
    }

    //进入添加type页面
    @GetMapping("/types/input")
    public String typesInput(){
        return "admin/type-input";
    }

    //进入修改type的页面
    @GetMapping("/types/updateType/{typeId}")
    public String updateTypeView(@PathVariable("typeId")Long typeId, Model model){
        Type type = adminTypeService.queryTypeById(typeId);
        model.addAttribute("type",type);
        return "admin/type-update";
    }

    //进入tag 标签列表
    @GetMapping(value = "/tags/{pageIndex}")
    public String tags(@PathVariable("pageIndex")int pageIndex, Model model,HttpServletRequest request){
        String errMsg = request.getParameter("errMsg");
        if(errMsg!=null&&!errMsg.equals("")){
            model.addAttribute("errMsg",errMsg);
        }
        //查询tag的全部数量
        int tagsCount = adminTagService.queryAllTagCount();
        int pageSize = 7;
        //查询每个tag的关联doc数目
        List<Tag_Count> tagList = adminDocumentTagService.queryTagCountListByPage(pageIndex, pageSize);
        Map<String,Integer> pageInfo = PageUtil.page(pageIndex,pageSize,tagsCount);
        model.addAttribute("nextPage",pageInfo.get("nextPage"));
        model.addAttribute("prePage",pageInfo.get("prePage"));
        model.addAttribute("maxPage",pageInfo.get("maxPage"));
        model.addAttribute("tagList", tagList);
        return "admin/tags";
    }

    //进入添加tag页面
    @GetMapping("/tags/input")
    public String tagsInput(){
        return "admin/tag-input";
    }

    //进入修改标签页面
    @GetMapping("/tags/updateTag/{tagId}")
    public String updateTagView(@PathVariable("tagId")Long tagId, Model model){
        Tag tag = adminTagService.queryTagById(tagId);
        model.addAttribute("tag",tag);
        return "admin/tag-update";
    }
}
