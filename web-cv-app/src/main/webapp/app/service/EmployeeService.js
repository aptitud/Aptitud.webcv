app.service('EmployeeService', function($http, API_END_POINT, HOST){
	
	 this.listEmployees = function(){
		 return $http.get('http://'+HOST+':'+API_END_POINT+'/web-cv-rest/employees');
	 };
	 
	 this.saveEmployee = function(data){
		 return $http.post('http://'+HOST+':'+API_END_POINT+'/web-cv-rest/employees',data);
	 };

});