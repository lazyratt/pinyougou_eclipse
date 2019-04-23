 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){	
		var id = $location.search()['id'];//获取参数值
		if(id==null){
			return
		}
		
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;	
				//向富文本编辑器添加商品
				editor.html($scope.entity.goodsDesc.introduction);
				//显示图片列表
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);	
				//显示规格
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//SKU列表规格转换
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			});				
	}
	
	//根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributrValue=function(specName,optionName){
		var items=$scope.entity.goodsDesc.specificationItems;
		var object=$scope.searchObjectByKey(items,'attributeName',specName);
		if(object==null){
			return false;
		}else{
			if(object.attributeValues.indexOf(optionName)>=0){
				return true;
			} else{
				return false;
			}
		}
	}
	
	//保存 
	$scope.save=function(){		
		//提取文本编辑器的值
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.tbGoods.id!=null){//如果有ID
			alert(111);
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert("保存成功");
//					$scope.entity={};
//					editor.html("");
					location.href="goods.html";//跳转到商品列表页
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				//商品上下架状态
				for(var i=0;i<$scope.list.length;i++){
					if($scope.list[i].isMarketable=="1"){
						$scope.list[i].isMarketable="上架状态";
					}else{
						$scope.list[i].isMarketable="已下架";
					}
				}
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//保存
	$scope.add=function(){
		$scope.entity.goodsDesc.introduction=editor.html();//添加商品介绍
		goodsService.add($scope.entity).success(function(response){
			if(response.success){
				alert("保存成功");
				$scope.entity={};
				editor.html("");//清空富文本
			} else{
				alert(response.message);
			}
		});
	}
	
	//上传图片
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(function(response){
			if(response.success){
				$scope.image_entity.url=response.message;
			} else{
				alert(response.message);
			}
		}).error(function(){
			alert("上传文件错误");
		});
	}
	
	//图片列表
	$scope.entity={goodsDesc:{itemImages:[],specificationItems:[]}};
	//添加图片列表
    $scope.add_image_entity=function(){
    	$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //删除图片列表
    $scope.remove_image_entity=function(index){
    	$scope.entity.goodsDesc.itemImages.splice(index,1);
    }
    
    //读取一级分类
    $scope.selectItemCat1List=function(){
    	itemCatService.findByParentId(0).success(function(response){
    		$scope.itemCat1List=response;
    	});
    }
    //读取二级分类
    $scope.$watch('entity.tbGoods.category1Id',function(newValue,oldValue){
    	//根据选择的查询二级分类
    	itemCatService.findByParentId(newValue).success(function(response){
    		$scope.itemCat2List=response;
    		$scope.itemCat3List=[];
    		$scope.entity.tbGoods.typeTemplateId="";
    	});
    });
    //读取三级分类
    $scope.$watch('entity.tbGoods.category2Id',function(newValue,oldValue){
    	//根据选择的查询二级分类
    	itemCatService.findByParentId(newValue).success(function(response){
    		$scope.itemCat3List=response;
    	});
    });
    
    //读取模板id,当第三个列表选择完成后更新模板id
    $scope.$watch('entity.tbGoods.category3Id',function(newValue,oldValue){
    	itemCatService.findOne(newValue).success(function(response){
    		$scope.entity.tbGoods.typeTemplateId=response.typeId;
    	});
    });
   
    //商品模板确定后，更新品牌列表
	$scope.$watch('entity.tbGoods.typeTemplateId',function(newValue,oldValue){
		//品牌列表
		typeTemplateService.findOne(newValue).success(function(response){
			$scope.typeTemplate=response;//获取模板信息
			$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//品牌列表
			if($location.search()['id']==null){
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
			}
		});
		
		//查询规格列表
		typeTemplateService.findSpecList(newValue).success(function(response){
			$scope.specList=response;
		});
	});
	
	
	$scope.updateSpecAttribute=function($event,name,value){
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if(object!=null){
			if($event.target.checked){
				object.attributeValues.push(value);
			}else{
				//取消勾选
				object.attributeValues.splice(object.attributeValues.indexOf(value),1);
				//如果选项都取消了，将此条记录移除
				if(object.attributeValues.length==0){
					$scope.entity.goodsDesc.specificationItems.splice(
					$scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push(
					{"attributeName":name,"attributeValues":[value]});
		}
	}
	
	//创建SKU列表
	$scope.createItemList=function(){
		//初始化参数
		$scope.entity.itemList=[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];
		var items = $scope.entity.goodsDesc.specificationItems;
		
		for(var i=0;i<items.length;i++){
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValues);
		}
	}
	
	addColumn=function(list,columnName,columnValues){
		var newList=[];//创建新集合
		for(var i=0;i<list.length;i++){
			
			var oldRow = list[i];
			
			for(var j=0;j<columnValues.length;j++){
				var newRow=JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columnName]=columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
	
	//商品状态
	$scope.status=['未审核','已审核','审核未通过','关闭'];
	
	$scope.itemCatList=[];//商品分类列表
	
	//加载商品分类列表
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(function(response){
			for(var i=0;i<response.length;i++){
				$scope.itemCatList[response[i].id]=response[i].name;
			}
		});
	}
	//更改状态
	$scope.updateStatus=function(status){
		goodsService.updateStatus($scope.selectIds,status).success(function(response){
			if(response.success){
				//成功，刷新列表
				$scope.reloadList();
				//清空数据
				$scope.selectIds=[];
			}else{
				alert(response.message);
			}
		});
	}
	
	//更改上下架状态
	$scope.updateMarkeTable=function(status){
		goodsService.updateMarkeTable($scope.selectIds,status).success(function(response){
			if(response.success){
				//成功，刷新列表
				$scope.reloadList();
				//清空数据
				$scope.selectIds=[];
			}else{
				alert(response.message);
			}
		});
	}
	
});	
