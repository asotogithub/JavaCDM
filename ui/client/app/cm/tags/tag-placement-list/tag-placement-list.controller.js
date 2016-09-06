(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('TagPlacementListController', TagPlacementListController);

    TagPlacementListController.$inject = [
        '$scope',
        '$translate',
        'CONSTANTS',
        'AdvertiserService',
        'BrandService',
        'CampaignsUtilService',
        'DialogFactory',
        'UserService',
        'Utils',
        'lodash',
        'storage'
    ];

    function TagPlacementListController(
        $scope,
        $translate,
        CONSTANTS,
        AdvertiserService,
        BrandService,
        CampaignsUtilService,
        DialogFactory,
        UserService,
        Utils,
        lodash,
        storage) {
        var DIALOG = DialogFactory.DIALOG,
            vm = this,
            firstAdvertiser = {
                id: -1,
                name: $translate.instant('advertiser.selectAdvertiser')
            },
            firstBrand = {
                id: -1,
                name: $translate.instant('advertiser.selectBrand')
            },
            firstTime = true,
            storageFiltersParents;

        vm.DIALOG = {
            DETAILS_OPEN: 'OPEN'
        };

        vm.STATUS = CONSTANTS.STATUS;
        vm.TRAFFIC = {
            PENDING: {
                key: 0,
                name: $translate.instant('global.pending')
            },
            TRAFFICKED: {
                key: 1,
                name: $translate.instant('global.trafficked')
            }
        };

        vm.dialogDetailsStatus = null;
        vm.filtersParents = {
            advertiser: null,
            brand: null
        };
        vm.flyOutDefaultState = CONSTANTS.FLY_OUT.STATE.HALF_VIEW;
        vm.flyOutModel = {};
        vm.getAdTagsDetails = getAdTagsDetails;
        vm.getStatusName = getStatusName;
        vm.getTrafficName = getTrafficName;
        vm.openFlyOut = openFlyOut;
        vm.hasDetailsPermission = UserService.hasPermission(CONSTANTS.PERMISSION.TAGS.DETAILS);
        vm.onSearchCounter = onSearchCounter;
        vm.pageSize = CONSTANTS.TAGS.PAGE_SIZE;
        vm.onCloseFlyout = onCloseFlyout;
        vm.openSendAdTags = openSendAdTags;
        vm.placements = [];
        vm.placementsTotal = 0;
        vm.selectRows = selectRows;
        vm.selectedRows = [];
        vm.selectAdvertiser = selectAdvertiser;
        vm.selectBrand = selectBrand;
        vm.statusList = lodash.values(CampaignsUtilService.getIOStatusList());

        vm.myMapCampaign = [];
        vm.myMapSite = [];
        vm.isOpenFlyOut = false;

        //This work with the te-table
        vm.filterValues = [
            {
                fieldName: 'campaignName',
                values: []
            },
            {
                fieldName: 'siteName',
                values: []
            },
            {
                fieldName: 'statusTraffic',
                values: []
            }
        ];

        //This work with the directive filters
        vm.filterOption = {
            CAMPAIGN: {
                text: $translate.instant('global.campaign'),
                value: []
            },
            SITE: {
                text: $translate.instant('global.site'),
                value: []
            },
            TRAFFICKED: {
                text: $translate.instant('global.trafficked'),
                value: []
            }
        };

        //action with directive
        vm.actionFilter = [
            {
                onSelectAll: campaignAllAction,
                onDeselectAll: campaignDeselectAll,
                onItemAction: campaignItemAction
            },
            {
                onSelectAll: siteAllAction,
                onDeselectAll: siteDeselectAll,
                onItemAction: siteItemAction
            },
            {
                onSelectAll: trafficAllAction,
                onDeselectAll: trafficDeselectAll,
                onItemAction: trafficItemAction
            }
        ];

        activate();

        function activate() {
            var auxFilterValues;

            auxFilterValues = storage.get(CONSTANTS.TAGS.STORAGE.LIST_FILTER_PARENTS + '_' +
                UserService.getUsername());

            storageFiltersParents = Utils.isUndefinedOrNull(auxFilterValues) ?
                auxFilterValues : JSON.parse(auxFilterValues[0]);

            setListAdvertiser();
        }

        function loadFilters() {
            var auxFilterValues,
                auxOptionsFilter,
                campaignList = [],
                siteList = [],
                traffickedList = [],
                storageFilters,
                siteOptions;

            lodash.forEach(vm.placements, function (value) {
                value.statusTraffic = getTrafficName(value.isTrafficked);
                mappingFilters(value);

                if (lodash.indexOf(campaignList, value.campaignName) < 0) {
                    campaignList.push(value.campaignName);
                }

                if (lodash.indexOf(siteList, value.siteName) < 0) {
                    siteList.push(value.siteName);
                }

                if (lodash.indexOf(traffickedList, value.statusTraffic) < 0) {
                    traffickedList.push(value.statusTraffic);
                }
            });

            vm.myMapCampaign = lodash.mapKeys(vm.myMapCampaign, function (value, key) {
                return key;
            });

            vm.myMapSite = lodash.mapKeys(vm.myMapSite, function (value, key) {
                return key;
            });

            vm.filterOption.CAMPAIGN.value = lodash.clone(lodash.sortBy(campaignList), true);
            vm.filterOption.SITE.value = lodash.clone(lodash.sortBy(siteList), true);
            vm.filterOption.TRAFFICKED.value = lodash.clone(lodash.sortBy(traffickedList), true);

            storageFilters = storage.get(CONSTANTS.TAGS.STORAGE.LIST_FILTER + '_' + UserService.getUsername());

            if (Utils.isUndefinedOrNull(storageFilters)) {
                updateFilters(campaignList, siteList, traffickedList);
            }
            else {
                auxFilterValues = JSON.parse(storageFilters[0]);
                auxOptionsFilter = JSON.parse(storageFilters[1]);

                if (lodash.isEqual(vm.filterOption.CAMPAIGN.value, auxOptionsFilter.CAMPAIGN.value)) {
                    siteOptions = updateSiteOptions(auxFilterValues[0].values);

                    auxFilterValues[1].values = lodash.intersection(siteOptions, auxFilterValues[1].values);

                    auxFilterValues[2].values = auxFilterValues[1].values.length > 0 ?
                        auxFilterValues[2].values : updateTrafficOptions(auxFilterValues[1].values,
                        auxFilterValues[2].values);
                }
                else {
                    auxFilterValues[0].values = verifyFiltersStorage(vm.filterOption.CAMPAIGN.value,
                        auxOptionsFilter.CAMPAIGN.value, auxFilterValues[0].values);

                    siteOptions = updateSiteOptions(auxFilterValues[0].values);

                    auxFilterValues[1].values = verifyFiltersStorage(siteOptions,
                        auxOptionsFilter.SITE.value, auxFilterValues[1].values);

                    auxFilterValues[2].values = updateTrafficOptions(auxFilterValues[1].values,
                        auxFilterValues[2].values);
                }

                updateFilters(auxFilterValues[0].values, auxFilterValues[1].values, auxFilterValues[2].values);
            }
        }

        //Close or Close and Open Dialog to Send Emails
        function onCloseFlyout(params) {
            $scope.$broadcast('tagPlacementList:closeFlyout');
            if (!Utils.isUndefinedOrNull(params) && params.openSendTagDialog) {
                openSendAdTags(vm.currentSelectPlacement);
            }
        }

        //UPDATE OPTIONS
        function updateSiteOptions(listCampaign) {
            var siteOptions = [];

            lodash.forEach(vm.myMapSite, function (value, id) {
                if (!Utils.isUndefinedOrNull(value)) {
                    if (lodash.intersection(value.campaign, listCampaign).length > 0) {
                        siteOptions.push(id);
                    }
                }
            });

            vm.filterOption.SITE.value = lodash.clone(lodash.sortBy(siteOptions), true);

            return siteOptions;
        }

        function updateTrafficOptions(listSite) {
            var trafficOptions = [];

            lodash.forEach(vm.myMapSite, function (value, id) {
                if (!Utils.isUndefinedOrNull(value)) {
                    if (lodash.indexOf(listSite, id) >= 0) {
                        trafficOptions = lodash.union(trafficOptions, value.traffic);
                    }
                }
            });

            vm.filterOption.TRAFFICKED.value = lodash.clone(lodash.sortBy(trafficOptions), true);

            return trafficOptions;
        }

        function verifyFiltersStorage(valuesFilter, valuesFilterStorage, valuesCurrent) {
            var newValue = lodash.difference(valuesFilter, valuesFilterStorage);

            valuesCurrent = lodash.intersection(valuesFilter, valuesCurrent);

            if (!Utils.isUndefinedOrNull(newValue) && newValue.length > 0) {
                lodash.forEach(newValue, function (value) {
                    valuesCurrent.push(value);
                });
            }
            else {
                valuesCurrent = lodash.intersection(valuesFilter, valuesFilterStorage);
            }

            return valuesCurrent;
        }

        function updateFilters(campaignList, siteList, traffickedList) {
            vm.filterValues[0].values = campaignList;
            vm.filterValues[1].values = siteList;
            vm.filterValues[2].values = traffickedList;
        }

        //STORAGE FILTER CAMPAIGN - SITE - TRAFFICKED
        $scope.$watch('vmTagPlacement.filterValues', function (newValue, oldValue) {
            if (!lodash.isEqual(newValue, oldValue)) {
                storage.set(CONSTANTS.TAGS.STORAGE.LIST_FILTER + '_' + UserService.getUsername(),
                    [
                        JSON.stringify(newValue),
                        JSON.stringify(vm.filterOption)
                    ]);
            }
        }, true);

        //STORAGE DROP-DOWNS ADVERTISER - BRAND
        $scope.$watch('vmTagPlacement.filtersParents', function (newValue, oldValue) {
            if (!lodash.isEqual(newValue, oldValue)) {
                storage.set(CONSTANTS.TAGS.STORAGE.LIST_FILTER_PARENTS + '_' + UserService.getUsername(),
                    [
                        JSON.stringify(newValue)
                    ]);
            }
        }, true);

        //Refresh Table to can show data update-when change filters
        function refreshTable() {
            var placementListBackup = [];

            angular.copy(vm.placements , placementListBackup);
            vm.placements = [];
            angular.copy(placementListBackup, vm.placements);
        }

        //Update Site Filter
        function updateSiteFilter(newValue, oldValue) {
            var siteSelected = [],
                siteOptions = [],
                newValueSelect = lodash.difference(newValue[0].values, oldValue),
                selectAux;

            lodash.forEach(vm.myMapSite, function (value, id) {
                if (!Utils.isUndefinedOrNull(value)) {
                    if (lodash.intersection(value.campaign, newValueSelect).length > 0) {
                        siteSelected.push(id);
                    }
                }
            });

            lodash.forEach(vm.myMapSite, function (value, id) {
                if (!Utils.isUndefinedOrNull(value)) {
                    if (lodash.intersection(value.campaign, newValue[0].values).length > 0) {
                        siteOptions.push(id);
                    }
                }
            });

            selectAux = lodash.intersection(siteOptions, newValue[1].values);

            lodash.forEach(siteSelected, function (value) {
                if (lodash.indexOf(selectAux, value) < 0) {
                    selectAux.push(value);
                }
            });

            newValue[1].values = selectAux;
            vm.filterOption.SITE.value = lodash.clone(lodash.sortBy(siteOptions), true);

            updateTrafficFilter();
        }

        //Update Traffic Filter
        function updateTrafficFilter() {
            var traffic = [],
                newValue;

            lodash.forEach(vm.myMapSite, function (value, id) {
                if (lodash.indexOf(vm.filterValues[1].values, id) >= 0) {
                    traffic = lodash.union(traffic, value.traffic);
                }
            });

            newValue = lodash.difference(traffic, vm.filterOption.TRAFFICKED.value);

            lodash.forEach(newValue, function (value) {
                vm.filterValues[2].values.push(value);
            });

            vm.filterValues[2].values = lodash.intersection(traffic, vm.filterValues[2].values);
            vm.filterOption.TRAFFICKED.value = lodash.clone(lodash.sortBy(traffic), true);
        }

        //Dropdown Advertisers
        function setListAdvertiser() {
            vm.promise = UserService.getAdvertisers();
            return vm.promise.then(function (response) {
                vm.listAdvertisers = [firstAdvertiser].concat(lodash.sortBy(response, 'name'));
                if (!Utils.isUndefinedOrNull(vm.listAdvertisers)) {
                    if (Utils.isUndefinedOrNull(storageFiltersParents) ||
                        Utils.isUndefinedOrNull(storageFiltersParents.advertiser) ||
                        storageFiltersParents.advertiser.name === firstAdvertiser.name) {
                        vm.filtersParents.advertiser = vm.listAdvertisers[1];
                    }
                    else if (!Utils.isUndefinedOrNull(lodash.find(vm.listAdvertisers,
                        lodash.matchesProperty('id', storageFiltersParents.advertiser.id)))) {
                        vm.filtersParents.advertiser = storageFiltersParents.advertiser;
                    }

                    selectAdvertiser(vm.filtersParents.advertiser);
                }
            });
        }

        function selectAdvertiser(advertiser) {
            onCloseFlyout();
            if (Utils.isUndefinedOrNull(advertiser) || advertiser.name === firstAdvertiser.name) {
                vm.filtersParents.brand = firstBrand;
                vm.placementsTotal = 0;
                resetModels();
                return;
            }

            vm.filtersParents.advertiser = advertiser;
            setListBrand(advertiser);
        }

        //Dropdown Brands
        function setListBrand(advertiser) {
            var selectedBrand;

            vm.filtersParents.brand = firstBrand;
            if (advertiser.name === firstBrand.name) {
                vm.listBrands = [firstBrand];
                selectedBrand = vm.listBrands[0];
                selectBrand(selectedBrand);
                return;
            }

            vm.promise = AdvertiserService.getAdvertiserBrands(advertiser.id).then(function (response) {
                vm.listBrands = [firstBrand].concat(lodash.sortBy(response, 'name'));

                if (Utils.isUndefinedOrNull(storageFiltersParents) ||
                    Utils.isUndefinedOrNull(storageFiltersParents.brand) ||
                    storageFiltersParents.brand.name === firstBrand.name) {
                    selectedBrand = vm.listBrands[0];
                    if (firstTime === true && vm.listBrands.length > 1) {
                        selectedBrand = vm.listBrands[1];
                        firstTime = false;
                    }
                }
                else if (Utils.isUndefinedOrNull(lodash.find(vm.listBrands,
                    lodash.matchesProperty('id', storageFiltersParents.brand.id)))) {
                    selectedBrand = vm.listBrands[0];
                }
                else {
                    selectedBrand = storageFiltersParents.brand;
                }

                selectBrand(selectedBrand);
            }).catch(function () {
                vm.placements = [];
                vm.placementsTotal = 0;
                vm.filtersParents.brand = firstBrand;
            });
        }

        function selectBrand(brand) {
            vm.filtersParents.brand = brand;
            vm.placements = [];
            vm.placementsTotal = 0;
            onCloseFlyout();
            if (brand.name === firstBrand.name) {
                resetModels();
            }
            else {
                vm.promise = BrandService.getPlacements(brand.id).then(function (response) {
                    if (Utils.isUndefinedOrNull(response.records[0].PlacementView)) {
                        resetModels();
                    }
                    else {
                        vm.placements = response.records[0].PlacementView;
                        vm.placementsTotal = Utils.isUndefinedOrNull(response) ||
                        Utils.isUndefinedOrNull(response.records) ? 0 : response.records[0].PlacementView.length;
                        loadFilters();
                    }
                });
            }
        }

        function resetModels() {
            vm.placements = [];
            vm.filterOption.CAMPAIGN.value = [];
            vm.filterOption.SITE.value = [];
            vm.filterOption.TRAFFICKED.value = [];

            vm.filterValues[0].values = [];
            vm.filterValues[1].values = [];
            vm.filterValues[2].values = [];
        }

        //Actions Campaign with directives filters
        function campaignDeselectAll(isSelectedAll, oldFilterValues, isPartialDeselect) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                if (!Utils.isUndefinedOrNull(isPartialDeselect) && isPartialDeselect) {
                    updateSiteFilter(vm.filterValues, oldFilterValues);
                    refreshTable();
                    return;
                }

                vm.filterValues[1].values = [];
                vm.filterOption.SITE.value = [];
                vm.filterValues[2].values = [];
                vm.filterOption.TRAFFICKED.value = [];
                refreshTable();
            }
        }

        function campaignAllAction(oldFilterValues) {
            updateSiteFilter(vm.filterValues, oldFilterValues);
            refreshTable();
        }

        function campaignItemAction(id, isSelectedAll, oldFilterValues) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                updateSiteFilter(vm.filterValues, oldFilterValues);
                refreshTable();
            }
        }

        //SITE ACTIONS
        function siteAllAction() {
            updateTrafficFilter();
            refreshTable();
        }

        function siteDeselectAll(isSelectedAll, oldFilterValues, isPartialDeselect) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                if (!Utils.isUndefinedOrNull(isPartialDeselect) && isPartialDeselect) {
                    refreshTable();
                    return;
                }

                vm.filterValues[2].values = [];
                vm.filterOption.TRAFFICKED.value = [];
                refreshTable();
            }
        }

        function siteItemAction(id, isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                updateTrafficFilter();
                refreshTable();
            }
        }

        //TRAFFIC ACTIONS
        function trafficAllAction() {
            refreshTable();
        }

        function trafficDeselectAll(isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshTable();
            }
        }

        function trafficItemAction(id, isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshTable();
            }
        }

        //Mapping Filters CAMPAIGN-SITE
        function mappingFilters(placement) {
            var dataCampaign = vm.myMapCampaign[placement.campaignName],
                data,
                dataSite;

            if (Utils.isUndefinedOrNull(dataCampaign)) {
                data = {
                    site: [],
                    traffic: []
                };

                data.site.push(placement.siteName);
                data.traffic.push(placement.statusTraffic);
                vm.myMapCampaign[placement.campaignName] = data;
            }
            else {
                if (lodash.indexOf(dataCampaign.site, placement.siteName) < 0) {
                    dataCampaign.site.push(placement.siteName);
                }

                if (lodash.indexOf(dataCampaign.traffic, placement.statusTraffic) < 0) {
                    dataCampaign.traffic.push(placement.statusTraffic);
                }
            }

            dataSite = vm.myMapSite[placement.siteName];
            if (Utils.isUndefinedOrNull(dataSite)) {
                data = {
                    campaign: [],
                    traffic: []
                };

                data.campaign.push(placement.campaignName);
                data.traffic.push(placement.statusTraffic);
                vm.myMapSite[placement.siteName] = data;
            }
            else {
                if (lodash.indexOf(dataSite.campaign, placement.campaignName) < 0) {
                    dataSite.campaign.push(placement.campaignName);
                }

                if (lodash.indexOf(dataSite.traffic, placement.statusTraffic) < 0) {
                    dataSite.traffic.push(placement.statusTraffic);
                }
            }
        }

        function onSearchCounter(counterSearch) {
            var legendResource = 'tags.rowsTagPlacements',
                legendData = {
                    rows: vm.placementsTotal
                };

            if (parseInt(counterSearch) !== vm.placementsTotal) {
                legendResource = 'tags.rowsSearchTagPlacements';
                legendData.rowsSearch = counterSearch;
            }

            vm.tagPlacementLegend = $translate.instant(legendResource, legendData);
        }

        function isPlacementList1x1(selectedRows) {
            var placementSizeName1x1 = CONSTANTS.TAGS.SEND_AD_TAGS.PLACEMENT_SIZE_NAME_1X1.toUpperCase(),
                result = true;

            if (selectedRows.length > 0) {
                lodash.forEach(selectedRows, function (placement) {
                    if (placement.sizeName.toUpperCase() !== placementSizeName1x1) {
                        result = false;
                    }
                });
            }
            else {
                result = false;
            }

            return result;
        }

        function getSelectedPlacementList(selectedRows) {
            return lodash.map(selectedRows, function (placement) {
                return placement.id;
            });
        }

        function getSelectedSiteList(selectedRows) {
            var siteObject,
                placementList,
                result = [];

            lodash
                .uniq(selectedRows, function (placement) {
                    return placement.siteId;
                })
                .forEach(function (site) {
                    siteObject = {};
                    placementList = lodash.filter(selectedRows, function (placement) {
                        return placement.siteId === site.siteId;
                    });

                    siteObject.site = site.siteName;
                    siteObject.siteId = site.siteId;
                    siteObject.placements = getSelectedPlacementList(placementList);
                    siteObject.placementsCount = siteObject.placements.length;
                    siteObject.isOnly1x1 = isPlacementList1x1(placementList);

                    result.push(siteObject);
                });

            return result;
        }

        function openSendAdTags(currentSelectPlacement) {
            var selectedList = Utils.isUndefinedOrNull(currentSelectPlacement) ?
                vm.selectedRows : currentSelectPlacement;

            DialogFactory.showCustomDialog({
                controller: 'SendAdTagsController as vmSendAdTags',
                size: DIALOG.SIZE.LARGE,
                template: 'app/cm/tags/tag-placement-list/send-ad-tags-modal/send-ad-tags.html',
                type: DIALOG.TYPE.CUSTOM,
                windowClass: 'modal-' + DIALOG.SIZE.EXTRA_LARGE,
                backdrop: 'static',
                data: {
                    siteList: getSelectedSiteList(selectedList)
                }
            });
        }

        //Enable button Send
        function selectRows(selection) {
            vm.selectedRows = selection;
            vm.enableActivate = true;

            return lodash.find(selection, function (item) {
                if (item.statusTraffic === vm.TRAFFIC.PENDING.name || item.status === vm.STATUS.REJECTED) {
                    vm.enableActivate = false;
                }
            });
        }

        //status of placement (Active-Inactive)
        function getStatusName(key) {
            return lodash.find(vm.statusList, function (item) {
                return item.key === key;
            }).name;
        }

        //status of traffic (Pending-Tafficked)
        function getTrafficName(key) {
            return lodash.find(vm.TRAFFIC, function (item) {
                return item.key === key;
            }).name;
        }

        //DETAILS FLY-OUT
        function openFlyOut(model) {
            model.enableSendBtn = model.statusTraffic === vm.TRAFFIC.TRAFFICKED.name &&
                getStatusName(model.status) === vm.statusList[1].name;

            vm.isOpenflyout = true;
            vm.flyOutModel = {
                title: model.name,
                data: model
            };
        }

        function getAdTagsDetails(selected) {
            vm.currentSelectPlacement = selected;
            openFlyOut(selected[0]);
        }
    }
})();
