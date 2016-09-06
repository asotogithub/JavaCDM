(function () {
    'use strict';

    angular.module('uiApp')
        .controller('ScheduleImportErrorsController', ScheduleImportErrorsController);

    ScheduleImportErrorsController.$inject = [
        '$modalInstance',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'DialogFactory',
        'ErrorRequestHandler',
        'data'
    ];

    function ScheduleImportErrorsController(
        $modalInstance,
        $translate,
        CONSTANTS,
        CampaignsService,
        DialogFactory,
        ErrorRequestHandler,
        data) {
        var vm = this;

        vm.cancel = cancel;
        vm.completeBulk = completeBulk;
        vm.exportIssues = exportIssues;
        vm.exportIssuesSuccessful = false;
        vm.issues = [];
        vm.isActionInProgress = isActionInProgress;
        vm.isValidModel = true;
        vm.pageSize = CONSTANTS.TE_TABLE.PAGE_SIZE;
        vm.promise = null;
        vm.rowsErrors = 0;
        vm.rowsUpdate = 0;
        vm.totalRows = 0;

        activate();

        function activate() {
            initializeModel();
        }

        function completeBulk() {
            if (angular.isDefined(data.uuid) && data.uuid !== null) {
                vm.promise = CampaignsService.importResource(data.campaignId,
                    CONSTANTS.SCHEDULE.FILE_TYPE.CREATIVE_INSERTION, data.uuid, true);
                validateActions(false);

                return vm.promise.then(function (success) {
                    var rowsUpdated = parseInt(success.message.split(' ')[0]);

                    success.status = true;
                    success.rowsUpdated = rowsUpdated;
                    closeModal(success);
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                        rowsUpdated === 0 ? $translate.instant('schedule.import.noChanges') :
                            $translate.instant('schedule.import.success', {
                                rows: rowsUpdated
                            }));
                }).catch(function (error) {
                    validateActions(true);
                    ErrorRequestHandler.handle('Cannot complete import', error, function () {
                        $modalInstance.dismiss();
                    });
                });
            }
        }

        function cancel() {
            closeModal();
        }

        function closeModal(responseStatus) {
            if (responseStatus) {
                $modalInstance.close(responseStatus);
            }
            else {
                $modalInstance.dismiss();
            }
        }

        function exportIssues() {
            if (vm.issues.length > 0) {
                vm.promise = CampaignsService.exportIssuesResource(data.campaignId, CONSTANTS.SCHEDULE.FILE_TYPE.XLSX,
                    CONSTANTS.SCHEDULE.FILE_TYPE.CREATIVE_INSERTION, data.uuid);

                return vm.promise.then(function (response) {
                    var blob = new Blob([response.data],
                        {
                            type: response.type
                        });

                    saveAs(blob, response.name);
                    vm.exportIssuesSuccessful = true;
                    vm.promise = null;
                });
            }
        }

        function initializeModel() {
            vm.rowsErrors = data.response.invalidCount;
            vm.totalRows = data.response.total;
            vm.rowsUpdate = data.response.validCount;
            vm.isValidComplete = parseInt(data.response.validCount) > 0;

            angular.forEach(data.response.error, function (error) {
                vm.issues.push({
                    row: parseInt(error.field),
                    description: error.message
                });
            });
        }

        function isActionInProgress() {
            return vm.promise && vm.promise.$$state && vm.promise.$$state.status === 0;
        }

        function validateActions(isValid) {
            vm.isValidComplete = isValid;
        }
    }
})();
