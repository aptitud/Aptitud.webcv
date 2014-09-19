app.controller('FileUploadController', function($scope, $rootScope, $http, API_END_POINT){
	acceptedTypes = {
		      'image/png': true,
		      'image/jpeg': true,
		      'image/gif': true
		    };
	
	$scope.$on('loadcv', function(event, args) { 
		if(validImgSrc(args.img, acceptedTypes)){
			addImg(args.img);
		}else{
			document.getElementById("filedrag").innerHTML = "<p>Drop a picture</p>";
		}
	});
	
	$scope.$on('clearimg', function(event, args) { 
		document.getElementById("filedrag").innerHTML = "<p>Drop a picture</p>";
	});
	
    $scope.uploadFile = function(e){	
		var f = $scope.file;
		if(acceptedTypes[f.type]){
			var r = new FileReader();
			r.onloadend = function(e){
				addImg(e.target.result);
		        $rootScope.$broadcast('imgloaded', e.target.result);
			}
			r.readAsDataURL(f);
    	}
    }
    
    function addImg(img){
    	var image = new Image();
    	image.src = img;
    	image.width = 150; // a fake resize
    	document.getElementById("filedrag").innerHTML = "";
    	document.getElementById('filedrag').appendChild(image);
    	var adjust = document.getElementById('filedrag').clientHeight -130;
    	if(adjust > 0){
    		$("#consultantinfo").css('margin-bottom', adjust+'px')
    	}
    }
    
    function validImgSrc(src, acceptedTypes){
    	var result = false;
    	angular.forEach(acceptedTypes, function(value, key) {
    		if(src != null && src.indexOf(key) != -1){
    			result = true;
    		}
    	});
    	return result;
    }
});


app.directive('fileSelect', function($parse){
	return{
        restrict: 'AE',
        link: function(scope, elem, attrs){
            var onchange = $parse(attrs.fileSelect);
            var modelGet = $parse(attrs.fileInput);
            var modelSet = modelGet.assign;
            var updateModel = function (e) {
            	e.preventDefault();
        		var files = e.target.files || e.dataTransfer.files;
        		var f = files[0];
            	modelSet(scope, f);
                scope.$apply(onchange);                    
            };
            elem[0].addEventListener("change", updateModel, false);
        }
    };
});

app.directive('fileDrag', function($parse){
    return{
        restrict: 'AE',
        link: function(scope, elem, attrs){
            var ondrop = $parse(attrs.fileDrag);
            var modelGet = $parse(attrs.fileInput);
            var modelSet = modelGet.assign;
            var updateModel = function (e) {
            	e.preventDefault();
        		var files = e.target.files || e.dataTransfer.files;
        		var f = files[0];
            	modelSet(scope, f);
                scope.$apply(ondrop);                    
            };
            elem[0].addEventListener("drop", updateModel, false);
            elem[0].addEventListener('dragover', function(e){e.preventDefault();}, false);
        }
    };
});

