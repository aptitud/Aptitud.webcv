app.service('EmployeeService', function($http, API_END_POINT){
	
	 this.listEmployees = function(searchText){
		 return $http.get(API_END_POINT+'/employees' + (searchText && '?searchText=' + encodeURI(searchText)));
	 };

	this.getEmployeeById = function(id){
		return $http.get(API_END_POINT+'/employees/' + id);
	};

	this.saveEmployee = function(data){
		 return $http.post(API_END_POINT+'/employees',data);
	 };

});