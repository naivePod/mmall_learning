package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;

import jdk.nashorn.internal.parser.Token;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount;
        resultCount = userMapper.checkUser(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMsg("用户名不存在");
        }
        //todo 密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectUser(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMsg("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User registerInfo) {
        ServerResponse<String> response =  valicate(registerInfo.getUsername(), Const.USER_NAME);
        if(!response.isSuccess()) {
            return response;
        }
        //todo 注册信息校验,封装成方法
        response =  valicate(registerInfo.getEmail(), Const.EMAIL);
        if(!response.isSuccess()) {
            return response;
        }
        response = valicate(registerInfo.getPhone(), Const.PHONE);
        if(!response.isSuccess()) {
            return response;
        }

        registerInfo.setRole(Const.Role.ROLE_CUSTOMER);
        registerInfo.setPassword(MD5Util.MD5EncodeUtf8(registerInfo.getPassword()));
        int checkCount = userMapper.insert(registerInfo);

        if(checkCount == 0) {
            return ServerResponse.createByErrorMsg("注册失败");
        }

        return ServerResponse.createBySuccessMsg("注册成功");
    }


    @Override
    public ServerResponse<String> valicate(String str, String type) {
        //为了实时校验
        if(StringUtils.isNotBlank(str)) {
            if(StringUtils.equals(type, Const.USER_NAME)) {
                int checkCount = userMapper.checkUser(str);
                if(checkCount > 0) {
                    return ServerResponse.createByErrorMsg("用户名已存在");
                }
            } else if(StringUtils.equals(type, Const.EMAIL)) {
                int checkCount = userMapper.selectEmail(str);
                if(checkCount > 0) {
                    return ServerResponse.createByErrorMsg("邮箱已存在");
                }
            }

        } else {
            return ServerResponse.createByErrorMsg("参数错误");
        }
        return ServerResponse.createBySuccessMsg("校验成功");
    }

    @Override
    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, String username) {
        String md5Password = MD5Util.MD5EncodeUtf8(oldPassword);
        User user = userMapper.selectUser(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMsg("密码错误");
        }
        String newMd5Password = MD5Util.MD5EncodeUtf8(newPassword);
        int checkCount = userMapper.updateByUsernameAndPassword(username, md5Password, newMd5Password);
        if(checkCount == 0) {
            return ServerResponse.createByErrorMsg("重置密码失败");
        }

        return ServerResponse.createBySuccessMsg("重置密码成功");
    }

    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {
        ServerResponse<String> validResponse = this.valicate(username, Const.USER_NAME);
        if(validResponse.isSuccess()) return ServerResponse.createByErrorMsg("用户名不存在");
        else if(!validResponse.isSuccess() && validResponse.getMsg().equals("参数错误")) return validResponse;
        String question = userMapper.getQuestionByUsername(username);

        return ServerResponse.createBySuccess(question);
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int checkCount = userMapper.selectUserByUsernameAndAnswer(username, question, answer);
        if(checkCount == 0) return ServerResponse.createByErrorMsg("答案错误");

        String forgetToken = UUID.randomUUID().toString();
        TokenCache.setKey(TokenCache.FORGET_TOKEN+username, forgetToken);
        return ServerResponse.createBySuccess(forgetToken);
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(username)) {
            return ServerResponse.createByErrorMsg("用户名不为空");
        }
        ServerResponse<String> validResponse = this.valicate(username, Const.USER_NAME);
        if(validResponse.isSuccess()) {
            return ServerResponse.createByErrorMsg("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.FORGET_TOKEN+username);
        if(StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMsg("token无效或过期");
        }
        if(StringUtils.equals(token, forgetToken)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
            if(resultCount > 0) {
                return ServerResponse.createBySuccessMsg("重置密码成功");
            }
        } else {
            return ServerResponse.createByErrorMsg("错误的token，重新传送参数");
        }

        return ServerResponse.createByErrorMsg("重置密码失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
        if(resultCount > 0) {
            return ServerResponse.createByErrorMsg("email已存在");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultCount>0) return ServerResponse.createBySuccess("更新成功",user);

        return ServerResponse.createByErrorMsg("更新失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null) return ServerResponse.createByErrorMsg("用户不存在");

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if(user != null && user.getRole().equals(Const.Role.ROLE_ADMIN)) {
            return ServerResponse.createBySuccess();
        }
        Map map = new HashMap<>();
        return ServerResponse.createByErrorMsg("无权限，无法修改");
    }
}
