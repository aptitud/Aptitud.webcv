app.controller('EditController', function($scope, $rootScope, EmployeeService, CVService, Loader, API_END_POINT){
	
	$scope.showCVBox = false;
	$scope.endpoint= API_END_POINT;
	$scope.selectedLang = "SE";
	$scope.employeeForEdit = {};
	loadEmployees();
	
	$scope.createEmployee = function(){
		var employeeExists = getEmployee($scope.employeeForEdit.name);
		if(!employeeExists){
			Loader.start();
			EmployeeService.saveEmployee($scope.employeeForEdit).success(openCreatedEmployee);
		}else{
			alert("Employee already exists");
		}
	}
	
	function openCreatedEmployee(){
		EmployeeService.listEmployees().success(function(data){
			$scope.employees = data;
			var createdEmployee = getEmployee($scope.employeeForEdit.name);
			$scope.employeeForEdit = createdEmployee;
			$scope.showCVBox = true;
			CVService.getCV(createdEmployee.id, "SE").success(applyCV);
			Loader.end();
			$rootScope.$broadcast('employeesLoaded', data);
		});
	}
	
	function getEmployee(name){
		var searchresult = {};
		angular.forEach($scope.employees, function(employee) {
			if(employee.name != null){
				var employeeToSearch = employee.name.toLowerCase();
				var searchFor = name.toLowerCase();
			    if(employeeToSearch == searchFor){
			    	this.result = employee;
			    }
			}
		 }, searchresult);
		return searchresult.result;
	}
	
	$scope.saveChanges = function(){
		var employee = $scope.employeeForEdit;
		var cv = $scope.selectedCV;
		cv.lang = $scope.selectedLang;
		cv.employeeId = employee.id;
		Loader.start();
		EmployeeService.saveEmployee(employee).success(loadEmployees);
		CVService.saveCV(cv).success(Loader.end);
	};

	$scope.changeLang = function(lang){
		$scope.selectedLang = lang;
		var employeeID = $scope.employeeForEdit.id;
		$scope.showCVBox = true;
		CVService.getCV(employeeID, lang).success(applyCV);
		if($scope.selectedCV.lang != lang){
			$scope.selectedCV = {};
		}
	}
	
	$scope.$on('loadcv', function(event, args) { 
		var employeeID = args.id;
		$scope.showCVBox = true;
		$scope.employeeForEdit = args;
		CVService.getCV(employeeID, "SE").success(applyCV);
	});
	
	$scope.$on('imgloaded', function(event, args) { 
		$scope.employeeForEdit.img = args;
	});
	
	$scope.addNewEmployee = function(){ 
		$scope.showCVBox = false;
		$scope.employeeForEdit = {};
		$scope.selectedCV = null;
		$rootScope.$broadcast('clearimg');
	}
	
	$scope.addAssignment = function(){
		var assignments = $scope.selectedCV.assignments;
		var assignment = {'customer':'','role':'','techniques':'','description':''};
		assignments.push(assignment);
		$scope.selectedCV.assignments = assignments;
	}
	
	function loadEmployees(){
		EmployeeService.listEmployees().success(applyEmployees);
	}

	function applyEmployees(data){
		$scope.employees = data;
		$rootScope.$broadcast('employeesLoaded', data);
	}
	
	function applyCV(data){
		$scope.selectedCV = data;
	}
	
	
	
});
