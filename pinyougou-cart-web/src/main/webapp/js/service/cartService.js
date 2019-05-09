app.service("cartService",function($http){
	
	//获取购物车列表
	this.findCartList=function(){
		return $http.get("/cart/findCartList.do");
	}
	
	//增加减商品数量
	this.addGoodsToCartList=function(itemId,num){
		return $http.get("/cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
	}
	
	//求合计数量
	this.sum=function(cartList){
		var totalValue={totalNum:0,totalMoney:0.00};
		for(var i=0;i<cartList.length;i++){
			var cart=cartList[i];
			for(var j=0;j<cart.orderItemList.length;j++){
				var orderItem = cart.orderItemList[j];
				totalValue.totalNum+=orderItem.num;
				totalValue.totalMoney+=orderItem.totalFee;
			}
		}
		return totalValue;
	}
});