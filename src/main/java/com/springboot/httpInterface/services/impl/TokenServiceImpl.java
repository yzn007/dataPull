package com.springboot.httpInterface.services.impl;

import com.springboot.httpInterface.dao.TokenMapper;
import com.springboot.httpInterface.entity.Token;
import com.springboot.httpInterface.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yzn00 on 2019/7/11.
 */
@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    TokenMapper tokenMapper;
    @Override
    public List<Token> getAllToken() {
        return tokenMapper.getAllToken();
    }
}
