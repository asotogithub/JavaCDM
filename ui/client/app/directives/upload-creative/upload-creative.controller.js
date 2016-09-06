(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('UploadCreativeController', UploadCreativeController);

    UploadCreativeController.$inject = [
        '$log',
        '$q',
        '$scope',
        '$stateParams',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'DialogFactory',
        'Utils',
        'lodash'
    ];

    function UploadCreativeController(
        $log,
        $q,
        $scope,
        $stateParams,
        $translate,
        CONSTANTS,
        CampaignsService,
        DialogFactory,
        Utils,
        lodash) {
        $log.debug('Running UploadCreativeController');
        var vm = this,
            creativeListBackup = [],
            isUploadingFiles = false;

        vm.creativeList = [];
        vm.creativeVersionedList = [];
        vm.files = [];
        vm.listUpload = [];

        vm.addCreativeToList = addCreativeToList;
        vm.addExistingAction = addExistingAction;
        vm.deleteAllCreatives = deleteAllCreatives;
        vm.deleteCreative = deleteCreative;
        vm.deleteSingleCreative = deleteSingleCreative;
        vm.pageSize = CONSTANTS.TE_TABLE.PAGE_SIZE;
        vm.statusUpload = CONSTANTS.CREATIVE.STATUS_UPLOAD;
        vm.upload = upload;
        vm.aliasValidate = aliasValidate;
        vm.invalidCreativesList = [];

        $scope.$watch('vm.creativesModel.length', function (size) {
            if (size) {
                validate();
            }
        });

        $scope.$on('validateAllAlias', function () {
            angular.forEach(vm.creativeVersionedList, function (creative) {
                aliasValidate(creative);
            });
        });

        function addCreativeToList(file) {
            var newCreative = {
                alias: null,
                filename: file.name,
                file: file,
                isValid: true
            };

            lodash.remove(vm.creativeList, function (creative) {
                return creative.filename === file.name;
            });

            lodash.remove(vm.creativeVersionedList, function (creative) {
                return creative.filename === file.name;
            });

            vm.creativeList.push(newCreative);
        }

        function addExistingAction() {
            vm.addExisting();
        }

        function allSettled(promises) {
            var wrappedPromises = angular.isArray(promises) ? promises.slice(0) : {};

            angular.forEach(promises, function (promise, index) {
                wrappedPromises[index] = promise.then(function (value) {
                        return {
                            state: true,
                            value: value
                        };
                    },

                    function (reason) {
                        return {
                            state: false,
                            reason: reason
                        };
                    });
            });

            return $q.all(wrappedPromises);
        }

        function deleteAllCreatives(creativeList) {
            angular.forEach(creativeList, function (creative) {
                vm.creativesModel.splice(lodash.findIndex(vm.creativesModel,
                    {
                        filename: creative.filename
                    }), 1);
            });

            creativeList.length = 0;
            validate();
            refreshTable();
        }

        function deleteCreative(creativeList, creative) {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant(creative ?
                    'creative.confirm.removeCreative' : 'creative.confirm.removeAllCreatives'),
                buttons: creative ? CONSTANTS.DIALOG.BUTTON_SET.REMOVE_NO : CONSTANTS.DIALOG.BUTTON_SET.YES_NO
            }).result.then(
                function () {
                    if (creative) {
                        deleteSingleCreative(creativeList, creative);
                    }

                    else {
                        deleteAllCreatives(creativeList);
                    }
                });
        }

        function deleteSingleCreative(creativeList, creative) {
            var findCreative = {},
                indexCreative;

            angular.forEach(vm.creativesModel, function (newCreative) {
                if (creative.filename === newCreative.filename) {
                    findCreative = newCreative;
                }
            });

            indexCreative = vm.creativesModel.indexOf(findCreative);

            if (indexCreative >= 0) {
                vm.creativesModel.splice(indexCreative, 1);
            }

            creativeList.splice(creativeList.indexOf(creative), 1);
            validate();
            refreshTable();
        }

        function isValidFile(file) {
            var validFileType,
                str = angular.lowercase(file.name),
                fileType = str.split(/[\s.]+/);

            fileType = fileType[fileType.length - 1];
            validFileType = fileType === CONSTANTS.CREATIVE.FILE_TYPE.GIF ||
                fileType === CONSTANTS.CREATIVE.FILE_TYPE.JPEG ||
                fileType === CONSTANTS.CREATIVE.FILE_TYPE.JPG ||
                fileType === CONSTANTS.CREATIVE.FILE_TYPE.THIRD_PARTY ||
                fileType === CONSTANTS.CREATIVE.FILE_TYPE.TXT ||
                fileType === CONSTANTS.CREATIVE.FILE_TYPE.XML ||
                fileType === CONSTANTS.CREATIVE.FILE_TYPE.ZIP;

            if (!validFileType) {
                angular.forEach(vm.creativeList, function (creative) {
                    if (creative.filename === file.name) {
                        resultStatus(creative, vm.statusUpload.UNSUPPORTED_FORMAT);
                    }
                });
            }

            return validFileType;
        }

        function refreshTable() {
            angular.copy(vm.creativeList, creativeListBackup);
            vm.creativeList = [];
            angular.copy(creativeListBackup, vm.creativeList);

            angular.copy(vm.creativeVersionedList, creativeListBackup);
            vm.creativeVersionedList = [];
            angular.copy(creativeListBackup, vm.creativeVersionedList);
        }

        function removeDuplicate(filename) {
            var result;

            result = lodash.find(vm.creativesModel, function (item) {
                return item.filename === filename;
            });

            if (result) {
                vm.creativesModel.splice(vm.creativesModel.indexOf(result), 1);
            }
        }

        function resultStatus(creative, status) {
            creative.file.status = status;
            switch (status) {
                case vm.statusUpload.SUCCESS:
                    creative.file.statusTooltip = $translate.instant('creative.upload.status.success');
                    break;
                case vm.statusUpload.BAD_REQUEST:
                    creative.file.statusTooltip = $translate.instant('creative.upload.status.badRequest');
                    break;
                case vm.statusUpload.DUPLICATE:
                    creative.file.statusTooltip = $translate.instant('creative.upload.status.duplicate');
                    break;
                case vm.statusUpload.UNKNOWN:
                    creative.file.statusTooltip = $translate.instant('creative.upload.status.unknown');
                    break;
                case vm.statusUpload.INTERNAL_ERROR:
                    creative.file.statusTooltip = $translate.instant('creative.upload.status.internalError');
                    break;
                case vm.statusUpload.UNSUPPORTED_FORMAT:
                    creative.file.statusTooltip = $translate.instant('creative.upload.status.unsupportedFormat');
                    break;
                case vm.statusUpload.EMPTY_FILE:
                    creative.file.statusTooltip = $translate.instant('creative.upload.status.emptyFile');
                    break;
                default:
                    creative.file.statusTooltip = $translate.instant('creative.upload.status.unknown');
            }
        }

        function setCreative(data, status) {
            angular.forEach(vm.creativeList, function (creative) {
                if (creative.filename === data.filename) {
                    resultStatus(creative, status);
                    creative.agencyId = data.agencyId;
                    creative.alias = data.alias;
                    creative.campaignId = data.campaignId;
                    creative.creativeType = data.creativeType;
                    creative.filename = data.filename;
                    creative.height = data.height;
                    creative.id = data.id;
                    creative.width = data.width;
                    creative.isValid = true;
                }
            });

            if (!Utils.isUndefinedOrNull(data.id)) {
                var isLastVersion = Utils.isUndefinedOrNull(data.versions) ? 0 :
                        data.versions[data.versions.length - 1].isDateSet,
                    indexCreative = lodash.findIndex(vm.creativeList,
                        {
                            filename: data.filename
                        }),
                    newCreativeVersioned;

                if (Utils.isUndefinedOrNull(data.versions) ||
                    isLastVersion === 0 && data.creativeVersion === 2 && data.versions.length === 1) {
                    data.creativeVersion = 1;
                    data.alias = data.alias;
                }
                else {
                    data.alias = data.alias + CONSTANTS.CREATIVE.VERSIONING_SUFFIX + data.creativeVersion;
                }

                if (indexCreative !== -1) {
                    newCreativeVersioned = vm.creativeList[indexCreative];
                    newCreativeVersioned.alias = data.alias;
                    newCreativeVersioned.filename = data.filename;
                    newCreativeVersioned.height = data.height;
                    newCreativeVersioned.creativeVersion = data.creativeVersion;
                    newCreativeVersioned.versions = Utils.isUndefinedOrNull(data.versions) ? [] : data.versions;

                    aliasValidate(vm.creativeVersionedList[vm.creativeVersionedList.push(newCreativeVersioned) - 1]);

                    deleteSingleCreative(vm.creativeList, vm.creativeList[indexCreative]);
                }
            }
        }

        function upload($files) {
            var arrayPromise = [];

            refreshTable();
            vm.isValidModel = false;
            isUploadingFiles = true;
            if ($files && $files.length) {
                angular.forEach($files, function (newCreative) {
                    vm.addCreativeToList(newCreative);
                    removeDuplicate(newCreative.name);
                    if (isValidFile(newCreative)) {
                        arrayPromise.push(uploadingCreative(newCreative));
                    }
                });
            }

            allSettled(arrayPromise).then(function () {
                isUploadingFiles = false;
                refreshTable();
            });
        }

        function uploadingCreative(newCreative) {
            var uploading = CampaignsService.uploadCreative($stateParams.campaignId, newCreative);

            vm.listUpload.push(uploading);
            uploading.progress(
                function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);

                    newCreative.progress = progressPercentage;
                }).success(
                function (data, status) {
                    setCreative(data, status);
                    removeDuplicate(data.filename);
                    vm.creativesModel.push(data);
                }).error(
                function (data, status) {
                    if (data) {
                        if (data.error) {
                            newCreative.status = data.error.message;
                        }
                        else {
                            angular.forEach(vm.creativeList, function (creative) {
                                if (creative.filename === newCreative.name) {
                                    resultStatus(creative, status);
                                }
                            });
                        }
                    }
                    else {
                        angular.forEach(vm.creativeList, function (creative) {
                            if (creative.filename === newCreative.name) {
                                resultStatus(creative, vm.statusUpload.UNKNOWN);
                            }
                        });
                    }
                });

            return uploading.then();
        }

        function validate() {
            vm.isValidModel = !isUploadingFiles &&
                vm.creativesModel &&
                vm.creativesModel.length > 0 &&
                vm.invalidCreativesList.length === 0;
        }

        function aliasValidate(creative) {
            var payloadCreative;

            if (Utils.isUndefinedOrNull(creative.alias)) {
                creative.isValid = false;
                creative.isDuplicate = false;
                vm.invalidCreativesList = lodash.union(vm.invalidCreativesList, [creative.filename]);
            }
            else {
                if (!creative.isValid) {
                    lodash.remove(vm.invalidCreativesList, function (n) {
                        return n === creative.filename;
                    });
                }

                creative.isValid = true;

                if (lodash.findIndex(creative.versions,
                        {
                            alias: creative.alias,
                            isDateSet: 1
                        }) === -1) {
                    creative.isDuplicate = false;
                    lodash.remove(vm.invalidCreativesList, function (n) {
                        return n === creative.filename;
                    });

                    payloadCreative = lodash.find(vm.creativesModel, {
                        filename: creative.filename
                    });
                }
                else {
                    creative.isDuplicate = true;
                    vm.invalidCreativesList = lodash.union(vm.invalidCreativesList, [creative.filename]);
                }

                if (!Utils.isUndefinedOrNull(payloadCreative)) {
                    payloadCreative.alias = creative.alias;
                }
            }

            validate();
        }
    }
})();
