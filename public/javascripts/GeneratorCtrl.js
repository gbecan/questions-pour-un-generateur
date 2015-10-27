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
                $scope.counter = response.data.counter;
                $scope.playVariant(response.data.variant);
            }, function(error){
                console.error(error);
            })
    };


    // Audio
    $scope.playVariant = function(newVariant) {
        $scope.variant = newVariant;
        $scope.tweetUrl = "http://genquestions.variability.io/play?variant=" + encodeURIComponent(encodeURIComponent($scope.variant));
        $scope.scoreSet = false;

        playerSource.src = "/audio?variant=" + encodeURIComponent($scope.variant);
        player.load();
        player.play();
    };

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
        $scope.overScore = score;
        $http.get("/score?variant=" + encodeURIComponent($scope.variant) + "&score=" + score)
    };

    // Bonus

    $scope.tpmp = function() {
        var bonusPlayer = document.getElementById("playerTPMP");
        bonusPlayer.play();
    };

    $scope.merNoire = function() {
        var bonusPlayer = document.getElementById("playerMer");
        bonusPlayer.play();
    };


    // Init

    if (typeof variant !== "undefined") {
        $scope.playVariant(variant);
    }

    if (typeof counter !== "undefined") {
        $scope.counter = counter;
    }

});