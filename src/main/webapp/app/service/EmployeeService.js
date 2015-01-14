app.service('EmployeeService', function ($http, API_END_POINT) {
    return {
        listEmployees: function
            (searchText) {
            return $http.get(API_END_POINT + '/employees' + (searchText && '?searchText=' + encodeURI(searchText)));
        },

        getEmployeeById: function (id) {
            return $http.get(API_END_POINT + '/employees/' + id);
        },

        saveEmployee: function (data) {
            return $http.post(API_END_POINT + '/employees', data);
        }
    };
});