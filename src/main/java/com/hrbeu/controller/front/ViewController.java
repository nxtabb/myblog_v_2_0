package com.hrbeu.controller.front;

import com.hrbeu.dao.admin.AdminDocumentDao;
import com.hrbeu.pojo.*;
import com.hrbeu.pojo.vo.File_Len;
import com.hrbeu.pojo.vo.Tag_Count;
import com.hrbeu.pojo.vo.Type_Count;
import com.hrbeu.service.admin.AdminDocumentService;
import com.hrbeu.service.admin.AdminFileService;
import com.hrbeu.service.front.*;
import com.hrbeu.utils.PageUtil;
import com.hrbeu.utils.PathUtil;
import com.hrbeu.utils.RedisUtil;
import com.youbenzi.mdtool.tool.MDTool;
import org.apache.catalina.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class ViewController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private FrontDocumentService frontDocumentService;
    @Autowired
    private FrontTypeService frontTypeService;
    @Autowired
    private FrontTagService frontTagService;
    @Autowired
    private FrontFileService frontFileService;
    @Autowired
    private FrontCommentService frontCommentService;
    @Autowired
    private AdminDocumentService adminDocumentService;
    @Autowired
    private AdminFileService adminFileService;
    /*
    //进入网站如： www.ningxitong.cn,判断是否登录了，如果登录，则进入/index平台首页，如果没有
    登录则需要跳转到login界面
    */
    @GetMapping("/")
    public String indexRouter(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user!=null){
            return "redirect:/front";
        }else {
            model.addAttribute("errMsg","亲爱的用户，您可以选择登录或直接访问");
            return "front/login";
        }
    }

    //博客首页
    @GetMapping(value = {"/front/index","/front","/front/{pageIndex}"})
    public String index(HttpServletRequest request,Model model,@PathVariable(value = "pageIndex",required = false)Integer pageIndex){
        String errMsg = request.getParameter("errMsg");
        if(errMsg!=null&&!errMsg.trim().equals("")){
            model.addAttribute("errMsg",errMsg);
        }
        //判断是否是游客访问，如果是，则删除session里的user
        String guest = request.getParameter("guest");
        if(guest!=null&&guest.equals("1")){
            HttpSession session = request.getSession();
            if(session.getAttribute("user")!=null){
                session.removeAttribute("user");
            }
        }
        //定义当前页码
        if(pageIndex==null){
            pageIndex = 1;
        }
        //每页要显示的文章个数
        Integer pageSize = 5;
        //首页显示的type个数
        int typeCount = 6;
        //首页现实的tag个数
        int tagCount =10;
        //首页显示的推荐文章个数
        Integer recommendDocumentCount = 7;
        //查询published的文章列表
        List<Document> publishedDocumentList = frontDocumentService.queryPublishedDocumentList(pageIndex,pageSize);
        //published的文章个数
        Integer publishedDocumentCount = frontDocumentService.queryPublishedDocumentCount();
        //查询type和关联文档(不包括未发布的)的个数
        List<Type_Count> typeCountList = frontTypeService.queryTypeAndCount();
        //进行排序，并取出前typeCount个
        typeCountList = typeCountList.stream().sorted(new Comparator<Type_Count>() {
            @Override
            public int compare(Type_Count o1, Type_Count o2) {
                if (o1.getCount() < o2.getCount()) {
                    return 1;
                } else if (o1.getCount().equals(o2.getCount())) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }).limit(typeCount).collect(Collectors.toList());
        //查询tag和关联文档(不包括未发布的)的个数
        List<Tag_Count> tagCountList = frontTagService.getTagAndCount();
        //对个数进行排序并且
        tagCountList = tagCountList.stream().sorted(new Comparator<Tag_Count>() {
            @Override
            public int compare(Tag_Count o1, Tag_Count o2) {
                if(o1.getCount()<o2.getCount()){
                    return 1;
                }else if(o1.getCount().equals(o2.getCount())){
                    return 0;
                }else {
                    return -1;
                }
            }
        }).limit(tagCount).collect(Collectors.toList());
        //查询推荐（不包括未发布的）的文章
        List<Document> recommendDocumentList = frontDocumentService.queryRecommendDocumentList(recommendDocumentCount);
        //查询分页信息
        Map<String,Integer> pageInfo = PageUtil.page(pageIndex,pageSize,publishedDocumentCount);
        //查询viewCount
        List<Integer> viewCountList = new ArrayList<>();
        for (Document document : publishedDocumentList) {
            Integer viewCount = 0;
            Object viewCountObject = redisUtil.hget("viewCount",document.getDocumentId()+"");
            if(viewCountObject!=null){
                viewCount = (Integer)viewCountObject;
            }
            viewCountList.add(viewCount);
        }
        model.addAttribute("viewCountList",viewCountList);
        model.addAttribute("recommendDocumentList",recommendDocumentList);
        model.addAttribute("tagCountList",tagCountList);
        model.addAttribute("currentPage",pageIndex);
        model.addAttribute("typeCountList",typeCountList);
        model.addAttribute("nextPage",pageInfo.get("nextPage"));
        model.addAttribute("prePage",pageInfo.get("prePage"));
        model.addAttribute("documentList",publishedDocumentList);
        model.addAttribute("maxCount",publishedDocumentCount);
        return "front/index";
    }

    //注册页面
    @GetMapping("/front/register")
    public String toRegister(){
        return "front/plt-register";
    }

    //登录页面
    @GetMapping("/front/login")
    public String toLogin(){
        return "front/login";
    }

    @GetMapping("/about")
    public String about(){
        return "front/about";
    }

    //进入具体的document中
    @GetMapping("/document/{documentId}")
    public String document(@PathVariable("documentId")Long documentId, Model model){
        //使用redis查询viewCount功能
        Object viewCountObject = redisUtil.hget("viewCount", documentId + "");
        if(viewCountObject!=null){
            Integer viewCount = (Integer)viewCountObject+1;
            redisUtil.hset("viewCount",documentId+"",viewCount);
            model.addAttribute("viewCount",viewCount);
        }else {
            redisUtil.hset("viewCount",documentId+"",1);
            model.addAttribute("viewCount",1);
        }
        //查询document最全的信息
        Document document = frontDocumentService.queryDocumentById(documentId);
        //如果该文件是空，或者非法使用url进入未发布的文章，则直接返回首页
        if(document==null||!document.getPublished().equals(1)){
            return "redirect:/front";
        }
        //查询document附属文件信息
        List<File_Len> fileLenList = frontFileService.queryFileAndLenByDocument(documentId);
        //查找flag
        String flagStr = "原创";
        if(document.getFlag()!=null){
            if(document.getFlag().equals("2")){
                flagStr = "转载";
            }
            if(document.getFlag().equals("3")){
                flagStr="翻译";
            }
        }
        //使用新的md2Html包
        String contentHtml = MDTool.markdown2Html(document.getContent());
        //String contentHtml = Md2Html.md2htmlPro(document.getContent());
        model.addAttribute("contentHtml",contentHtml);
        model.addAttribute("fileLenList",fileLenList);
        model.addAttribute("document",document);
        model.addAttribute("flagStr",flagStr);
        //评论显示
        //根评论
        List<Comment> commentList = frontCommentService.queryCommentListByDocument(documentId);
        model.addAttribute("commentList",commentList);
        return "front/document";
    }

    //进入分类列表页
    @GetMapping("types/{typeId}/{pageIndex}")
    public String types(@PathVariable("typeId")Long typeId,@PathVariable("pageIndex") int pageIndex, Model model){
        //查询全部已发表文章的type和count
        List<Type_Count> typeCountList = frontTypeService.queryTypeAndCount();
        if(typeCountList==null||typeCountList.size()==0){
            model.addAttribute("errMsg","没有发布任何文档");
            return "front/types";
        }
        Integer pageSize = 5;
        if(typeId.equals(-1L)){
            //如果没传typeid，则设为-1
            typeId = typeCountList.get(0).getTypeId();

        }
        //根据type来查询文章
        List<Document> documentList = frontDocumentService.queryDocumentPageByTypeId(pageIndex,pageSize,typeId);
        //查询查询到的全部数量
        Integer count = frontDocumentService.queryPublishedDocumentCountByTypeId(typeId);
        if(count==null){
            count = 0;
        }
        //获取分页信息
        Map<String,Integer> pageInfo =  PageUtil.page(pageIndex,pageSize,count);
        //查看viewCount
        List<Integer> viewCountList = new ArrayList<>();
        for (Document document : documentList) {
            Integer viewCount = 0;
            Object viewCountObject = redisUtil.hget("viewCount",document.getDocumentId()+"");
            if(viewCountObject!=null){
                viewCount = (Integer)viewCountObject;
            }
            viewCountList.add(viewCount);
        }
        model.addAttribute("currentPage",pageIndex);
        model.addAttribute("viewCountList",viewCountList);
        model.addAttribute("count",count);
        model.addAttribute("prePage",pageInfo.get("prePage"));
        model.addAttribute("nextPage",pageInfo.get("nextPage"));
        model.addAttribute("maxPage",pageInfo.get("maxPage"));
        model.addAttribute("typeCountList",typeCountList);
        model.addAttribute("documentList",documentList);
        model.addAttribute("currenttypeId",typeId);
        return "front/types";
    }

    //进入tag列表页
    @GetMapping("/tags/{tagId}/{pageIndex}")
    public String tags(@PathVariable("tagId")Long tagId, @PathVariable("pageIndex")int pageIndex, Model model){
        List<Tag_Count> tagCountList = frontTagService.getTagAndCount();
        int pageSize = 5;
        if(tagCountList==null||tagCountList.size()==0){
            model.addAttribute("errMsg","没有发布任何文档");
            return "front/tags";
        }
        if(tagId.equals(-1L)){
            tagId = tagCountList.get(0).getTagId();
        }
        List<Document> documentList = frontDocumentService.queryDocumentPageByTagId(pageIndex,pageSize,tagId);
        //查询该文件的所有tagList
        Integer count = frontDocumentService.queryPublishedDocumentCountByTagId(tagId);
        Map<String,Integer> pageInfo = PageUtil.page(pageIndex,pageSize,count);
        //查看viewCount
        List<Integer> viewCountList = new ArrayList<>();
        for (Document document : documentList) {
            Integer viewCount = 0;
            Object viewCountObject = redisUtil.hget("viewCount",document.getDocumentId()+"");
            if(viewCountObject!=null){
                viewCount = (Integer)viewCountObject;
            }
            viewCountList.add(viewCount);
        }
        model.addAttribute("currentPage",pageIndex);
        model.addAttribute("viewCountList",viewCountList);
        model.addAttribute("tagCountList",tagCountList);
        model.addAttribute("prePage",pageInfo.get("prePage"));
        model.addAttribute("nextPage",pageInfo.get("nextPage"));
        model.addAttribute("count",count);
        model.addAttribute("currenttagId",tagId);
        model.addAttribute("documentList",documentList);
        model.addAttribute("maxPage",pageInfo.get("maxPage"));
        return "front/tags";
    }

    //进入归档页面
    @GetMapping("/archives/{pageIndex}")
    public String archives(@PathVariable("pageIndex")Integer pageIndex, Model model){
        //设置每页最多记录三个月的文章
        Integer pageSize = 3;
        //查询时间戳
        List<String> timeList = frontDocumentService.queryDocumentByYearAndMonthAndPage(pageIndex,pageSize);
        Map<String,List<Document>> resultMap = new HashMap<>();
        int count = 0;
        //查询每个时间戳下的文档
        for(String time:timeList){
            List<Document>documentList = frontDocumentService.queryPublishedDocumentByTime(time);
            resultMap.put(time,documentList);
            count += documentList.size();
        }
        //查询时间戳个数
        Integer timeCount = 0;
        List<String> timeCountList = frontDocumentService.queryDocumentByYearAndMonth();
        if(timeCountList!=null){
            timeCount = timeCountList.size();
        }
        //返回分页信息
        Map<String, Integer> pageInfo = PageUtil.page(pageIndex, pageSize, timeCount);
        model.addAttribute("resultMap",resultMap);
        model.addAttribute("count",count);
        model.addAttribute("currentPage",pageIndex);
        model.addAttribute("prePage",pageInfo.get("prePage"));
        model.addAttribute("nextPage",pageInfo.get("nextPage"));
        return "front/archives";
    }

    //查看我的文档
    @GetMapping("/documentsIndex/{pageIndex}")
    public String myDocuments(@PathVariable("pageIndex")Integer pageIndex,Model model,HttpServletRequest request){
        int pageSize = 7;
        //获取session中的user
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        //如果没有登录
        if(user==null){
            //如果没有登录就想看文章，那是不行的，会返回登录页面
            model.addAttribute("errMsg","使用我的文章功能前需要登录哦～");
            return "front/login";
        }
        //查询其本人的全部文章，包括未发布文章
        List<Document> documentList = frontDocumentService.queryDocumentByUserId(pageIndex,pageSize,user.getUserId());
        Integer maxCount = 0;
        //查询其本人文章的数量
        maxCount = frontDocumentService.queryDocumentCountByUserId(user.getUserId());
        //前端需要传入typeList
        List<Type> typeList = frontTypeService.queryAllType();
        //查询分页信息
        Map<String,Integer> pageInfo = PageUtil.page(pageIndex,pageSize,maxCount);
        model.addAttribute("currentPage",pageIndex);
        model.addAttribute("typeList",typeList);
        model.addAttribute("prePage",pageInfo.get("prePage"));
        model.addAttribute("nextPage",pageInfo.get("nextPage"));
        model.addAttribute("documentList",documentList);
        return "front/doc-mine";
    }

    //进入我的文档->新增文档页面
    @GetMapping("/documents/adddocument")
    public String addDocument(Model model,HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        //如果没有登录
        if(user==null){
            //如果没有登录就想看文章，那是不行的，会返回登录页面
            model.addAttribute("errMsg","使用我的文章功能前需要登录哦～");
            return "front/login";
        }
        //查询全部type
        List<Type> typeList = frontTypeService.queryAllType();
        //查询全部tag
        List<Tag> tagList = frontTagService.queryAllTag();
        model.addAttribute("typeList",typeList);
        model.addAttribute("tagList",tagList);
        return "front/doc-publish";
    }

    //进入查看编辑文档界面
    @GetMapping("/documents/updatedocument/{documentId}")
    public String updatedDocument(@PathVariable("documentId") Long documentId, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        //如果没有登录
        if(user==null){
            //如果没有登录就想看文章，那是不行的，会返回登录页面
            model.addAttribute("errMsg","使用我的文章功能前需要登录哦～");
            return "front/login";
        }
        //获取document的超级详细信息，测试成功
        Document document = adminDocumentService.queryDocumentAndUserAndTagsById(documentId);
        User owner = document.getUser();
        //判断当前文档的所属用户是否是登录的用户，如果不是则不能修改，直接返回文档列表页面
        if (!owner.getUserId().equals(user.getUserId())) {
            return "redirect:/documentsIndex/1";
        }
        //查询所有的type
        List<Type> typeList= frontTypeService.queryAllType();
        //查询全部Tag
        List<Tag> tagList = frontTagService.queryAllTag();
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
        return "front/doc-update";
    }

}

