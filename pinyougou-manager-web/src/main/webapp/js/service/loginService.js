app.service("loginService",function($http){
	
	//获取登录人名称
	this.loginName=function(){
		return $http.get("../login/name.do");
	}
});