package com.example.demo.dao.test.autowired.dao.impl;

import com.example.demo.dao.test.autowired.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class ADaoImpl implements BaseDao {
    @Autowired
    private BaseDao baseDao;

    @Override
    public void print() {
        System.out.println("--- ADaoImpl --- " + baseDao.getClass().getName());
        baseDao.print();
    }
}
