(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('TagInjectionTabController', TagInjectionTabController);

    TagInjectionTabController.$inject = [
        '$rootScope',
        '$scope',
        '$state',
        '$translate',
        'AdvertiserService',
        'TagInjectionUtilService',
        'UserService',
        'Utils',
        'lodash'
    ];

    function TagInjectionTabController(
        $rootScope,
        $scope,
        $state,
        $translate,
        AdvertiserService,
        TagInjectionUtilService,
        UserService,
        Utils,
        lodash) {
        var vm = this,
            firstAdvertiser = {
                id: -1,
                name: $translate.instant('advertiser.selectAdvertiser')
            },
            firstBrand = {
                id: -1,
                name: $translate.instant('advertiser.selectBrand')
            },
            firstTime = true;

        vm.advertiser = null;
        vm.brand = null;
        vm.selectAdvertiser = selectAdvertiser;
        vm.selectBrand = selectBrand;
        vm.setListAdvertiser = setListAdvertiser;
        vm.backToCampaign = backToCampaign;
        vm.oldAdvertiser = null;
        vm.oldBrand = null;
        vm.campaignSelected = {};

        activate();

        function activate() {
            setListAdvertiser();
        }

        function clearPlacementsTree() {
            $scope.$broadcast('tagInjection.clearPlacementsTree');
        }

        function selectAdvertiser(advertiser) {
            vm.brand = null;
            if (Utils.isUndefinedOrNull(advertiser)) {
                vm.brands = null;
                return;
            }

            vm.advertiser = advertiser;
            setListBrand(advertiser.id);
            clearPlacementsTree();
        }

        function selectBrand(brand) {
            vm.brand = brand;
            reloadPlacementsTree();
        }

        function setListAdvertiser() {
            var index;

            vm.promiseTab = UserService.getAdvertisers();
            return vm.promiseTab.then(function (response) {
                vm.listAdvertisers = [firstAdvertiser].concat(response);
                if (!Utils.isUndefinedOrNull(vm.listAdvertisers)) {
                    if (Utils.isUndefinedOrNull(vm.campaignSelected.id)) {
                        vm.oldAdvertiser = vm.listAdvertisers[1];
                        vm.advertiser = vm.listAdvertisers[1];
                    }
                    else {
                        index = lodash.findIndex(
                            vm.listAdvertisers,
                            function (advertiser) {
                                return advertiser.id === vm.campaignSelected.advertiserId;
                            });

                        vm.oldAdvertiser = vm.listAdvertisers[index];
                        vm.advertiser = vm.listAdvertisers[index];
                    }

                    selectAdvertiser(vm.advertiser);
                }
            });
        }

        function reloadPlacementsTree() {
            $scope.$broadcast('tagInjection.reloadPlacementsTree', {
                brand: vm.brand,
                advertiser: vm.advertiser,
                campaignId: vm.campaignSelected.id
            });
        }

        function setListBrand(advertiserId) {
            var selectedBrand;

            if (advertiserId < 0) {
                vm.listBrands = [firstBrand];
                selectedBrand = vm.listBrands[0];
                selectBrand(selectedBrand);
                return;
            }

            vm.promiseTab = AdvertiserService.getAdvertiserBrands(advertiserId).then(function (response) {
                var index;

                vm.listBrands = [firstBrand].concat(response);
                selectedBrand = vm.listBrands[0];
                if (firstTime === true) {
                    if (Utils.isUndefinedOrNull(vm.campaignSelected.id)) {
                        vm.oldBrand = vm.listBrands[1];
                        selectedBrand = vm.listBrands[1];
                    }
                    else {
                        index = lodash.findIndex(
                            vm.listBrands,
                            function (brand) {
                                return brand.id === vm.campaignSelected.brandId;
                            });

                        vm.oldBrand = vm.listBrands[index];
                        selectedBrand = vm.listBrands[index];
                    }

                    firstTime = false;
                }

                selectBrand(selectedBrand);
            });
        }

        function backToCampaign() {
            var params = {
                campaignId: vm.campaignSelected.id,
                campaign: {
                    id: vm.campaignSelected.id,
                    brandId: vm.campaignSelected.brandId,
                    advertiserId: vm.campaignSelected.advertiserId
                }
            };

            $state.go(vm.campaignSelected.currentState, params);
        }
    }
})();
