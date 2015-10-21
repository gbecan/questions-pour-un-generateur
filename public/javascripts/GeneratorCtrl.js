angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http) {

    var player = document.getElementById("player");
    var playerSource = player.getElementsByTagName("source")[0];

    $scope.variant = "";

    $scope.generate = function() {
        $http
            .get("/generate")
            .then(function(response) {
                $scope.variant = response.data;
                playerSource.src = response.data;
                player.load();
                player.play();
            }, function(error){
                console.error(error);
            })
    };



});