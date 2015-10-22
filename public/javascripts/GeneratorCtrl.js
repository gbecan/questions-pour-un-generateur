angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http) {

    var player = document.getElementById("player");
    var playerSource = player.getElementsByTagName("source")[0];

    $scope.variant = "";
    $scope.tweetUrl = "";
    $scope.scoreSet = false;

    // Generation
    $scope.generate = function() {
        $http.get("/generate")
            .then(function(response) {
                $scope.playVariant(response.data);
            }, function(error){
                console.error(error);
            })
    };


    // Audio
    $scope.playVariant = function(newVariant) {
        $scope.variant = newVariant;
        $scope.tweetUrl = encodeURIComponent("http://genquestions.variability.io/play/" + $scope.variant);
        $scope.updownSet = false;
        $scope.isUp = false;
        $scope.isDown = false;

        playerSource.src = "/audio/" + $scope.variant;
        player.load();
        player.play();
    };

    if (typeof variant !== "undefined") {
        $scope.playVariant(variant);
    }

    // Score
    $scope.scoreRange = function() {
        return [4, 3, 2, 1, 0];
    };

    $scope.overScore = -1;

    $scope.setOverScore = function(score) {
        $scope.overScore = score;
    };

    $scope.setScore = function(score) {
        $scope.scoreSet = true;
        $http.get("/score?variant=" + encodeURIComponent($scope.variant) + "&score=" + score)
    }


});