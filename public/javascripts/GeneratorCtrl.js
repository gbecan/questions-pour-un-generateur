angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http, $timeout) {

    $scope.variant = [];
    $scope.indexVariant = 0;
    $scope.progress = 0;
    $scope.totalDuration = 0;
    $scope.newCurrentTime = 0;

    $scope.generate = function() {
        $http
            .get("/generate")
            .then(function(response) {
                $scope.playerLoaded = false;
                $scope.variant = response.data;
                $scope.indexVariant = 0;

                // Compute duration of sequence
                $scope.totalDuration = 0;
                $scope.variant.forEach(function(element) {
                    $scope.totalDuration += element.duration;
                    element.canPlay = false;
                    element.playerLoaded = false;
                });

            }, function(error){
                console.error(error);
            })
    };


    $scope.preparePlayer = function(index) {
        var playerLoaded = $scope.variant[index].playerLoaded;

        if (!playerLoaded) {
            $scope.variant[index].playerLoaded = true;
            var player = document.getElementById("player" + index);
            player.load();
            $scope.variant[index].player = player;

            player.addEventListener("canplay", function(e) {
                $scope.variant[index].canPlay = true;
                $scope.play();
            });

            player.addEventListener("ended", function(e) {
                $scope.indexVariant++;
                $scope.playNextTrack();
            });
        }
    };

    $scope.play = function() {
        var canPlay = true;
        $scope.variant.forEach(function(elem) {
            canPlay = canPlay && elem.canPlay;
        });

        if (canPlay) {
            $scope.playNextTrack();
        }
    };

    $scope.playNextTrack = function() {
        if ($scope.indexVariant < $scope.variant.length) {
            var player = $scope.variant[$scope.indexVariant].player;
            player.play();
        }
    };

    //var player = document.getElementById("player");
    //var playerSource = player.getElementsByTagName("source")[0];


    //var playNextTrack = function() {
    //    playerSource.src = $scope.variant[$scope.indexVariant].src;
    //    player.load();
    //    player.play();
    //};
    //
    //
    //player.addEventListener("play", function(e) {
    //    $scope.play = true;
    //    $scope.$apply();
    //});
    //
    //player.addEventListener("ended", function(e) {
    //    if ($scope.indexVariant < $scope.variant.length - 1) {
    //        $scope.indexVariant++;
    //        playNextTrack();
    //    } else {
    //        $scope.progress = 100;
    //        $scope.play = false;
    //        $scope.$apply();
    //    }
    //});
    //
    //player.addEventListener("timeupdate", function(e) {
    //    var previousTracks = 0;
    //    for (var i = 0; i < $scope.indexVariant; i++) {
    //        previousTracks += $scope.variant[i].duration;
    //    }
    //    $scope.progress = (previousTracks + player.currentTime) * 100 / $scope.totalDuration ;
    //    $scope.$apply();
    //});
    //
    //$scope.togglePause = function() {
    //    if (player.paused) {
    //        player.play();
    //        $scope.play = true;
    //    } else {
    //        player.pause();
    //        $scope.play = false;
    //    }
    //
    //};
    //
    //$scope.stop = function() {
    //    player.pause();
    //    playerSource.src = $scope.variant[$scope.indexVariant].src;
    //    player.load();
    //    $scope.progress = 0;
    //    $scope.play = false;
    //    $scope.indexVariant = 0;
    //};
    //
    //function setTime() {
    //    player.currentTime = $scope.newCurrentTime;
    //    if ($scope.play) {
    //        player.play();
    //    }
    //    player.removeEventListener("canplay", setTime);
    //}
    //
    //$scope.jump = function(e) {
    //    var selectedProgress = e.layerX / e.target.offsetWidth;
    //
    //    var newCurrentTime = selectedProgress * $scope.totalDuration;
    //
    //    for (var i = 0; i < $scope.variant.length; i++) {
    //        var currentDuration = $scope.variant[i].duration;
    //
    //        if (newCurrentTime < currentDuration) {
    //            $scope.indexVariant = i;
    //            playerSource.src = $scope.variant[i].src;
    //            player.load();
    //            $scope.newCurrentTime = newCurrentTime;
    //            player.addEventListener("canplay", setTime);
    //            break;
    //        } else {
    //            newCurrentTime = newCurrentTime - currentDuration;
    //        }
    //    }
    //
    //}

});