app.controller("cartController", function($scope, cartService, addressService) {

	// 获取购物车列表
	$scope.findCartList = function() {
		cartService.findCartList().success(function(response) {
			$scope.cartList = response;
			$scope.totalValue = cartService.sum($scope.cartList);
		});
	}
	// 增加减商品数量
	$scope.addGoodsToCartList = function(itemId, num) {
		cartService.addGoodsToCartList(itemId, num).success(function(response) {
			$scope.findCartList();
		});
	}

	// 获取用户地址列表
	$scope.findAddressList = function() {
		cartService.findAddressList().success(function(response) {
			$scope.addressList = response;
			for (var i = 0; i < $scope.addressList.length; i++) {
				if ($scope.addressList[i].isDefault == '1') {
					$scope.address = $scope.addressList[i];
					// 默认状态
					$scope.entity.isDefault = $scope.address.isDefault;
					break;
				}
			}
		});
	}

	// 选择地址
	$scope.selectAddress = function(address) {
		$scope.address = address;
	}

	// 地址是否选中
	$scope.isSelectAddress = function(address) {
		return address == $scope.address;
	}

	$scope.order = {
		paymentType : '1'
	};
	// 选择支付方式
	$scope.selectPayType = function(type) {
		$scope.order.paymentType = type;
	}

	// 是否默认
	$scope.entity = {
		isDefault : '0'
	}

	$scope.selectIsDefault = function(event) {
		if (event.target.checked) {
			$scope.entity.isDefault = '1';
		} else {
			$scope.entity.isDefault = '0';
		}
		//alert($scope.entity.isDefault);
	}

	// 添加或修改收货地址
	$scope.save = function() {
		if ($scope.entity.id) {// 修改
			addressService.update($scope.entity).success(function(response) {
				if (response.success) {
					$scope.entity = {};
					$scope.findAddressList();
					$scope.clear();
				} else {
					alert(response.message);
				}
			});
		} else {// 新增
			addressService.add($scope.entity).success(function(response) {
				if (response.success) {
					$scope.entity = {};
					location.href = 'getOrderInfo.html';
				} else {
					alert(response.message);
				}
			});
		}

	}

	// 读取单个收货地址
	$scope.findOne = function(id) {
		for (var i = 0; i < $scope.addressList.length; i++) {
			if ($scope.addressList[i].id == id) {
				var stringJson = JSON.stringify($scope.addressList[i]);
				$scope.entity = JSON.parse(stringJson);
				if ($scope.entity.isDefault == '1') {
					$("#isDefault").prop("checked", true);
				}else{
					$("#isDefault").prop("checked", false);
				}
				break;
			}
		}
	}

	$scope.clear = function() {
		$scope.entity = {};
	}

	// 删除单个收货地址
	$scope.del = function(id) {
		addressService.del(id).success(function(response) {
			if (response.success) {
				$scope.entity = {};
				location.href = 'getOrderInfo.html';
			} else {
				alert(response.message);
			}
		});
	}

	// 保存订单
	$scope.submitOrder = function() {
		$scope.order.receiverAreaName = $scope.address.address;
		$scope.order.receiverMobile = $scope.address.mobile;
		$scope.order.receiver = $scope.address.contact;
		cartService.submitOrder($scope.order).success(function(response) {
			if (response.success) {// 成功，页面跳转
				if ($scope.order.paymentType == "1") {
					location.href = "pay.html";
				} else {
					location.href = "paysuccess.html";
				}
			} else {
				alert(response.message);
			}
		});
	}
});