(function () {
    'use strict';

    angular
        .module('te.components')
        .factory('Sha1', Sha1);

    Sha1.$inject = ['$window'];

    function Sha1($window) {
        return {
            hash: $window.sha1
        };
    }
})();
