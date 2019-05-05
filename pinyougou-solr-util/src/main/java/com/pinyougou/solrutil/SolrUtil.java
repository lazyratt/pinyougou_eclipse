package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import javax.management.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	/**
	 * 导入商品信息
	 */
	public void importItemData() {
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		//已经审核
		criteria.andStatusEqualTo("1");
		List<TbItem> items = itemMapper.selectByExample(example);
		System.out.println("========商品列表========");
		for (TbItem tbItem : items) {
			System.out.println(tbItem.getId()+" "+tbItem.getTitle()+" "+tbItem.getPrice());
			Map<String, Object> map= JSON.parseObject(tbItem.getSpec(),Map.class);
			tbItem.setSpecMap(map);
		}
		//保存到搜索服务器中
		solrTemplate.saveBeans(items);
		solrTemplate.commit();
		System.out.println("==========结束============");
	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/application*.xml");
		SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
//		solrUtil.importItemData();
		solrUtil.del();
		
	}
	
	//清空
	public void del() {
		SolrDataQuery query = new SimpleQuery("*:*") ;
		solrTemplate.delete(query );
		solrTemplate.commit();
		System.out.println("执行成功");
	}
	
	
}
