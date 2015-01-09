app.controller('SearchController', function ($scope, $rootScope, CVService, EmployeeService, $location) {
    loadCVList();

    $scope.selected = {};

    $rootScope.$on('employeeChanged', function (event, data) {
        loadEmployees();
    });

    $scope.onSelect = function () {
        $location.path('/edit/' + $scope.selected.employee.id);
    };

    $scope.search = function (searchText) {
        if (!searchText || searchText.length == 0) {
            return [];
        }
        var searchresult = getSearchResultEmployees(searchText);
        if (searchresult.length == 0) {
            searchresult = getSearchResult(searchText);
        }
        return getFilteredEmployeeList(searchresult);
    };

    function getFilteredEmployeeList(filter) {
        var filteredList = [];
        angular.forEach($scope.employeeList, function (employee) {
            if ($.inArray(employee.id, filter) != -1) {
                this.push(employee);
            }
        }, filteredList);
        return filteredList;
    }

    function getSearchResultEmployees(searchquery) {
        var searchresult = [];
        angular.forEach($scope.employeeList, function (employee) {
            if (employee.name != null) {
                var employeeToSearch = employee.name.toLowerCase();
                var searchFor = searchquery.toLowerCase();
                if (employeeToSearch.indexOf(searchFor) != -1) {
                    this.push(employee.id);
                }
            }
        }, searchresult);
        return searchresult;
    }

    function getSearchResult(searchquery) {
        var searchresult = [];
        angular.forEach($scope.cvlist, function (cv) {
            var cvToSearch = JSON.stringify(cv).toLowerCase();
            var searchFor = searchquery.toLowerCase();
            if (cvToSearch.indexOf(searchFor) != -1) {
                this.push(cv.employeeId);
            }
        }, searchresult);
        return searchresult;
    }

    function loadCVList() {
        $("#loadtext").show();
        CVService.listCVs().success(function (data) {
            $scope.cvlist = data;
            $rootScope.$broadcast('cvsLoaded', data);
            loadEmployees();
        });
    }

    function loadEmployees() {
        EmployeeService.listEmployees().success(function (data) {
            $scope.employeeList = data;
            $rootScope.$broadcast('employeesLoaded', data);
            $("#loadtext").hide();
        });
    }

});
