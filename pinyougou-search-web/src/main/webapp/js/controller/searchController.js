app.controller('searchController', function($scope, searchService) {

	// 搜索
	$scope.search = function() {
		searchService.search($scope.searchMap).success(function(response) {
			$scope.resultMap = response;// 返回搜索结果
		});
	}

	// 搜索对象
	$scope.searchMap = {
		'keywords' : '',
		'category' : '',
		'brand' : '',
		'spec' : {}
	};

	// 添加搜索项
	$scope.addSearchItem = function(key, value) {
		if (key == "category" || key == "brand"){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		
		//执行搜索
		$scope.search();
	}
	
	//移除搜索项
	$scope.removeSearchItem = function(key){
		if (key == "category" || key == "brand"){
			$scope.searchMap[key]='';
		}else{
			 delete $scope.searchMap.spec[key];
		}
		//执行搜索
		$scope.search();
	}
});