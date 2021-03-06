package com.pinyougou.page.service;

public interface ItemPageService {

	/**
	 * 根据商品ID生成商品详细页
	 * @param goodsId
	 * @return
	 */
	public boolean genItemHtml(Long goodsId);

	boolean deleteHtml(Long goodsId);
}
