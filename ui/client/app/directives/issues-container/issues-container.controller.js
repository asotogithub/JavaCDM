(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('IssuesContainerController', IssuesContainerController);

    IssuesContainerController.$inject = [
        '$translate',
        'DialogFactory',
        'Utils'
    ];

    function IssuesContainerController(
        $translate,
        DialogFactory,
        Utils) {
        var vm = this;

        vm.ISSUES = {
            ACTIONS: {
                key: 'actions.tab'
            },
            ERRORS: {
                key: 'errors.tab'
            },
            WARNINGS: {
                key: 'warnings.tab'
            }
        };

        vm.activate = activate;
        vm.buildModelActions = buildModelActions;
        vm.cancel = cancel;
        vm.completeBulk = completeBulk;
        vm.exportIssues = vm.onExportIssues;
        vm.isActionInProgress = isActionInProgress;
        vm.issues = [];
        vm.pageSize = 0;
        vm.setTab = setTab;
        vm.showIssueGrid = showIssueGrid;
        vm.validateImportBtn = validateImportBtn;
        vm.isValidActions = false;
        vm.tab = {
            actions: {
                active: false
            },
            errors: {
                active: true
            },
            warnings: {
                active: false
            }
        };
        vm.resource = {};

        function activate() {
            initializeModel();
        }

        function setTab(selectedTab) {
            switch (selectedTab) {
                case vm.ISSUES.ACTIONS:
                    vm.tab.actions.active = true;
                    vm.tab.errors.active = false;
                    vm.tab.warnings.active = false;
                    break;
                case vm.ISSUES.ERRORS:
                    vm.tab.actions.active = false;
                    vm.tab.errors.active = true;
                    vm.tab.warnings.active = false;
                    break;
                case vm.ISSUES.WARNINGS:
                    vm.tab.actions.active = false;
                    vm.tab.errors.active = false;
                    vm.tab.warnings.active = true;
                    break;
            }
        }

        function cancel() {
            closeModal();
        }

        function closeModal(responseStatus) {
            vm.onCancel({
                responseStatus: responseStatus
            });
        }

        function completeBulk() {
            if (!Utils.isUndefinedOrNull(vm.config.uuid)) {
                vm.promise = vm.onContinue();
                vm.promise.then(function (success) {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                            success.rowsUpdated === 0 ? $translate.instant(vm.config.resources.messageNoUpdate) :
                            $translate.instant(vm.config.resources.messageSuccess, {
                                rows: success.rowsUpdated
                            }));
                }).catch(function () {
                    validateActions(true);
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.ERROR,
                        $translate.instant('global.errors.failure'));
                });
            }
        }

        function initializeModel() {
            if (!Utils.isUndefinedOrNull(vm.config)) {
                vm.rowsActions = vm.config.rowsActions;
                vm.rowsErrors = vm.config.rowsErrors;
                vm.rowsIssues = vm.config.rowsIssues;
                vm.rowsWarnings = vm.config.rowsWarnings;
                vm.totalRows = vm.config.totalRows;
                vm.isValidComplete = vm.config.isValidComplete;
                vm.rowsUpdate = vm.config.rowsUpdate;
                vm.resource.completeImport = vm.config.resources.completeImport;
                vm.resource.mainDescription = vm.config.resources.mainDescription;
                vm.pageSize = vm.config.pageSize;
                vm.isValidActions = vm.rowsActions > 0;

                if (!Utils.isUndefinedOrNull(vm.config.tabs)) {
                    vm.resource.errorTabTitle = vm.config.tabs.errorTitle;
                    vm.resource.warningTabTitle = vm.config.tabs.warningTitle;
                    vm.resource.actionTabTitle = vm.config.tabs.actionTitle;
                }

                if (!vm.rowsErrors) {
                    vm.tab.errors.active = false;
                    vm.tab.warnings.active = vm.rowsWarnings > 0;
                    vm.tab.actions.active = vm.rowsWarnings === 0;
                }
            }
        }

        function isActionInProgress() {
            return vm.promise && vm.promise.$$state && vm.promise.$$state.status === 0;
        }

        function showIssueGrid(currentGrid) {
            return vm.ISSUES.ERRORS === currentGrid ? vm.tab.errors.active && vm.rowsErrors :
                vm.tab.warnings.active && vm.rowsWarnings;
        }

        function validateActions(isValid) {
            vm.isValidComplete = isValid;
        }

        function buildModelActions(issue) {
            vm.onModelActions({
                issue: issue
            }).then(function (issueSize) {
                vm.isValidActions = parseInt(issueSize.value) > 0;
            });
        }

        function validateImportBtn() {
            return vm.isValidComplete || vm.isValidActions || vm.isActionInProgress();
        }
    }
})();
