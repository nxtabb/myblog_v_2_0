package com.hrbeu.service.front;

import java.util.List;
import java.util.Map;

/**
 * @Classname FrontESService
 * @Description TODO
 * @Date 2021/5/17 15:30
 * @Created by nxt
 */
public interface FrontESService {
    List<Map<String, Object>> queryDocumentsByPage(String keyword, Integer pageIndex, Integer pageSize);
}
