package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

/**
 * 购物车服务实现类
 * @author Ly
 *
 */
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		//1.根据商品 SKU ID 查询 SKU 商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if (item == null) {
			throw new RuntimeException("商品不存在");
		}
		if (!"1".equals(item.getStatus())) {
			throw new RuntimeException("无效的商品");
		}
		//2.获取商家 ID
		String sellerId = item.getSellerId();
		//3.根据商家 ID 判断购物车列表中是否存在该商家的购物车
		Cart cart = searchCartBySellerId(cartList, sellerId);
		//4.如果购物车列表中不存在该商家的购物车
		if (cart == null) {
			//4.1 新建购物车对象
			cart = new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());
			TbOrderItem orderItem = createOrderItem(item, num);
			List<TbOrderItem> orderItemList = new ArrayList<>();
			orderItemList.add(orderItem);
			cart.setOrderItemList(orderItemList);
			//4.2 将新建的购物车对象添加到购物车列表
			cartList.add(cart);
		} else {
			//5.如果购物车列表中存在该商家的购物车
			// 查询购物车明细列表中是否存在该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
			if (orderItem == null) {
				//5.1. 如果没有，新增购物车明细
				orderItem = createOrderItem(item, num);
				cart.getOrderItemList().add(orderItem);
			}else {
				//5.2. 如果有，在原购物车明细上添加数量，更改金额
				orderItem.setNum(orderItem.getNum()+num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
				
				//如果数量操作后小于等于0,则移除
				if (orderItem.getNum() <= 0) {
					//移除购物车明细
					cart.getOrderItemList().remove(orderItem);
				}
				
				//如果移除后 cart 的明细数量为 0，则将 cart 移除
				if (cart.getOrderItemList().size() == 0) {
					cartList.remove(cart);
				}
			}
		}
		return cartList;
	}
	
	/**
	 * 根据商品明细Id查询
	 * @param orderItemList
	 * @param itemId
	 * @return
	 */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
		
		for (TbOrderItem tbOrderItem : orderItemList) {
			if (tbOrderItem.getItemId().longValue() == itemId.longValue()) {
				return tbOrderItem;
			}
		}
		return null;
	}

	/**
	 * 根据商家id查询购物车对象
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart searchCartBySellerId(List<Cart> cartList,String sellerId) {
		
		for (Cart cart : cartList) {
			if (cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		
		return null;
	}
	
	/**
	 * 创建订单明细
	 * @param item
	 * @param num
	 * @return
	 */
	private TbOrderItem createOrderItem(TbItem item,Integer num) {
		if (num <= 0) {
			throw new RuntimeException("商品数量无效");
		}
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return orderItem;
	}

	/**
	 * 从redis中获取数据
	 */
	@Override
	public List<Cart> findCartListFromRedis(String username) {
		System.out.println("从redis中获取数据");
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		if (cartList == null) {
			cartList = new ArrayList<>();
		}
		return cartList;
	}
	
	/**
	 * 向redis添加数据
	 */
	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		System.out.println("向redis添加数据");
		redisTemplate.boundHashOps("cartList").put(username, cartList);
		
	}
	
	/**
	 * 合并购物车
	 */
	@Override
	public List<Cart> mergeCarList(List<Cart> cartList1, List<Cart> cartList2) {
		
		for (Cart cart : cartList2) {
			for(TbOrderItem orderItem :cart.getOrderItemList()) {
				cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(),orderItem.getNum());
			}
		}
		return cartList1;
	}
}
