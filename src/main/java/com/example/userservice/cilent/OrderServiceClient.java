package com.example.userservice.cilent;

import com.example.userservice.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    // interface에서는 어차피 public이라 안붙여도 ㄱㅊ
    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrder(@PathVariable("userId") String userId);
}
