package com.pinyougou.sellergoods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	/**
	 * 查询所有品牌信息
	 */
	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}
	
	/**
	 * 查询当前页的数据
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
		return pageResult;
	}

	/**
	 * 添加品牌
	 */
	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);	
	}

	/**
	 * 修改品牌
	 */
	@Override
	public void update(TbBrand tbBrand) {
		brandMapper.updateByPrimaryKey(tbBrand);
		
	}

	/**
	 * 根据id查询
	 */
	@Override
	public TbBrand findById(long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	/**
	 * 删除
	 */
	@Override
	public void deleteByIds(String[] ids) {
		for (String idStr : ids) {
			long id = Long.parseLong(idStr);
			brandMapper.deleteByPrimaryKey(id);
		}
	}

}
