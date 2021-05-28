package com.hrbeu.service.admin.Impl;

import com.hrbeu.dao.admin.AdminTagDao;
import com.hrbeu.pojo.Tag;
import com.hrbeu.service.admin.AdminTagService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @Classname AdminTagServiceImpl
 * @Description TODO
 * @Date 2021/5/10 11:06
 * @Created by nxt
 */
@Service
public class AdminTagServiceImpl implements AdminTagService {
    @Autowired
    private AdminTagDao adminTagDao;
    @Override
    public List<Tag> queryAllTags() {
        return adminTagDao.queryAllTags();
    }

    @Override
    public List<Tag> queryTagListByIds(String[] tags_str) {
        List<Long> tagIds = new LinkedList<>();
        for (String s : tags_str) {
            tagIds.add(Long.parseLong(s));
        }
        return adminTagDao.queryTagsByIds(tagIds);
    }
    //查询全部tag的数量
    @Override
    public int queryAllTagCount() {
        return adminTagDao.queryAllTagCount();
    }

    @Override
    public void deleteTagById(Long tagId) {
        adminTagDao.deleteTagById(tagId);
    }

    @Override
    public Tag queryTagById(Long tagId) {
        return adminTagDao.queryTagById(tagId);
    }

    @Override
    public void updateTagName(Long tagId, String tagName) {
        adminTagDao.updateTagName(tagId,tagName);
    }

    @Override
    public void addTag(String tagName) {
        adminTagDao.addTag(tagName);
    }
}
