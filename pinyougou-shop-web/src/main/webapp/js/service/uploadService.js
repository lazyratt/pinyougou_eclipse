app.service('uploadService',function($http){
	this.uploadFile=function(){
		var formData = new FormData();
		formData.append('file',file.files[0]);
		return $http({
			url:"../upload.do",
			method:"post",
			data:formData,
			headers:{"Content-type":undefined},
			transformRequest: angular.identity
		});
	}
});