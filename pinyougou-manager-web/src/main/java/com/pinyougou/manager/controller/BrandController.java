package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.*;
import entity.*;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;

	/**
	 * 查询所有品牌信息
	 * @return
	 */
	@RequestMapping("findAll")
	public List<TbBrand> findAll() {
		System.out.println("findAll方法执行了");
		return brandService.findAll();
	}

	/**
	 * 分页查询品牌信息
	 * @param page 当前页码数
	 * @param size 当前页码显示的条数
	 * @return
	 */
	@RequestMapping("findPage")
	public PageResult findPage(int page, int size) {
		System.out.println("findPage方法执行了");
		return brandService.findPage(page, size);
	}
	
	/**
	 * 添加品牌信息
	 * @param brand 品牌信息
	 * @return
	 */
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
	
	@RequestMapping("/findById")
	public TbBrand findById(long id) {
		System.out.println("findById执行了         id:"+id);
		return brandService.findById(id);
	}
	
	/**
	 * 修改品牌信息
	 * @param tbBrand
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand tbBrand) {
		try {
			brandService.update(tbBrand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	
	
	@RequestMapping("/delete")
	public Result delete(String[] ids) {
		try {
			brandService.deleteByIds(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	
	
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand brand,int page, int size) {
		System.out.println("findPage方法执行了"+brand);
		return brandService.findPage(brand,page, size);
	}
	
	
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}
}
