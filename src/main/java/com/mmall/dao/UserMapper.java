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

    int checkUsername(String username);

    int checkEmail(String email);
    //mybatis传递多个参数的时候，注解里面的命名和sql里面的相对应，不写的话就用后边个
    User selectLogin(@Param("username") String username, @Param("password")String password);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);

    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);
}