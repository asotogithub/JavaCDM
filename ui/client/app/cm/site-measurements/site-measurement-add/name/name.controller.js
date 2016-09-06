(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SMCampaignNameController', SMCampaignNameController);

    SMCampaignNameController.$inject = [
        '$filter',
        '$scope',
        'CONSTANTS',
        'AdvertiserService',
        'UserService',
        'Utils'
    ];

    function SMCampaignNameController(
        $filter,
        $scope,
        CONSTANTS,
        AdvertiserService,
        UserService,
        Utils) {
        var smCampaignController = $scope.$parent.vmSMCampaign,
            campaignObject = smCampaignController.campaign,
            step = smCampaignController.STEP.NAME,
            vm = this;

        vm.advertiserList = [];
        vm.brandList = [];
        vm.maxLength = CONSTANTS.SITE_MEASUREMENT.INPUT.MAX_LENGTH.CAMPAIGN_NAME;
        vm.reloadBrand = reloadBrand;

        activate();

        function activate() {
            loadAdvertisers();
        }

        function loadAdvertisers() {
            var promise = smCampaignController.promise = UserService.getAdvertisers();

            promise.then(function (response) {
                vm.advertiserList = $filter('orderBy')(response, 'name');
            });
        }

        function loadBrands(advertiserId) {
            var promise = smCampaignController.promise = AdvertiserService.getAdvertiserBrands(advertiserId);

            promise.then(function (response) {
                vm.brandList = $filter('orderBy')(response, 'name');
            });
        }

        function reloadBrand() {
            var advertiser = campaignObject.advertiser;

            campaignObject.brand = null;

            if (Utils.isUndefinedOrNull(advertiser)) {
                vm.brandList = [];
            }
            else {
                loadBrands(advertiser.id);
            }
        }

        $scope.$watch('vmAddSMCampaignName.formWizard.$valid', function (newVal) {
            step.isValid = newVal;
        });
    }
})();
