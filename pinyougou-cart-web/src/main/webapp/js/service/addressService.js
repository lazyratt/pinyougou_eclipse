app.service("addressService",function($http){
	
	//添加收货地址
	this.add=function(address){
		return $http.post("/address/add.do",address);
	}
	
	//修改收货地址
	this.update=function(address){
		return $http.post("/address/update.do",address);
	}
	
	//删除收货地址
	this.del=function(id){
		return $http.get("/address/delete.do?id="+id);
	}
});