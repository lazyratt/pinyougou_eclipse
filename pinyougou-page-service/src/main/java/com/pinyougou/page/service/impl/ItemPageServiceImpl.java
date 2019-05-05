package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsDescExample;
import com.pinyougou.pojo.TbGoodsDescExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class ItemPageServiceImpl implements ItemPageService{

	@Value("${pagedir}")
	private String pagedir;
	
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	
	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public boolean genItemHtml(Long goodsId) {
		try {
			 Configuration configuration = freeMarkerConfig.getConfiguration();
			 Template template = configuration.getTemplate("item.ftl");
			 Map dataModel = new HashMap<>();
			 
			 //1.加载商品表数据
			 TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
			 dataModel.put("goods", goods);
			 
			//2.加载商品扩展表数据
			 TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			 dataModel.put("goodsDesc", goodsDesc);
			 
			 //3.商品分类
			 String itemCat1=itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
			 String itemCat2=itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
			 String itemCat3=itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
			 
			 dataModel.put("itemCat1", itemCat1);
			 dataModel.put("itemCat2", itemCat2);
			 dataModel.put("itemCat3", itemCat3);
			 
			 //4.sku列表
			 TbItemExample example = new TbItemExample();
			 com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
			 criteria.andGoodsIdEqualTo(goodsId);
			 criteria.andStatusEqualTo("1");
			 example.setOrderByClause("is_default desc");//按照状态降序
			 List<TbItem> items = itemMapper.selectByExample(example);
			 dataModel.put("itemList", items);
			 
			 
			 Writer out = new FileWriter(pagedir+goodsId+".html");
			 template.process(dataModel, out);
			 out.close();
			 return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * 删除静态页面
	 * @param goodsId
	 * @return
	 */
	@Override
	public boolean deleteHtml(Long goodsId) {
		
		try {
			new File(pagedir + goodsId + ".html").delete();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

}
