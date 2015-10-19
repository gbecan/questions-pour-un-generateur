angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http) {

    $scope.variant = [];

    $scope.generate = function() {
        $http
            .get("/generate")
            .then(function(response) {
                $scope.variant = response.data;
            }, function(error){
                console.error(error);
            })
    };

});