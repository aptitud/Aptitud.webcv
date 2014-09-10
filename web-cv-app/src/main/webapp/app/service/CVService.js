app.service('CVService', function($http, API_END_POINT, HOST){
	
	 this.getCV = function(employeeID){
		 return $http.get('http://'+HOST+':'+API_END_POINT+'/web-cv-rest/cv', {
			 params: {
				 	employeeID: employeeID
		        }
		 });
	 };
	 this.saveCV = function(data){
		 return $http.post('http://'+HOST+':'+API_END_POINT+'/web-cv-rest/cv',data);
	 };

});