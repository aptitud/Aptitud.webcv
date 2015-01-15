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