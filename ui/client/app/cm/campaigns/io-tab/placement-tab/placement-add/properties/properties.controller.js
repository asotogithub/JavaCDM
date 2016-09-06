(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddPlacementPropertiesController', AddPlacementPropertiesController);

    AddPlacementPropertiesController.$inject = [
        '$log',
        '$scope',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CostUtilService',
        'DateTimeService',
        'PlacementUtilService',
        'Utils',
        'lodash'
    ];

    function AddPlacementPropertiesController(
        $log,
        $scope,
        $stateParams,
        $translate,
        CONSTANTS,
        CostUtilService,
        DateTimeService,
        PlacementUtilService,
        Utils,
        lodash) {
        $log.debug('Running AddPlacementPropertiesController');
        var vm = this;

        vm.REGEX_PLACEMENT_NAME = CONSTANTS.REGEX.PLACEMENT_SECTION_SITE_NAME;
        vm.placementConstants = CONSTANTS.COST_DETAIL;
        vm.propertiesForm = {};
        vm.propertiesListReadonly = [];
        vm.propertiesParsedList = [];
        vm.rateTypeList = [];
        vm.rateType = '';
        vm.step = $scope.$parent.vmAdd.STEP.PROPERTIES;
        vm.DATE_FORMAT_FULL = CONSTANTS.DATE.MOMENT.DATE_FULL;
        vm.DATE_FORMAT = CONSTANTS.DATE_FORMAT;
        vm.startDate = DateTimeService.getStartDate(new Date());
        vm.endDate = DateTimeService.getEndDate(
            DateTimeService.getMoment(new Date()).add(CONSTANTS.PLACEMENT.DATES.DAYS, 'days').toDate());
        vm.dateOptions = {
            formatYear: CONSTANTS.DATE_PICKER.DATE_OPTIONS.FORMAT_YEAR,
            startingDay: 1
        };
        vm.maxInputNumber = Number.MAX_SAFE_INTEGER;

        vm.applyToAll = applyToAll;
        vm.openStartDate = openStartDate;
        vm.openEndDate = openEndDate;
        vm.updateDates = updateDates;
        vm.activate = activate;
        vm.calculateInventory = calculateInventory;
        vm.rateTypeList = lodash.values(CONSTANTS.COST_DETAIL.RATE_TYPE.LIST);

        vm.nameMaxLength = CONSTANTS.INPUT.MAX_LENGTH.PLACEMENT_NAME;
        vm.maxCostInventoryValue = CONSTANTS.COST_DETAIL.INVENTORY.MAX_VALUE;

        vm.onVerifyPlacementName = onVerifyPlacementName;
        vm.onChangePlacementName = onChangePlacementName;
        vm.placementCharacters = $translate.instant('placement.addPlacementNameTooltip', {
            characters: vm.nameMaxLength
        });

        function activate(newList) {
            fillList(newList);
            breakDownProperties();
            toggleMin();
            resetApplyToRow();
        }

        function calculateInventory(placement) {
            var resultInventory =
                    CostUtilService.calculateInventory(placement.adSpend, placement.rate, placement.rateType),
                nameControl = 'inventoryInput' + placement.customKey;

            if (resultInventory < CONSTANTS.COST_DETAIL.INVENTORY.MAX_VALUE) {
                vm.propertiesForm[nameControl].$setValidity('inventoryInvalid', true);
                placement.inventory = resultInventory;
            }
            else {
                vm.propertiesForm[nameControl].$setValidity('inventoryInvalid', false);
                placement.inventory = $translate.instant('validation.error.invalid');
            }
        }

        function fillList(newList) {
            vm.propertiesListReadonly = newList;
        }

        function resetApplyToRow() {
            vm.headerRow = {};
        }

        function breakDownProperties() {
            vm.propertiesParsedList = PlacementUtilService.parserPlacementList(
                vm.propertiesListReadonly,
                $stateParams.campaignId,
                $stateParams.ioId);

            var customKey = 0;

            lodash.forEach(vm.propertiesParsedList, function (placement, i) {
                // Adding rate, rateType and adSpend properties
                if (!placement.rate) {
                    vm.propertiesParsedList[i].rate = CONSTANTS.COST_DETAIL.RATE.DEFAULT;
                }

                if (!placement.rateType) {
                    vm.propertiesParsedList[i].rateType = CostUtilService.getDefaultRateType();
                }

                if (!placement.adSpend) {
                    vm.propertiesParsedList[i].adSpend = CONSTANTS.COST_DETAIL.AD_SPEND.DEFAULT;
                }

                placement.startDate = vm.startDate;
                placement.endDate = vm.endDate;
                placement.customKey = customKey++;
            });

            vm.propertiesParsedList.sort(function (placement1, placement2) {
                var package1 = placement1.packageName.toLowerCase(),
                    package2 = placement2.packageName.toLowerCase(),
                    name1 = placement1.name.toLowerCase(),
                    name2 = placement2.name.toLowerCase();

                if (package1 === package2) {
                    return (name1 > name2) - (name1 < name2);
                }
                else {
                    if (package1 === '') {
                        return 1;
                    }

                    if (package2 === '') {
                        return -1;
                    }

                    return (package1 > package2) - (package1 < package2);
                }
            });

            lodash.forEach(vm.propertiesParsedList, function (placement, i) {
                if (placement.packageName === '') {
                    placement.isChild = false;
                }
                else {
                    if (i > 0 && placement.packageName &&
                        placement.packageName !== vm.propertiesParsedList[i - 1].packageName) {
                        placement.isChild = false;
                    }
                    else {
                        placement.isChild = i !== 0;
                    }
                }
            });

            $scope.$parent.vmAdd.placementList = vm.propertiesParsedList;
        }

        function convertToDate(modelDate, viewDate) {
            if (!angular.isDate(modelDate)) {
                var stringDate = new Date(viewDate.$viewValue);

                if (validateDates(stringDate)) {
                    modelDate = stringDate;
                    viewDate.$setValidity('date', true);
                }
            }

            return modelDate;
        }

        function open($event) {
            $event.preventDefault();
            $event.stopPropagation();
        }

        function openEndDate($event, placement) {
            open($event);
            closeDatePicker();
            placement.endDateOpened = true;
            placement.startDateOpened = false;
        }

        function openStartDate($event, placement) {
            open($event);
            closeDatePicker();
            placement.startDateOpened = true;
            placement.endDateOpened = false;
        }

        function closeDatePicker() {
            vm.headerRow.endDateOpened = false;
            vm.headerRow.startDateOpened = false;
            lodash.forEach(vm.propertiesParsedList, function (placement) {
                placement.endDateOpened = false;
                placement.startDateOpened = false;
            });
        }

        function updateDates(placement) {
            if (angular.isDefined(placement.customKey)) {
                placement.startDate = convertToDate(placement.startDate,
                    vm.propertiesForm['placement' + placement.customKey + 'StartDateInput']);
                placement.endDate = convertToDate(placement.endDate,
                    vm.propertiesForm['placement' + placement.customKey + 'EndDateInput']);
            }
            else {
                placement.startDate = convertToDate(placement.startDate, vm.propertiesForm.placementStartDateInput);
                placement.endDate = convertToDate(placement.endDate, vm.propertiesForm.placementEndDateInput);
            }

            if (angular.isDefined(placement.startDate)) {
                placement.startDate = DateTimeService.getStartDate(placement.startDate);
            }

            if (angular.isDefined(placement.endDate)) {
                placement.getDate = DateTimeService.getEndDate(placement.endDate);
            }
        }

        function toggleMin() {
            vm.minDate = DateTimeService.getDate(new Date());
        }

        function validateDates(modelDate) {
            return !!(new Date(modelDate) !== 'Invalid Date' && !isNaN(new Date(modelDate)));
        }

        function applyToAll() {
            lodash.forEach(vm.propertiesParsedList, function (placement) {
                if (!Utils.isUndefinedOrNull(vm.headerRow.adSpend)) {
                    placement.adSpend = vm.headerRow.adSpend;
                }

                if (!Utils.isUndefinedOrNull(vm.headerRow.rate)) {
                    placement.rate = vm.headerRow.rate;
                }

                if (!Utils.isUndefinedOrNull(vm.headerRow.rateType)) {
                    placement.rateType = vm.headerRow.rateType;
                }

                if (!Utils.isUndefinedOrNull(vm.headerRow.startDate)) {
                    placement.startDate = vm.headerRow.startDate;
                }

                if (!Utils.isUndefinedOrNull(vm.headerRow.endDate)) {
                    placement.endDate = vm.headerRow.endDate;
                }

                calculateInventory(placement);
            });

            resetApplyToRow();
        }

        function onVerifyPlacementName(placement) {
            if (Utils.isUndefinedOrNull(placement.name)) {
                placement.showErrorName = true;
            }
            else {
                if (placement.name.length === 0) {
                    placement.name = PlacementUtilService.generatePlacementName(placement);
                    vm.propertiesForm['placement' + placement.customKey + 'Name'].$setValidity('nameInvalid', true);
                }

                placement.showErrorName = false;
            }
        }

        function onChangePlacementName(placement) {
            if (Utils.isUndefinedOrNull(placement.name)) {
                placement.showErrorName = true;
            }
            else {
                placement.showErrorName = false;

                if (placement.name.length > CONSTANTS.INPUT.MAX_LENGTH.PLACEMENT_NAME) {
                    placement.name = placement.name.slice(0, CONSTANTS.INPUT.MAX_LENGTH.PLACEMENT_NAME);
                }
                else {
                    vm.propertiesForm['placement' + placement.customKey + 'Name'].$setValidity('nameInvalid',
                            Utils.isUndefinedOrNull(placement.name) || placement.name.length > 0);
                }
            }
        }

        $scope.$on(vm.step.key, function (event, data) {
            if (data.activate) {
                vm.propertiesParsedList = [];
                activate($scope.$parent.vmAdd.placementListDetails);
            }
        });

        $scope.$watch('vm.propertiesForm.$valid', function (newVal) {
            vm.step.isValid = newVal;
        });
    }
})();
