package com.example.demo.dao.hikari;

import com.example.demo.domain.Example;

import java.util.List;

public interface HikariExampleDao {

    void insertSelective(Example example);

    void deleteById(String id);

    void delete(Example example);

    void updateById(Example example);

    List<Example> find(Example example);

    Example getByPrimaryKey(String id);

    List<Example> findAll();

    Integer getCount(Example example);
}
