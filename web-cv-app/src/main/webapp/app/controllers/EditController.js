app.controller('EditController', function ($scope, $rootScope, EmployeeService, CVService, Loader, API_END_POINT) {

    $scope.showCVBox = false;
    $scope.endpoint = API_END_POINT;
    $scope.selectedLang = "SE";
    $scope.employeeForEdit = {};
    loadEmployees();

    $scope.createEmployee = function () {
        var employeeExists = getEmployee($scope.employeeForEdit.name);
        if (!employeeExists) {
            Loader.start();
            EmployeeService.saveEmployee($scope.employeeForEdit).success(openCreatedEmployee);
        } else {
            alert("Employee already exists");
        }
    }

    function openCreatedEmployee() {
        EmployeeService.listEmployees().success(function (data) {
            $scope.employees = data;
            var createdEmployee = getEmployee($scope.employeeForEdit.name);
            $scope.employeeForEdit = createdEmployee;
            $scope.showCVBox = true;
            CVService.getCV(createdEmployee.id, "SE").success(applyCV);
            Loader.end();
            $rootScope.$broadcast('employeesLoaded', data);
        });
    }

    function getEmployee(name) {
        var searchresult = {};
        angular.forEach($scope.employees, function (employee) {
            if (employee.name != null) {
                var employeeToSearch = employee.name.toLowerCase();
                var searchFor = name.toLowerCase();
                if (employeeToSearch == searchFor) {
                    this.result = employee;
                }
            }
        }, searchresult);
        return searchresult.result;
    }

    $scope.saveChanges = function () {
        var employee = $scope.employeeForEdit;
        var cv = $scope.selectedCV;
        cv.lang = $scope.selectedLang;
        cv.employeeId = employee.id;
        Loader.start();
        EmployeeService.saveEmployee(employee).success(loadEmployees);
        CVService.saveCV(cv).success(Loader.end);
    };

    $scope.changeLang = function (lang) {
        $scope.selectedLang = lang;
        var employeeID = $scope.employeeForEdit.id;
        $scope.showCVBox = true;
        CVService.getCV(employeeID, lang).success(applyCV);
        if ($scope.selectedCV.lang != lang) {
            $scope.selectedCV = {};
        }
    }

    $scope.$on('loadcv', function (event, args) {
        var employeeID = args.id;
        $scope.showCVBox = true;
        $scope.employeeForEdit = args;
        CVService.getCV(employeeID, "SE").success(applyCV);
    });

    $scope.$on('imgloaded', function (event, args) {
        $scope.employeeForEdit.img = args;
    });

    $scope.addNewEmployee = function () {
        $scope.showCVBox = false;
        $scope.employeeForEdit = {};
        $scope.selectedCV = null;
        $rootScope.$broadcast('clearimg');
    }

    $scope.addAssignment = function () {
        $scope.selectedCV.assignments.push({'customer': '', 'role': '', 'techniques': '', 'description': ''});
    }

    $scope.removeAssignment = function (assignmentToRemove) {
        var assignments = $scope.selectedCV.assignments;
        var i = assignments.indexOf(assignmentToRemove);
        if (i == -1) {
            return;
        }
        assignments.splice(i, 1);
    }

    $scope.moveUp = function (assignment) {
        var assignments = $scope.selectedCV.assignments;
        if (assignments.length < 2) {
            return;
        }
        var i = assignments.indexOf(assignment);
        if (i == -1 || i == 0) {
            return;
        }
        swap(assignments, i - 1, i);
    }

    $scope.moveDown = function (assignment) {
        var assignments = $scope.selectedCV.assignments;
        if (assignments.length < 2) {
            return;
        }
        var i = assignments.indexOf(assignment);
        if (i == -1 || i == (assignments.length - 1)) {
            return;
        }
        swap(assignments, i + 1, i);
    }

    function swap(array, pos1, pos2) {
        var v1 = array[pos1];
        var v2 = array[pos2];
        array[pos1] = v2;
        array[pos2] = v1;
    }
    
    function loadEmployees() {
        EmployeeService.listEmployees().success(applyEmployees);
    }

    function applyEmployees(data) {
        $scope.employees = data;
        $rootScope.$broadcast('employeesLoaded', data);
    }

    function applyCV(data) {
        $scope.selectedCV = data;
    }

});
