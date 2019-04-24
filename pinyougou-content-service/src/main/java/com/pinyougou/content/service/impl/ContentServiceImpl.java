package com.pinyougou.content.service.impl;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//更新缓存
//		redisTemplate.boundHashOps("content").put(content.getCategoryId(),content);
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//获取更改前的广告id
		Long contentId = content.getId();
		TbContent oldContent = contentMapper.selectByPrimaryKey(contentId);
		Long categoryId = oldContent.getCategoryId();
		contentMapper.updateByPrimaryKey(content);
		//获取更改后的广告id
		Long categoryId2 = content.getCategoryId();
		if (categoryId != categoryId2) {
			redisTemplate.boundHashOps("content").delete(categoryId2);
		}
		redisTemplate.boundHashOps("content").delete(categoryId);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		Set<Long> set = new HashSet<>();
		for(Long id:ids){
			TbContent content = contentMapper.selectByPrimaryKey(id);
			set.add(id);
			contentMapper.deleteByPrimaryKey(id);
		}
		//清除缓存
		for (Long categoryId : set) {
			redisTemplate.boundHashOps("content").delete(categoryId);
		}
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		/**
		 * 根据广告分类ID查询广告列表
		 */
		@Override
		public List<TbContent> findByCategoryId(Long categoryId) {
			//先从缓存中查询广告列表，如果缓存中没有再去查数据库
			List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
			if (list == null) {
				TbContentExample example = new TbContentExample();
				Criteria criteria = example.createCriteria();
				criteria.andCategoryIdEqualTo(categoryId);
				criteria.andStatusEqualTo("1");//开启状态
				example.setOrderByClause("sort_order");//排序
				list = contentMapper.selectByExample(example);
				//添加到缓存中
				redisTemplate.boundHashOps("content").put(categoryId,list);
				System.out.println("从数据库中查询。。。。");
			} else {
				System.out.println("从缓存中查询。。。。");
			}
			
			return list;
		}
	
}
