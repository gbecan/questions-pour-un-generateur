angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http) {

    var player = document.getElementById("player");
    var playerSource = player.getElementsByTagName("source")[0];

    $scope.variant = "";
    $scope.tweetUrl = "";
    $scope.updownSet = false;
    $scope.isUp = false;
    $scope.isDown = false;

    $scope.generate = function() {
        $http.get("/generate")
            .then(function(response) {
                $scope.playVariant(response.data);
            }, function(error){
                console.error(error);
            })
    };


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

    $scope.up = function() {
        $scope.updownSet = true;
        $scope.isUp = true;
        $http.get("/up/" + $scope.variant)
    };

    $scope.down = function() {
        $scope.updownSet = true;
        $scope.isDown = true;
        $http.get("/down/" + $scope.variant)
    };


    if (typeof variant !== "undefined") {
        $scope.playVariant(variant);
    }


});