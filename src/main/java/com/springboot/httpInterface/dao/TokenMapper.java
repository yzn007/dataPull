package com.springboot.httpInterface.dao;

import com.springboot.httpInterface.entity.Token;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yzn00 on 2019/6/19.
 */
@Repository
@Mapper
public interface TokenMapper {
     List<Token> getAllToken();
}
