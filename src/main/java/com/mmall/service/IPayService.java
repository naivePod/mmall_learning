package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

public interface IPayService {
    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse aliCallback(Map<String, String> params);

    ServerResponse queryPayStatus(Long orderNo);
}
