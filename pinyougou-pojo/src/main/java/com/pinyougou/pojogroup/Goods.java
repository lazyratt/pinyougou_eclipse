package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

public class Goods implements Serializable {

	private TbGoods tbGoods;//商品spu
	
	private TbGoodsDesc goodsDesc;//商品扩展属性
	
	private List<TbItem> itemList;//sku

	public Goods(TbGoods tbGoods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
		super();
		this.tbGoods = tbGoods;
		this.goodsDesc = goodsDesc;
		this.itemList = itemList;
	}

	public Goods() {
		super();
	}

	public TbGoods getTbGoods() {
		return tbGoods;
	}

	public void setTbGoods(TbGoods tbGoods) {
		this.tbGoods = tbGoods;
	}

	public TbGoodsDesc getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(TbGoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public List<TbItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}

	@Override
	public String toString() {
		return "Goods [tbGoods=" + tbGoods + ", goodsDesc=" + goodsDesc + ", itemList=" + itemList + "]";
	}

	
	
}
