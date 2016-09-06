(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddCampaignDatesController', AddCampaignDatesController);

    AddCampaignDatesController.$inject = [
        '$filter',
        '$log',
        '$scope',
        'CONSTANTS',
        'DateTimeService'
    ];

    function AddCampaignDatesController(
        $filter,
        $log,
        $scope,
        CONSTANTS,
        DateTimeService) {
        $log.debug('Running AddCampaignDatesController');
        var vm = this;

        vm.format = CONSTANTS.DATE_FORMAT;
        vm.startDate = DateTimeService.getStartDate(new Date());
        vm.endDate = DateTimeService.getEndDate(
            DateTimeService.getMoment(new Date()).add(CONSTANTS.CAMPAIGN.DATES.DAYS, 'days').toDate());
        vm.datesForm = {};
        vm.step = $scope.$parent.vmCampaign.STEP.DATES_BUDGET;
        vm.dateOptions = {
            formatYear: CONSTANTS.DATE_PICKER.DATE_OPTIONS.FORMAT_YEAR,
            startingDay: 1
        };

        vm.openStartDate = openStartDate;
        vm.openEndDate = openEndDate;
        vm.updateDates = updateDates;
        vm.validateBudget = validateBudget;

        function activate() {
            toggleMin();
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

        function setValueToCampaign() {
            $scope.$parent.vmCampaign.campaign.startDate = vm.startDate;
            $scope.$parent.vmCampaign.campaign.endDate = vm.endDate;
            $scope.$parent.vmCampaign.campaign.budget = vm.budget;
        }

        function updateDates() {
            vm.startDate = DateTimeService.getStartDate(convertToDate(vm.startDate, vm.datesForm.startDate));
            vm.endDate = DateTimeService.getEndDate(convertToDate(vm.endDate, vm.datesForm.endDate));
            setValueToCampaign();
        }

        function toggleMin() {
            vm.minDate = DateTimeService.getDate(new Date());
        }

        function validateDates(modelDate) {
            return !!(new Date(modelDate) !== 'Invalid Date' && !isNaN(new Date(modelDate)));
        }

        function validateBudget(budget) {
            if (budget) {
                var compareMin = parseFloat(budget),
                    compareMax = parseFloat(CONSTANTS.CAMPAIGN.BUDGET.MAX);

                if (budget < CONSTANTS.CAMPAIGN.BUDGET.MIN || parseFloat(compareMin) > parseFloat(compareMax)) {
                    vm.datesForm.budget.$setValidity('budgetInvalid', false);
                }
                else {
                    vm.datesForm.budget.$setValidity('budgetInvalid', true);
                    setValueToCampaign();
                }
            }
        }

        $scope.$on(vm.step.key, function (event, data) {
            if (data.activate) {
                activate();
            }
        });

        $scope.$watch('vm.datesForm.$valid', function (newVal) {
            vm.step.isValid = newVal;
        });
    }
})();
