(function () {
    'use strict';

    angular.module('uiApp')
        .controller('ScheduleImportController', ScheduleImportController);

    ScheduleImportController.$inject = [
        '$modalInstance',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'DialogFactory',
        'ErrorRequestHandler',
        'data'
    ];

    function ScheduleImportController(
        $modalInstance,
        $translate,
        CONSTANTS,
        CampaignsService,
        DialogFactory,
        ErrorRequestHandler,
        data) {
        var LIMIT_SIZE_MSG = $translate.instant('global.exceedSizeFile', {
                size: (CONSTANTS.SCHEDULE.STATUS_UPLOAD.LIMIT_SIZE / 1024).toLocaleString()
            }),
            vm = this;

        vm.cancel = cancel;
        vm.importBulk = importBulk;
        vm.isActionInProgress = isActionInProgress;
        vm.isProgress = false;
        vm.isValidModel = false;
        vm.promise = null;
        vm.upload = upload;

        activate();

        function activate() {
            initializeModel();
        }

        function importBulk() {
            if (angular.isDefined(data.uuid) && data.uuid !== null) {
                vm.promise = CampaignsService.importResource(data.campaignId,
                    CONSTANTS.SCHEDULE.FILE_TYPE.CREATIVE_INSERTION, data.uuid, false);
                validateActions(false);

                return vm.promise.then(function (success) {
                    var rowsUpdated = parseInt(success.message.split(' ')[0]);

                    cancelUploading();
                    success.info = {};
                    success.info.importSuccess = true;
                    success.info.rowsUpdated = rowsUpdated;
                    closeModal(success);
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                        rowsUpdated === 0 ? $translate.instant('schedule.import.noChanges') :
                            $translate.instant('schedule.import.success', {
                                rows: rowsUpdated
                            }));
                }).catch(function (response) {
                    if (angular.isDefined(response.data.error) && response.data.error !== null) {
                        response.info = {};
                        response.info.importSuccess = false;
                        response.info.uuid = data.uuid;
                        importShowError(response);
                    }
                    else {
                        validateActions(true);
                        showDialogError(response);
                    }
                });
            }
        }

        function importShowError(response) {
            if (angular.isDefined(response.data) && angular.isDefined(response.data.total) &&
                response.data.total !== null) {
                response.data.error = angular.isArray(response.data.error) ?
                    response.data.error : [response.data.error];
                cancelUploading();
                closeModal(response);
            }
            else if (response.data.error.code.$ === '414') {
                validateActions(true);
                DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.ERROR,
                    response.data.error.message);
            }
            else {
                validateActions(true);
                showDialogError(response);
            }
        }

        function cancel() {
            cancelUploading();
            closeModal();
        }

        function cancelUploading() {
            if (vm.uploading.upload !== null) {
                vm.uploading.isCancelByUser = true;
                vm.uploading.upload.abort();
            }
        }

        function closeModal(importData) {
            if (importData) {
                $modalInstance.close(importData);
            }
            else {
                $modalInstance.dismiss();
            }
        }

        function initializeModel() {
            vm.uploading = {
                upload: null,
                isCancelByUser: false
            };
            vm.isValidModel = false;
        }

        function isActionInProgress() {
            return vm.promise && vm.promise.$$state && vm.promise.$$state.status === 0;
        }

        function isValidFile(file) {
            var validFileLimit = file.size <= CONSTANTS.SCHEDULE.STATUS_UPLOAD.LIMIT_SIZE,
                validFileType,
                validFileSize = file.size > 0,
                str = angular.lowercase(file.name),
                fileType = str.split(/[\s.]+/);

            fileType = fileType[fileType.length - 1];
            validFileType = fileType === CONSTANTS.SCHEDULE.FILE_TYPE.XLSX;

            if (!validFileType) {
                resultStatus(file, CONSTANTS.CREATIVE.STATUS_UPLOAD.UNSUPPORTED_FORMAT);
            }
            else if (!validFileSize) {
                resultStatus(file, CONSTANTS.CREATIVE.STATUS_UPLOAD.EMPTY_FILE);
            }
            else if (!validFileLimit) {
                resultStatus(file, CONSTANTS.SCHEDULE.STATUS_UPLOAD.EXCEED_FILE_OPTION);
            }

            return validFileSize && validFileType && validFileLimit;
        }

        function resultStatus(file, status) {
            switch (status) {
                case CONSTANTS.CREATIVE.STATUS_UPLOAD.UNSUPPORTED_FORMAT:
                    file.status = $translate.instant('schedule.upload.status.unsupportedFormat');
                    break;
                case CONSTANTS.CREATIVE.STATUS_UPLOAD.EMPTY_FILE:
                    file.status = $translate.instant('schedule.upload.status.emptyFile');
                    break;
                case CONSTANTS.SCHEDULE.STATUS_UPLOAD.EXCEED_FILE_OPTION:
                    file.status = LIMIT_SIZE_MSG;
                    break;
                default:
                    file.status = $translate.instant('schedule.upload.status.unknown');
            }
        }

        function showDialogError(error) {
            ErrorRequestHandler.handle('Cannot import uploaded file', error, function () {
                vm.cancel();
            });

            vm.isValidModel = true;
        }

        function upload($files) {
            cancelUploading();
            vm.isValidModel = false;
            if ($files && $files.length) {
                vm.isProgress = true;
                vm.uploading.isCancelByUser = false;
                angular.forEach($files, function (fileImport) {
                    if (isValidFile(fileImport)) {
                        uploadingFile(fileImport);
                    }
                    else {
                        vm.isProgress = false;
                        cancelUploading();
                        DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.ERROR, fileImport.status);
                    }
                });
            }
        }

        function uploadingFile(file) {
            vm.uploading.upload = CampaignsService.uploadResource(data.campaignId, file,
                CONSTANTS.SCHEDULE.FILE_TYPE.CREATIVE_INSERTION);
            vm.uploading.upload.progress(
                function (evt) {
                    var progressPorcentage = parseInt(100.0 * evt.loaded / evt.total);

                    file.progress = progressPorcentage;
                }).success(function (success) {
                    if (angular.isDefined(success.message) && success.message !== null) {
                        DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                            $translate.instant('info.operationCompleted'));
                        data.uuid = success.message;
                        vm.isProgress = false;
                        validate();
                        vm.uploading.upload = null;
                    }
                }).catch(function (response) {
                    vm.uploading.upload = null;
                    if (!vm.uploading.isCancelByUser && response) {
                        ErrorRequestHandler.handleAndReject('Cannot upload file', null, function () {
                                vm.cancel();
                            })(response);
                    }
                });
        }

        function validate() {
            vm.isValidModel = vm.file && angular.isDefined(vm.file.name) && vm.file.name !== null;
        }

        function validateActions(isValid) {
            vm.isValidModel = isValid;
        }
    }
})();
