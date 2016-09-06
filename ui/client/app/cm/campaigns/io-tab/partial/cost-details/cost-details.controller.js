(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CostDetailsController', CostDetailsController);

    CostDetailsController.$inject = [
        '$filter',
        '$log',
        '$scope',
        '$translate',
        'CONSTANTS',
        'CostUtilService',
        'DateTimeService',
        'lodash'
    ];

    function CostDetailsController(
        $filter,
        $log,
        $scope,
        $translate,
        CONSTANTS,
        CostUtilService,
        DateTimeService,
        lodash) {
        $log.debug('Running CostDetailsController');
        var vm = this,
            limitTo = $filter('limitTo'),
            codeAdd = 0,
            listCost;

        vm.model = $scope.$parent.$parent.vmEdit.costDetails;
        vm.costForm = {};
        vm.dateOptions = {
            formatYear: CONSTANTS.DATE_PICKER.DATE_OPTIONS.FORMAT_YEAR,
            startingDay: 1
        };
        vm.format = CONSTANTS.DATE_FORMAT;
        vm.maxCostInventoryValue = CONSTANTS.COST_DETAIL.INVENTORY.MAX_VALUE;
        vm.maxInputNumber = Number.MAX_SAFE_INTEGER;
        vm.maxMarginValue = CONSTANTS.COST_DETAIL.MARGIN.MAX_VALUE;
        vm.placementConstants = CONSTANTS.COST_DETAIL;
        vm.rateTypeList = lodash.values(CONSTANTS.COST_DETAIL.RATE_TYPE.LIST);
        vm.showInfo = false;
        vm.totalItems = 0;

        vm.activate = activate;
        vm.addCost = addCost;
        vm.addRowForEndDate = addRowForEndDate;
        vm.calculateCostInventory = calculateCostInventory;
        vm.calculateNetValues = calculateNetValues;
        vm.disableFirstStartDate = disableFirstStartDate;
        vm.openEndDate = openEndDate;
        vm.openStartDate = openStartDate;
        vm.removeCost = removeCost;
        vm.updateDates = updateDates;
        vm.updateNetAdSpend = updateNetAdSpend;
        vm.updateNetRate = updateNetRate;

        function activate(costData) {
            var lastCost,
                costSorted,
                orderBy = $filter('orderBy');

            listCost = costData;
            costSorted = orderBy(listCost, 'startDate', false);
            angular.forEach(costSorted, function (val, index) {
                vm.model.push(
                    {
                        id: val.id,
                        costKey: index,
                        margin: val.margin,
                        plannedGrossAdSpend: val.plannedGrossAdSpend || CONSTANTS.COST_DETAIL.AD_SPEND.DEFAULT,
                        plannedGrossRate: val.plannedGrossRate || CONSTANTS.COST_DETAIL.RATE.DEFAULT,
                        inventory: val.inventory || CONSTANTS.COST_DETAIL.INVENTORY.DEFAULT,
                        plannedNetAdSpend: val.plannedNetAdSpend,
                        plannedNetRate: val.plannedNetRate,
                        rateType: CostUtilService.getRateType(val.rateType),
                        startDate: DateTimeService.parse(val.startDate),
                        endDate: DateTimeService.parse(val.endDate),
                        isLast: false
                    });
                codeAdd = index;
            });

            lastCost = vm.model[vm.model.length - 1];
            lastCost.isLast = true;
            lastCost.endDate = null;
            vm.totalItems = vm.model.length;
            updateListCost();
        }

        function addCost(addCostDetailEndDate) {
            var lastCost;

            if (vm.costForm.$invalid) {
                return;
            }

            lastCost = vm.costData[vm.costData.length - 1];
            lastCost.isLast = false;

            if (addCostDetailEndDate || lastCost.endDate === null) {
                lastCost.endDate = DateTimeService.getStartDate(
                    DateTimeService.getMoment(
                        lastCost.startDate).add(31, 'days').toDate()
                );
            }

            codeAdd++;
            vm.model.push(getNewCost(lastCost, codeAdd));
            vm.totalItems = vm.model.length;
            $scope.$parent.$parent.vmEdit.editForm.$dirty = true;
            $scope.$parent.$parent.vmEdit.editForm.$pristine = false;
            updateListCost();
        }

        function calculateCostInventory(cost) {
            var resultInventory =
                CostUtilService.calculateInventory(cost.plannedGrossAdSpend, cost.plannedGrossRate, cost.rateType),
                controlName = 'inventoryInput' + cost.costKey;

            if (resultInventory < CONSTANTS.COST_DETAIL.INVENTORY.MAX_VALUE) {
                if (typeof vm.costForm[controlName] !== 'undefined') {
                    vm.costForm[controlName].$setValidity('inventoryInvalid', true);
                }

                cost.inventory = resultInventory;
            }
            else {
                vm.costForm[controlName].$setValidity('inventoryInvalid', false);
                cost.inventory = $translate.instant('validation.error.invalid');
            }
        }

        function calculateNetValues(cost) {
            updateNetAdSpend(cost);
            updateNetRate(cost);
        }

        function convertToDate(modelDate, viewDate) {
            if (!angular.isDate(modelDate)) {
                var stringDate = viewDate.$viewValue;

                if (validateDates(new Date(stringDate)) && stringDate !== null) {
                    modelDate = new Date(stringDate);
                    viewDate.$setValidity('date', true);
                }
            }

            return modelDate;
        }

        function disableFirstStartDate(index) {
            return index === 0 && vm.totalItems > CONSTANTS.COST_DETAIL.MAX_ROW_DISPLAY;
        }

        function getNewCost(lastCost, codeIncrement) {
            return {
                costKey: codeIncrement,
                margin: lastCost.margin,
                plannedGrossAdSpend: CONSTANTS.COST_DETAIL.AD_SPEND.DEFAULT,
                plannedGrossRate: CONSTANTS.COST_DETAIL.RATE.DEFAULT,
                inventory: CONSTANTS.COST_DETAIL.INVENTORY.DEFAULT,
                plannedNetAdSpend: 0,
                plannedNetRate: 0,
                rateType: lastCost.rateType,
                isLast: true,
                endDate: null,
                startDate: DateTimeService.getStartDate(DateTimeService
                    .getMoment(lastCost.endDate).add(1, 'days').toDate())
            };
        }

        function open($event) {
            $event.preventDefault();
            $event.stopPropagation();
        }

        function openEndDate($event, cost) {
            open($event);
            closeDatePicker();
            cost.endDateOpened = true;
            cost.startDateOpened = false;
        }

        function openStartDate($event, cost) {
            open($event);
            closeDatePicker();
            cost.startDateOpened = true;
            cost.endDateOpened = false;
        }

        function closeDatePicker() {
            lodash.forEach(vm.model, function (cost) {
                cost.endDateOpened = false;
                cost.startDateOpened = false;
            });
        }

        function removeCost() {
            if (vm.totalItems > 2) {
                vm.model.pop();
                vm.totalItems = vm.model.length;
                var lastItem = vm.model.slice(-1).pop();

                lastItem.endDate = null;
                lastItem.isLast = true;
            }

            if (codeAdd > 0) {
                codeAdd--;
            }

            updateListCost();
            $scope.$parent.$parent.vmEdit.editForm.$dirty = true;
            $scope.$parent.$parent.vmEdit.editForm.$pristine = false;
        }

        $scope.$on('loadListCost', function (event, data) {
            if (data.activate) {
                activate(data.rootData);
            }
        });

        function updateDates(cost) {
            if (angular.isDefined(cost.costKey)) {
                cost.startDate = DateTimeService.getStartDate(convertToDate(cost.startDate,
                    vm.costForm['cost' + cost.costKey + 'StartDateInput']));
                cost.endDate = DateTimeService.getEndDate(convertToDate(cost.endDate,
                    vm.costForm['cost' + cost.costKey + 'EndDateInput']));
            }
            else {
                cost.startDate = DateTimeService.getStartDate(
                    convertToDate(
                        cost.startDate,
                        vm.costForm.costStartDateInput
                    )
                );
                cost.endDate = DateTimeService.getEndDate(
                    convertToDate(
                        cost.endDate,
                        vm.costForm.costEndDateInput
                    )
                );
            }
        }

        function addRowForEndDate(cost, addCostDetail) {
            if (vm.costForm.$invalid) {
                if (cost.isLast) {
                    cost.endDate = null;
                }

                return;
            }

            if (cost.isLast) {
                if (addCostDetail) {
                    addCost(!addCostDetail);
                }
                else {
                    cost.endDate = DateTimeService.getStartDate(
                        DateTimeService.getMoment(
                            cost.startDate).add(31, 'days').toDate()
                    );
                }
            }
        }

        function updateListCost() {
            if (vm.totalItems > 2) {
                vm.model[vm.model.length - 1].isLast = true;
            }

            vm.showInfo = vm.totalItems > 10;
            vm.costData = limitTo(vm.model, -10, vm.model.length);
        }

        function updateNetAdSpend(cost) {
            cost.netAdSpend = CostUtilService.calculateNetAdSpend(cost.adSpend, cost.margin);
        }

        function updateNetRate(cost) {
            cost.netRate = CostUtilService.calculateNetRate(cost.rate, cost.margin);
        }

        function validateDates(modelDate) {
            return !!(new Date(modelDate) !== 'Invalid Date' && !isNaN(new Date(modelDate)));
        }
    }
})();
