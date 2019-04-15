//服务层
	app.service('brandService',function($http){
		//查询所有品牌信息
		this.findAll=function(){
			return $http.get("../brand/findAll.do");
		}
		//分页查询品牌列表
		this.findPage=function(page,size,searchEntity){
			return $http.post("../brand/findPage.do?page=" + page + "&size=" +size,searchEntity);
		}
		//添加品牌信息
		this.save=function(entity){
			return $http.post("../brand/save.do",entity)
		}
		//修改品牌信息
		this.update=function(entity){
			return $http.post("../brand/update.do",entity);
		}
		//根据id查询品牌
		this.findById=function(id){
			return $http.post("../brand/findById.do?id=" + id);
		}
		//点击删除发送请求
		this.dele=function(selectIds){
			return $http.get("../brand/delete.do?ids=" + selectIds);
		}
		//查条件查询
		this.search=function(page,size,searchEntity){
			return $http.post("../brand/search.do?page=" + page + "&size=" +size,searchEntity);
		}
		
	});