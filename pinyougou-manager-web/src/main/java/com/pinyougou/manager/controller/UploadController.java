package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadController {

	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;//图片服务器地址
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {
		
		//获取文件格式
		String originalFilename = file.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		
		try {
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			String uploadFile = fastDFSClient.uploadFile(file.getBytes(), extName);
			
			String url = FILE_SERVER_URL+uploadFile;
			return new Result(true,url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false,"上传失败");
		}
	}
}
