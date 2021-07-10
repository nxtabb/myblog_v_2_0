package com.hrbeu.controller.front.FileController;

import com.hrbeu.pojo.File;
import com.hrbeu.service.admin.AdminFileService;
import com.hrbeu.service.front.FrontFileService;
import com.hrbeu.utils.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @Classname FrontFileController
 * @Description TODO
 * @Date 2021/5/14 09:55
 * @Created by nxt
 */
@Controller
public class  FrontFileController {
    @Autowired
    private FrontFileService frontFileService;
    @Autowired
    private AdminFileService adminFileService;
    //在修改文档界面下载文件
    @GetMapping("/files/downloadFile/{documentId}/{fileId}")
    public void downloadFile(@PathVariable("documentId")Long documentId,
                             @PathVariable("fileId")Long fileId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, IOException {
        //从数据库中查询文档详细信息
        File file = frontFileService.queryFileById(fileId);
        //得到文件路径
        String filePath = PathUtil.getBasePath()+file.getFilePath();
        //定义File对象
        java.io.File file_download = new java.io.File(filePath);
        if(!file_download.exists()){
            //如果文件不存在，返回错误结果
            request.setAttribute("errMsg","文件不存在或已经损坏");
            request.getRequestDispatcher("/document/"+documentId).forward(request,response);
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
            response.sendRedirect("/documents/updatedocument/"+documentId);
        }else {
            request.setAttribute("errMsg","文件删除失败");
            request.getRequestDispatcher("/documentsIndex/1").forward(request,response);
        }
    }
    @GetMapping("/files/deletefile/{documentId}/{fileId}")
    //在浏览文件时删除文件，而不是在管理文档中删除文件，因此不跳转到其他页面
    public String deleteFileInDocument(@PathVariable("documentId")Long documentId,
                                       @PathVariable("fileId")Long fileId,
                                       HttpServletRequest request,
                                       HttpServletResponse response,
                                       Model model) throws IOException, ServletException {
        //删除文件，并且删除tb_file中的数据
        boolean flag = adminFileService.deleteFile(fileId);
        if(flag){
            //删除文件成功，直接返回当前页面
            return "redirect:/document/"+documentId;
        }else {
            model.addAttribute("errMsg","文件删除失败");
            return "front/index";
        }
    }
}
