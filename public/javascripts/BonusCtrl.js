angular.module("JLApp").controller("BonusCtrl", function($scope, $http) {

    $scope.tpmp = function() {
        var bonusPlayer = document.getElementById("playerTPMP");
        bonusPlayer.play();
    };

    $scope.merNoire = function() {
        var bonusPlayer = document.getElementById("playerMer");
        bonusPlayer.play();
    };
});