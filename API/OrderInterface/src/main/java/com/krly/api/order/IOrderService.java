package com.krly.api.order;

/**
 * Created by Administrator on 2018/4/19.
 */
public interface IOrderService {
    Order create(int userId, int projectId, int productId, int cents, String description);
    int setPaid(int id, boolean isPaid);
    int delete(int id);
    boolean isPaid(int id);
    boolean isPaid(String token);
    Order get(int id);
    Order get(String token);
}
