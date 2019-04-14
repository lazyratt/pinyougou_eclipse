package com.pinyougou.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;

	@RequestMapping("findAll")
	public List<TbBrand> findAll() {
		System.out.println("findAll方法执行了");
		return brandService.findAll();
	}

	/**
	 * 分页查询
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("findPage")
	public PageResult findPage(int page, int size) {
		System.out.println("findPage方法执行了");
		return brandService.findPage(page, size);
	}
	
	@RequestMapping("/save")
	public Result save(@RequestBody TbBrand brand) {
		try {
			brandService.add(brand);
			return new Result(true,"添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败");
		}
	}
	
}
