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
	 * 分页查询
	 * @param page
	 * @param size
	 * @return
	 */
	PageResult findPage(int page, int size);
	
	/**
	 * 条件查询
	 * @param tbBrand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

	/**
	 * 添加品牌
	 * @param brand
	 */
	void add(TbBrand brand);

	/**
	 * 修改品牌信息
	 * @param tbBrand
	 */
	void update(TbBrand tbBrand);

	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	TbBrand findById(long id);

	/**
	 * 根据id批量删除品牌
	 * @param ids
	 */
	void deleteByIds(String[] ids);

	
}
