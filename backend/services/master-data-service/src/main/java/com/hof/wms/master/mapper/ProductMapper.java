package com.hof.wms.master.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hof.wms.master.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT * FROM product WHERE status = 'on' ORDER BY updated_at DESC")
    List<Product> findActiveProducts();

    @Select("SELECT * FROM product WHERE sku_code = #{skuCode}")
    Product findBySkuCode(@Param("skuCode") String skuCode);
}
