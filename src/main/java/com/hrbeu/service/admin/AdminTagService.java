package com.hrbeu.service.admin;

import com.hrbeu.pojo.Tag;

import java.util.List;

/**
 * @Classname AdminTagService
 * @Description TODO
 * @Date 2021/5/10 11:04
 * @Created by nxt
 */
public interface AdminTagService {
    List<Tag> queryAllTags();
    List<Tag> queryTagListByIds(String[] tags_str);
    int queryAllTagCount();
    void deleteTagById(Long tagId);
    Tag queryTagById(Long tagId);
    void updateTagName(Long tagId, String tagName);
    void addTag(String tagName);
}
