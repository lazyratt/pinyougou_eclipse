package com.pinyougou.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

/**
 * 品牌接口
 * @author Ly
 *
 */
public interface BrandService {

	/**
	 * 查询所有品牌信息
	 * @return 所有品牌信息
	 */
	List<TbBrand> findAll();
	
	/**
	 * 
	 * @param pageNum 当前页码数
	 * @param pageSize 页码显示条数
	 * @return
	 */
	PageResult findPage(int pageNum,int pageSize);

	/**
	 * 添加品牌
	 * @param brand
	 */
	void add(TbBrand brand);
}
