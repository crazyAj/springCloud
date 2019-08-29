package com.example.demo;

import com.example.demo.dao.test.autowired.dao.BaseDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAutowired {

    @Autowired
    private BaseDao baseDao;

    /**
     * --- test --- com.aj.demo.dao.impl.ADaoImpl
     * --- ADaoImpl --- com.aj.demo.dao.impl.BDaoImpl
     * --- BDaoImpl --- com.aj.demo.dao.impl.ADaoImpl
     */
    @Test
    public void t() {
        System.out.println("--- test --- " + baseDao.getClass().getName());
        baseDao.print();
    }

}
