app.service("loginService",function($http){
	//获取登录名
	this.showName=function(){
		return $http.get("login/name.do");
	}
});