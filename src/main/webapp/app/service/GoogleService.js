app.service('GoogleService', function ($http, $location, CLIENT_ID) {
    return {
        signIn: function (callback) {
            return gapi.auth.signIn({
                clientid: CLIENT_ID,
                cookiepolicy: "single_host_origin",
                requestvisibleactions: "http://schemas.google.com/AddActivity",
                scope: "https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.profile.emails.read",
                callback: callback,
                accesstype: 'online'
            });
        },

        getUserInfo: function (callback) {
            return gapi.client.request({
                'path': '/plus/v1/people/me',
                'method': 'GET',
                'callback': callback
            });
        }
    };
});