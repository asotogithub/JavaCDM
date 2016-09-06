(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = ['$stateProvider'];

    function State($stateProvider) {
        $stateProvider
            .state('campaign-creative-versions', {
                parent: 'campaign-creative-details-tab',
                url: '/creative-versions',
                templateUrl: 'app/cm/campaigns/creative-details/creative-details-tab/' +
                'creative-versions/creative-versions.html',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
