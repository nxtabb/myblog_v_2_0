package com.hrbeu.controller.front.DocumentController;

import com.hrbeu.pojo.Document;
import com.hrbeu.pojo.Tag;
import com.hrbeu.pojo.Type;
import com.hrbeu.pojo.User;
import com.hrbeu.service.admin.AdminDocumentService;
import com.hrbeu.service.admin.AdminTagService;
import com.hrbeu.service.admin.AdminTypeService;
import com.hrbeu.service.front.FrontDocumentService;
import com.hrbeu.utils.ESUtil;
import com.hrbeu.utils.FileUploadUtil;
import com.hrbeu.utils.RequestUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Classname FrontDocumentController
 * @Description TODO
 * @Date 2021/5/14 09:55
 * @Created by nxt
 */
@Controller
public class FrontDocumentController {
    @Autowired
    private AdminDocumentService adminDocumentService;
    @Autowired
    private FrontDocumentService frontDocumentService;
    @Autowired
    private AdminTypeService adminTypeService;
    @Autowired
    private AdminTagService adminTagService;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    //删除指定的文档
    @GetMapping("/documents/deleteById/{documentId}")
    public String deleteDocumentById(@PathVariable("documentId")Long documentId, HttpServletRequest request, Model model){
        User user =(User) request.getSession().getAttribute("user");
        if(user==null){
            //如果没有登录就想看文章，那是不行的，会返回登录页面
            model.addAttribute("errMsg","使用我的文章功能前需要登录哦～");
            return "front/login";
        }
        //查询document
        Document document = frontDocumentService.queryDocumentById(documentId);
        //删除与document相关的一切事物
        adminDocumentService.deleteDocumentAndFileAndComment(document,user);
        //删除ES中的索引
        ESUtil.deleteDocumentInES(document, restHighLevelClient);
        return "redirect:/documentsIndex/1";
    }

    @PostMapping("/documents/adddocument")
    public String addDocument(HttpServletRequest request,
                              @RequestParam("title")String title,
                              @RequestParam("content")String content,
                              @RequestParam("typeId")String typeId,
                              @RequestParam("firstPicture")String firstPicture,
                              @RequestParam("flag")String flag,
                              @RequestParam("description")String description,
                              @RequestParam("tagIdList") String[] tags,
                              @RequestParam("saveorpublic")String saveOrPublic,
                              Model model) throws IOException {
        //获取当前用户信息，以备后续存储文件时使用
        User user =(User) request.getSession().getAttribute("user");
        if(user==null){
            //如果没有登录就想看文章，那是不行的，会返回登录页面
            model.addAttribute("errMsg","使用我的文章功能前需要登录哦～");
            return "front/login";
        }
        //得到是那一堆单选框的值
        Integer appreciate = RequestUtil.isRequestIsOn(0,request,"appreciate");
        Integer shareInfo = RequestUtil.isRequestIsOn(0,request,"shareInfo");
        Integer commentAble = RequestUtil.isRequestIsOn(0,request,"commentAble");
        Integer recommend = RequestUtil.isRequestIsOn(0,request,"recommend");
        Integer published = Integer.parseInt(saveOrPublic);
        //获取typeId下的type
        Type type = adminTypeService.queryTypeById(Long.parseLong(typeId));
        //通过tagsId获取到TagList,已测试成功
        List<Tag> tagList = adminTagService.queryTagListByIds(tags);
        Document document = new Document(title,content,firstPicture,flag,0,appreciate,shareInfo,commentAble,published,recommend,new Date(),new Date(),type,tagList,user,description);
        //向document、documentTag、file表中添加数据，并且存盘,已测试成功
        adminDocumentService.addDocumentAndDocumentTagAndSaveFiles(document,tagList,request);
        //向ElasticSearch添加索引
        boolean hasFailure = ESUtil.addDocumentInES(document, restHighLevelClient);
        //如果没有错误，则返回文档列表
        if(!hasFailure){
            return "redirect:/documentsIndex/1";
        }else {
            //如果出现错误，则进入添加文档界面
            return "redirect:/documents/adddocument";
        }



    }

    @PostMapping("/documents/updatedocument/{documentId}")
    public String updateDocument(@PathVariable("documentId")Long documentId,
                                 @RequestParam("title")String title,
                                 @RequestParam("content")String content,
                                 @RequestParam("typeId")String typeId,
                                 @RequestParam("firstPicture")String firstPicture,
                                 @RequestParam("flag")String flag,
                                 @RequestParam("description")String description,
                                 @RequestParam("tagIdList") String[] tags,
                                 @RequestParam("saveorpublic")String saveOrPublic,
                                 HttpServletRequest request,
                                 Model model){
        User user =(User) request.getSession().getAttribute("user");
        if(user==null){
            //如果没有登录就想看文章，那是不行的，会返回登录页面
            model.addAttribute("errMsg","使用我的文章功能前需要登录哦～");
            return "front/login";
        }
        Document oldDocument = adminDocumentService.queryDocumentAndUserAndTagsById(documentId);
        User owner = oldDocument.getUser();
        //判断当前文档的所属用户是否是登录的用户，如果不是则不能修改，直接返回文档列表页面
        if (!owner.getUserId().equals(user.getUserId())) {
            return "redirect:/documentsIndex/1";
        }
        //得到是那一堆单选框的值
        Integer appreciate = RequestUtil.isRequestIsOn(0,request,"appreciate");
        Integer shareInfo = RequestUtil.isRequestIsOn(0,request,"shareInfo");
        Integer commentAble = RequestUtil.isRequestIsOn(0,request,"commentAble");
        Integer recommend = RequestUtil.isRequestIsOn(0,request,"recommend");
        Integer published = Integer.parseInt(saveOrPublic);
        //获取typeId下的type
        Type type = adminTypeService.queryTypeById(Long.parseLong(typeId));
        List<Tag> tagList = adminTagService.queryTagListByIds(tags);
        Document document = new Document(title,content,firstPicture,flag,appreciate,shareInfo,commentAble,published,recommend,new Date(),type,tagList,user,description);
        document.setDocumentId(documentId);
        //更新tb_document表和tb_file表
        adminDocumentService.updateDocumentAndDocumentTagAndFiles(request,document,oldDocument);
        //判断document中，title或者content或者published或者lastEditTime是否修改了
        boolean isDocumentUpdated = isDocumentUpdated(oldDocument, document);
        if(isDocumentUpdated){
            ESUtil.updateDocumentInES(document,restHighLevelClient);
        }
        //如果修改则直接修改ES中的document内容，如果没有修改，则直接跳转
        return "redirect:/documentsIndex/1";
    }



    public boolean isDocumentUpdated(Document oldDocument,Document newDocument){
        return !(oldDocument.getTitle().equals(newDocument.getTitle())
                && oldDocument.getContent().equals(newDocument.getContent())
                && oldDocument.getPublished().equals(newDocument.getPublished())
                && oldDocument.getLastEditTime().equals(newDocument.getLastEditTime()));
    }

}
