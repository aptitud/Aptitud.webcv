app.controller('NavbarController', function ($scope, $rootScope, $location) {

    $scope.isAuthenticated = !angular.isUndefined(sessionStorage.getItem('signedIn'));

    $scope.newConsultant = function () {
        $scope.$broadcast("resetSelected");
        $location.path("/new");
    };

    $rootScope.$on('authenticated', function (event, data) {
        $scope.isAuthenticated = true;
    });

    $rootScope.$on('logout', function (event, data) {
        $scope.isAuthenticated = false;
    });

    $scope.logout = function () {
        sessionStorage.removeItem('signedIn');
        sessionStorage.removeItem('user');
        gapi.auth.signOut();
        $rootScope.$broadcast('logout');
        $location.path('/login')
    }
});