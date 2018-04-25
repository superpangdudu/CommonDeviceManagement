package com.krly.dao.mapper;

import com.krly.api.order.Order;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);
    int insert(Order record);
    int insertSelective(Order record);
    Order selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Order record);
    int updateByPrimaryKey(Order record);

    Order selectByToken(String token);
}