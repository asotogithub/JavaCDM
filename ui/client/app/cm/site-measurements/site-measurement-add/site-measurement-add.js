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
            .state('add-sm-campaign', {
                parent: 'site-measurements',
                url: '/add-sm-campaign',
                templateUrl: 'app/cm/site-measurements/site-measurement-add/site-measurement-add.html',
                controller: 'AddSMCampaignController',
                controllerAs: 'vmSMCampaign',
                permission: CONSTANTS.PERMISSION.SITE_MEASUREMENT.CREATE_SITE_MEASUREMENT,
                ncyBreadcrumb: {
                    label: '{{"siteMeasurement.newSMCampaign"| translate}}',
                    parent: 'site-measurements'
                }
            });
    }
})();
