package com.mmall.service.impl;

import com.google.common.collect.Sets;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("ICategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse<List<Category>> getCategory(Integer categoryId) {
        //default 0
        if(categoryId == null) categoryId = 0;

        List<Category> list = categoryMapper.selectByParentId(categoryId);
        if(list.size() == 0) {
            logger.error("未找到该品类");
            return ServerResponse.createByErrorMsg("未找到该品类");
        }

        return ServerResponse.createBySuccess(list);
    }

    @Override
    public ServerResponse<String> addCategory(Integer parentId, String categoryName) {
        ServerResponse<Category> response = this.checkValid(parentId, Const.PARENT_ID);
        //check if parentId is alive
        if(response.isSuccess()) return ServerResponse.createByErrorMsg("父目录id不存在");
        //check if categoryName is alive
        ServerResponse<String> validResponse = this.checkValid(categoryName, Const.CATEGORY_NAME);
        if(!validResponse.isSuccess()) return validResponse;

        Category categoryNew = new Category();
        categoryNew.setParentId(parentId);
        categoryNew.setName(categoryName);
        categoryNew.setStatus(true);

        int resultCount = categoryMapper.insert(categoryNew);
        if(resultCount == 0) return ServerResponse.createByErrorMsg("添加品类失败");

        return ServerResponse.createBySuccessMsg("添加品类成功");

    }


    public ServerResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNotBlank(str)) {
            if(StringUtils.equals(type, Const.CATEGORY_NAME)) {
                int resultCount = categoryMapper.checkCategoryName(str);
                if(resultCount > 0) return ServerResponse.createByErrorMsg("目录名已存在");
            }
        } else {
            return ServerResponse.createByErrorMsg("参数错误");
        }

        return ServerResponse.createBySuccessMsg("校验成功");
    }


    public ServerResponse<Category> checkValid(Integer i, String type) {
        if(StringUtils.equals(type, Const.PARENT_ID)) {
            if(i == null || i == 0) return ServerResponse.createByErrorMsg("parentId为0");
            Category category = categoryMapper.selectByPrimaryKey(i);
            if(category != null) return ServerResponse.createByErrorMsg("目录已存在");
        } else if(StringUtils.equals(type, Const.CATEGORY_ID)) {
            Category category = categoryMapper.selectByPrimaryKey(i);
            if(category != null) return ServerResponse.createByError(category);
        }

        return ServerResponse.createBySuccessMsg("校验成功");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        ServerResponse<Category> validResponse = this.checkValid(categoryId, Const.CATEGORY_ID);
        if(validResponse.isSuccess()) return ServerResponse.createByErrorMsg("目录不存在");
        Category category = validResponse.getData();
        ServerResponse<String> response = checkValid(categoryName, Const.CATEGORY_NAME);
        if(!response.isSuccess()) return ServerResponse.createByErrorMsg("目录名重复");
        category.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(resultCount > 0) return ServerResponse.createBySuccessMsg("更新成功");

        return ServerResponse.createByErrorMsg("更新失败");
    }

    @Override
    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();

        helper(categoryId, categorySet);
        List<Integer> integerList = new ArrayList<>();
        for(Category categoryItem : categorySet) {
            integerList.add(categoryItem.getId());
        }

        return ServerResponse.createBySuccess(integerList);
    }

    private void helper(Integer categoryId, Set<Category> categorySet) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null) {
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectByParentId(category.getId());
        for(Category categoryItem : categoryList) {
            helper(categoryItem.getId(), categorySet);
        }

    }
}
