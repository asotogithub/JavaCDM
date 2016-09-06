(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('ImportDataController', ImportDataController);

    ImportDataController.$inject = [
        '$scope',
        '$translate',
        'CONSTANTS',
        'ErrorRequestHandler',
        'Utils'
    ];

    function ImportDataController(
        $scope,
        $translate,
        CONSTANTS,
        ErrorRequestHandler,
        Utils) {
        var status = {},
            uploadConfiguration = {},
            uuidFile = null,
            vm = this;

        vm.cancel = cancel;
        vm.importProcess = importProcess;
        vm.isActionInProgress = isActionInProgress;
        vm.isProgress = false;
        vm.isValidModel = false;
        vm.popupText = {};
        vm.promise = null;
        vm.status = {};
        vm.upload = upload;

        activate();

        $scope.$on('import-data.cancel', function () {
            cancel();
        });

        function activate() {
            initializeModel();
        }

        function cancel() {
            cancelUploading();
            vm.onClose();
        }

        function cancelUploading() {
            if (vm.uploading.upload !== null) {
                vm.uploading.isCancelByUser = true;
                vm.uploading.upload.abort();
            }
        }

        function importProcess() {
            if (!Utils.isUndefinedOrNull(uuidFile)) {
                vm.promise = vm.onImportResource({
                    uuid: uuidFile
                });
                validateActions(false);

                return vm.promise.then(function (success) {
                    var rowsUpdated = parseInt(success.message.split(' ')[0].replace(',', ''));

                    cancelUploading();
                    success.info = {};
                    success.info.importSuccess = true;
                    success.info.rowsUpdated = rowsUpdated;
                    vm.onClose({
                        importData: success
                    });
                }).catch(function (response) {
                    if (Utils.isUndefinedOrNull(response.data.error)) {
                        validateActions(true);
                        vm.onImportError({
                            message: $translate.instant('global.errors.failure')
                        });
                    }
                    else {
                        response.info = {};
                        response.info.importSuccess = false;
                        response.info.uuid = uuidFile;
                        importShowError(response);
                    }
                });
            }
        }

        function importShowError(response) {
            if (!Utils.isUndefinedOrNull(response.data) && !Utils.isUndefinedOrNull(response.data.total) &&
                response.data.total > 0) {
                response.data.error = angular.isArray(response.data.error) ?
                    response.data.error : [response.data.error];
                cancelUploading();
                vm.onClose({
                    importData: response
                });
            }
            else if (response.data.error.code.$ === '414' || response.data.error.code.$ === '417') {
                validateActions(true);
                vm.onImportError({
                    message: response.data.error.message
                });
            }
            else {
                validateActions(true);
                ErrorRequestHandler.handle($translate.instant('global.cannotImport'), response.data.error, function () {
                    vm.cancel();
                });
            }
        }

        function initializeModel() {
            uuidFile = null;

            vm.popupText.description = vm.options.description;
            vm.popupText.url = vm.options.url;
            vm.popupText.urlTitle = vm.options.urlTitle;
            status.emptyFile = vm.options.statusEmptyFile;
            status.exceedFileOption = vm.options.statusExceedFileOption;
            status.limitSize = vm.options.statusLimitSize;
            status.unknown = vm.options.statusUnknown;
            status.unsupportedFormat = vm.options.statusUnsupportedFormat;

            uploadConfiguration.fileType = vm.options.fileType;
            uploadConfiguration.limitSize = vm.options.limitSize;

            vm.popupText.cancelButton = $translate.instant('global.cancel');
            vm.popupText.dragDrop = $translate.instant('global.dropYourFile');
            vm.popupText.note = $translate.instant('global.note');
            vm.popupText.importButton = $translate.instant('global.import');
            vm.popupText.openButton = $translate.instant('global.single');

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
            var validFileLimit = file.size <= uploadConfiguration.limitSize,
                validFileType,
                validFileSize = file.size > 0,
                str = angular.lowercase(file.name),
                fileType = str.split(/[\s.]+/);

            fileType = fileType[fileType.length - 1];
            validFileType = fileType === uploadConfiguration.fileType;

            if (!validFileType) {
                resultStatus(file, CONSTANTS.CREATIVE.STATUS_UPLOAD.UNSUPPORTED_FORMAT);
            }
            else if (!validFileSize) {
                resultStatus(file, CONSTANTS.CREATIVE.STATUS_UPLOAD.EMPTY_FILE);
            }
            else if (!validFileLimit) {
                resultStatus(file, status.exceedFileOption);
            }

            return validFileSize && validFileType && validFileLimit;
        }

        function resultStatus(file, codeStatus) {
            switch (codeStatus) {
                case CONSTANTS.CREATIVE.STATUS_UPLOAD.UNSUPPORTED_FORMAT:
                    file.status = status.unsupportedFormat;
                    break;
                case CONSTANTS.CREATIVE.STATUS_UPLOAD.EMPTY_FILE:
                    file.status = status.emptyFile;
                    break;
                case status.exceedFileOption:
                    file.status = status.limitSize;
                    break;
                default:
                    file.status = status.unknown;
            }
        }

        function upload($files) {
            uuidFile = null;
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
                        vm.onUploadError({
                            message: fileImport.status
                        });
                    }
                });
            }
        }

        function uploadingFile(file) {
            vm.uploading.upload = vm.onUploadResource({
                file: file
            });
            vm.uploading.upload.progress(
                function (evt) {
                    file.progress = parseInt(100.0 * evt.loaded / evt.total);
                }).success(function (success) {
                    if (!Utils.isUndefinedOrNull(success.message)) {
                        uuidFile = success.message;
                        vm.onUploadSuccess();
                        vm.isProgress = false;
                        validate();
                        vm.uploading.upload = null;
                    }
                }).error(function (response) {
                    vm.uploading.upload = null;
                    if (!vm.uploading.isCancelByUser && response) {
                        var message = angular.isDefined(response.error) && angular.isDefined(response.error.message) ?
                            response.error.message : $translate.instant('global.errors.failure');

                        vm.onUploadError({
                            message: message
                        });
                    }
                });
        }

        function validate() {
            vm.isValidModel = vm.file && !Utils.isUndefinedOrNull(vm.file.name);
        }

        function validateActions(isValid) {
            vm.isValidModel = isValid;
        }
    }
})();
