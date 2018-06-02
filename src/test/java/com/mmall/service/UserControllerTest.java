package com.mmall.service;

import com.mmall.Basic;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;



public class UserControllerTest extends Basic{


    @Autowired
    IUserService iUserService;

    @Test
    public void testCheckValid() {
        iUserService.valicate("admin", "username");
    }
}
