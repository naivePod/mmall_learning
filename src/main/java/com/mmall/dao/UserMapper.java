package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int updateByUsernameAndPassword(@Param("username") String username, @Param("password") String password, @Param("newPassword") String newPassword);

    int checkUser(String username);

    User selectUser(@Param("username") String username,@Param("password") String password);

    int selectEmail(String email);

    User selectUserByUsername(String username);

    int selectUserByUsernameAndAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);

    String getQuestionByUsername(String username);

    int updatePasswordByUsername(@Param("username") String username,@Param("passwordNew") String passwordNew);

    int checkEmailByUserId(@Param("userId") Integer userId,@Param("email") String email);
}