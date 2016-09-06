(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignDetailsTabController', CampaignDetailsTabController);

    CampaignDetailsTabController.$inject = [
        '$log',
        '$q',
        '$scope',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'DateTimeService',
        'DialogFactory',
        'ErrorHandlingService',
        'UserService',
        'lodash'
    ];

    function CampaignDetailsTabController(
        $log,
        $q,
        $scope,
        $stateParams,
        $translate,
        CONSTANTS,
        CampaignsService,
        DateTimeService,
        DialogFactory,
        ErrorHandlingService,
        UserService,
        lodash) {
        var vm = this,
            promise = $q.all([
                CampaignsService.getCampaignDetails($stateParams.campaignId),
                UserService.getDomains(),
                CampaignsService.getCampaignMetrics($stateParams.campaignId)
            ]);

        vm.CAMPAIGN_MAX_LENGTH = CONSTANTS.CAMPAIGN.NAME.MAX_LENGTH;
        vm.DATE_FORMAT = CONSTANTS.DATE_FORMAT;
        vm.DATE_FORMAT_FULL = CONSTANTS.DATE.MOMENT.DATE_FULL;
        vm.domains = [];
        vm.campaignDetailsForm = {};
        vm.dateOptions = {
            formatYear: 'yy',
            startingDay: 1
        };

        vm.selectedDomain = {};
        vm.submit = submit;
        vm.openStartDate = openStartDate;
        vm.openEndDate = openEndDate;
        vm.updateDates = updateDates;
        vm.validateBudget = validateBudget;

        vm.promise = promise;

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

        function buildChart(metrics) {
            var data = lodash.chain(metrics).groupBy(function (item) {
                return item.day; // Group by date for the x-axis
            }).map(function (item) {
                var response = CampaignsService.reduceMetricsData(item);

                response.day = item && item[0] && item[0].day;
                return response;
            }).sortBy('day');

            vm.chartConfig.xAxis[0].categories = data.map(function (item) {
                return new Date(item.day);
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

        promise.then(
            function (args) {
                var campaignDetails = args[0], // From CampaignsService.getCampaingDetails()
                    domains = args[1], // From UserService.getDomains()
                    metrics = args[2],// From CampaignService.getCampaignMetrics()
                    campaign = campaignDetails.campaign,
                    cookieDomainId = campaign && campaign.cookieDomainId,
                    selectedDomain = cookieDomainId && lodash.findWhere(domains, {
                        id: cookieDomainId
                    });

                //Parse to date objects
                campaignDetails.campaign = applyDateOffset(campaignDetails.campaign);
                if ($scope.$parent !== null && $scope.$parent.vm !== null) {
                    $scope.$parent.vm.campaignName = campaignDetails.campaign.name;
                }

                if (cookieDomainId && !selectedDomain) {
                    selectedDomain = {
                        id: cookieDomainId,
                        domain: campaign.domain
                    };
                    domains.unshift(selectedDomain);
                }

                angular.extend(vm, campaignDetails);
                vm.selectedDomain = selectedDomain;
                vm.domains = domains;
                if (angular.isArray(metrics) && metrics.length > 0) {
                    vm.metrics = metrics;
                    buildChart(metrics);
                }
            });

        function submit() {
            var _promise,
                payload = angular.copy(vm.campaign);

            payload.startDate = DateTimeService.inverseParse(payload.startDate);
            payload.endDate = DateTimeService.inverseParse(payload.endDate);
            _promise = CampaignsService.updateCampaignDetails(angular.extend({
                cookieDomainId: vm.selectedDomain.id
            }, lodash.pick(payload, [
                'advertiserId',
                'agencyId',
                'brandId',
                'description',
                'endDate',
                'id',
                'isActive',
                'logicalDelete',
                'name',
                'startDate',
                'statusId',
                'overallBudget'
            ])));

            vm.promise = _promise;

            _promise.then(
                function (response) {
                    $translate('info.operationCompleted').then(function (message) {
                        $.bootstrapGrowl(message);
                    });

                    vm.campaign = applyDateOffset(response);
                    vm.campaignDetailsForm.$setPristine();
                    vm.errorMessage = null;
                    $scope.$parent.vm.campaignName = vm.campaign.name;

                    reloadDomains();
                },

                function (error) {
                    var errorMessage = error && error.data && error.data.errors &&
                                       ErrorHandlingService.getErrorMessage(error.data.errors);

                    vm.errorMessage = errorMessage;

                    if (!errorMessage) {
                        DialogFactory.showDialog(CONSTANTS.DIALOG.TYPE.ERROR);
                        $log.error('Cannot get campaign: ' + angular.toJson(error));
                    }
                });
        }

        activate();

        function activate() {
            toggleMin();
        }

        function toggleMin() {
            vm.minDate = DateTimeService.getDate(new Date());
        }

        function openStartDate($event) {
            open($event);
            vm.startDateOpened = true;
            vm.endDateOpened = false;
        }

        function openEndDate($event) {
            open($event);
            vm.endDateOpened = true;
            vm.startDateOpened = false;
        }

        function open($event) {
            $event.preventDefault();
            $event.stopPropagation();
        }

        function reloadDomains() {
            var promises = $q.all([
                    CampaignsService.getCampaignDetails($scope.campaignId),
                    UserService.getDomains()
                ]);

            vm.promise = promises;
            vm.promise.then(function (args) {
                var campaignDetails = args[0],
                    domains = args[1],
                    campaign = campaignDetails.campaign,
                    cookieDomainId = campaign && campaign.cookieDomainId,
                    selectedDomain = cookieDomainId && lodash.findWhere(domains, {
                        id: cookieDomainId
                    });

                if (cookieDomainId && !selectedDomain) {
                    selectedDomain = {
                        id: cookieDomainId,
                        domain: campaign.domain
                    };
                    domains.unshift(selectedDomain);
                }

                vm.selectedDomain = selectedDomain;
                vm.domains = domains;
            });
        }

        function applyDateOffset(campaign) {
            campaign.startDate = DateTimeService.parse(campaign.startDate);
            campaign.endDate = DateTimeService.parse(campaign.endDate);
            campaign.createdDate = DateTimeService.parse(campaign.createdDate);
            return campaign;
        }

        function updateDates() {
            vm.campaign.startDate = DateTimeService.getStartDate(
                convertToDate(vm.campaign.startDate, vm.campaignDetailsForm.startDate));
            vm.campaign.endDate = DateTimeService.getEndDate(
                convertToDate(vm.campaign.endDate, vm.campaignDetailsForm.endDate));
        }

        function convertToDate(modelDate, viewDate) {
            if (!angular.isDate(modelDate)) {
                var stringDate = viewDate.$viewValue;

                if (validateDates(new Date(stringDate))) {
                    modelDate = new Date(stringDate);
                    viewDate.$setValidity('date', true);
                }
            }

            return modelDate;
        }

        function validateDates(modelDate) {
            return !!(new Date(modelDate) !== 'Invalid Date' && !isNaN(new Date(modelDate)));
        }

        function validateBudget(budget) {
            if (budget) {
                var compareMin = parseFloat(budget),
                    compareMax = parseFloat(CONSTANTS.CAMPAIGN.BUDGET.MAX);

                if (budget < CONSTANTS.CAMPAIGN.BUDGET.MIN || parseFloat(compareMin) > parseFloat(compareMax)) {
                    vm.campaignDetailsForm.budget.$setValidity('budgetInvalid', false);
                }
                else {
                    vm.campaignDetailsForm.budget.$setValidity('budgetInvalid', true);
                    vm.campaign.overallBudget = budget;
                }
            }
        }
    }
})();
