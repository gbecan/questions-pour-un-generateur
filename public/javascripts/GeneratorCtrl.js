angular.module("JLApp").controller("GeneratorCtrl", function($scope, $http, $mdDialog) {

    var player = document.getElementById("player");
    var playerSource = player.getElementsByTagName("source")[0];

    $scope.variant = "";
    $scope.tweetUrl = "";
    $scope.shareLink = "";
    $scope.facebookUrl = "";
    $scope.scoreSet = false;

    // Generation
    $scope.generate = function() {
        $scope.overScore = -1;
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
        $scope.shareLink = "http://genquestions.variability.io/play?variant=" + encodeURIComponent($scope.variant);
        $scope.tweetUrl = "http://genquestions.variability.io/play?variant=" + encodeURIComponent(encodeURIComponent($scope.variant));
        $scope.facebookUrl = "http://genquestions.variability.io/play?variant=" + encodeURIComponent(encodeURIComponent($scope.variant));
        $scope.scoreSet = false;

        playerSource.src = "/audio?variant=" + encodeURIComponent($scope.variant);
        player.load();
        player.play();
    };

    // Score
    $scope.scoreRange = function() {
        return [4, 3, 2, 1, 0];
    };

    $scope.setOverScore = function(score) {
        $scope.overScore = score;
    };

    $scope.setScore = function(score) {
        $scope.scoreSet = true;
        $scope.overScore = score;
        $http.get("/score?variant=" + encodeURIComponent($scope.variant) + "&score=" + score)
    };

    // Upload file
    $scope.uploadFile = function() {
        var formData = new FormData(document.querySelector("form"));
        var fileName = document.getElementById("fileToUpload").value;
        if (fileName.endsWith(".mp3")) {
            $http.post("/upload", formData, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).then(function() {
                $mdDialog.show(
                    $mdDialog.alert()
                        .clickOutsideToClose(true)
                        .title('Merci pour votre contribution')
                        .content('Nous allons vérifier votre extrait et le mettre en ligne dès que possible')
                        .ariaLabel('merci')
                        .ok('Ok')
                );
            }, function(err) {
                $mdDialog.show(
                    $mdDialog.alert()
                        .clickOutsideToClose(true)
                        .title('Oops...')
                        .content('Un problème est survenu lors du chargement de votre fichier. Nous vous invitons à nous contacter si le problème persiste.')
                        .ariaLabel('problème')
                        .ok('Ok')
                );
            })
        } else {
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title('Mauvais format de fichier')
                    .content("Pour faciliter le traitement des contributions, nous n'acceptons uniquement des fichiers au format MP3.")
                    .ariaLabel('merci')
                    .ok('Ok')
            );
        }

    };

    // Init

    if (typeof variant !== "undefined") {
        $scope.playVariant(variant);
    }

    if (typeof counter !== "undefined") {
        $scope.counter = counter;
    }
});