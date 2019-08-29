package com.example.demo.dao.test.autowired.dao.impl;

import com.example.demo.dao.test.autowired.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BDaoImpl implements BaseDao {
    @Autowired
    private BaseDao baseDao;

    @Override
    public void print() {
        System.out.println("--- BDaoImpl --- " + baseDao.getClass().getName());
    }
}
