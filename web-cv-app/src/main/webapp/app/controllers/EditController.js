app.controller('EditController', function($scope, $rootScope, EmployeeService, CVService, API_END_POINT){
	
	$scope.selectedEmployee = null;
	$scope.employeeForEdit = null;
	$scope.selectedCV = null;
	$scope.showCVBox = false;
	$scope.endpoint= API_END_POINT;
	loadEmployees();
	
	
	$scope.createEmployee = function(){
		EmployeeService.saveEmployee($scope.employeeForEdit).success(loadEmployees);
	}
	
	$scope.saveChanges = function(){
		var employee = $scope.employeeForEdit;
		var cv = $scope.selectedCV;
		cv.employeeId = employee.id;
		EmployeeService.saveEmployee(employee).success(loadEmployees);
		CVService.saveCV(cv).success($scope.loadCV);
	};

	
	$scope.$on('loadcv', function(event, args) { 
		var employeeID = args.id;
		$scope.showCVBox = true;
		$scope.employeeForEdit = args;
		CVService.getCV(employeeID).success(applyCV);
	});
	
	$scope.$on('newemployee', function(event, args) { 
		$scope.showCVBox = false;
		$scope.employeeForEdit = null;
		$scope.selectedCV = null;
	});
	
	$scope.loadCV = function(){
		var employeeID = $scope.selectedEmployee.id;
		$scope.showCVBox = true;
		$scope.employeeForEdit = $scope.selectedEmployee;
		CVService.getCV(employeeID).success(applyCV);
	}
	
	$scope.addAssignment = function(){
		var assignments = $scope.selectedCV.assignments;
		var assignment = {'customer':'edit','role':'edit','techniques':'edit','description':'edit'};
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
