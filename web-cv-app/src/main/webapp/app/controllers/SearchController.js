app.controller('SearchController', function($scope, $rootScope, CVService, API_END_POINT){
	loadCVList();
	
	$scope.loadCV = function(employee){
		$rootScope.$broadcast('loadcv', employee);
	}
	
	$scope.addNewEmployee = function(employee){
		$rootScope.$broadcast('newemployee');
	}
	
	$scope.$on('employeesLoaded', function(event, args) { 
		$scope.employeeList = args;
		$scope.employeeFilterList = args;
	});
	
	$scope.search = function(){
		var searchresult = getSearchResult($scope.searchAttr);
		var filteredList = getFilteredEmployeeList(searchresult);
		if(!$scope.searchAttr || $scope.searchAttr.length == 0){
			 filteredList = $scope.employeeList;
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
	
	function getSearchResult(searchquery){
		var searchresult = [];
		angular.forEach($scope.cvlist, function(cv) {
			var cvToSearch = JSON.stringify(cv).toLowerCase();
			var searchFor = searchquery.toLowerCase();
		    if(cvToSearch.search(searchFor) != -1){
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

