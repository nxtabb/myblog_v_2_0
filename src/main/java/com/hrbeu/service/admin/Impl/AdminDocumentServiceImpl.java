package com.hrbeu.service.admin.Impl;

import com.hrbeu.dao.admin.AdminCommentDao;
import com.hrbeu.dao.admin.AdminDocumentDao;
import com.hrbeu.dao.admin.AdminDocumentTagDao;
import com.hrbeu.dao.admin.AdminFileDao;
import com.hrbeu.pojo.Document;
import com.hrbeu.pojo.File;
import com.hrbeu.pojo.Tag;
import com.hrbeu.pojo.User;
import com.hrbeu.service.admin.AdminDocumentService;
import com.hrbeu.utils.FileUploadUtil;
import com.hrbeu.utils.PathUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AdminDocumentServiceImpl implements AdminDocumentService {
    private static final Logger logger = Logger.getLogger(AdminDocumentService.class);
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    @Autowired
    private AdminDocumentDao adminDocumentDao;
    @Autowired
    private AdminCommentDao adminCommentDao;
    @Autowired
    private AdminFileDao adminFileDao;
    @Autowired
    private AdminDocumentTagDao adminDocumentTagDao;
    @Override
    public int queryAllDocumentCount() {
        //查询已发布的文档个数
        return adminDocumentDao.queryAllDocumentCount();
    }

    @Override
    public List<Document> queryAllDocumentListByPage(int pageIndex, int pageSize) {
        int begin = pageSize*(pageIndex-1);
        //查询所有已发布的文档
        return adminDocumentDao.queryAllDocumentByPage(begin,pageSize);
    }

    @Override
    public Document queryDocumentById(Long documentId) {
        return adminDocumentDao.queryDocumentById(documentId);
    }

    @Override
    @Transactional(readOnly = false,rollbackFor = Exception.class,timeout = 60)
    public boolean deleteDocumentAndFileAndComment(Document document, User user) {
        try {
            //根据documentId 删除评论信息,真正删除评论之前必须先将外间约束设置为null
            adminCommentDao.setParentCommentToNull(document.getDocumentId());
            adminCommentDao.deleteCommentByDocumentId(document.getDocumentId());
            //根据documentId删除数据库中文件信息
            adminFileDao.deleteFileByDocumentId(document.getDocumentId());
            //根据documentId删除document和tag关联的信息
            adminDocumentTagDao.deleteDocumentTagByDocumentId(document.getDocumentId());
            //删除document表信息
            adminDocumentDao.deleteDocumentById(document.getDocumentId());
        }catch (Exception e){
            logger.debug("删除文档时出现了错误："+e.getMessage()+"数据库会进行回滚");
            return false;
        }
        //如果数据库没有问题，则删除磁盘上的文件
        deleteFileOfDocument(document,user);
        return true;
    }

    @Override
    @Transactional(readOnly = false,rollbackFor = Exception.class,timeout = 600)
    public void addDocumentAndDocumentTagAndSaveFiles(Document document, List<Tag> tagList, HttpServletRequest request) {
        try {
            //1、向document表中添加数据
            adminDocumentDao.addDocument(document);
            //2、向documentTags表中添加数据
            adminDocumentTagDao.addDocumentTag(document.getDocumentId(), tagList);
            //3、存盘
            Map<String, List<String>> fileInfo = null;
            try {
                fileInfo = FileUploadUtil.fileUpload(request, document.getTitle(), format.format(new Date()));
            }catch (Exception e){
                e.printStackTrace();
            }
            //4、向file表中添加数据
            //对文件的数据表进行添加
            if(fileInfo!=null&&fileInfo.get("fileNameList").get(0)!=null&&fileInfo.get("fileNameList").size()!=0){
                List<String> fileNameList = fileInfo.get("fileNameList");
                List<String> filePathList = fileInfo.get("filePathList");
                List<String> fileOriginNameList = fileInfo.get("fileOriginNameList");
                //进入dao层添加数据
                adminFileDao.addFile(fileNameList,filePathList,fileOriginNameList,document,new Date(),new Date());
            }
        }catch (Exception e){
            logger.debug("上传文档期间出现错误："+e.getMessage());
        }
    }

    @Override
    public Document queryDocumentAndUserAndTagsById(Long documentId) {
        return adminDocumentDao.queryDocumentAndUserAndTagsById(documentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class,timeout = 600)
    public void updateDocumentAndDocumentTagAndFiles(HttpServletRequest request, Document document,Document oldDocument) {
        List<File> oldFiles = adminFileDao.queryFilesByDocument(oldDocument.getDocumentId());
        Map<String, List<String>> fileInfo = null;
        try {
            //上传新文件
            fileInfo = FileUploadUtil.fileUpload(request, document.getTitle(), format.format(new Date()));
            //向tb_file表插入数据
            //4、向file表中添加数据
            //对文件的数据表进行添加
            if(fileInfo!=null&&fileInfo.get("fileNameList").get(0)!=null&&fileInfo.get("fileNameList").size()!=0){
                List<String> fileNameList = fileInfo.get("fileNameList");
                List<String> filePathList = fileInfo.get("filePathList");
                List<String> fileOriginNameList = fileInfo.get("fileOriginNameList");
                //进入dao层添加数据
                adminFileDao.addFile(fileNameList,filePathList,fileOriginNameList,document,new Date(),new Date());
            }
            //修改document_tag表
            adminDocumentTagDao.deleteDocumentTagByDocumentId(document.getDocumentId());
            adminDocumentTagDao.addDocumentTag(document.getDocumentId(),document.getTagList());
            //修改document表
            adminDocumentDao.updateDocument(document);
        }catch (Exception e){
            logger.debug("修改文档时出现错误："+e.getMessage());
        }
        //上传磁盘文件,如果更改了document的名称，则必须先将原文件中的所有文件复制到新文件夹下
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            //如果修改了title，那么就需要将已存在文件复制到新文件夹中
            if(!oldDocument.getTitle().equals(document.getTitle())){
                //获取旧文件的信息
                //如果有旧文件
                if(oldFiles!=null&&oldFiles.size()>0){
                    //获取新文件夹的路径，并创建该文件夹，防止出现异常
                    java.io.File new_dir = new java.io.File(PathUtil.getBasePath()+ java.io.File.separator +document.getUser().getUsername()+ java.io.File.separator+document.getTitle());
                    if(!new_dir.exists()){
                        new_dir.mkdirs();
                    }
                    //遍历每个旧文件
                    for (File file : oldFiles) {
                        //旧文件路径
                        java.io.File trueFile = new java.io.File(PathUtil.getBasePath() + file.getFilePath());
                        inputStream = new FileInputStream(trueFile);
                        //待复制的文件路径
                        java.io.File outPutPath = new java.io.File(PathUtil.getBasePath()+ java.io.File.separator +document.getUser().getUsername()+ java.io.File.separator+document.getTitle()+ java.io.File.separator+file.getFileName());
                        outputStream = new FileOutputStream(outPutPath);
                        int length = -1;
                        byte[] buffer = new byte[1024];
                        bufferedInputStream = new BufferedInputStream(inputStream);
                        bufferedOutputStream = new BufferedOutputStream(outputStream);
                        while ((length = bufferedInputStream.read(buffer))!=-1 ){
                            bufferedOutputStream.write(buffer,0,length);
                        }
                        //得到文件的路径
                        String filePath = java.io.File.separator +document.getUser().getUsername()+ java.io.File.separator+document.getTitle()+ java.io.File.separator+file.getFileName();
                        //根据id，更新file数据库中file_path字段
                        adminFileDao.updateFilePathInfo(filePath,file.getFileId());
                        //删除原文件
                        trueFile.delete();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭所有的流
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //根据文档和要删除的User，删除文档路径下的所有文件
    public void deleteFileOfDocument(Document document, User user) {
        //删除文件
        String filePathStr = PathUtil.getBasePath()+ java.io.File.separator+user.getUsername()+ java.io.File.separator+document.getTitle();
        java.io.File filePath = new java.io.File(filePathStr);
        //判断路径是否存在
        if(filePath.exists()){
            //判断是否是文件夹
            if(filePath.isDirectory()){
                //获取文件夹下的所有文件
                java.io.File[] files = filePath.listFiles();
                //遍历删除
                for(java.io.File f:files){
                    f.delete();
                }
            }
            //最后删除该文件夹或文件。
            filePath.delete();
        }
    }


}
