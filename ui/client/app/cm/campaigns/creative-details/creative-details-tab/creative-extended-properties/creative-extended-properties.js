(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaign-creative-details-extended-properties', {
                parent: 'campaign-creative-details-tab',
                url: '/extended-properties',
                templateUrl: 'app/cm/campaigns/creative-details/creative-details-tab/' +
                             'creative-extended-properties/creative-extended-properties.html',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
