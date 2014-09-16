app.controller('SearchController', function($scope, $rootScope, CVService, API_END_POINT){
	loadCVList();
	
	$scope.loadCV = function(employee){
		$rootScope.$broadcast('loadcv', employee);
	}
	
	$scope.addNewEmployee = function(employee){
		$rootScope.$broadcast('newemployee');
	}
	
	$scope.search = function(){
		var searchresult = [];
		angular.forEach($scope.cvlist, function(cv) {
			var cvToSearch = JSON.stringify(cv).toLowerCase();
			var searchFor = $scope.searchAttr.toLowerCase();
		    if(cvToSearch.search(searchFor) != -1){
		    	this.push(cv.employeeId);
		    }
		 }, searchresult);
		var filteredList = [];
		angular.forEach($scope.employeeList, function(employee) {
			if($.inArray(employee.id, searchresult) != -1){
				this.push(employee);
			}
		}, filteredList);
		if(!$scope.searchAttr || $scope.searchAttr.length == 0){
			 filteredList = $scope.employeeList;
		}
		$scope.employeeFilterList = filteredList;
	}
	
	$scope.$on('employeesLoaded', function(event, args) { 
		console.log("employeesLoaded");
		$scope.employeeList = args;
		$scope.employeeFilterList = args;
	});
	
	function loadCVList(){
		CVService.listCVs().success(applyCVList);
	}
	
	function applyCVList(data){
		$scope.cvlist = data;
	}
	
	    
});

