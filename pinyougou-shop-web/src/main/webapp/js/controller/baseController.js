app.controller('baseController',function($scope){
	//查询所有
	$scope.findAll=function(){
		brandService.findAll().success(function(data) {
			$scope.list = data;
		});
	}
	//分页控件配置
	$scope.paginationConf = {
		currentPage : 1,
		totalItems : 10,
		itemsPerPage : 10,
		perPageOptions : [ 10, 20, 30, 40, 50 ],
		onChange : function() {
			$scope.reloadList();
		}
	};

	//重新加载列表
	$scope.reloadList = function() {
		$scope.search($scope.paginationConf.currentPage,
				$scope.paginationConf.itemsPerPage);
	}
	
	//选择删除功能
	$scope.selectIds = [];//选择复选框的id集合 
	//选择复选框，将id添加到数组中，取消复选框，将id从数组中删除
	$scope.updateSelect = function($event, id) {
		//判断是否选中
		if ($event.target.checked) {
			//选中
			$scope.selectIds.push(id);
		} else {
			//取消选中
			var index = $scope.selectIds.indexOf(id);//获取id对应的坐标位置
			//从集合中删除id
			$scope.selectIds.splice(index, 1);
		}
	}
	//把json转化为字符串
	$scope.jsonToString=function(jsonString,key){
		var json = JSON.parse(jsonString);
		var value="";
		for(var i=0;i<json.length;i++){
			if(i>0){
				value+=",";
			}
			value+=json[i][key];
		}
		return value;
	}
	
	//从集合中按照Key查询对象
	$scope.searchObjectByKey=function(list,key,keyValue){
		for(var i=0;i<list.length;i++){
			if(list[i][key]==keyValue){
				return list[i];
			}
		}
		return null;
	}
	
	

});