app.controller('SearchController', function($scope, $rootScope, CVService, API_END_POINT){
	loadCVList();
	
	$scope.loadCV = function(employee){
		$rootScope.$broadcast('loadcv', employee);
	}
	
	$scope.$on('employeesLoaded', function(event, args) { 
		$scope.employeeList = args;
	});
	
	$scope.search = function(){
		var searchresult = getSearchResultEmployees($scope.searchAttr);
		if(searchresult.length == 0){
			searchresult = getSearchResult($scope.searchAttr);	
		}
		var filteredList = getFilteredEmployeeList(searchresult);
		if(!$scope.searchAttr || $scope.searchAttr.length == 0){
			 filteredList = [];
		}
		$scope.employeeFilterList = filteredList;
	}
	
	function getFilteredEmployeeList(filter){
		var filteredList = [];
		angular.forEach($scope.employeeList, function(employee) {
			if($.inArray(employee.id, filter) != -1){
				this.push(employee);
			}
		}, filteredList);
		return filteredList;
	}
	
	function getSearchResultEmployees(searchquery){
		var searchresult = [];
		angular.forEach($scope.employeeList, function(employee) {
			var employeeToSearch = employee.name.toLowerCase();
			var searchFor = searchquery.toLowerCase();
		    if(employeeToSearch.indexOf(searchFor) != -1){
		    	this.push(employee.id);
		    }
		 }, searchresult);
		return searchresult;
	}
	
	function getSearchResult(searchquery){
		var searchresult = [];
		angular.forEach($scope.cvlist, function(cv) {
			var cvToSearch = JSON.stringify(cv).toLowerCase();
			var searchFor = searchquery.toLowerCase();
		    if(cvToSearch.indexOf(searchFor) != -1){
		    	this.push(cv.employeeId);
		    }
		 }, searchresult);
		return searchresult;
	}
	
	function loadCVList(){
		CVService.listCVs().success(applyCVList);
	}
	
	function applyCVList(data){
		$scope.cvlist = data;
	}
	
	    
});

