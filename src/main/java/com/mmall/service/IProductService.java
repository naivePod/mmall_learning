package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse<String> saveProduct(Product product);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> searchProducts(String productName, Integer productId, Integer pageNum, Integer pageSize);

    ServerResponse<ProductDetailVo> getDetail(Integer productId);

    ServerResponse<PageInfo> getListByKeywordCategoryId(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);
}
