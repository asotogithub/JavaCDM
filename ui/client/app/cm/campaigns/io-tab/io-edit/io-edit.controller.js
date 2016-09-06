(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignIoEditController', CampaignIoEditController);

    CampaignIoEditController.$inject = [
        '$q',
        '$scope',
        '$state',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CampaignsUtilService',
        'DialogFactory',
        'InsertionOrderService',
        'lodash'
    ];

    function CampaignIoEditController(
        $q,
        $scope,
        $state,
        $stateParams,
        $translate,
        CONSTANTS,
        CampaignsUtilService,
        DialogFactory,
        InsertionOrderService,
        lodash) {
        var vm = this;

        vm.cancel = cancel;
        vm.save = save;
        vm.validations = {
            alphanumeric: CONSTANTS.REGEX.ASCII_PRINTABLE,
            dateFormat: CONSTANTS.DATE.DATE_FORMAT,
            numeric: CONSTANTS.REGEX.NUMERIC,
            maxIONumber: CONSTANTS.DB_DATA_TYPE_LIMITS.INT.MAX,
            maxLength: CONSTANTS.INSERTION_ORDER.NAME.MAX_LENGTH,
            maxLengthNotes: CONSTANTS.INSERTION_ORDER.NOTES.MAX_LENGTH
        };

        activate();

        function activate() {
            vm.promise = $q.all([
                InsertionOrderService.getInsertionOrder($stateParams.ioId)
            ]).then(function (promises) {
                $scope.$parent.vm.io = promises[0];
                watchParentsIO();
                watchStatusChanges();
            });
        }

        function cancel() {
            $state.go('insertion-order-tab', {
                campaignId: vm.io.campaignId
            });
        }

        function formatInput(io) {
            vm.statusList = CampaignsUtilService.getIOStatusList();
            if (!angular.equals(io.status, vm.statusList.NEW.key)) {
                delete vm.statusList.NEW;
            }

            vm.statusList = lodash.values(vm.statusList);
            io.status = lodash.find(vm.statusList, function (item) {
                return item.key === io.status;
            });

            io.ioNumber = parseInt(io.ioNumber);
        }

        function formatOutput(io) {
            io.status = io.status.key;
        }

        function save(io) {
            formatOutput(io);
            vm.promise = InsertionOrderService.updateInsertionOrder(io);
            vm.promise.then(
                function (response) {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                    $scope.$parent.vm.ioName = vm.io.name;
                    $scope.$parent.vm.activate();
                    vm.io = response;
                    formatInput(response);
                    if (vm.editForm) {
                        vm.editForm.$setPristine();
                    }
                });
        }

        function watchParentsIO() {
            $scope.$watch('vm.io', function (newValue) {
                if (newValue && !vm.io) {
                    vm.io = angular.copy(newValue);
                    $scope.$parent.vm.ioName = vm.io.name;
                    formatInput(vm.io);
                }
            });
        }

        function watchStatusChanges() {
            $scope.$watch('vmEdit.io.status', function (newStatus, oldStatus) {
                if (angular.isDefined(newStatus) && angular.isDefined(oldStatus)) {
                    showStatusWarning(newStatus, oldStatus);
                }
            });
        }

        function showStatusWarning(newStatus, oldStatus) {
            var warningDescription = '',
                showWarning = false; // This flag is needed due in some cases 'planning' is the default status.

            if (newStatus.name === 'global.inactive' && oldStatus.name === 'global.active') {
                warningDescription = $translate.instant('insertionOrder.toInactiveStatusWarning');
                showWarning = true;
            }
            else {
                if (newStatus.name === 'global.active' && oldStatus.name === 'global.inactive') {
                    warningDescription = $translate.instant('insertionOrder.toActiveStatusWarning');
                    showWarning = true;
                }
            }

            if (showWarning) {
                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.INFORMATIONAL,
                    title: $translate.instant('global.warning'),
                    description: warningDescription
                });
            }
        }
    }
})();
