(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AdmReporting', AdmReporting);

    AdmReporting.$inject = [
        '$interpolate',
        '$log',
        '$rootScope',
        '$scope',
        '$stateParams',
        '$templateCache',
        '$timeout',
        '$translate',
        '$window',
        'CONSTANTS',
        'DatasetService',
        'DateTimeService',
        'lodash'
    ];

    function AdmReporting(
        $interpolate,
        $log,
        $rootScope,
        $scope,
        $stateParams,
        $templateCache,
        $timeout,
        $translate,
        $window,
        CONSTANTS,
        DatasetService,
        DateTimeService,
        lodash) {
        var vm = this;

        vm.radialProgressStyle = CONSTANTS.RADIAL_PROGRESS.STYLE;
        vm.reloadMetrics = reloadMetrics;
        vm.showPreciseCurrent = showPreciseCurrent;
        vm.activate = activate;
        vm.formatDate = CONSTANTS.DATE_FORMAT;
        vm.startDate = DateTimeService.getStartDate(new Date());
        vm.endDate = DateTimeService.getEndDate(
            DateTimeService.getMoment(new Date()).add(CONSTANTS.CAMPAIGN.DATES.DAYS, 'days').toDate());
        vm.datesForm = {};
        vm.dateOptions = {
            formatYear: CONSTANTS.DATE_PICKER.DATE_OPTIONS.FORMAT_YEAR,
            startingDay: 1
        };
        vm.openStartDate = openStartDate;
        vm.openEndDate = openEndDate;
        vm.updateDates = updateDates;

        $scope.$on('adm.dataset.metrics', function (event, metricsData) {
            activate(metricsData);
        });

        function activate(metricsData) {
            var metrics = metricsData.metrics;

            toggleMin();
            vm.validMetrics = metrics && metrics.summary && metrics.summary.engagements > 0;

            if (!vm.validMetrics) {
                $log.warn('Invalid dataset metrics. Using dataset ID: %s', metricsData.dataSetId);
                return;
            }

            configureSummaryChart(metrics);
            configureChart(metrics);

            vm.startDate = vm.chartConfig.xAxis[0].categories[0];
            vm.endDate = vm.chartConfig.xAxis[0].categories[vm.chartConfig.xAxis[0].categories.length - 1];
            vm.lastDays = $translate.instant('global.lastDays',
                {
                    day: DateTimeService.getDateDifference(vm.startDate, vm.endDate)
                }
            );

            vm.metrics = metrics;
        }

        function configureSummaryChart(metrics) {
            vm.radialProgress = CONSTANTS.RADIAL_PROGRESS.OPTIONS;
            vm.radialProgress.current = round(metrics.summary.matchRate * 100, 0);
        }

        function configureChart(metrics) {
            vm.chartConfig = {
                chart: {
                    zoomType: CONSTANTS.CHARTING.STYLE.DEFAULT_ZOOM_TYPE
                },
                title: {
                    text: $translate.instant('adm.details.chart.title')
                },
                credits: {
                    enabled: false
                },
                xAxis: [
                    CONSTANTS.CHARTING.STYLE.DEFAULT_X_DATE_STYLE
                ],
                yAxis: [
                    {
                        title: {
                            text: $translate.instant('global.engagements'),
                            style: CONSTANTS.CHARTING.STYLE.PRIMARY_Y_STYLE
                        },
                        labels: {
                            style: CONSTANTS.CHARTING.STYLE.PRIMARY_Y_STYLE
                        },
                        min: 0
                    }, {
                        title: {
                            text: $translate.instant('global.matchRate'),
                            style: CONSTANTS.CHARTING.STYLE.SECONDARY_Y_STYLE
                        },
                        labels: {
                            format: '{value}%',
                            style: CONSTANTS.CHARTING.STYLE.SECONDARY_Y_STYLE
                        },
                        min: 0,
                        opposite: true
                    }
                ],
                loading: true,
                options: CONSTANTS.CHARTING.DEFAULT_OPTIONS
            };

            vm.chartConfig.options.tooltip.formatter = function () {
                var template = $templateCache.get('tool-tip-chart.html'),
                    scope = $rootScope.$new(true);

                scope.vm = {
                    dateToolTip: DateTimeService.formatDate(
                        this.x, CONSTANTS.DATE.MOMENT.DATE_DAY_TIME_FULL,
                        CONSTANTS.TIMEZONE.DEFAULT),
                    engagements: this.y,
                    matchRate: this.points[1].y
                };

                return $interpolate(template)(scope);
            };

            var data = lodash.chain(metrics.dates[0].date).map(function (item) {
                item.dateObj = DateTimeService.parse(item.date);
                return item;
            }).sortBy('dateObj');

            vm.chartConfig.xAxis[0].categories = data.map(function (item) {
                return item.dateObj;
            }).value();

            vm.chartConfig.series = [
                {
                    name: $translate.instant('global.engagements'),
                    type: 'column',
                    color: CONSTANTS.CHARTING.STYLE.PRIMARY_Y_STYLE.color,
                    data: data.map(function (item) {
                        return item.engagements;
                    }).value()
                },
                {
                    name: $translate.instant('global.matchRate'),
                    type: 'line',
                    color: CONSTANTS.CHARTING.STYLE.SECONDARY_Y_STYLE.color,
                    yAxis: 1,
                    data: data.map(function (item) {
                        return round(item.matchRate * 100, 0);
                    }).value(),
                    tooltip: {
                        valueSuffix: '%'
                    }
                }
            ];
            vm.chartConfig.loading = false;
        }

        function round(value, decimals) {
            return parseFloat(value.toFixed(decimals));
        }

        function showPreciseCurrent(amount) {
            $timeout(function () {
                if (amount <= 0) {
                    vm.radialProgress.preciseCurrent = vm.radialProgress.current;
                }
                else {
                    var math = $window.Math;

                    vm.radialProgress.preciseCurrent = math.min(math.round(amount), vm.radialProgress.max);
                }
            });
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

        function open($event) {
            $event.preventDefault();
            $event.stopPropagation();
        }

        function openEndDate($event) {
            open($event);
            vm.endDateOpened = true;
            vm.startDateOpened = false;
        }

        function openStartDate($event) {
            open($event);
            vm.startDateOpened = true;
            vm.endDateOpened = false;
        }

        function updateDates() {
            vm.startDate = DateTimeService.getStartDate(convertToDate(vm.startDate, vm.datesForm.startDate));
            vm.endDate = DateTimeService.getEndDate(convertToDate(vm.endDate, vm.datesForm.endDate));
        }

        function toggleMin() {
            vm.minDate = DateTimeService.getDate(new Date());
        }

        function validateDates(modelDate) {
            return !!(new Date(modelDate) !== 'Invalid Date' && !isNaN(new Date(modelDate)));
        }

        function reloadMetrics() {
            if (!angular.isUndefined(vm.startDate) && !angular.isUndefined(vm.endDate)) {
                if (isValidDateInterval(vm.startDate, vm.endDate)) {
                    var datasetMetricsPromise = DatasetService.getDatasetMetrics(
                        $stateParams.id,
                        DateTimeService.inverseParse(DateTimeService.getStartDate(vm.startDate)),
                        DateTimeService.inverseParse(DateTimeService.getEndDate(vm.endDate)));

                    $scope.$parent.$parent.vm.promise = datasetMetricsPromise.then(function (response) {
                        var result = {
                            metrics: response
                        };

                        activate(result);
                    });
                }
            }
        }

        function isValidDateInterval(startDate, endDate) {
            return DateTimeService.isBeforeOrEqual(
                startDate,
                endDate);
        }
    }
})();
