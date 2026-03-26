package com.jq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jq.dto.Result;
import com.jq.entity.Shop;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IShopService extends IService<Shop> {


    Result queryById(Long id);

    Object updateShop(Shop shop);

    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
