package com.hrbeu.dao.admin;

import com.hrbeu.pojo.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname AdminTagDao
 * @Description TODO
 * @Date 2021/5/10 11:07
 * @Created by nxt
 */
@Repository
public interface AdminTagDao {
    List<Tag> queryAllTags();
    List<Tag> queryTagsByIds(@Param("tagIds") List<Long> tagIds);
    int queryAllTagCount();
    void deleteTagById(@Param("tagId") Long tagId);
    Tag queryTagById(@Param("tagId") Long tagId);
    void updateTagName(@Param("tagId") Long tagId,@Param("tagName") String tagName);
    void addTag(@Param("tagName") String tagName);
}
