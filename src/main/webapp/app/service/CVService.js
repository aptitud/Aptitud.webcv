app.service('CVService', function ($http, API_END_POINT) {
    return {
        getCV: function (employeeId, lang) {
            return $http.get(API_END_POINT + '/cv', {
                params: {
                    employeeId: employeeId,
                    lang: lang
                }
            });
        },
        createCVIfNotFound: function(employeeId, lang) {
            return $http.post(API_END_POINT + '/cv', {
                employeeId: employeeId,
                lang: lang
            });
        },
        saveCV: function (data) {
            return $http.put(API_END_POINT + '/cv', data);
        },
        deleteCVsForEmployee: function (employeeId) {
            return $http.delete(API_END_POINT + '/cv/' + employeeId);
        },
        listCVs: function () {
            return $http.get(API_END_POINT + '/cv/list');
        }
    };
});