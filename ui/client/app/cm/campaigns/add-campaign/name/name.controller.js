(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddCampaignNameController', AddCampaignNameController);

    AddCampaignNameController.$inject = [
        '$log',
        '$scope',
        'CONSTANTS',
        'AdvertiserService',
        'UserService'
    ];

    function AddCampaignNameController(
        $log,
        $scope,
        CONSTANTS,
        AdvertiserService,
        UserService) {
        $log.debug('Running AddCampaignNameController');
        var vm = this;

        vm.campaign = $scope.$parent.vmCampaign.campaign;
        vm.maxLength = CONSTANTS.CAMPAIGN.NAME.MAX_LENGTH;
        vm.retrieveAdvertisers = retrieveAdvertisers;
        vm.retrieveBrands = retrieveBrands;
        vm.setSelectedAdvertiser = setSelectedAdvertiser;
        vm.step = $scope.$parent.vmCampaign.STEP.NAME;

        activate();

        function activate() {
            reloadAdvertisers();
        }

        function retrieveAdvertisers() {
            $scope.$parent.vmCampaign.promiseRequest = UserService.getAdvertisers();
            return $scope.$parent.vmCampaign.promiseRequest;
        }

        function retrieveBrands(advertiserId) {
            $scope.$parent.vmCampaign.promiseRequest = AdvertiserService.getAdvertiserBrands(advertiserId);
            return $scope.$parent.vmCampaign.promiseRequest;
        }

        function reloadAdvertisers() {
            retrieveAdvertisers().then(function (result) {
                vm.advertisers = result;
            });
        }

        function reloadBrands(advertiserId) {
            retrieveBrands(advertiserId).then(function (result) {
                vm.brands = result;
            });
        }

        function setSelectedAdvertiser(advertiser) {
            vm.campaign.brand = null;
            if (angular.isUndefined(advertiser)) {
                vm.brands = null;
            }
            else {
                reloadBrands(advertiser.id);
            }
        }

        $scope.$watch('vm.nameForm.$valid', function (newVal) {
            vm.step.isValid = newVal;
        });
    }
})();
