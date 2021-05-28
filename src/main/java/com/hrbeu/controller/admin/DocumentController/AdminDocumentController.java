package com.hrbeu.controller.admin.DocumentController;

import com.hrbeu.pojo.*;
import com.hrbeu.pojo.File;
import com.hrbeu.service.admin.AdminDocumentService;
import com.hrbeu.service.admin.AdminFileService;
import com.hrbeu.service.admin.AdminTagService;
import com.hrbeu.service.admin.AdminTypeService;
import com.hrbeu.utils.FileUploadUtil;
import com.hrbeu.utils.PageUtil;
import com.hrbeu.utils.PathUtil;
import com.hrbeu.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Classname AdminDocumentController
 * @Description TODO
 * @Date 2021/5/9 21:07
 * @Created by nxt
 */
@Controller
@RequestMapping("/admin")
public class AdminDocumentController {
    @Autowired
    private AdminDocumentService adminDocumentService;
    @Autowired
    private AdminTypeService adminTypeService;
    @Autowired
    private AdminTagService adminTagService;
    @Autowired
    private AdminFileService adminFileService;

    @GetMapping("/documents/deleteById/{documentId}")
    public String deleteDocumentById(@PathVariable("documentId")Long documentId,HttpServletRequest request) {
        //按照文档id查询文档
        Document document = adminDocumentService.queryDocumentById(documentId);
        User user = document.getUser();
        boolean flag = adminDocumentService.deleteDocumentAndFileAndComment(document, user);
        if (flag) {
            return "redirect:/admin/documentsIndex/1";
        } else {
            request.setAttribute("errMsg", "删除文件时出现错误，请重试或联系开发人员！");
            return "admin/index";
        }
    }
    //添加文档操作
    @PostMapping("/documents/adddocument")
    public String addDocument(HttpServletRequest request,
                              @RequestParam("title")String title,
                              @RequestParam("content")String content,
                              @RequestParam("typeId")String typeId,
                              @RequestParam("firstPicture")String firstPicture,
                              @RequestParam("flag")String flag,
                              @RequestParam("description")String description,
                              @RequestParam("tagIdList") String[] tags,
                              @RequestParam("saveorpublic")String saveOrPublic) throws IOException {
        //得到是那一堆单选框的值
        Integer appreciate = RequestUtil.isRequestIsOn(0,request,"appreciate");
        Integer shareInfo = RequestUtil.isRequestIsOn(0,request,"shareInfo");
        Integer commentAble = RequestUtil.isRequestIsOn(0,request,"commentAble");
        Integer recommend = RequestUtil.isRequestIsOn(0,request,"recommend");
        Integer published = Integer.parseInt(saveOrPublic);
        //获取typeId下的type
        Type type = adminTypeService.queryTypeById(Long.parseLong(typeId));
        //获取当前用户信息，以备后续存储文件时使用
        User user = (User) request.getSession().getAttribute("user");
        //通过tagsId获取到TagList,已测试成功
        List<Tag> tagList = adminTagService.queryTagListByIds(tags);
        Document document = new Document(title,content,firstPicture,flag,0,appreciate,shareInfo,commentAble,published,recommend,new Date(),new Date(),type,tagList,user,description);
        //向document、documentTag、file表中添加数据，并且存盘,已测试成功
        adminDocumentService.addDocumentAndDocumentTagAndSaveFiles(document,tagList,request);
        return "redirect:/admin/documentsIndex/1";
    }
    //修改文档操作
    @PostMapping("/documents/update/{documentId}")
    public String updateDocument(@PathVariable("documentId") Long documentId,
                                 @RequestParam("title")String title,
                                 @RequestParam("content")String content,
                                 @RequestParam("typeId")String typeId,
                                 @RequestParam("firstPicture")String firstPicture,
                                 @RequestParam("flag")String flag,
                                 @RequestParam("description")String description,
                                 @RequestParam("tagIdList") String[] tags,
                                 @RequestParam("saveorpublic")String saveOrPublic,
                                 HttpServletRequest request) throws IOException {
        Document oldDocument = adminDocumentService.queryDocumentAndUserAndTagsById(documentId);
        //得到是那一堆单选框的值
        Integer appreciate = RequestUtil.isRequestIsOn(0,request,"appreciate");
        Integer shareInfo = RequestUtil.isRequestIsOn(0,request,"shareInfo");
        Integer commentAble = RequestUtil.isRequestIsOn(0,request,"commentAble");
        Integer recommend = RequestUtil.isRequestIsOn(0,request,"recommend");
        Integer published = Integer.parseInt(saveOrPublic);
        //获取typeId下的type
        Type type = adminTypeService.queryTypeById(Long.parseLong(typeId));
        User user = (User) request.getSession().getAttribute("user");
        List<Tag> tagList = adminTagService.queryTagListByIds(tags);
        Document document = new Document(title,content,firstPicture,flag,appreciate,shareInfo,commentAble,published,recommend,new Date(),type,tagList,user,description);
        document.setDocumentId(documentId);
        //更新tb_document表和tb_file表
        adminDocumentService.updateDocumentAndDocumentTagAndFiles(request,document,oldDocument);
        return "redirect:/admin/documentsIndex/1";
    }

    //在修改文档界面下载文件
    @GetMapping("/files/downloadFile/{fileId}")
    public void downloadFile(@PathVariable("fileId")Long fileId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //从数据库中查询文档详细信息
        File file = adminFileService.queryFileById(fileId);
        //得到文件路径
        String filePath = PathUtil.getBasePath()+file.getFilePath();
        //定义File对象
        java.io.File file_download = new java.io.File(filePath);
        if(!file_download.exists()){
            //如果文件不存在，返回错误结果
            request.setAttribute("errMsg","文件不存在或已经损坏");
            request.getRequestDispatcher("/admin/documentsIndex/1").forward(request,response);
            return;
        }
        else {
            //如果文件存在，获取文件名
            String fileName = file.getFileOriginName();
            //使用utf-8进行编码
            fileName = URLEncoder.encode(fileName,"UTF-8");
            response.addHeader("Content-Disposition","attachment;filename=" + fileName);
            response.setContentType("multipart/form-data");
            response.setContentLength((int)file_download.length());
            FileInputStream inputStream = new FileInputStream(filePath);
            OutputStream outputStream = response.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = bufferedInputStream.read(buffer))!=-1){
                bufferedOutputStream.write(buffer,0,len);
            }
            bufferedOutputStream.close();
            bufferedInputStream.close();
            inputStream.close();
            outputStream.close();
        }
    }

    //在修改文档界面删除文件
    @GetMapping("/files/onlyDeleteFile/{documentId}/{fileId}")
    public void onlyDeleteFile(@PathVariable("documentId")Long documentId,
                               @PathVariable("fileId")Long fileId,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException, ServletException {
        //删除文件，并且删除tb_file中的数据
        boolean flag = adminFileService.deleteFile(fileId);
        if(flag){
            //删除文件成功，直接返回当前页面
            response.sendRedirect("/admin/documents/updatedocument/"+documentId);
        }else {
            request.setAttribute("errMsg","文件删除失败");
            request.getRequestDispatcher("/admin/documentsIndex/1").forward(request,response);
        }
    }
}
