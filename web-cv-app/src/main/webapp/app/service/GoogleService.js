app.service('GoogleService', function($http, $location, CLIENT_ID, API_END_POINT){	
	
	this.signIn = function(callback){
		return gapi.auth.signIn({
			clientid: CLIENT_ID,
            cookiepolicy: "single_host_origin",
            requestvisibleactions: "http://schemas.google.com/AddActivity",
            scope: "https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.profile.emails.read",
            callback: callback,
            accesstype: 'online'
		});
	}
	
	this.getUserInfo  = function(callback){
		return gapi.client.request({
			'path':'/plus/v1/people/me',
			'method':'GET',
			'callback': callback
		});
	}
	
});