app.controller("cartController",function($scope,cartService){
	
	//获取购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(function(response){
			$scope.cartList=response;
			$scope.totalValue=cartService.sum($scope.cartList);
		});
	}
	//增加减商品数量
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(function(response){
		     $scope.findCartList();
		});
	}
});