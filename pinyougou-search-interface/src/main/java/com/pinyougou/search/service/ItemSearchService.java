package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbItemCat;

public interface ItemSearchService {

	/**
	 * 搜索
	 * @param search
	 * @return
	 */
	public Map<String, Object> search(Map search);
	
	/**
	 * 导入数据
	 * @param list
	 */
	public void importList(List list);
	
	/**
	 * 删除数据
	 * @param goodIdList
	 */
	public void deleteList(List goodIdList);
}
