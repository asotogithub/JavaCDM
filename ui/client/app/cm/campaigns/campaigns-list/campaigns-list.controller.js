(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignsListController', CampaignsListController);

    CampaignsListController.$inject = [
        '$q',
        '$scope',
        '$state',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'UserService',
        'Utils',
        'lodash',
        'storage'
    ];

    function CampaignsListController(
        $q,
        $scope,
        $state,
        $translate,
        CONSTANTS,
        CampaignsService,
        UserService,
        Utils,
        lodash,
        storage) {
        var vm = this;

        vm.CURRENCY_DEFAULT = CONSTANTS.CURRENCY_DEFAULT;
        vm.NUMERIC_DEFAULT = CONSTANTS.NUMERIC_DEFAULT;
        vm.addCampaign = addCampaign;
        vm.campaigns = null;
        vm.chartConfig = null;
        vm.go = go;
        vm.hasAddPermission = UserService.hasPermission(CONSTANTS.PERMISSION.CAMPAIGN.ADD);
        vm.metrics = null;
        vm.myMapAdvertiser = [];
        vm.myMapBrand = [];
        vm.promise = null;

        vm.filterValues = [
            {
                fieldName: 'advertiserName',
                values: []
            },
            {
                fieldName: 'brandName',
                values: []
            },
            {
                fieldName: 'isActiveDisplay',
                values: []
            }
        ];

        vm.filterOption = {
            ADVERTISER: {
                text: $translate.instant('global.advertiser'),
                value: []
            },
            BRAND: {
                text: $translate.instant('global.brand'),
                value: []
            },
            STATUS: {
                text: $translate.instant('global.status'),
                value: []
            }
        };

        vm.actionFilter = [
            {
                onSelectAll: advertiserAllAction,
                onDeselectAll: advertiserDeselectAll,
                onItemAction: advertiserItemAction
            },
            {
                onSelectAll: brandAllAction,
                onDeselectAll: brandDeselectAll,
                onItemAction: brandItemAction
            },
            {
                onSelectAll: statusAllAction,
                onDeselectAll: statusDeselectAll,
                onItemAction: statusItemAction
            }
        ];

        activate();

        function activate() {
            var campaignsPromise = CampaignsService.getList(),
                metricsPromise = CampaignsService.getListMetrics();

            vm.chartConfig = {
                chart: {
                    zoomType: CONSTANTS.CHARTING.STYLE.DEFAULT_ZOOM_TYPE
                },
                title: {
                    text: $translate.instant('campaigns.chart.title')
                },
                credits: {
                    enabled: false
                },
                xAxis: [
                        CONSTANTS.CHARTING.STYLE.DEFAULT_X_DATE_STYLE
                ],
                yAxis: [
                    { // Primary yAxis
                        title: {
                            text: $translate.instant('global.impressions'),
                            style: CONSTANTS.CHARTING.STYLE.PRIMARY_Y_STYLE
                        },
                        labels: {
                            style: CONSTANTS.CHARTING.STYLE.PRIMARY_Y_STYLE
                        }
                    }, { // Secondary yAxis
                        title: {
                            text: $translate.instant('global.conversions'),
                            style: CONSTANTS.CHARTING.STYLE.SECONDARY_Y_STYLE
                        },
                        labels: {
                            style: CONSTANTS.CHARTING.STYLE.SECONDARY_Y_STYLE
                        },
                        opposite: true
                    }
                ],
                loading: true,
                options: CONSTANTS.CHARTING.DEFAULT_OPTIONS
            };

            vm.promise = $q.all([campaignsPromise, metricsPromise]).then(function (promises) {
                var campaignData = promises[0],
                    metricsData = promises[1];

                if (metricsData.length > 0) {
                    // We need to augment the campaign list with the metrics result
                    vm.campaigns = lodash.map(campaignData, function (campaign) {
                        // Find the current campaign in the metrics
                        var data = CampaignsService.reduceMetricsData(metricsData.filter(function (item) {
                                return item.id === campaign.id;
                            }

                        ));

                        campaign.eCpa = data.eCpa();
                        campaign.cost = data.cost;
                        campaign.conversions = data.conversions;
                        return campaign;
                    });

                    vm.metrics = CampaignsService.buildKpiDisplay(metricsData);
                    buildChart(metricsData);
                }
                else {
                    vm.campaigns = campaignData;
                }

                loadFilters();
            });
        }

        function loadFilters() {
            var advertiserList = [],
                brandList = [],
                brandsOptions = [],
                statusList = [],
                auxFilterValues,
                auxOptionsFilter,
                storagesFilters;

            lodash.forEach(vm.campaigns, function (value) {
                mappingFilters(value);

                if (lodash.indexOf(advertiserList, value.advertiserName) < 0) {
                    advertiserList.push(value.advertiserName);
                }

                if (lodash.indexOf(brandList, value.brandName) < 0) {
                    brandList.push(value.brandName);
                }

                if (lodash.indexOf(statusList, value.isActiveDisplay) < 0) {
                    statusList.push(value.isActiveDisplay);
                }
            });

            vm.myMapAdvertiser = lodash.mapKeys(vm.myMapAdvertiser, function (value, key) {
                return key;
            });

            vm.myMapBrand = lodash.mapKeys(vm.myMapBrand, function (value, key) {
                return key;
            });

            vm.filterOption.ADVERTISER.value = lodash.clone(lodash.sortBy(advertiserList), true);
            vm.filterOption.BRAND.value = lodash.clone(lodash.sortBy(brandList), true);
            vm.filterOption.STATUS.value = lodash.clone(lodash.sortBy(statusList), true);

            storagesFilters = storage.get(CONSTANTS.CAMPAIGN.STORAGE.LIST_FILTER + '_' + UserService.getUsername());

            if (Utils.isUndefinedOrNull(storagesFilters)) {
                updateFilters(advertiserList, brandList, statusList);
            }
            else {
                auxFilterValues = JSON.parse(storagesFilters[0]);
                auxOptionsFilter = JSON.parse(storagesFilters[1]);

                if (lodash.isEqual(vm.filterOption.ADVERTISER.value, auxOptionsFilter.ADVERTISER.value)) {
                    brandsOptions = updateBrandOptions(auxFilterValues[0].values);

                    auxFilterValues[1].values = lodash.intersection(brandsOptions, auxFilterValues[1].values);

                    auxFilterValues[2].values = updateStatusOptions(auxFilterValues[1].values,
                        auxFilterValues[2].values);
                }
                else {
                    auxFilterValues[0].values = verifyFiltersStorage(vm.filterOption.ADVERTISER.value,
                        auxOptionsFilter.ADVERTISER.value, auxFilterValues[0].values);

                    brandsOptions = updateBrandOptions(auxFilterValues[0].values);

                    auxFilterValues[1].values = verifyFiltersStorage(brandsOptions,
                        auxOptionsFilter.BRAND.value, auxFilterValues[1].values);

                    auxFilterValues[2].values = updateStatusOptions(auxFilterValues[1].values,
                        auxFilterValues[2].values);
                }

                updateFilters(auxFilterValues[0].values, auxFilterValues[1].values, auxFilterValues[2].values);
            }
        }

        function updateBrandOptions(listAdvertiser) {
            var brandsOptions = [];

            lodash.forEach(vm.myMapBrand, function (value, id) {
                if (lodash.intersection(value.advertiser, listAdvertiser).length > 0) {
                    brandsOptions.push(id);
                }
            });

            vm.filterOption.BRAND.value = lodash.clone(lodash.sortBy(brandsOptions), true);

            return brandsOptions;
        }

        function updateStatusOptions(listBrand, statusStorage) {
            var status = [];

            lodash.forEach(vm.myMapBrand, function (value, id) {
                if (lodash.indexOf(listBrand, id) >= 0) {
                    status = lodash.union(status, value.status);
                }
            });

            vm.filterOption.STATUS.value = lodash.clone(lodash.sortBy(status), true);
            status = lodash.intersection(status, statusStorage);

            return status;
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

        function updateFilters(advertiserList, brandList, statusList) {
            vm.filterValues[0].values = advertiserList;
            vm.filterValues[1].values = brandList;
            vm.filterValues[2].values = statusList;
        }

        $scope.$watch('vm.filterValues', function (newValue, oldValue) {
            if (!lodash.isEqual(newValue, oldValue)) {
                storage.set(CONSTANTS.CAMPAIGN.STORAGE.LIST_FILTER + '_' + UserService.getUsername(),
                    [
                        JSON.stringify(newValue),
                        JSON.stringify(vm.filterOption)
                    ]);
            }
        }, true);

        //Update brands Dinamic.
        function updateBrand(newValue, oldValue) {
            var brandsSelected = [],
                brandsOptions = [],
                newValueSelect = lodash.difference(newValue[0].values, oldValue),
                selectAux;

            lodash.forEach(vm.myMapBrand, function (value, id) {
                if (lodash.intersection(value.advertiser, newValueSelect).length > 0) {
                    brandsSelected.push(id);
                }
            });

            lodash.forEach(vm.myMapBrand, function (value, id) {
                if (lodash.intersection(value.advertiser, newValue[0].values).length > 0) {
                    brandsOptions.push(id);
                }
            });

            selectAux = lodash.intersection(brandsOptions, newValue[1].values);

            lodash.forEach(brandsSelected, function (value) {
                if (lodash.indexOf(selectAux, value) < 0) {
                    selectAux.push(value);
                }
            });

            newValue[1].values = selectAux;
            vm.filterOption.BRAND.value = lodash.clone(lodash.sortBy(brandsOptions), true);

            updateStatus();
        }

        function updateStatus() {
            var status = [],
                newValue;

            lodash.forEach(vm.myMapBrand, function (value, id) {
                if (lodash.indexOf(vm.filterValues[1].values, id) >= 0) {
                    status = lodash.union(status, value.status);
                }
            });

            newValue = lodash.difference(status, vm.filterOption.STATUS.value);

            lodash.forEach(newValue, function (value) {
                vm.filterValues[2].values.push(value);
            });

            vm.filterValues[2].values = lodash.intersection(status, vm.filterValues[2].values);
            vm.filterOption.STATUS.value = lodash.clone(lodash.sortBy(status), true);
        }

        function refreshTable() {
            var campaignListBackup = [];

            angular.copy(vm.campaigns , campaignListBackup);
            vm.campaigns = [];
            angular.copy(campaignListBackup, vm.campaigns);
        }

        function buildChart(metrics) {
            var data = lodash.chain(metrics).map(function (item) {
                item.dateObj = new Date(item.day); // Normalize the date field
                return item;
            }).groupBy(function (item) {
                return item.dateObj; // Group by date for the x-axis
            }).map(function (item) {
                var response = CampaignsService.reduceMetricsData(item);

                response.dateObj = item && item[0] && item[0].dateObj;
                return response;
            }).map(function (item) {
                // Highcharts wont actually invoke the function so we need to do the calculation here
                item.ctr = item.ctr();
                return item;
            }).sortBy('dateObj');

            vm.chartConfig.xAxis[0].categories = data.map(function (item) {
                return item.dateObj;
            }).value();

            vm.chartConfig.series = [
                {
                    name: $translate.instant('global.impressions'),
                    type: 'spline',
                    color: CONSTANTS.CHARTING.STYLE.PRIMARY_Y_STYLE.color,
                    data: data.map(function (item) {
                        return item.impressions;
                    }).value()
                },
                {
                    name: $translate.instant('global.conversions'),
                    type: 'spline',
                    color: CONSTANTS.CHARTING.STYLE.SECONDARY_Y_STYLE.color,
                    yAxis: 1,
                    data: data.map(function (item) {
                        return item.conversions;
                    }).value()
                }
            ];

            vm.chartConfig.loading = false;
        }

        function go(campaign) {
            var params = {
                campaignId: campaign.id,
                campaign: {
                    id: campaign.id,
                    brandId: campaign.brandId,
                    advertiserId: campaign.advertiserId
                }
            };

            $state.go('campaign-details', params);
        }

        function addCampaign() {
            $state.go('add-campaign');
        }

        //LOAD MAPPING
        function mappingFilters(campaign) {
            var dataAdvertiser = vm.myMapAdvertiser[campaign.advertiserName],
                data,
                dataBrand;

            if (Utils.isUndefinedOrNull(dataAdvertiser)) {
                data = {
                    status: [],
                    brand: []
                };

                data.status.push(campaign.isActiveDisplay);
                data.brand.push(campaign.brandName);
                vm.myMapAdvertiser[campaign.advertiserName] = data;
            }
            else {
                if (lodash.indexOf(dataAdvertiser.status, campaign.isActiveDisplay) < 0) {
                    dataAdvertiser.status.push(campaign.isActiveDisplay);
                }

                if (lodash.indexOf(dataAdvertiser.brand, campaign.brandName) < 0) {
                    dataAdvertiser.brand.push(campaign.brandName);
                }
            }

            dataBrand = vm.myMapBrand[campaign.brandName];
            if (Utils.isUndefinedOrNull(dataBrand)) {
                data = {
                    status: [],
                    advertiser: []
                };

                data.status.push(campaign.isActiveDisplay);
                data.advertiser.push(campaign.advertiserName);
                vm.myMapBrand[campaign.brandName] = data;
            }
            else {
                if (lodash.indexOf(dataBrand.status, campaign.isActiveDisplay) < 0) {
                    dataBrand.status.push(campaign.isActiveDisplay);
                }

                if (lodash.indexOf(dataBrand.advertiser, campaign.advertiserName) < 0) {
                    dataBrand.advertiser.push(campaign.advertiserName);
                }
            }
        }

        //ADVERTISER ACTIONS
        function advertiserDeselectAll(isSelectedAll, oldFilterValues, isPartialDeselect) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                if (!Utils.isUndefinedOrNull(isPartialDeselect) && isPartialDeselect) {
                    updateBrand(vm.filterValues, oldFilterValues);
                    refreshTable();
                    return;
                }

                vm.filterValues[1].values = [];
                vm.filterOption.BRAND.value = [];
                vm.filterValues[2].values = [];
                vm.filterOption.STATUS.value = [];
                refreshTable();
            }
        }

        function advertiserAllAction(oldFilterValues) {
            updateBrand(vm.filterValues, oldFilterValues);
            refreshTable();
        }

        function advertiserItemAction(id, isSelectedAll, oldFilterValues) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                updateBrand(vm.filterValues, oldFilterValues);
                refreshTable();
            }
        }

        //BRAND ACTIONS
        function brandAllAction() {
            updateStatus();
            refreshTable();
        }

        function brandDeselectAll(isSelectedAll, oldFilterValues, isPartialDeselect) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                if (!Utils.isUndefinedOrNull(isPartialDeselect) && isPartialDeselect) {
                    refreshTable();
                    return;
                }

                vm.filterValues[2].values = [];
                vm.filterOption.STATUS.value = [];
                refreshTable();
            }
        }

        function brandItemAction(id, isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                updateStatus();
                refreshTable();
            }
        }

        //STATUS ACTIONS
        function statusAllAction() {
            refreshTable();
        }

        function statusDeselectAll(isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshTable();
            }
        }

        function statusItemAction(id, isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshTable();
            }
        }
    }
})();
