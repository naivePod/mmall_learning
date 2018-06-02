package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User registerInfo);

    ServerResponse<String> valicate(String str, String type);

    ServerResponse<String> resetPassword(String oldPassword, String newPassword, String username);

    ServerResponse<String> forgetGetQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);
}
