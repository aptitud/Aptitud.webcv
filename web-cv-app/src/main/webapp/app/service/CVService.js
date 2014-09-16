app.service('CVService', function($http, API_END_POINT){
	
	 this.getCV = function(employeeID){
		 return $http.get(API_END_POINT+'/cv', {
			 params: {
				 	employeeID: employeeID
		        }
		 });
	 };
	 this.saveCV = function(data){
		 return $http.post(API_END_POINT+'/cv',data);
	 };
	 
	 this.listCVs = function(){
		 return $http.get(API_END_POINT+'/cv/list');
	 };

});