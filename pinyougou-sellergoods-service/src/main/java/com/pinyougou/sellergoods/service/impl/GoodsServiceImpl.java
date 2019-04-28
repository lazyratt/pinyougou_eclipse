package com.pinyougou.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */ 
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	
	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getTbGoods();
		tbGoods.setAuditStatus("0");//未申请状态
		tbGoods.setIsMarketable("0");//上架状态
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
	
		//插入商品表
		goodsMapper.insert(tbGoods);
		
		goodsDesc.setGoodsId(tbGoods.getId());
		//插入商品扩展属性
		goodsDescMapper.insert(goodsDesc);
		saveItemList(goods);//插入商品SKU列表数据
		
	}
	
	private void saveItemList(Goods goods) {
		if ("1".equals(goods.getTbGoods().getIsEnableSpec())) {
			for(TbItem item :goods.getItemList()) {
				//标题
				String title = goods.getTbGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				
				for(String key:specMap.keySet()) {
					title+=" "+specMap.get(key);
				}
				item.setTitle(title);
				setItemValues(goods, item);
				itemMapper.insert(item);
			}
		} else {
			
			TbItem item = new TbItem();
			item.setTitle(goods.getTbGoods().getGoodsName());
			item.setPrice(goods.getTbGoods().getPrice());
			item.setStatus("1");
			item.setIsDefault("1");
			item.setNum(9999);
			item.setSpec("{}");
			setItemValues(goods, item);
		}
	}

	private void setItemValues(Goods goods,TbItem item) {
		
		//商品sku编号
		item.setGoodsId(goods.getTbGoods().getId());
		//商家编号
		item.setSellerId(goods.getTbGoods().getSellerId());
		//商品分类编号
		item.setCategoryid(goods.getTbGoods().getCategory3Id());
		//创建日期
		item.setCreateTime(new Date());
		//修改日期
		item.setUpdateTime(new Date());
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
		item.setBrand(brand.getName());
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
		item.setSeller(seller.getNickName());
		//图片地址(取商品第一个图片)
		List<Map> imageList=JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (imageList.size()>0) {
			item.setImage((String)imageList.get(0).get("url"));
		}
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//设置未申请状态
		goods.getTbGoods().setAuditStatus("0");
		//保存商品表
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());
		//保存商品扩展表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//删除原有的sku列表数据
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		itemMapper.deleteByExample(example);
		//添加新的sku列表数据
		saveItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods=new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setTbGoods(tbGoods);
		
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(goodsDesc);
		
		//加载sku商品信息
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
//				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
							criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		/**
		 * 修改商品审核状态
		 */
		@Override
		public void updateStatus(Long[] ids, String status) {
			
			for(Long id :ids) {
				TbGoods goods = goodsMapper.selectByPrimaryKey(id);
				goods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(goods);
			}
			
		}

		/**
		 * 修改商品上下架状态
		 * @throws Exception 
		 */
		@Override
		public void updateMarkeTable(Long[] ids, String status) throws Exception {
			for(Long id :ids) {
				TbGoods goods = goodsMapper.selectByPrimaryKey(id);
				if (!"1".equals(goods.getAuditStatus())) {
						throw new Exception("有未审核商品");
				}
				goods.setIsMarketable(status);
				goodsMapper.updateByPrimaryKey(goods);
			}
			
		}

		@Override
		public List<TbItem> findItemListByGoodsIds(Long[] goodsIds,String status) {
			List<TbItem> items = new ArrayList<>();
			if ("1".equals(status)) {
				TbItemExample example = new TbItemExample();
				com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
				criteria.andGoodsIdIn(Arrays.asList(goodsIds));
				items.addAll(itemMapper.selectByExample(example));
			}
			return items;
		}
	
}
