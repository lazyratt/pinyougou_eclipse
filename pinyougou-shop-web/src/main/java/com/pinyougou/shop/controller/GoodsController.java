package com.pinyougou.shop.controller;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	
	@Reference
	private ItemSearchService itemSearchService;
	
	@Reference(timeout=40000)
	private ItemPageService itemPageService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取添加商品的商家id
			SecurityContextHolder contextHolder = new SecurityContextHolder();
			String name = contextHolder.getContext().getAuthentication().getName();
			//设置
			goods.getTbGoods().setSellerId(name);
			System.out.println(goods);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		//校验是否是当前商家id
		Goods goods2 = goodsService.findOne(goods.getTbGoods().getId());
		//如果传递过来的商家id不是登录的商家id，则属于非法操作
		if (!goods2.getTbGoods().getSellerId().equals(goods.getTbGoods().getSellerId())) {
			return new Result(false,"非法操作");
		}
		try {
			System.out.println(goods);
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		//获取商家id
		SecurityContextHolder contextHolder = new SecurityContextHolder();
		String name = contextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(name);
		
		return goodsService.findPage(goods, page, rows);		
	}
	
	/**
	 * 
	 * @param ids
	 * @param satus
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status) {
		try {
			goodsService.updateStatus(ids, status);
			return new Result(true,"审核成功");
		} catch (Exception e) {
			 e.printStackTrace();
			 return new Result(false,"审核失败");
		}
	}
	
	/**
	 * 
	 * @param ids
	 * @param satus
	 * @return
	 */
	@RequestMapping("/updateMarkeTable")
	public Result updateMarkeTable(Long[] ids,String status) {
		try {
			goodsService.updateMarkeTable(ids, status);
			List<TbItem> items = goodsService.findItemListByGoodsIds(ids, status);
			//新增
			if ("1".equals(status)) {
				if (items!=null && items.size() >0) {
					itemSearchService.importList(items);
					//静态页面生成
					for (Long id : ids) {
						genHtml(id);
					}
				}
			}
			//删除
			if ("0".equals(status)) {
				itemSearchService.deleteList(items);
			}
			
			return new Result(true,"操作成功");
		} catch (Exception e) {
			 e.printStackTrace();
			 return new Result(false,"操作失败");
		}
	}
	
	/**
	 * 生成静态页面(测试)
	 * @param goodsId
	 */
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId) {
		itemPageService.genItemHtml(goodsId);
	}
}
