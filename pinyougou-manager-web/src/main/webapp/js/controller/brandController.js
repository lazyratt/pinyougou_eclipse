//表现层
	app.controller('brandController', function($scope,$controller,brandService) {
		
		$controller('baseController',{$scope:$scope});
		
		//分页查询品牌列表
		$scope.findPage = function(page,size) {
			brandService.findPage(page,size,$scope.searchEntity).success(function(data) {
				$scope.list = data.rows;//当前页数据
				$scope.paginationConf.totalItems = data.total;//更新总记录数
			});
		}

		//添加品牌
		$scope.save = function() {
			var object = null;
			if ($scope.entity.id != null) {
				object = brandService.update($scope.entity);
			}else{
				object = brandService.save($scope.entity);
			}
				object.success(function(data) {
						//如果添加成功，重新加载页面,添加失败弹出提示框
						if (data.success) {
							$scope.reloadList();
						} else {
							alert(data.message);
						}
					});

		}

		//根据id查询品牌
		$scope.findById = function(id) {
			brandService.findById(id).success(function(data) {
				$scope.entity = data;
			});
		}

		
		//点击删除发送请求
		$scope.dele = function() {
			brandService.dele($scope.selectIds).success(
					function(data) {
						//删除成功，重新刷新列表
						if (data.success) {
							$scope.reloadList();
						} else {
							//删除失败提示
							alert(data.message);
						}
					});
		}
		//查条件查询
		$scope.searchEntity={};
		$scope.search = function(page, size) {
			brandService.search(page,size,$scope.searchEntity).success(function(data) {
						$scope.list = data.rows;//当前页数据
						$scope.paginationConf.totalItems = data.total;//更新总记录数
			});
		}
	});	