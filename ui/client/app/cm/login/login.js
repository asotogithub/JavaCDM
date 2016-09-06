(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('login', {
                url: '/login',
                templateUrl: 'app/cm/login/login.html',
                controller: 'LoginController'
            });
    }
})();
