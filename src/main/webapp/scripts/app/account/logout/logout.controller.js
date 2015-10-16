'use strict';

angular.module('questionspourungenApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
