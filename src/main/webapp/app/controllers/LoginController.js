app.controller('LoginController', function ($scope, $rootScope, $routeParams, $location, $http, AuthService, Loader, CLIENT_ID) {

    $scope.processAuth = function (authResult) {
        if (authResult['status']['signed_in']) {
            Loader.start();
            var token = authResult.access_token;
            AuthService.auth(token)
                .success(function (data) {
                    sessionStorage.setItem('access_token', token);
                    sessionStorage.setItem('domain', data.domain);
                    sessionStorage.setItem('user', data.displayName);
                    $rootScope.$broadcast('authenticated', data);
                    $location.url("/home");
                    Loader.end();
                })
                .error(function (data, status) {
                    if (status == 401 || status == 410) {
                        resetUserSession();
                        $scope.error = 'not_authenticated';
                    } else {
                        $scope.error = status;
                    }
                    Loader.end();
                });
        } else if (authResult['error']) {
            var token = sessionStorage.access_token;
            resetUserSession();
            $scope.error = authResult['error'];
            if (authResult['error'] == 'user_signed_out') {
                Loader.start();
                AuthService.logout(token)
                    .then(function () {
                        Loader.end();
                    });
            }
//            console.log(authResult);
//            console.log("google auth fail");
        }
    }

    function resetUserSession() {
        sessionStorage.removeItem('access_token');
        sessionStorage.removeItem('domain');
        sessionStorage.removeItem('user');
        $rootScope.$broadcast('notauthenticated');
    }

    $scope.hasState = function (state) {
        if ('signedIn' == state) {
            return !angular.isUndefined(sessionStorage.access_token);
        }
        if ('changeAccount' == state) {
            return $scope.error == 'not_authenticated';
        }
        if ('signedOut' == state) {
            return $scope.isLogout || ($scope.error && $scope.error == 'user_signed_out');
        }
        if ('unauth' == state) {
            return $scope.isUnauth;
        }
        return false;
    };

    $scope.logout = function () {
        sessionStorage.removeItem('access_token');
        sessionStorage.removeItem('domain');
        sessionStorage.removeItem('user');
        $rootScope.$broadcast('notauthenticated');
        $scope.isLogout = true;
        $scope.isUnauth = false;
        $scope.error = undefined;
        gapi.auth.signOut();
    }

    $scope.showLogin = function () {
        $scope.isLogout = false;
        $scope.isUnauth = false;
        $scope.error = undefined;
        startLogin();
    };

    $scope.isLogout = $routeParams.state == 'logout';
    $scope.isUnauth = $routeParams.state == 'auth';

    function startLogin() {
        if (!sessionStorage.access_token) {
            gapi.signin.render('signinButton', {
                clientid: CLIENT_ID,
                cookiepolicy: "single_host_origin",
                requestvisibleactions: "http://schemas.google.com/AddActivity",
                scope: "https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.profile.emails.read",
                callback: $scope.processAuth,
                accesstype: 'online',
                width: 'wide'
            });
        } else {
            $location.url('/home');
        }
    }

    $rootScope.$broadcast('notauthenticated');

    if (!$scope.isLogout && !$scope.isUnauth) {
        startLogin();
    }
});