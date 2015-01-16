var urlUtils = {
    getParameterByName: function (name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }
}
var useLocalEndPoint = urlUtils.getParameterByName('local');
var endPoint = useLocalEndPoint ? 'http://localhost:8000/rest' : '/rest';
angular.module('configuration', ['ui.select'])
    //.constant('API_END_POINT','https://aptitudcvonlinerest.herokuapp.com')
    //.constant('API_END_POINT', '/rest')
    .constant('API_END_POINT', endPoint)
    .constant('CLIENT_ID', '853597222975-dpjpp1tselrldr1dmg9qj95pd5dar7e7.apps.googleusercontent.com');
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
                message: response.data && response.data.message,
                url: response.config.url,
                messageOrUrl : function() {
                    return this.message || this.url;
                }
            });
        },
        alerts: function () {
            return alerts;
        },
        dismiss: function (alert) {
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

app.controller('HomeController', function ($scope, $location) {
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

// Redirect to login page on navigation in case that there us no token..
app.run(function ($rootScope, $location) {
    $rootScope.$on('$routeChangeStart', function (e) {
        if ($location.path() != 'login' && !sessionStorage.access_token) {
            e.preventDefault();
            $location.path('login');
        }
    });
});

app.run(function($location) {
        if ($location.path() != 'login' && !sessionStorage.access_token) {
            $location.path('login');
        }
    }
);