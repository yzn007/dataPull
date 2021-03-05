package com.springboot.httpInterface.services.impl;

import com.springboot.httpInterface.entity.Person;
import com.springboot.httpInterface.dao.PersonMapper;
import com.springboot.httpInterface.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yzn00 on 2019/6/19.
 */
@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonMapper personMapper;
    @Override
    public int insertPerson() {
        return personMapper.insertPerson();
    }

    @Override
    public int updatePerson(Person person) {
        return personMapper.updatePerson(person);
    }

    @Override
    public List<Person> selectAllPerson() {
        return personMapper.selectAllPerson();
    }
}
