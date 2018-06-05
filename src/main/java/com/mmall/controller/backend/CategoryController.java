package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping(value = "/manage/category/")
public class CategoryController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(Integer categoryId, HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录");
        ServerResponse response = userService.checkAdminRole(user);
        if(!response.isSuccess()) {
            return response;
        }
        return iCategoryService.getCategory(categoryId);
    }

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addCategory(@RequestParam(value = "parentId", defaultValue = "0") int parentId, String categoryName, HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录");
        ServerResponse response = userService.checkAdminRole(user);
        if(!response.isSuccess()) {
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
        }
        return iCategoryService.addCategory(parentId, categoryName);
    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setCategoryName(Integer categoryId, String categoryName, HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录");
        ServerResponse response = userService.checkAdminRole(user);
        if(!response.isSuccess()) {
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
        }
        return iCategoryService.updateCategoryName(categoryId, categoryName);
    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId, HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "该用户未登录");
        ServerResponse response = userService.checkAdminRole(user);
        if(!response.isSuccess()) {
            return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
        }

        return iCategoryService.getDeepCategory(categoryId);
    }
}
