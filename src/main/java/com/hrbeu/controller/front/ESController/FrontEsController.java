package com.hrbeu.controller.front.ESController;

import com.hrbeu.pojo.Document;
import com.hrbeu.pojo.User;
import com.hrbeu.service.front.FrontESService;
import com.hrbeu.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
public class FrontEsController {
    @Autowired
    private FrontESService frontESService;

    @RequestMapping("/search/{pageIndex}")
    public String searchDocumentByES(@PathVariable("pageIndex")Integer pageIndex, HttpServletRequest request, Model model, RedirectAttributes attributes){
        //获取关键字
        HttpSession session = request.getSession();
        String sessionKeyWord = null;
        String keyword = request.getParameter("query");
        if(keyword!=null&&!keyword.trim().equals("")){
            session.setAttribute("keyword",keyword);
        }else {
            //获取session中的keyword
            keyword = (String)session.getAttribute("keyword");
        }
        //如果关键字为空，那么直接返回首页
        if(keyword==null||keyword.trim().equals("")) {
            attributes.addAttribute("errMsg", "关键字不能为空");
            return "redirect:/front";
        }
        //获取session的user，如果为空，不能使用查询功能
        User user = (User)session.getAttribute("user");
        if(user==null){
            model.addAttribute("errMsg","未登录不能使用全站检索功能");
            return "front/login";
        }
        //设置页码长度
        Integer pageSize = 5;
        //进入es查询文档
        List<Map<String, Object>> documents = frontESService.queryDocumentsByPage(keyword,pageIndex,pageSize);
        //如果没有文档，则返回首页
        if(documents==null||documents.size()<=0){
            attributes.addAttribute("errMsg","查询结果为空");
            return "redirect:/front";
        }
        //
        List<Document> documentList = new ArrayList<>();
        for (Map<String, Object> document : documents) {
            Document doc = new Document();
            Integer documentId = (Integer) document.get("documentId");
            doc.setDocumentId(documentId.longValue());
            doc.setTitle((String)document.get("title"));
            StringBuffer contentBuffer = (StringBuffer) document.get("content");
            String content = contentBuffer.toString();
            doc.setContent(content);
            doc.setPublished((Integer)document.get("published"));
            Long lastEditTimeLong = (Long)document.get("lastEditTime");
            Date lastEditTime = new Date(lastEditTimeLong);
            doc.setLastEditTime(lastEditTime);
            documentList.add(doc);
        }
        documentList = documentList.stream().filter(new Predicate<Document>() {
            @Override
            public boolean test(Document document) {
                return document.getPublished().equals(1);
            }
        }).sorted(new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return (-1)*o1.getLastEditTime().compareTo(o2.getLastEditTime());
            }
        }).collect(Collectors.toList());

        Integer count = (Integer) frontESService.queryDocumentsByPage(keyword,null,null).get(0).get("length");
        Map<String, Integer> pageInfo = PageUtil.page(pageIndex, pageSize, count);
        model.addAttribute("currentPage",pageIndex);
        model.addAttribute("nextPage",pageInfo.get("nextPage"));
        model.addAttribute("prePage",pageInfo.get("prePage"));
        model.addAttribute("maxPage",pageInfo.get("maxPage"));
        model.addAttribute("documentList",documentList);
        model.addAttribute("keyword",keyword);
        Integer maxCount = documents.size();
        model.addAttribute("maxCount",count);
        return "front/searchresult";
    }
}
