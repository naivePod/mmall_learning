package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> saveProduct(Product product) {
        if(product == null) return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        String[] imgs = product.getSubImages().split(",");
        if(imgs != null && imgs.length > 0) product.setMainImage(imgs[0]);

        Product productOld = productMapper.selectByPrimaryKey(product.getId());
        // check if product is exist.if not,insert new product;or update product.
        if(product.getId() == null || productOld == null) {
            int resultCount = productMapper.insert(product);
            if(resultCount > 0) return ServerResponse.createBySuccessMsg("添加产品成功");
            else return ServerResponse.createByErrorMsg("添加产品失败");
        } else {
            int resultCount = productMapper.updateByPrimaryKeySelective(product);
            if(resultCount > 0) return ServerResponse.createBySuccessMsg("更新产品成功");
            else return ServerResponse.createByErrorMsg("更新产品失败");
        }

    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if(productId == null || status == null) return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Product updateProduct = new Product();
        updateProduct.setId(productId);
        updateProduct.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(updateProduct);
        if(rowCount > 0) return ServerResponse.createBySuccessMsg("设置状态成功");

        return ServerResponse.createByErrorMsg("设置状态失败");

    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if(productId == null) return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) return ServerResponse.createByErrorMsg("该产品不存在");
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setParentCategoryId(product.getCategoryId());
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectProducts();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();

        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        productListVo.setPrice(product.getPrice());

        return productListVo;
    }

    @Override
    public ServerResponse<PageInfo> searchProducts(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        if(productName != null) productName = new StringBuilder().append("%").append(productName).append("%").toString();
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectProductByNameAndId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVo> getDetail(Integer productId) {
        if(productId == null) return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null || product.getStatus() != Const.ProductStatus.ON_SALE.getStatus()) {
            return ServerResponse.createByErrorMsg("商品不存在或已下架");
        }

        ProductDetailVo productDetailVo = this.assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getListByKeywordCategoryId(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        if(StringUtils.isBlank(keyword) && categoryId == null) return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        List<Integer> categoryIds = Lists.newArrayList();

        if(categoryId != null) {
            ServerResponse<List<Integer>> response = iCategoryService.getDeepCategory(categoryId);
            categoryIds = response.getData();
            if(StringUtils.isBlank(keyword) && (categoryIds==null || categoryIds.size() == 0)) {
                List<ProductDetailVo> productDetailVos = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productDetailVos);
                return ServerResponse.createBySuccess(pageInfo);
            }

        }

        if(StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        if(StringUtils.isNotBlank(orderBy)) {
            if(Const.OrderBy.PRICE_DESC_ASC.contains(orderBy)) {
                String[] strs = orderBy.split("_");
                orderBy = new StringBuilder().append(strs[0]).append(" ").append(strs[1]).toString();
            } else {
                orderBy = "";
            }
        }
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy(orderBy);
        List<Product> productList = productMapper.selectProductsByKeywordAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword, categoryIds.size() == 0?null:categoryIds);
        List<ProductDetailVo> productDetailVos = Lists.newArrayList();
        for(Product product : productList) {
            ProductDetailVo productDetailVo = this.assembleProductDetailVo(product);
            productDetailVos.add(productDetailVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productDetailVos);

        return ServerResponse.createBySuccess(pageInfo);

    }
}
