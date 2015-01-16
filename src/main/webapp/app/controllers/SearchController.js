app.controller('SearchController', function ($scope, EmployeeService, $location) {

    $scope.selected = {};
    $scope.employees = [];

    $scope.$on('resetSelected', function() {
        $scope.selected = {};
        $scope.employees = undefined;
    });

    $scope.load = function(searchText) {
/*        if (!searchText || searchText == '') {
            $scope.employees = [];
            return;
        } */
        EmployeeService.listEmployees(searchText).success(function (data) {
            $scope.employees = data;
        });
    };

    $scope.onSelect = function () {
        $location.path('/edit/' + $scope.selected.employee.id);
    };

});
