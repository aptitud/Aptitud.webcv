app.controller('EditController', function ($scope, $rootScope, EmployeeService, CVService, Loader, API_END_POINT, $routeParams) {

    $scope.showCVBox = false;
    $scope.endpoint = API_END_POINT;
    $scope.selectedLang = "SE";
    $scope.employeeForEdit = {};

    if (!angular.isUndefined($routeParams.id)) {
        $scope.showCVBox = true;
        $scope.employeeIdForEdit = $routeParams.id;
        EmployeeService.getEmployeeById($routeParams.id).success(function (data) {
            $scope.employeeForEdit = data;
            $scope.$broadcast('loadimg', data);
        })
        CVService.getCV($routeParams.id, "SE").success(function (data) {
            $scope.selectedCV = data;
        });
    }

    $scope.createEmployee = function () {
        Loader.start();
        EmployeeService.saveEmployee($scope.employeeForEdit).success(openCreatedEmployee);
    }

    function openCreatedEmployee(data, status, headers) {
        var location = headers('location');
        var createdId = headers('X-createdId');
        EmployeeService.getEmployeeById(createdId).success(function (data) {
            $scope.employeeForEdit = data;
            $scope.$broadcast('loadimg', data);
            $scope.showCVBox = true;
            CVService.getCV($scope.employeeForEdit.id, "SE").success(applyCV);
            Loader.end();
        });
    }

    $scope.saveChanges = function () {
        var employee = $scope.employeeForEdit;
        var cv = $scope.selectedCV;
        cv.lang = $scope.selectedLang;
        cv.employeeId = employee.id;
        Loader.start();
        EmployeeService.saveEmployee(employee).success(function () {
            $rootScope.broadcast('employeeChanged', employee);
        });
        CVService.saveCV(cv).success(Loader.end);
    };

    $scope.changeLang = function (lang) {
        $scope.selectedLang = lang || $scope.selectedLang;
        if ($scope.selectedCV.lang != $scope.selectedLang) {
            var employeeID = $scope.employeeForEdit.id;
            $scope.showCVBox = true;
            CVService.getCV(employeeID, $scope.selectedLang).success(function (data) {
                $scope.selectedCV = data;
            });
        }
    }

    $scope.$on('imgloaded', function (event, args) {
        $scope.employeeForEdit.img = args;
    });

    $scope.addNewEmployee = function () {
        $scope.showCVBox = false;
        $scope.employeeForEdit = {};
        $scope.selectedCV = null;
        $scope.$broadcast('clearimg');
    }

    $scope.addAssignmentFirst = function () {
        $scope.selectedCV.assignments.unshift({'customer': '', 'role': '', 'techniques': '', 'description': ''});
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
});
