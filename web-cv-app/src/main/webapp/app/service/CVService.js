app.service('CVService', function ($http, API_END_POINT) {
    return {
        getCV: function (employeeID, lang) {
            return $http.get(API_END_POINT + '/cv', {
                params: {
                    employeeID: employeeID,
                    lang: lang
                }
            });
        },
        saveCV: function (data) {
            return $http.post(API_END_POINT + '/cv', data);
        },

        listCVs: function () {
            return $http.get(API_END_POINT + '/cv/list');
        }
    };
});