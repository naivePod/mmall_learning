package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping(value = "save.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> save(Product product, HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.createByErrorMsg("用户未登录");
        //check if user is admin

        if(!iUserService.checkAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMsg("没有权限");
        }

        return iProductService.saveProduct(product);
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId, HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.createByErrorMsg("用户未登录");
        //check if user is admin
        if(!iUserService.checkAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMsg("没有权限");
        }

        return iProductService.getProductDetail(productId);

    }

    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status, HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.createByErrorMsg("用户未登录");
        //check if user is admin
        if(!iUserService.checkAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMsg("没有权限");
        }

        return iProductService.setSaleStatus(productId, status);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> getList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.createByErrorMsg("用户未登录");
        //check if user is admin
        if(!iUserService.checkAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMsg("没有权限");
        }
        return iProductService.getProductList(pageNum, pageSize);
    }

    @RequestMapping(value = "search.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(@RequestParam(value = "productName",required = false)String productName,@RequestParam(value = "productId",required = false) Integer productId,@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) return ServerResponse.createByErrorMsg("用户未登录");
        //check if user is admin
        if(!iUserService.checkAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMsg("没有权限");
        }

        return iProductService.searchProducts(productName, productId, pageNum, pageSize);
    }

    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath("upload");

        String targetFileName = iFileService.upload(path, file);

        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        Map map = Maps.newHashMap();
        map.put("uri", targetFileName);
        map.put("url", url);
        return ServerResponse.createBySuccess(map);
    }
}
