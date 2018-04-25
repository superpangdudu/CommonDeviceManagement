package com.krly.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.krly.api.order.IOrderService;
import com.krly.api.order.Order;
import com.krly.dao.mapper.OrderMapper;
import com.krly.utils.common.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order create(int userId, int projectId, int productId, int cents, String description) {
        Order order = new Order();
        order.setUserId(userId);
        order.setProjectId(projectId);
        order.setProductId(productId);
        order.setCents(cents);
        order.setDescription(description);
        order.setCreateTime(new Date());

        String token = RandomUtil.getRandomString(32);
        order.setToken(token);

        //
        orderMapper.insert(order);
        return order;
    }

    @Override
    public int setPaid(int id, boolean isPaid) {
        return 0;
    }

    @Override
    public int delete(int id) {
        return 0;
    }

    @Override
    public boolean isPaid(int id) {
        return false;
    }

    @Override
    public boolean isPaid(String token) {
        return false;
    }

    @Override
    public Order get(int id) {
        return null;
        //return orderMapper.selectByPrimaryKey(id);
    }

    @Override
    public Order get(String token) {
        return orderMapper.selectByToken(token);
    }
}
