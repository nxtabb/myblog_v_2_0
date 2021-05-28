package com.hrbeu.dao.front;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Classname FrontDocumentTagDao
 * @Description TODO
 * @Date 2021/5/14 10:06
 * @Created by nxt
 */
@Repository
public interface FrontDocumentTagDao {
    Integer queryTagCountInPublishedDocument(@Param("tagId") Long tagId);
}
