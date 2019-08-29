package com.example.demo.service;

import com.example.demo.domain.Example;

import java.util.List;

public interface ExampleService {
    //////////////// HikariCP ////////////////
    void insertSelective(Example example);
    void deleteById(String id);
    void delete(Example example);
    void updateById(Example example);
    List<Example> find(Example example);
    Example getByPrimaryKey(String id);
    List<Example> findAll();
    Integer getCount(Example example);
    void testTransaction();
    //////////////// DRUID ////////////////
    void insertSelectiveDruid(Example example);
    void deleteByIdDruid(String id);
    void deleteDruid(Example example);
    void updateByIdDruid(Example example);
    List<Example> findDruid(Example example);
    Example getByPrimaryKeyDruid(String id);
    List<Example> findAllDruid();
    Integer getCountDruid(Example example);
    void testTransactionDruid();
    //////////////// DRUID2 ////////////////
    void insertSelectiveDruid2(Example example);
    void deleteByIdDruid2(String id);
    void deleteDruid2(Example example);
    void updateByIdDruid2(Example example);
    List<Example> findDruid2(Example example);
    Example getByPrimaryKeyDruid2(String id);
    List<Example> findAllDruid2();
    Integer getCountDruid2(Example example);
    void testTransactionDruid2();
    //////////////// Mix混合事务，独立提交 ////////////////
    void testTransactionMix();
    //////////////// atomikos分布式事务管理 ////////////////
    void testTransactionAtomikos();
}
