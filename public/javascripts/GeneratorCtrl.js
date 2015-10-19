angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http) {

    var player = document.getElementById("player");
    var playerSource = player.getElementsByTagName("source")[0];

    $scope.variant = [];
    $scope.indexVariant = 0;

    var playNextTrack = function() {
        playerSource.src = $scope.variant[$scope.indexVariant];
        player.load();
        player.play();
    };

    player.addEventListener("ended", function(e) {
        if ($scope.indexVariant < $scope.variant.length - 1) {
            $scope.indexVariant++;
            playNextTrack();
        }
    });

    $scope.generate = function() {
        $http
            .get("/generate")
            .then(function(response) {
                $scope.variant = response.data;
                $scope.indexVariant = 0;

                playNextTrack();
            }, function(error){
                console.error(error);
            })
    };



});