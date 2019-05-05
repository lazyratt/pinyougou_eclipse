package com.pinyougou.search.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

/**
 * 新增solr
 * @author Ly
 *
 */
@Component
public class ItemSearchListener implements MessageListener {

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage)message;
		try {
			String text = textMessage.getText();
			List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
			System.out.println("新增solr数据："+itemList);
			itemSearchService.importList(itemList);
			System.out.println("--------完成--------------");
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
