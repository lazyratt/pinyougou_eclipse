package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout=6000)
	private CartService cartService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	/**
	 * 购物车列表
	 * 
	 * @return
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {

		// 获取登录人账号
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(username);
		
		//获取cookie中的数据
		String cartListString = util.CookieUtil.getCookieValue(request, "cartList", "utf-8");

		if (cartListString == null || cartListString.equals("")) {
			cartListString = "[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
		//如果是未登录状态
		if (username.equals("anonymousUser")) {
			return cartList_cookie;
		}else {
			//登录状态,返回redis中的值
			List<Cart> cartList = cartService.findCartListFromRedis(username);
			//如果存在cookie，则合并数据
			if (cartList_cookie.size()>0) {
				cartList = cartService.mergeCarList(cartList_cookie, cartList);
			}
			//保存到redis中
			cartService.saveCartListToRedis(username, cartList);
			//清空缓存
			util.CookieUtil.deleteCookie(request, response, "cartList");
			return cartList;
		}
		
	}

	/**
	 * 添加商品到购物车
	 * 
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodsToCartList(Long itemId, Integer num) {
		try {
			// 获取登录人账号
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			List<Cart> cartList = findCartList();
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			//未登录状态
			if (username.equals("anonymousUser")) {
				util.CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "utf-8");
			}else {
				//登录状态,存入redis中
				cartService.saveCartListToRedis(username, cartList);
			}
			
			return new Result(true, "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}

	}
}
