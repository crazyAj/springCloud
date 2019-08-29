package com.example.demo.service.impl;

import com.example.demo.dao.druid.DruidExampleDao;
import com.example.demo.dao.druid2.Druid2ExampleDao;
import com.example.demo.dao.hikari.HikariExampleDao;
import com.example.demo.domain.Example;
import com.example.demo.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "xaTX", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
public class ExampleServiceImpl implements ExampleService {

    @Autowired
    private HikariExampleDao hikariExampleDao;
    @Autowired
    private DruidExampleDao druidExampleDao;
    @Autowired
    private Druid2ExampleDao druid2ExampleDao;

    //////////////// HikariCP ////////////////
    @Override
    public void insertSelective(Example example) {
        hikariExampleDao.insertSelective(example);
    }

    @Override
    public void deleteById(String id) {
        hikariExampleDao.deleteById(id);
    }

    @Override
    public void delete(Example example) {
        hikariExampleDao.delete(example);
    }

    @Override
    public void updateById(Example example) {
        hikariExampleDao.updateById(example);
    }

    @Override
    public List<Example> find(Example example) {
        return hikariExampleDao.find(example);
    }

    @Override
    public Example getByPrimaryKey(String id) {
        return hikariExampleDao.getByPrimaryKey(id);
    }

    @Override
    public List<Example> findAll() {
        return hikariExampleDao.findAll();
    }

    @Override
    public Integer getCount(Example example) {
        return hikariExampleDao.getCount(example);
    }

    @Override
    @Transactional(transactionManager = "transactionManager_hikari", propagation = Propagation.REQUIRED)
    public void testTransaction() {
        Example e1 = new Example();
        e1.setId("6");
        e1.setEmpKey("testTransaction1 hikariExampleDao");
        hikariExampleDao.updateById(e1);

        Example e2 = new Example();
        e2.setId("7");
        e2.setEmpKey("testTransaction2 hikariExampleDao");
        hikariExampleDao.updateById(e2);

        String s = null;
        s.length();
    }

    //////////////// DRUID ////////////////
    @Override
    public void insertSelectiveDruid(Example example) {
        druidExampleDao.insertSelective(example);
    }

    @Override
    public void deleteByIdDruid(String id) {
        druidExampleDao.deleteById(id);
    }

    @Override
    public void deleteDruid(Example example) {
        druidExampleDao.delete(example);
    }

    @Override
    public void updateByIdDruid(Example example) {
        druidExampleDao.updateById(example);
    }

    @Override
    public List<Example> findDruid(Example example) {
        return druidExampleDao.find(example);
    }

    @Override
    public Example getByPrimaryKeyDruid(String id) {
        return druidExampleDao.getByPrimaryKey(id);
    }

    @Override
    public List<Example> findAllDruid() {
        return druidExampleDao.findAll();
    }

    @Override
    public Integer getCountDruid(Example example) {
        return druidExampleDao.getCount(example);
    }

    @Override
    public void testTransactionDruid() {
        Example e1 = new Example();
        e1.setId("6");
        e1.setEmpKey("testTransaction1 druidExampleDao");
        druidExampleDao.updateById(e1);

        Example e2 = new Example();
        e2.setId("7");
        e2.setEmpKey("testTransaction11 druidExampleDao");
        druidExampleDao.updateById(e2);

        String s = null;
        s.length();
    }

    //////////////// DRUID2 ////////////////
    @Override
    public void insertSelectiveDruid2(Example example) {
        druid2ExampleDao.insertSelective(example);
    }

    @Override
    public void deleteByIdDruid2(String id) {
        druid2ExampleDao.deleteById(id);
    }

    @Override
    public void deleteDruid2(Example example) {
        druid2ExampleDao.delete(example);
    }

    @Override
    public void updateByIdDruid2(Example example) {
        druid2ExampleDao.updateById(example);
    }

    @Override
    public List<Example> findDruid2(Example example) {
        return druid2ExampleDao.find(example);
    }

    @Override
    public Example getByPrimaryKeyDruid2(String id) {
        return druid2ExampleDao.getByPrimaryKey(id);
    }

    @Override
    public List<Example> findAllDruid2() {
        return druid2ExampleDao.findAll();
    }

    @Override
    public Integer getCountDruid2(Example example) {
        return druid2ExampleDao.getCount(example);
    }

    @Override
    public void testTransactionDruid2() {
        Example e1 = new Example();
        e1.setId("6");
        e1.setEmpKey("testTransaction2 druid2ExampleDao");
        druid2ExampleDao.updateById(e1);

        Example e2 = new Example();
        e2.setId("7");
        e2.setEmpKey("testTransaction22 druid2ExampleDao");
        druid2ExampleDao.updateById(e2);

        String s = null;
        s.length();
    }

    //////////////// Mix混合事务，独立提交 ////////////////
    @Override
    public void testTransactionMix() {
        Example e1 = new Example();
        e1.setId("6");
        e1.setEmpKey("testTransaction1 hikariExampleDao");
        hikariExampleDao.updateById(e1);

        Example e2 = new Example();
        e2.setId("7");
        e2.setEmpKey("testTransaction2 druidExampleDao");
        druidExampleDao.updateById(e2);

        String s = null;
        s.length();

    }

    //////////////// atomikos分布式事务管理 ////////////////
    @Override
    public void testTransactionAtomikos() {
        Example e1 = new Example();
        e1.setId("6");
        e1.setEmpKey("testTransaction1 druidExampleDao");
        druidExampleDao.updateById(e1);

        Example e2 = new Example();
        e2.setId("7");
        e2.setEmpKey("testTransaction2 druid2ExampleDao");
        druid2ExampleDao.updateById(e2);

        String s = null;
        s.length();

    }

}
