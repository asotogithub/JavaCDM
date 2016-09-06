(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AdmListController', AdmListController);

    AdmListController.$inject = [
        '$q',
        '$scope',
        '$state',
        '$translate',
        'AdmService',
        'DatasetService',
        'DateTimeService',
        'Utils',
        'CONSTANTS'
    ];

    function AdmListController(
        $q,
        $scope,
        $state,
        $translate,
        AdmService,
        DatasetService,
        DateTimeService,
        Utils,
        CONSTANTS) {
        var vm = this,
            currentDay = new Date(),
            defaultStartDate = new Date(CONSTANTS.ADM_CONSTANTS.METRICS.DEFAULT.START_DATE),
            viewChange = false;

        vm.STATUS = {
            ACTIVE: $translate.instant('global.active'),
            INACTIVE: $translate.instant('global.inactive')
        };
        vm.Utils = Utils;
        vm.goToDetails = goToDetails;
        vm.formatDateView = CONSTANTS.DATE.DATE_FORMAT;

        activate();

        function activate() {
            vm.promise = AdmService.getADMDatasetConfigs();
            vm.promise.then(function (response) {
                vm.model = response;
                getMetrics(CONSTANTS.ADM_CONSTANTS.METRICS.START_INTERVAL, CONSTANTS.ADM_CONSTANTS.METRICS.INTERVAL);
            });
        }

        $scope.$on('$stateChangeStart', function () {
            viewChange = true;
        });

        function getMetrics(startInterval, interval) {
            var arrayPromise = getArrayMetricsPromises(startInterval, interval),
                newStartInterval = startInterval + interval;

            $q.all(arrayPromise).then(function (promises) {
                    addPropertiesItem(startInterval, interval, newStartInterval, false, promises);
                },

                function () {
                    addPropertiesItem(startInterval, interval, newStartInterval, true);
                });
        }

        function addPropertiesItem(startInterval, interval, newStartInterval, error, promises) {
            var cont = 0,
                i;

            for (i = startInterval; i < newStartInterval; i++) {
                if (Utils.isUndefinedOrNull(vm.model[i])) {
                    break;
                }

                if (error) {
                    vm.model[i].error = error;
                }
                else {
                    vm.model[i].matchRate = promises[cont].summary.matchRate;
                    vm.model[i].attempts = promises[cont].summary.engagements;
                    cont++;
                }
            }

            if (!viewChange && newStartInterval <= vm.model.length) {
                getMetrics(newStartInterval, interval);
            }
        }

        function getArrayMetricsPromises(startInterval, interval) {
            var startDate = DateTimeService.inverseParse(DateTimeService.getStartDate(defaultStartDate)),
                endDate = DateTimeService.inverseParse(DateTimeService.getEndDate(currentDay)),
                arrayPromise = [],
                index;

            for (index = startInterval; index < startInterval + interval; index++) {
                if (!Utils.isUndefinedOrNull(vm.model[index])) {
                    arrayPromise.push(
                        DatasetService.getDatasetMetrics(
                            vm.model[index].datasetId,
                            startDate,
                            endDate
                        )
                    );
                }
            }

            return arrayPromise;
        }

        function goToDetails(adm) {
            $state.go('adm-details', {
                id: adm.datasetId
            });
        }
    }
})();
