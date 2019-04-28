app.controller('searchController', function($scope,$location, searchService) {

	// 搜索
	$scope.search = function() {
		$scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(function(response) {
			$scope.resultMap = response;// 返回搜索结果
			buildPageLabel();// 分页
		});
	}

	// 搜索对象
	$scope.searchMap = {
		'keywords' : '',
		'category' : '',
		'brand' : '',
		'spec' : {},
		'price' : '',
		'pageNo' : 1,
		'pageSize' : 40,
		'sortField':'',
		'sort':''
	};

	// 添加搜索项
	$scope.addSearchItem = function(key, value) {
		if (key == "category" || key == "brand" || key == 'price') {
			$scope.searchMap[key] = value;
		} else {
			$scope.searchMap.spec[key] = value;
		}

		// 执行搜索
		$scope.search();
	}

	// 移除搜索项
	$scope.removeSearchItem = function(key) {
		if (key == "category" || key == "brand" || key == 'price') {
			$scope.searchMap[key] = '';
		} else {
			delete $scope.searchMap.spec[key];
		}
		// 执行搜索
		$scope.search();
	}

	// 构建分页标签
	buildPageLabel = function() {
		$scope.pageLabel = [];// 新增分页属性
		var totalPages = $scope.resultMap.totalPages;// 总页数
		var pageNo = $scope.searchMap.pageNo;// 当前页数
		var maxPageNo = $scope.resultMap.totalPages;// 得到最后页数
		var firstPage = 1;// 开始页码
		var lastPage = maxPageNo;// 截止页数
		
		$scope.firstDot=true;//前面省略号
		$scope.firstDot=true;//后面省略号
		if (totalPages > 5) {
			if (pageNo <= 3) {
				lastPage = 5;
				$scope.firstDot=false;//前面没点
			} else if (pageNo >= lastPage - 2) {
				firstPage = maxPageNo - 4;
				$scope.lastDot=false;//后面省略号
			} else {
				firstPage = pageNo - 2;
				lastPage = pageNo + 2;
			}
		}else{
			$scope.firstDot=false;//前面省略号
			$scope.lastDot=false;//后面省略号
		}
		// 循环产生页码标签
		for (var i = firstPage; i <= lastPage; i++) {
			$scope.pageLabel.push(i);
		}
	}

	// 提交页码查询
	$scope.queryByPage = function(pageNo) {
		// 页码验证
		if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
			return;
		}
		$scope.searchMap.pageNo = pageNo;
		$scope.search();
	}
	
	//页码不可用样式,判断当前页是否是第一页
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}
	}
	
	//判断当前页是否是最后一页
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	//排序规则
	$scope.sortSearch = function(sortField,sort){
		$scope.searchMap.sortField=sortField;
		$scope.searchMap.sort=sort;
		$scope.search();
	}
	
	//判断关键字是不是品牌
	$scope.keywordsIsBrand=function(){
		for(var i=0;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
	
	//加载查询字符串
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords=$location.search()['keywords'];
		$scope.search();
	}
});