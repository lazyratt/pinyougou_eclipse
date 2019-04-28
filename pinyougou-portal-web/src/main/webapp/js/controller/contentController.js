app.controller('contentController',function($scope,contentService){
	$scope.contentList=[];//广告集合
	
	$scope.findByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(function(response){
			$scope.contentList[categoryId]=response;
		});
	}
	
	//搜索跳转
	$scope.search=function(){
		if($scope.keywords){
			location.href='http://localhost:9104/search.html#?keywords='+$scope.keywords;
		}else{
			alert("请输入搜索内容");
		}
		
	}
});