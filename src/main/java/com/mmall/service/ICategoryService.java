package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ICategoryService {

    ServerResponse<List<Category>> getCategory(Integer categoryId);

    ServerResponse<String> addCategory(Integer parentId, String categoryName);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Integer>> getDeepCategory(Integer categoryId);
}
