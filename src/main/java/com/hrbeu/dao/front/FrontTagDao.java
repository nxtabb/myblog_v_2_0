package com.hrbeu.dao.front;

import com.hrbeu.pojo.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname FrontTagDao
 * @Description TODO
 * @Date 2021/5/14 10:06
 * @Created by nxt
 */
@Repository
public interface FrontTagDao {
    List<Tag> queryTagList();
}
