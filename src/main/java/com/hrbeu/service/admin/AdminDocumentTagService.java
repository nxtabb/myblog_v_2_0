package com.hrbeu.service.admin;

import com.hrbeu.pojo.vo.Tag_Count;

import java.util.List;

/**
 * @Classname AdminDocumentTagService
 * @Description TODO
 * @Date 2021/5/13 21:49
 * @Created by nxt
 */
public interface AdminDocumentTagService {
    List<Tag_Count> queryTagCountListByPage(int pageIndex, int pageSize);
    Integer queryTagInDocumentsCountById(Long tagId);
}
