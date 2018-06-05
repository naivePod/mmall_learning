package com.mmall.service;


import com.mmall.Basic;
import com.mmall.common.ServerResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CategoryServiceTest extends Basic {

    @Autowired
    ICategoryService iCategoryService;
    @Test
    public void testGetDeepCategory() {

        ServerResponse<List<Integer>> response = iCategoryService.getDeepCategory(100001);
        List<Integer> list = response.getData();
        if(list != null) {
            System.out.println(list);
        }
    }
}
