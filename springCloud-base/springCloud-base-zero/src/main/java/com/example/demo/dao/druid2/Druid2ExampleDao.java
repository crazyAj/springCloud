package com.example.demo.dao.druid2;

import com.example.demo.domain.Example;

import java.util.List;

public interface Druid2ExampleDao {

    void insertSelective(Example example);

    void deleteById(String id);

    void delete(Example example);

    void updateById(Example example);

    List<Example> find(Example example);

    Example getByPrimaryKey(String id);

    List<Example> findAll();

    Integer getCount(Example example);
}
