(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SMCampaignDomainController', SMCampaignDomainController);

    SMCampaignDomainController.$inject = [
        '$filter',
        '$scope',
        'CONSTANTS',
        'SiteMeasurementsUtilService',
        'UserService'
    ];

    function SMCampaignDomainController(
        $filter,
        $scope,
        CONSTANTS,
        SiteMeasurementsUtilService,
        UserService) {
        var smCampaignController = $scope.$parent.vmSMCampaign,
            step = smCampaignController.STEP.DOMAIN,
            vm = this;

        vm.cookieDomainList = [];
        vm.maxLength = CONSTANTS.SITE_MEASUREMENT.INPUT.MAX_LENGTH.CAMPAIGN_DESCRIPTION;
        vm.statusList = SiteMeasurementsUtilService.getStatusList();

        activate();

        function activate() {
            loadDomains();
        }

        function loadDomains() {
            var promise = smCampaignController.promise = UserService.getDomains();

            promise.then(
                function (response) {
                    vm.cookieDomainList = $filter('orderBy')(response, 'domain');
                });
        }

        $scope.$watch('vmAddSMCampaignDomain.formWizard.$valid', function (newVal) {
            step.isValid = newVal;
        });
    }
})();
