(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('TagInjectionFlyOutController', TagInjectionFlyOutController);

    TagInjectionFlyOutController.$inject = [
        '$q',
        '$scope',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'ErrorRequestHandler',
        'PlacementsTableFilterService',
        'TagInjectionService',
        'Utils',
        'lodash'
    ];

    function TagInjectionFlyOutController($q,
                                          $scope,
                                          $translate,
                                          CONSTANTS,
                                          DialogFactory,
                                          ErrorRequestHandler,
                                          PlacementsTableFilterService,
                                          TagInjectionService,
                                          Utils,
                                          lodash) {
        var vm = this,
            flyOutController = $scope.vmTeFlyOutController,
            tagInjectionId = flyOutController.flyOutModel.data.id,
            searchContext = false;

        vm.DATE_FORMAT = CONSTANTS.DATE_FORMAT;
        vm.FLYOUT_STATE = CONSTANTS.FLY_OUT.STATE;
        vm.PAGE_SIZE = CONSTANTS.TE_TABLE.PAGE_SIZE;
        vm.blurButton = blurButton;
        vm.clearSearch = clearSearch;
        vm.close = close;
        vm.contentMaxLength = CONSTANTS.INPUT.MAX_LENGTH.TRACKING_TAG_HTML_CONTENT;
        vm.flyOutForm = {};
        vm.flyoutState = flyOutController.flyoutState;
        vm.namePattern = new RegExp(CONSTANTS.REGEX.ALPHANUMERIC);
        vm.onSearchCounter = onSearchCounter;
        vm.searchPlacements = searchPlacements;
        vm.showFilter = true;
        vm.tagInjectionMacrosList = lodash.values(CONSTANTS.TAG_INJECTION.MACROS);
        vm.tagNameMaxLength = CONSTANTS.INPUT.MAX_LENGTH.TRACKING_TAG_NAME;
        vm.toggleFilter = toggleFilter;
        vm.totalAssociations = 0;
        vm.updateTagInjection = updateTagInjection;
        vm.validateContentHtml = validateContentHtml;

        vm.searchFields = [
            {
                enabled: true,
                field: 'placement',
                position: 4,
                title: 'Placement'
            },
            {
                enabled: true,
                field: 'site',
                position: 2,
                title: 'Site'
            },
            {
                enabled: true,
                field: 'campaign',
                position: 1,
                title: 'Campaign'
            }
        ];

        vm.filterValues = [
            {
                fieldName: 'campaignName',
                values: []
            },
            {
                fieldName: 'siteName',
                values: []
            },
            {
                fieldName: 'name',
                values: []
            }
        ];

        vm.filterOptions = [
            {
                text: $translate.instant('global.campaign'),
                value: [],
                index: 0,
                fieldName: 'campaignName'
            },
            {
                text: $translate.instant('global.site'),
                value: [],
                index: 1,
                fieldName: 'siteName'
            },
            {
                text: $translate.instant('global.placement'),
                value: [],
                index: 2,
                fieldName: 'name'
            }
        ];

        vm.actionFilter = [
            {
                onSelectAll: onCampaignSelectAll,
                onDeselectAll: onCampaignDeselectAll,
                onItemAction: onCampaignItemAction
            },
            {
                onSelectAll: onSiteSelectAll,
                onDeselectAll: onSiteDeselectAll,
                onItemAction: onSiteItemAction
            },
            {
                onSelectAll: onPlacementSelectAll,
                onDeselectAll: onPlacementDeselectAll,
                onItemAction: onPlacementItemAction
            }
        ];

        activate();

        function activate() {
            vm.promiseTIFlyOut = $q.all([
                TagInjectionService.getHtmlInjectionTagsById(tagInjectionId),
                TagInjectionService.getTagPlacementsAssociated(tagInjectionId)
            ]).then(function (promises) {
                vm.model = promises[0];
                vm.associations = promises[1].placements;
                vm.associationsCounter = promises[1].totalNumberOfRecords;
                vm.totalAssociations = Utils.isUndefinedOrNull(vm.associations) ? 0 : vm.associations.length;
                PlacementsTableFilterService.loadFilterOptions(vm.associations, vm.filterOptions, vm.filterValues);
            });
        }

        function refreshTable() {
            var associationsBackup = [];

            angular.copy(vm.associations, associationsBackup);
            vm.associations = [];
            angular.copy(associationsBackup, vm.associations);
        }

        function updateTagInjection() {
            vm.promiseTIFlyOut = TagInjectionService.updateHtmlInjectionTag(vm.model).then(
                function () {
                    flyOutController.flyOutModel.data.name = vm.model.name;
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                    flyOutController.close();
                }).catch(function (error) {
                    if (!Utils.isUndefinedOrNull(error.data) &&
                        !Utils.isUndefinedOrNull(error.data.error) &&
                        !Utils.isUndefinedOrNull(error.data.error.code)) {
                        DialogFactory.showCustomDialog({
                            type: CONSTANTS.DIALOG.TYPE.ERROR,
                            title: $translate.instant('global.error'),
                            description: error.data.error.message
                        });
                    }
                    else {
                        ErrorRequestHandler.handleAndReject(error.data.error.message)(error);
                    }
                });
        }

        function close() {
            if (vm.flyOutForm.$dirty) {
                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                    title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                    description: $translate.instant('global.confirm.closeUnsavedChanges'),
                    buttons: CONSTANTS.DIALOG.BUTTON_SET.DISCARD_CANCEL
                }).result.then(
                    function () {
                        flyOutController.close();
                    });
            }
            else {
                flyOutController.close();
            }
        }

        function validateContentHtml(contentHtml, element) {
            var stringContent = contentHtml,
                regEx = new RegExp(CONSTANTS.REGEX.CONTENT_HTML, 'g'),
                matchTags = Utils.isUndefinedOrNull(stringContent) ? null : stringContent.match(regEx);

            if ((Utils.isUndefinedOrNull(vm.model.htmlContent) || vm.model.htmlContent === '') &&
                (Utils.isUndefinedOrNull(vm.model.secureHtmlContent) || vm.model.secureHtmlContent === '')) {
                vm.flyOutForm[element].$setValidity('contentHtmlRequired', false);
                vm.flyOutForm[element].$setValidity('contentHtmlValid', true);
            }
            else {
                vm.flyOutForm.tagContent.$setValidity('contentHtmlRequired', true);
                vm.flyOutForm.tagSecureContent.$setValidity('contentHtmlRequired', true);
                if (contentHtml !== '' && Utils.isUndefinedOrNull(matchTags)) {
                    vm.flyOutForm[element].$setValidity('contentHtmlValid', false);
                }
                else {
                    vm.flyOutForm[element].$setValidity('contentHtmlValid', true);
                }
            }
        }

        function onSearchCounter(searchCounter) {
            var legendResource = 'tags.rowsTagPlacements',
                legendData = {
                    rows: vm.totalAssociations
                };

            if (parseInt(searchCounter) !== vm.totalAssociations) {
                legendResource = 'tags.rowsSearchTagPlacements';
                legendData.rowsSearch = searchCounter;
            }

            vm.associationCounterLegend = $translate.instant(legendResource, legendData);
        }

        function toggleFilter() {
            if (flyOutController.flyoutState === vm.FLYOUT_STATE.HALF_VIEW) {
                flyOutController.full();
                vm.showFilter = true;
            }
            else {
                vm.showFilter = !vm.showFilter;
            }
        }

        function blurButton(buttonName) {
            angular.element('#' + buttonName).trigger('blur');
        }

        //CAMPAIGN ACTIONS
        function onCampaignDeselectAll(isSelectedAll, oldFilterValues, isPartialDeselect) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                if (!Utils.isUndefinedOrNull(isPartialDeselect) && isPartialDeselect) {
                    PlacementsTableFilterService.updateSite(vm.filterValues, oldFilterValues, vm.associations,
                        vm.filterOptions);
                    refreshTable();
                    return;
                }

                vm.filterValues[1].values = [];
                vm.filterOptions[1].value = [];
                vm.filterValues[2].values = [];
                vm.filterOptions[2].value = [];
                refreshTable();
            }
        }

        function onCampaignSelectAll(oldFilterValues) {
            PlacementsTableFilterService.updateSite(vm.filterValues, oldFilterValues, vm.associations,
                vm.filterOptions);
            refreshTable();
        }

        function onCampaignItemAction(id, isSelectedAll, oldFilterValues) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                PlacementsTableFilterService.updateSite(vm.filterValues, oldFilterValues, vm.associations,
                    vm.filterOptions);
                refreshTable();
            }
        }

        //SITE ACTIONS
        function onSiteDeselectAll(isSelectedAll, oldFilterValues, isPartialDeselect) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                if (!Utils.isUndefinedOrNull(isPartialDeselect) && isPartialDeselect) {
                    PlacementsTableFilterService.updatePlacement(vm.filterValues, oldFilterValues, vm.associations,
                        vm.filterOptions);
                    refreshTable();
                    return;
                }

                vm.filterValues[2].values = [];
                vm.filterOptions[2].value = [];
                refreshTable();
            }
        }

        function onSiteSelectAll(oldFilterValues) {
            PlacementsTableFilterService.updatePlacement(vm.filterValues, oldFilterValues, vm.associations,
                vm.filterOptions);
            refreshTable();
        }

        function onSiteItemAction(id, isSelectedAll, oldFilterValues) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                PlacementsTableFilterService.updatePlacement(vm.filterValues, oldFilterValues, vm.associations,
                    vm.filterOptions);
                refreshTable();
            }
        }

        //PLACEMENT ACTIONS
        function onPlacementDeselectAll() {
            refreshTable();
        }

        function onPlacementSelectAll() {
            refreshTable();
        }

        function onPlacementItemAction(id, isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshTable();
            }
        }

        function searchPlacements(searchTerm) {
            if (!Utils.isUndefinedOrNull(searchTerm.searchText)) {
                vm.promiseTIFlyOut = TagInjectionService.searchPlacementView(
                    tagInjectionId,
                    searchTerm.searchText,
                    vm.searchFields[0].enabled,
                    vm.searchFields[1].enabled,
                    vm.searchFields[2].enabled).then(function (response) {
                        if (!searchContext) {
                            searchContext = true;
                            vm.backUpModel = vm.associations;
                        }

                        vm.associations = response.placements;
                        PlacementsTableFilterService.loadFilterOptions(
                            vm.associations,
                            vm.filterOptions,
                            vm.filterValues
                        );
                    });
            }
        }

        function clearSearch() {
            if (searchContext) {
                searchContext = false;
                vm.associations = vm.backUpModel;
                PlacementsTableFilterService.loadFilterOptions(vm.associations, vm.filterOptions, vm.filterValues);
            }
        }

        $scope.$watch('vmTeFlyOutController.flyoutState', function (newValue) {
            vm.flyoutState = newValue;
        });

        $scope.$watch('vmTeFlyOutController.flyOutModel.data.id', function (newValue) {
            tagInjectionId = newValue;
            activate();
        });

        $scope.$on('te-flyout:closeFlyout', function () {
            close();
        });
    }
})();
