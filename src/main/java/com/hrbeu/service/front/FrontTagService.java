package com.hrbeu.service.front;

import com.hrbeu.pojo.Tag;
import com.hrbeu.pojo.vo.Tag_Count;

import java.util.List;

/**
 * @Classname FrontTagService
 * @Description TODO
 * @Date 2021/5/14 09:58
 * @Created by nxt
 */
public interface FrontTagService {
    List<Tag_Count> getTagAndCount();
    List<Tag> queryAllTag();
}
