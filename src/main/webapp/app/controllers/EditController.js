app.controller('EditController', function ($scope, $rootScope, EmployeeService, Loader,
                                           API_END_POINT, $routeParams, $location, $window) {

    $scope.showCVBox = false;
    $scope.endpoint = API_END_POINT;
    $scope.selectedLang = {
        value: "SE"
    };
    $scope.employeeForEdit = {};

    if (!angular.isUndefined($routeParams.id)) {
        $scope.employeeIdForEdit = $routeParams.id;
        EmployeeService.getEmployeeById($routeParams.id).success(function (data) {
            $scope.employeeForEdit = data;
            $scope.$broadcast('loadimg', data);
            $scope.showCVBox = true;
            $scope.selectedCV = data.cvs[$scope.selectedLang.value] || {};
            updateTextBoxLayoutData();
        }).error(function(data, status) {
            if (status == httpUtils.statusCodes.NOT_FOUND) {
                $scope.error = 'not_found';
            }
        });
    }

    $scope.textBoxLayout = {
        introduction: 5,
        language: 5,
        framework: 5,
        method: 5,
        forAssignment: function (a, property) {
            var layout = this.assignments[a.id];
            return (layout && layout[property]) || 5;
        },
        assignments: {}
    };

    function updateTextBoxLayoutData() {
        function toTextBoxRows(string) {
            // max 70 chars in width => 65 chars in avg per line rounded upwards plus one extra line at least 2
            return (string && string.length && Math.max(2, Math.ceil(string.length / 65) + 1)) || 5;
        }

        $scope.textBoxLayout.introduction = toTextBoxRows($scope.selectedCV.introduction);
        $scope.textBoxLayout.language = toTextBoxRows($scope.selectedCV.language);
        $scope.textBoxLayout.framework = toTextBoxRows($scope.selectedCV.framework);
        $scope.textBoxLayout.method = toTextBoxRows($scope.selectedCV.method);
        if ($scope.selectedCV.assignments) {
            $scope.selectedCV.assignments.forEach(function (a) {
                // no real id on a, but fake one if needed
                if (!a.id) {
                    a.id = Math.floor(Math.random() * 100000000000);
                }
                $scope.textBoxLayout.assignments[a.id] = {
                    'description': toTextBoxRows(a.description),
                    'techniques': toTextBoxRows(a.techniques)
                };
            });
        }
    }

    $scope.createEmployee = function () {
        Loader.start();
        EmployeeService.createEmployee($scope.employeeForEdit).success(openCreatedEmployee);
    }

    function openCreatedEmployee(data, status, headers) {
        var location = headers('location');
        var createdId = headers('X-createdId');
        $location.path('/edit/' + createdId);
        Loader.end();
    }

    $scope.saveChanges = function () {
        var employee = $scope.employeeForEdit;
        var cv = $scope.selectedCV;
        employee.cvs[$scope.selectedLang.value] = cv;
        Loader.start();
        EmployeeService.saveEmployee(employee).success(function () {
            $scope.editForm.$setPristine();
            $rootScope.$broadcast('employeeChanged', employee);
        }).finally(Loader.end);
    };

    $scope.deleteEmployee = function () {
        if ($window.confirm('Är du säker på att du vill arkivera ' + $scope.employeeForEdit.name + '?')) {
            var employee = $scope.employeeForEdit;
            Loader.start();
            EmployeeService.deleteEmployee(employee.id).success(function () {
                $rootScope.$broadcast('resetSelected');
                $location.path('/new');
            }).finally(Loader.end);
        }
    };

    $scope.changeLang = function (lang) {
        $scope.selectedLang.value = lang || $scope.selectedLang.value;
        if ($scope.selectedCV.lang != $scope.selectedLang.value) {
            var employeeID = $scope.employeeForEdit.id;
            $scope.showCVBox = true;
            $scope.selectedCV = $scope.employeeForEdit.cvs[$scope.selectedLang.value];
        }
    }

    $scope.$on('imgloaded', function (event, args) {
        $scope.employeeForEdit.img = args;
    });

    $scope.$on('resetSelected', function () {
        $scope.resetInput();
    });

    $scope.resetInput = function () {
        $scope.showCVBox = false;
        $scope.employeeForEdit = {};
        $scope.selectedCV = null;
        $scope.$broadcast('clearimg');
    }

    $scope.addAssignmentFirst = function () {
        if (!$scope.selectedCV.assignments) {
            $scope.selectedCV.assignments = [];
        }
        $scope.selectedCV.assignments.unshift({'customer': '', 'role': '', 'techniques': '', 'description': ''});
    }

    $scope.addAssignment = function () {
        if (!$scope.selectedCV.assignments) {
            $scope.selectedCV.assignments = [];
        }
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
