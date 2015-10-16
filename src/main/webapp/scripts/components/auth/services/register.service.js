'use strict';

angular.module('questionspourungenApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


