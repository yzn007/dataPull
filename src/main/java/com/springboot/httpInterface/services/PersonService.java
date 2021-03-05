package com.springboot.httpInterface.services;

import com.springboot.httpInterface.entity.Person;

import java.util.List;

/**
 * Created by yzn00 on 2019/6/19.
 */

public interface PersonService {
    public int insertPerson();
    public int updatePerson(Person person);
    public List<Person>selectAllPerson();
}
