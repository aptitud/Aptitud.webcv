app.controller('SearchController', function($scope, $rootScope, CVService, EmployeeService){
	loadCVList();

	$rootScope.$on('employeeChanged', function(event, data) {
		loadEmployees();
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
			if(employee.name != null){
				var employeeToSearch = employee.name.toLowerCase();
				var searchFor = searchquery.toLowerCase();
			    if(employeeToSearch.indexOf(searchFor) != -1){
			    	this.push(employee.id);
			    }
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
		$("#loadtext").show();
		CVService.listCVs().success(function(data) {
			$scope.cvlist = data;
			$rootScope.$broadcast('cvsLoaded', data);
			loadEmployees();
		});
	}

	function loadEmployees() {
		EmployeeService.listEmployees().success(function(data) {
			$scope.employeeList = data;
			$rootScope.$broadcast('employeesLoaded', data);
			$("#loadtext").hide();
		});
	}

	$scope.toggle = function(event){
		if(event.keyCode == 40){
			var activeIndex = $(".search-result").find("li[class*='active']").index()
			var liList = $(".search-result").find("li");
			$(liList[activeIndex]).removeClass('active');
			if(activeIndex != -1 && activeIndex < liList.last().index()){
				$(liList[activeIndex + 1]).addClass('active');
			}else{
				$(liList[0]).addClass('active');
			}
		}
		if(event.keyCode == 38){
			var activeIndex = $(".search-result").find("li[class*='active']").index()
			var liList = $(".search-result").find("li");
			var lastIndex = liList.last().index();
			$(liList[activeIndex]).removeClass('active');
			if(activeIndex != -1 && activeIndex > 0){
				$(liList[activeIndex - 1]).addClass('active');
			}else{
				$(liList[lastIndex]).addClass('active');
			}
		}
		if(event.keyCode == 13){
			var selected = $(".search-result").find("li[class*='active']").find('a').text();
			var searchresult = [];
			angular.forEach($scope.employeeList, function(employee) {
				if(employee.name != null){
					var employeeToSearch = employee.name.toLowerCase();
					var searchFor = selected.toLowerCase();
				    if(employeeToSearch.indexOf(searchFor) != -1){
				    	this.push(employee);
				    }
				}
			 }, searchresult);
			if(searchresult.length > 0){
				$scope.loadCV(searchresult[0]);
			}
		}
	}
	    
});

