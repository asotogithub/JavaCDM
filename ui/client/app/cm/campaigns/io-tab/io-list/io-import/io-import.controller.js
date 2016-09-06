(function () {
    'use strict';

    angular.module('uiApp')
        .controller('IOImportController', IOImportController);

    IOImportController.$inject = [
        '$scope',
        '$translate',
        '$uibModalInstance',
        'CONSTANTS',
        'CampaignsService',
        'DialogFactory',
        'Utils',
        'data'
    ];

    function IOImportController(
        $scope,
        $translate,
        $uibModalInstance,
        CONSTANTS,
        CampaignsService,
        DialogFactory,
        Utils,
        data) {
        var vm = this;

        vm.cancel = cancel;
        vm.close = close;
        vm.importError = importError;
        vm.importMedia = importMedia;
        vm.isActionInProgress = isActionInProgress;
        vm.promise = null;
        vm.uploadError = uploadError;
        vm.uploadMedia = uploadMedia;
        vm.uploadSuccess = uploadSuccess;

        activate();

        function activate() {
            initializeModel();
        }

        function cancel() {
            $scope.$broadcast('import-data.cancel');
        }

        function close(importData) {
            if (importData) {
                if (!Utils.isUndefinedOrNull(importData.info) &&
                    !Utils.isUndefinedOrNull(importData.info.importSuccess) && importData.info.importSuccess &&
                    !Utils.isUndefinedOrNull(importData.info.rowsUpdated)) {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                        importData.info.rowsUpdated === 0 ? $translate.instant('media.import.noChanges') :
                            $translate.instant('media.import.rowsUpdated', {
                            rows: importData.info.rowsUpdated.toLocaleString()
                        }));
                }

                $uibModalInstance.close(importData);
            }
            else {
                $uibModalInstance.dismiss();
            }
        }

        function importError(message) {
            showGlobalValidationError(message);
        }

        function importMedia(uuid) {
            vm.promise = CampaignsService.importResource(data.campaignId,
                CONSTANTS.INSERTION_ORDER.FILE_TYPE.MEDIA_INSERTION, uuid, false);

            return vm.promise;
        }

        function initializeModel() {
            vm.options = {
                description: $translate.instant('media.import.description'),
                fileType: CONSTANTS.INSERTION_ORDER.FILE_TYPE.XLSX,
                limitSize: CONSTANTS.INSERTION_ORDER.STATUS_UPLOAD.LIMIT_SIZE,
                url: CONSTANTS.INSERTION_ORDER.IMPORT.TEMPLATE_URL,
                urlTitle: $translate.instant('media.import.urlTitle'),
                statusEmptyFile: $translate.instant('media.import.status.cannotBeEmpty'),
                statusExceedFileOption: CONSTANTS.INSERTION_ORDER.STATUS_UPLOAD.EXCEED_FILE_OPTION,
                statusLimitSize: $translate.instant('global.exceedSizeFile', {
                        size: (CONSTANTS.INSERTION_ORDER.STATUS_UPLOAD.LIMIT_SIZE / 1024).toLocaleString()
                    }),
                statusUnknown: $translate.instant('media.import.status.cannotUpload'),
                statusUnsupportedFormat: $translate.instant('media.import.status.unsupportedFormat')
            };
        }

        function isActionInProgress() {
            return vm.promise && vm.promise.$$state && vm.promise.$$state.status === 0;
        }

        function showGlobalValidationError(message) {
            var wURL = 'app/cm/campaigns/io-tab/io-list/io-import/global-validation-warning-dialog.html';

            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                partialHTML: wURL,
                partialHTMLParams: {
                    message: message
                }
            });
        }

        function uploadError(message) {
            showGlobalValidationError(message);
        }

        function uploadMedia(file) {
            return CampaignsService.uploadResource(data.campaignId, file,
                CONSTANTS.INSERTION_ORDER.FILE_TYPE.MEDIA_INSERTION);
        }

        function uploadSuccess() {
            DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                $translate.instant('info.operationCompleted'));
        }
    }
})();
