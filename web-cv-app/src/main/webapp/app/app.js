angular.module('configuration', ['ui.select'])
    //.constant('API_END_POINT','http://afternoon-river-5325.herokuapp.com')
    .constant('API_END_POINT', 'http://localhost:8000')
    .constant('CLIENT_ID', '57103926862-ktsb4791lhv5fp42tista6jhocosc3rg.apps.googleusercontent.com');
var app = angular.module('WebCVApplication', ['configuration', 'ngRoute']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/home', {
            templateUrl: 'home.html'
        }).
        when('/edit/:id', {
            templateUrl: 'edit.html'
        }).
        when('/new', {
            templateUrl: 'edit.html'
        }).
        when('/login', {
            templateUrl: 'login.html'
        }).
        otherwise({
            redirectTo: '/login'
        });
}]);

// request interceptor
// 1. add token header bearer 'token' if token set
// response interceptor
// 2. Check 401 unauthorized or 410 gone, then send to login with param auth=unauthorized or auth=gone
app.factory('authInterceptor', function ($q, $location, Alerts) {
    return {
        request: function (config) {
            config.withCredentials = true;
            if (sessionStorage.access_token) {
                config.headers['Authorization'] = 'bearer ' + sessionStorage.access_token;
            }
            return config;
        },
        responseError: function (response) {
            // no redirect for /auth call since we already are on /login
            if (response.config.url.indexOf('/auth', response.config.url.length - 5) > -1) {
                return $q.reject(response);
            }
            if (response.status == 410 || response.status == 401) {
                var deferred = $q.defer();
                deferred.promise.finally(function () {
                    $location.url("/login?state=auth&status=" + response.status);
                });
                deferred.reject(response);
                return deferred.promise;
            }

            Alerts.handleError(response);

            return response;
        }
    };
});

app.controller('AlertController', function ($scope, Alerts) {
    $scope.alerts = Alerts.alerts();
    $scope.dismiss = function (a) {
        Alerts.dismiss(a);
    };
});

app.factory('Alerts', function () {
    var alerts = [];
    return {
        handleError: function (response) {
            alerts.push({
                type: 'ERROR',
                status: response.status,
                reason: response.data && response.data.reason,
                message: response.data && response.data.message
            });
        },
        alerts: function () {
            return alerts;
        },
        dismiss: function(alert) {
            var i = alerts.indexOf(alert);
            if (i > -1) {
                alerts.splice(i, 1);
            }
        }
    };
});

app.config(['$httpProvider', function ($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
}]);

app.controller('HomeController', function ($scope) {
    $scope.user = sessionStorage.user;
});

app.factory('Loader', function () {
    return {
        start: function () {
            $("#loader").addClass('overlay');
        },
        end: function () {
            $("#loader").removeClass('overlay');
        }
    };
});