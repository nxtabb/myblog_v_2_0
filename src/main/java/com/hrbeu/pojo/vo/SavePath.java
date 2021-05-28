package com.hrbeu.pojo.vo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Classname SavePath
 * @Description TODO
 * @Date 2021/5/10 09:25
 * @Created by nxt
 */
@Component
@ConfigurationProperties(prefix = "savepath")
public class SavePath {
    private String winSavePath;
    private String unixSavePath;

    public SavePath() {
    }

    public String getWinSavePath() {
        return winSavePath;
    }

    public void setWinSavePath(String winSavePath) {
        this.winSavePath = winSavePath;
    }

    public String getUnixSavePath() {
        return unixSavePath;
    }

    public void setUnixSavePath(String unixSavePath) {
        this.unixSavePath = unixSavePath;
    }

    public SavePath(String winSavePath, String unixSavePath) {
        this.winSavePath = winSavePath;
        this.unixSavePath = unixSavePath;
    }
}
