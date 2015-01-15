app.service('AuthService', function ($http, API_END_POINT) {
    return {
        auth: function (accessToken) {
            return $http.post(API_END_POINT + "/auth", {
                accessToken: accessToken
            });
        },

        logout: function (accessToken) {
            return $http.post(API_END_POINT + "/auth/logout", {
                accessToken: accessToken
            });
        }
    }
});