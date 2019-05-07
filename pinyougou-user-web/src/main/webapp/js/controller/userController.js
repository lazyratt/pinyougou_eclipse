//控制层 
app.controller('userController', function($scope, $controller, userService) {

	// 注册
	$scope.reg = function() {
		if ($scope.entity.username == null || $scope.entity.username == "") {
			alert("请输入用户名");
			return;
		}
		if ($scope.entity.password == null || $scope.password == null) {
			alert("请输入密码");
			return;
		}
		if ($scope.entity.password != $scope.password) {
			alert("密码不一致，请重新输入");
			$scope.entity.password.size = "";
			$scope.password = "";
			return;
		}
		if ($scope.entity.phone == null || $scope.entity.phone == "") {
			alert("请输入手机号");
			return;
		}
		if ($scope.code == null || $scope.code == "") {
			alert("请输入验证码");
			return;
		}
		userService.add($scope.entity, $scope.code).success(function(response) {
			alert(response.message);
			if (response.success) {
				location.href = "register.html";
			}
		});
	}

	// 发送验证码
	$scope.sendCode = function() {
		if (!$scope.entity.phone) {
			alert("请输入手机号");
			return;
		}
		userService.sendCode($scope.entity.phone).success(function(response) {
			alert(response.message)
			if (response.success) {

				interval = setInterval(timer, 1000);
			}
		});
	}
	var time = 60;
	var interval;
	var timer = function() {
		time--;
		if (time == 0) {
			clearInterval(interval);
			$("#sendCodeButton").prop("disabled", false);
			$("#sendCodeButton").html("获取短信验证码");
			time = 60;
		} else {
			$("#sendCodeButton").prop("disabled", true);
			$("#sendCodeButton").html(time + "秒后重新获取");
		}
	}
});
