package com.hrbeu.pojo.vo;

import java.util.Date;

/**
 * @Classname ESIndex
 * @Description TODO
 * @Date 2021/5/17 15:10
 * @Created by nxt
 */
public class ESIndex {
    private Long documentId;
    private String title;
    private String content;
    private Integer published;
    private Date lastEditTime;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPublished() {
        return published;
    }

    public void setPublished(Integer published) {
        this.published = published;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public ESIndex() {
    }

    public ESIndex(Long documentId, String title, String content, Integer published, Date lastEditTime) {
        this.documentId = documentId;
        this.title = title;
        this.content = content;
        this.published = published;
        this.lastEditTime = lastEditTime;
    }
}
