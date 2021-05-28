package com.hrbeu.service.admin.Impl;

import com.hrbeu.dao.admin.AdminDocumentDao;
import com.hrbeu.dao.admin.AdminTypeDao;
import com.hrbeu.pojo.Type;
import com.hrbeu.pojo.vo.Type_Count;
import com.hrbeu.service.admin.AdminTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class AdminTypeServiceImpl implements AdminTypeService {
    @Autowired
    private AdminTypeDao adminTypeDao;
    @Autowired
    private AdminDocumentDao adminDocumentDao;
    @Override
    public List<Type> queryAllTypes() {
        return adminTypeDao.queryAllTypes();
    }

    @Override
    public Type queryTypeById(Long typeId) {
        return adminTypeDao.queryTypeById(typeId);
    }

    @Override
    public int queryAllTypeCount() {
        return adminTypeDao.queryAllTypeCount();
    }

    @Override
    public List<Type> queryTypeListByPage(int pageIndex, int pageSize) {
        int begin = pageSize*(pageIndex-1);
        return adminTypeDao.queryTypeListByPage(begin,pageSize);
    }

    @Override
    public List<Type_Count> queryTypeCountListByPage(int pageIndex, int pageSize) {
        int begin = pageSize*(pageIndex-1);
        List<Type> typeList = adminTypeDao.queryTypeListByPage(begin,pageSize);
        List<Type_Count> typeCountList = new ArrayList<>();
        for (Type type : typeList) {
            Type_Count type_count = new Type_Count();
            type_count.setTypeId(type.getTypeId());
            type_count.setTypeName(type.getTypeName());
            Integer count = adminDocumentDao.queryTypeCountInDocuments(type.getTypeId());
            if(count==null){
                count = 0;
            }
            type_count.setCount(count);
            typeCountList.add(type_count);
        }
        return typeCountList;
    }

    @Override
    public void updateTypeName(Long typeId, String typeName) {
        adminTypeDao.updateTypeName(typeId,typeName);
    }

    @Override
    public void deleteTypeById(Long typeId) {
        adminTypeDao.deleteTypeById(typeId);
    }

    @Override
    public Integer queryTypeCountById(Long typeId) {
        return adminDocumentDao.queryTypeCountInDocuments(typeId);
    }

    @Override
    public void addType(String name) {
        adminTypeDao.addType(name);
    }
}
