app.controller('FileUploadController', function ($scope, $rootScope) {
    acceptedTypes = {
        'image/png': true,
        'image/jpeg': true,
        'image/gif': true
    };

    $scope.$on('loadimg', function (event, args) {
        if (validImgSrc(args.img, acceptedTypes)) {
            addImg(args.img);
        } else {
            $("#filedrag").html('<h3><i class="fa fa-file-image-o"></i></h3><p>Släpp en bild</p>');
            $("#filedrag").removeClass('uploaded');
        }
    });

    $scope.$on('clearimg', function (event, args) {
        $("#filedrag").html('<h3><i class="fa fa-file-image-o"></i></h3><p>Släpp en bild</p>');
        $("#filedrag").removeClass('uploaded');
    });

    $scope.uploadFile = function (e) {
        var f = $scope.file;
        if (acceptedTypes[f.type] && f.size < 500000) {
            var r = new FileReader();
            r.onloadend = function (e) {
                addImg(e.target.result);
                $rootScope.$broadcast('imgloaded', e.target.result);
            }
            r.readAsDataURL(f);
        } else {
            alert("Only supports files of type png, jpeg or gif. Max size 0.5Mb");
        }
    }

    function addImg(img) {
        var image = new Image();
        image.src = img;
        image.width = 150; // a fake resize
        document.getElementById("filedrag").innerHTML = "";
        document.getElementById('filedrag').appendChild(image);
        $("#filedrag").addClass('uploaded');

    }

    function validImgSrc(src, acceptedTypes) {
        var result = false;
        angular.forEach(acceptedTypes, function (value, key) {
            if (src != null && src.indexOf(key) != -1) {
                result = true;
            }
        });
        return result;
    }
});

