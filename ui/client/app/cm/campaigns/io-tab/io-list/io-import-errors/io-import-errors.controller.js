(function () {
    'use strict';

    angular.module('uiApp')
        .controller('IOImportErrorsController', IOImportErrorsController);

    IOImportErrorsController.$inject = [
        '$q',
        '$translate',
        '$uibModalInstance',
        'CONSTANTS',
        'CampaignsService',
        'UserService',
        'Utils',
        'data',
        'lodash'
    ];

    function IOImportErrorsController(
        $q,
        $translate,
        $uibModalInstance,
        CONSTANTS,
        CampaignsService,
        UserService,
        Utils,
        data,
        lodash) {
        var vm = this;

        vm.ACTIONS_IMPORT = CONSTANTS.ACTIONS_IMPORT;
        vm.ISSUE_TYPE = CONSTANTS.INSERTION_ORDER.IMPORT.ISSUE_TYPE;
        vm.closeModal = closeModal;
        vm.config = {};
        vm.completeBulk = completeBulk;
        vm.exportIssues = exportIssues;
        vm.hasExportPermission = UserService.hasPermission(CONSTANTS.PERMISSION.MEDIA.EXPORT_MEDIA_ISSUES);
        vm.buildModelActions = buildModelActions;
        vm.model = {
            actions: [],
            errors: [],
            warnings: []
        };
        vm.payloadActions = [];

        activate();

        function activate() {
            var actionsList;

            vm.config.invalidRows = data.response.invalidCount;
            vm.config.totalRows = data.response.total;
            vm.config.rowsUpdate = data.response.validCount;
            vm.config.isValidComplete = parseInt(data.response.validCount) > 0;
            vm.config.pageSize = CONSTANTS.TE_TABLE.PAGE_SIZE;
            vm.config.uuid = data.uuid;
            vm.config.campaignId = data.campaignId;

            angular.forEach(data.response.error, function (error) {
                if (error.type === vm.ISSUE_TYPE.ERROR) {
                    vm.model.errors.push({
                        row: parseInt(error.rownum),
                        description: error.message
                    });
                }
                else if (error.type === vm.ISSUE_TYPE.WARNING) {
                    if (Utils.isUndefinedOrNull(error.options)) {
                        vm.model.warnings.push({
                            description: error.message,
                            row: parseInt(error.rownum)

                        });
                    }
                    else {
                        actionsList = getActionsList(error.options);

                        vm.model.actions.push({
                            description: error.message,
                            inAppType: error.inAppType,
                            options: actionsList,
                            row: parseInt(error.rownum),
                            actionSelected: Utils.isUndefinedOrNull(error.defaultOption) ? actionsList[0] :
                                lodash.find(actionsList, function (item) {
                                    return item.action === error.defaultOption;
                                })
                        });
                    }
                }
            });

            vm.config.rowsErrors = vm.model.errors.length;
            vm.config.rowsWarnings = vm.model.warnings.length;
            vm.config.rowsActions = vm.model.actions.length;
            vm.config.rowsIssues = vm.config.rowsErrors + vm.config.rowsWarnings ;

            vm.config.tabs = {
                actionTitle: vm.config.rowsActions > 1 ? getTitle('global.actionsNumber', vm.config.rowsActions) :
                    getTitle('global.actionNumber', vm.config.rowsActions),
                errorTitle: vm.config.rowsErrors > 1 ? getTitle('global.errorsNumber', vm.config.rowsErrors) :
                    getTitle('global.errorNumber', vm.config.rowsErrors),
                warningTitle: vm.config.rowsWarnings > 1 ? getTitle('global.warningsNumber', vm.config.rowsWarnings) :
                    getTitle('global.warningNumber', vm.config.rowsWarnings)
            };

            vm.config.resources = {
                messageSuccess: 'media.import.success',
                messageNoUpdate: 'media.import.noChanges',
                completeImport: $translate.instant('media.warningIssues.completeImport'),
                mainDescription: $translate.instant('media.warningIssues.mainDescription', {
                    rows: '<span id="rowsIssues" class="text-highlight">' + vm.config.invalidRows + '</span>'
                })
            };

            if (vm.model.actions.length > 0) {
                buildModel();
            }
        }

        function buildModel() {
            angular.forEach(vm.model.actions, function (action) {
                buildPayLoad(action);
            });
        }

        function buildModelActions(issue) {
            var modelActions = $q.defer(),
                promise = modelActions.promise;

            modelActions.resolve({
                value: buildPayLoad(issue)
            });

            return promise;
        }

        function buildPayLoad(issue) {
            var issueFound;

            if (issue.actionSelected.id === 0) {
                lodash.remove(vm.payloadActions, function (item) {
                    return item.rownum === issue.row;
                });
            }
            else {
                issueFound = lodash.find(vm.payloadActions, function (item) {
                    return item.rownum === issue.row;
                });

                if (Utils.isUndefinedOrNull(issueFound)) {
                    vm.payloadActions.push({
                        action: issue.actionSelected.action,
                        rownum: issue.row,
                        inAppType: issue.inAppType
                    });
                }
                else {
                    issueFound.action = issue.actionSelected.action;
                    issueFound.rownum = issue.row;
                    issueFound.inAppType = issue.inAppType;
                }
            }

            return vm.payloadActions.length;
        }

        function completeBulk() {
            if (!Utils.isUndefinedOrNull(data.uuid) && data.uuid !== null) {
                vm.promise = CampaignsService.importResource(data.campaignId,
                    CONSTANTS.INSERTION_ORDER.FILE_TYPE.MEDIA_INSERTION, data.uuid, true,
                    {
                        records: {
                            Action: vm.payloadActions
                        }
                    });

                return vm.promise.then(function (response) {
                    var rowsUpdated = parseInt(response.message.split(' ')[0]);

                    response.rowsUpdated = rowsUpdated;
                    closeModal(response);
                    return response;
                });
            }
        }

        function closeModal(responseStatus) {
            if (responseStatus) {
                $uibModalInstance.close(responseStatus);
            }
            else {
                $uibModalInstance.dismiss();
            }
        }

        function exportIssues() {
            if (vm.model.actions.length > 0 || vm.model.errors.length > 0 || vm.model.warnings.length > 0) {
                vm.promise = CampaignsService.exportIssuesResource(data.campaignId,
                    CONSTANTS.INSERTION_ORDER.FILE_TYPE.XLSX, CONSTANTS.INSERTION_ORDER.FILE_TYPE.MEDIA_INSERTION,
                    data.uuid);

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

        function getTitle(resource, numberIssuesWarnings) {
            return $translate.instant(resource, {
                number: numberIssuesWarnings
            });
        }

        function getActionsList(options) {
            var actionsList = [];

            lodash.forEach(lodash.sortBy(options), function (item, index) {
                index++;
                actionsList.push({
                    id: index,
                    label: getOption(item),
                    action: item
                });
            });

            return actionsList;
        }

        function getOption(value) {
            var option;

            switch (value) {
                case vm.ACTIONS_IMPORT.DO_NOT_IMPORT:
                    option = $translate.instant('global.actionsImport.doNotImport');
                    break;
                case vm.ACTIONS_IMPORT.DUPLICATE:
                    option = $translate.instant('global.actionsImport.duplicate');
                    break;
            }

            return option;
        }
    }
})();

