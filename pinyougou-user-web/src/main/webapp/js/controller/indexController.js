app.controller("loginController", function($scope, loginService) {

	// 获取登录名
	$scope.showName = function() {
		loginService.showName().success(function(response) {
			$scope.loginName = response.loginName;
		});
	}
});