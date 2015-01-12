app.service('AuthService', function ($http, API_END_POINT) {
    return {
        auth: function (accessToken) {
            return $http.post(API_END_POINT + "/auth", {
                accessToken: accessToken
            });
        },

        logout: function () {
            return $http.post(API_END_POINT + "/auth/logout");
        }
    }
});