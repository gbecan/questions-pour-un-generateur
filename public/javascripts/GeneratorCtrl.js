angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http) {

    var player = document.getElementById("player");
    var playerSource = player.getElementsByTagName("source")[0];
    $scope.play = false;

    $scope.variant = [];
    $scope.indexVariant = 0;
    $scope.progress = 0;
    $scope.totalDuration = 0;

    var playNextTrack = function() {
        playerSource.src = $scope.variant[$scope.indexVariant].src;
        player.load();
        player.play();
        $scope.play = true;
    };

    player.addEventListener("ended", function(e) {
        if ($scope.indexVariant < $scope.variant.length - 1) {
            $scope.indexVariant++;
            playNextTrack();
        }
    });

    player.addEventListener("timeupdate", function(e) {
        console.log(player.currentTime);
        var previousTracks = 0;
        for (var i = 0; i < $scope.indexVariant; i++) {
            previousTracks += $scope.variant[i].duration;
        }
        $scope.progress = ((previousTracks + player.currentTime) / $scope.totalDuration) * 100;
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
        player.currentTime = 0;
        player.progress = 0;
        $scope.play = false;
        $scope.indexVariant = 0;
    };

    $scope.generate = function() {
        $http
            .get("/generate")
            .then(function(response) {
                $scope.variant = response.data;
                $scope.indexVariant = 0;

                // Compute duration of sequence
                $scope.totalDuration = 0;
                $scope.variant.forEach(function(element) {
                   $scope.totalDuration += element.duration;
                });

                playNextTrack();
            }, function(error){
                console.error(error);
            })
    };


    $scope.jump = function(e) {
        player.currentTime = e.layerX;
    }

});