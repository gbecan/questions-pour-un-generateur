angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http) {

    var player = document.getElementById("player");
    var playerSource = player.getElementsByTagName("source")[0];

    $scope.variant = "";
    $scope.updownSet = false;
    $scope.isUp = false;
    $scope.isDown = false;

    $scope.generate = function() {
        $http.get("/generate")
            .then(function(response) {
                $scope.variant = response.data;

                $scope.updownSet = false;
                $scope.isUp = false;
                $scope.isDown = false;

                playerSource.src = "audio/" + response.data;
                player.load();
                player.play();
            }, function(error){
                console.error(error);
            })
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


});