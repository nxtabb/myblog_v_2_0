package com.hrbeu.pojo.vo;

import com.hrbeu.pojo.File;

import java.io.Serializable;

public class File_Len implements Serializable {
    private File file;
    private String length;
    public File_Len(){}

    public File_Len(File file, String length) {
        this.file = file;
        this.length = length;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
