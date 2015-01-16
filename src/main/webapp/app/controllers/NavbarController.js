app.controller('NavbarController', function ($scope, $rootScope, $location) {

    $scope.isAuthenticated = !angular.isUndefined(sessionStorage.user);
    $scope.user = sessionStorage.user;

    $scope.newConsultant = function () {
        $scope.$broadcast("resetSelected");
        $location.path("/new");
    };

    $rootScope.$on('authenticated', function (event, data) {
        $scope.isAuthenticated = true;
        $scope.user = sessionStorage.user;
    });

    $rootScope.$on('notauthenticated', function (event, data) {
        $scope.isAuthenticated = false;
    });

    $scope.logout = function () {
        sessionStorage.removeItem('access_token');
        sessionStorage.removeItem('domain');
        sessionStorage.removeItem('user');
        $rootScope.$broadcast('notauthenticated');
        gapi.auth.signOut();
        $location.url('/login?state=logout')
    }
});