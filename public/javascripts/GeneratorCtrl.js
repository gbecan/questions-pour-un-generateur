angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http) {

    var player = document.getElementById("player");
    var playerSource = player.getElementsByTagName("source")[0];
    $scope.play = false;

    $scope.variant = [];
    $scope.indexVariant = 0;
    $scope.progress = 0;
    $scope.totalDuration = 0;
    $scope.newCurrentTime = 0;

    var playNextTrack = function() {
        playerSource.src = $scope.variant[$scope.indexVariant].src;
        player.load();
        player.play();
    };


    player.addEventListener("play", function(e) {
        $scope.play = true;
        $scope.$apply();
    });

    player.addEventListener("ended", function(e) {
        if ($scope.indexVariant < $scope.variant.length - 1) {
            $scope.indexVariant++;
            playNextTrack();
        } else {
            $scope.progress = 100;
            $scope.play = false;
            $scope.$apply();
        }
    });

    player.addEventListener("timeupdate", function(e) {
        var previousTracks = 0;
        for (var i = 0; i < $scope.indexVariant; i++) {
            previousTracks += $scope.variant[i].duration;
        }
        $scope.progress = (previousTracks + player.currentTime) * 100 / $scope.totalDuration ;
        $scope.$apply();
    });

    $scope.togglePause = function() {
        if (player.paused) {
            player.play();
            $scope.play = true;
        } else {
            player.pause();
            $scope.play = false;
        }

    };

    $scope.stop = function() {
        player.pause();
        playerSource.src = $scope.variant[$scope.indexVariant].src;
        player.load();
        $scope.progress = 0;
        $scope.play = false;
        $scope.indexVariant = 0;
    };

    $scope.generate = function() {
        $http
            .get("/generate")
            .then(function(response) {
                //$scope.variant = response.data;
                //$scope.indexVariant = 0;
                //
                //// Compute duration of sequence
                //$scope.totalDuration = 0;
                //$scope.variant.forEach(function(element) {
                //   $scope.totalDuration += element.duration;
                //});
                //
                //playNextTrack();

                playerSource.src = response.data;
                player.load();
                player.play();
            }, function(error){
                console.error(error);
            })
    };



    function setTime() {
        player.currentTime = $scope.newCurrentTime;
        if ($scope.play) {
            player.play();
        }
        player.removeEventListener("canplay", setTime);
    }

    $scope.jump = function(e) {
        var selectedProgress = e.layerX / e.target.offsetWidth;

        var newCurrentTime = selectedProgress * $scope.totalDuration;

        for (var i = 0; i < $scope.variant.length; i++) {
            var currentDuration = $scope.variant[i].duration;

            if (newCurrentTime < currentDuration) {
                $scope.indexVariant = i;
                playerSource.src = $scope.variant[i].src;
                player.load();
                $scope.newCurrentTime = newCurrentTime;
                player.addEventListener("canplay", setTime);
                break;
            } else {
                newCurrentTime = newCurrentTime - currentDuration;
            }
        }

    }

});