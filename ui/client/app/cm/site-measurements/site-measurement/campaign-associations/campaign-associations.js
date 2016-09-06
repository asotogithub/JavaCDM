(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(State);

    State.$inject = [
        '$stateProvider',
        'CONSTANTS'
    ];

    function State(
        $stateProvider,
        CONSTANTS) {
        $stateProvider
            .state('sm-campaign-associations', {
                parent: 'site-measurement',
                url: 'campaign-associations',
                permission: CONSTANTS.PERMISSION.SITE_MEASUREMENT.LIST,
                templateUrl: 'app/cm/site-measurements/site-measurement/campaign-associations/' +
                    'campaign-associations.html',
                controller: 'SiteMeasurementCampaignAssocController',
                controllerAs: 'vmAssociation',
                ncyBreadcrumb: {
                    skip: true
                }
            });
    }
})();
