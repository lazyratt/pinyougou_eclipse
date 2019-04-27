package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询品牌和规格列表
	 * 
	 * @param category
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		Map map = new HashMap<>();
		// 获取模板ID
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

		if (typeId != null) {
			// 根据模板ID查询品牌列表
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			// 返回值中添加品牌列表数据
			map.put("brandList", brandList);

			// 根据模板Id查询规格列表
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			// 返回值中添加品牌列表数据
			map.put("specList", specList);
		}

		return map;
	}

	// 查询高亮
	private Map searchHighlight(Map searchMap) {
		Map<String, Object> map = new HashMap<>();

		// 查询条件
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		HighlightQuery query = new SimpleHighlightQuery(criteria);
		HighlightOptions options = new HighlightOptions().addField("item_title");
		// 设置高亮样式
		options.setSimplePrefix("<em style='color:red'>");
		options.setSimplePostfix("</em>");

		// 设置高亮选项
		query.setHighlightOptions(options);

		// 设置查询条件
		query.addCriteria(criteria);

		// 按分类筛选
		if (!"".equals(searchMap.get("category"))) {
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addCriteria(filterCriteria);
		}
		// 按品牌筛选
		if (!"".equals(searchMap.get("brand"))) {
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addCriteria(filterCriteria);
		}
		// 按规格过滤
		if (searchMap.get("spec") != null) {
			Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
			for (String key : specMap.keySet()) {
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addCriteria(filterCriteria);
			}
		}

		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
		for (HighlightEntry<TbItem> entry : highlighted) {
			TbItem item = entry.getEntity();
			if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size() > 0) {
				item.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
			}
		}

		map.put("rows", page.getContent());
		return map;
	}

	/**
	 * 搜索查询
	 */
	@Override
	public Map<String, Object> search(Map searchMap) {
		Map<String, Object> map = new HashMap<>();

		// 1.根据关键字查询商品分类
		List<String> categoryList = (List<String>) searchCategoryList(searchMap);
		map.put("categoryList", categoryList);

		// 查询品牌和规格列表
		//搜索
		String categoryName = (String) searchMap.get("category");
		if (!"".equals(categoryName)) {
			map.putAll(searchBrandAndSpecList(categoryName));
		}else {
			if (categoryList.size() > 0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		
		// 2.高亮查询
		map.putAll(searchHighlight(searchMap));
		return map;
	}

	/**
	 * 查询分类列表
	 * 
	 * @param searchMap
	 * @return
	 */
	private List searchCategoryList(Map<String, Object> searchMap) {
		List<String> list = new ArrayList<>();
		Query query = new SimpleQuery();
		// 按照关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);

		// 设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);

		// 得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

		// 根据列得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");

		// 得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();

		// 得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for (GroupEntry<TbItem> groupEntry : content) {
			// 将分组结果的名称返回到结果集中
			list.add(groupEntry.getGroupValue());
		}
		return list;
	}

}
