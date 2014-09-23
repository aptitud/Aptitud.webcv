app.controller('EditController', function($scope, $rootScope, EmployeeService, CVService, API_END_POINT){
	
	$scope.showCVBox = false;
	$scope.endpoint= API_END_POINT;
	$scope.selectedLang = "SE";
	$scope.employeeForEdit = {};
	loadEmployees();
	
	$scope.createEmployee = function(){
		EmployeeService.saveEmployee($scope.employeeForEdit).success(loadEmployees);
	}
	
	$scope.saveChanges = function(){
		var employee = $scope.employeeForEdit;
		console.log("save" + employee.img);
		var cv = $scope.selectedCV;
		cv.lang = $scope.selectedLang;
		cv.employeeId = employee.id;
		EmployeeService.saveEmployee(employee).success(loadEmployees);
		CVService.saveCV(cv).success(function (){console.log("success")});
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
