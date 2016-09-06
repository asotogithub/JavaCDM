(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('ScheduleInsertionsController', ScheduleInsertionsController);

    ScheduleInsertionsController.$inject = [
        '$scope',
        '$timeout',
        '$translate',
        '$q',
        '$window',
        'CONSTANTS',
        'CampaignsService',
        'CreativeInsertionService',
        'DateTimeService',
        'DialogFactory',
        'FlyoutEditionStashService',
        'ScheduleConfigService',
        'ScheduleFlyoutUtilService',
        'ScheduleUtilService',
        'UserService',
        'Utils',
        'lodash',
        'uuid'
    ];

    function ScheduleInsertionsController($scope,
                                          $timeout,
                                          $translate,
                                          $q,
                                          $window,
                                          CONSTANTS,
                                          CampaignsService,
                                          CreativeInsertionService,
                                          DateTimeService,
                                          DialogFactory,
                                          FlyoutEditionStashService,
                                          ScheduleConfigService,
                                          ScheduleFlyoutUtilService,
                                          ScheduleUtilService,
                                          UserService,
                                          Utils,
                                          lodash,
                                          uuid) {
        var CONTEXT = $scope.$parent.vmList.CONTEXT,
            vm = this,
            updatedCreativeGroups = {},
            updatedCreativeInsertions = {},
            openFlyout = true;

        vm.DATE_FORMAT = CONSTANTS.DATE_FORMAT;
        vm.FLYOUT_STATE = $scope.$parent.vmList.FLYOUT;
        vm.PAGE_SIZE = CONSTANTS.TE_TREE_GRID.PAGE_SIZE.MEDIUM;
        vm.SCHEDULE_LEVEL = CONSTANTS.SCHEDULE.LEVEL;
        vm.SERVER_SIDE_SEARCH_OPTIONS = {
            searchInterval: CONSTANTS.TE_TREE_GRID.SEARCH_INTERVAL,
            searchMinLength: CONSTANTS.TE_TREE_GRID.SEARCH_MIN_LENGTH
        };
        vm.allMarkedAsChecked = [];
        vm.checkedList = [];
        vm.applyTo = applyTo;
        vm.blurButton = blurButton;
        vm.bulkEditControlsCollapsed = true;
        vm.bulkEditData = {};
        vm.bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
        vm.bulkUpdate = bulkUpdate;
        vm.context = $scope.$parent.vmList.context;
        vm.currentOptionFilter = [];
        vm.creativeInsertionPayload = creativeInsertionPayload;
        vm.filterControlsCollapsed = true;
        vm.filterOptions = $scope.$parent.vmList.filterOptions;
        vm.flyoutRowCollapsed = flyoutRowCollapsed;
        vm.flyoutRowExpanded = flyoutRowExpanded;
        vm.flyoutState = vm.FLYOUT_STATE.HALF_VIEW;
        vm.flyoutUUID = uuid.v4();
        vm.getAllChecked = getAllChecked;
        vm.getCheckboxHeaderRenderer = getCheckboxHeaderRenderer;
        vm.getCheckboxRenderer = getCheckboxRenderer;
        vm.getClickThroughEditorConf = getClickThroughEditorConf;
        vm.getColumnWidth = getColumnWidth;
        vm.getCTRenderer = getCTRenderer;
        vm.getCTUrlColumnTitle = getCTUrlColumnTitle;
        vm.getDateEditorConf = getDateEditorConf;
        vm.getDateRenderer = getDateRenderer;
        vm.getDetailColumnTitle = getDetailColumnTitle;
        vm.getFilterable = getFilterable;
        vm.getPosition = getPosition;
        vm.getRowRenderer = getRowRenderer;
        vm.getWeightEditorConf = getWeightEditorConf;
        vm.hasBulkEditPermission = UserService.hasPermission(
            CONSTANTS.PERMISSION.SCHEDULE.UPDATE_BULK_CREATIVE_INSERTION);
        vm.hasCreateCreativePermission = UserService.hasPermission(
            CONSTANTS.PERMISSION.SCHEDULE.CREATE_CREATIVE_INSERTION);
        vm.hierarchicalRemove = hierarchicalRemove;
        vm.isCreativeGroupVisible = false;
        vm.isCreativeVisible = false;
        vm.isGridValid = false;
        vm.isServerSearchScheduleTab = false;
        vm.isSmallResolution = false;
        vm.mainVmStatus = $scope.$parent.vmList.mainVmStatus;
        vm.modelInitial = null;
        vm.modelSearchFlyoutFields = null;
        vm.modelWithoutSearch = null;
        vm.modelWithoutFilter = null;
        vm.onCloseFlyout = onCloseFlyout;
        vm.performDelete = performDelete;
        vm.promise = null;
        vm.propertiesModel = null;
        vm.refreshColumns = true;
        vm.refreshListCheck = refreshListCheck;
        vm.reloadSearchOptions = reloadSearchOptions;
        vm.scheduleInsertionForm = {};
        vm.searchFields = [];
        vm.selectionMode = 'MULTI';
        vm.serverFlyoutSideClearSearch = serverFlyoutSideClearSearch;
        vm.serverSideSearch = serverSideSearch;
        vm.showAddGroupCreativeAssocButton = false;
        vm.showAddGroupCreativeDialog = showAddGroupCreativeDialog;
        vm.showAddScheduleButton = false;
        vm.showAddScheduleDialog = showAddScheduleDialog;
        vm.setFilterChecked = setFilterChecked;
        vm.setOptionToFilter = setOptionToFilter;
        vm.submodel = null;

        activate();

        function activate() {
            $window.onresize = function () {
                $timeout(function () {
                    vm.isSmallResolution = $window.innerWidth < CONSTANTS.RESOLUTION.WXGA_PLUS.WIDTH ?
                    vm.flyoutState === vm.FLYOUT_STATE.HALF_VIEW : false;
                });
            };
        }

        function clearCheckedItems() {
            var args = {};

            args.uuid = vm.flyoutUUID;
            $scope.$broadcast('te-tree-grid:checked:cleanAll', args);
        }

        ScheduleConfigService.isGridValid().then(
            function () {
            },

            function () {
            },

            function (valid) {
                vm.isGridValid = valid && hasChanges();
            });

        $scope.$on('scheduling-update', function (event, entityRow) {
            setUpdateEntityList(entityRow);
        });

        $scope.$on('schedule.flyout.modelUpdated', function (event, data) {
            if (data.isModelFlyout) {
                if (data.selectionRow) {
                    updateModelsSchedule(data.selectionRow);
                }
            }
            else if (data.selectionRow && vm.context === CONTEXT.DEFAULT && vm.scheduleTabContext === CONTEXT.DEFAULT) {
                updateModelsSchedule(data.selectionRow);
            }
            else if (vm.scheduleTabContext === CONTEXT.DEFAULT) {
                if (!data.selectionRow) {
                    return;
                }

                vm.modelWithoutSearch = [];
                vm.modelWithoutSearch = stripUUIDs([lodash.cloneDeep(data.selectionRow)]);
                vm.modelInitial = lodash.clone(vm.modelWithoutSearch, true);
                updateCheckedToModel(vm.modelWithoutSearch);
                bulkEditOnSite(data.selectionRow);
            }
        });

        $scope.$on('schedule.flyout.rowExpandedOrCollapsed', function (event, data) {
            var row;

            if (!Utils.isUndefinedOrNull(vm.submodel) && !vm.searchServerText) {
                row = $scope.$parent.vmList.findSelection(vm.submodel, data);

                if (!Utils.isUndefinedOrNull(row)) {
                    row.loadData = true;
                    expandCollapseRow(row, data);
                }
            }
            else {
                row = $scope.$parent.vmList.findSelection(vm.modelWithoutSearch, data);

                if (!Utils.isUndefinedOrNull(row)) {
                    row.loadData = true;
                    row.expanded = data.expanded;
                }
            }
        });

        $scope.$on('schedule.flyout.close', function () {
            closeFlyout();
        });

        $scope.$on('schedule.flyout.promiseUpdated', function (evt, args) {
            promiseUpdate(args.promise);
        });

        vm.close = function () {
            if (vm.isGridValid && vm.field === vm.SCHEDULE_LEVEL.PLACEMENT.KEY) {
                DialogFactory.showCustomDialog({
                    type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                    title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                    description: $translate.instant('creativeInsertion.confirmSaveBeforeClose'),
                    buttons: CONSTANTS.DIALOG.BUTTON_SET.YES_NO
                }).result.then(bulkUpdate, closeFlyout);
            }
            else {
                closeFlyout();
            }
        };

        vm.half = function () {
            if (vm.flyoutState === vm.FLYOUT_STATE.HALF_VIEW) {
                return;
            }

            var el = angular.element('#scheduleInsertionsContainer');

            vm.flyoutState = vm.FLYOUT_STATE.HALF_VIEW;
            el.trigger('flyoutHalf');
            reloadGridConfiguration({
                refreshColumns: true
            });
            vm.bulkEditControlsCollapsed = true;
            vm.filterControlsCollapsed = true;
        };

        vm.full = function () {
            if (vm.flyoutState === vm.FLYOUT_STATE.FULL_VIEW) {
                return;
            }

            var el = angular.element('#scheduleInsertionsContainer');

            vm.flyoutState = vm.FLYOUT_STATE.FULL_VIEW;
            el.trigger('flyoutFull');
            reloadGridConfiguration({
                refreshColumns: true
            });
        };

        vm.onSelectItem = function ($selection, $level, $coords, settingScheduleTab) {
            var el = angular.element('#scheduleInsertionsContainer'),
                editPanelFirst = angular.element('#collapseOne'),
                editPanelSecond = angular.element('#collapseTwo'),
                editPanelAssoc = angular.element('#collapseTagAssociations');

            vm.selectedPivot = settingScheduleTab.currentPivot;
            vm.modelWithoutFilter = $selection;
            vm.level = $level;
            vm.selectedRowField = $selection.field;
            vm.scheduleTabContext = settingScheduleTab.context;
            vm.isNewItemSelectd = true;

            checkSelectedLevel($selection, vm.selectedPivot);
            //showFilter
            vm.filterControlsVisible = vm.selectedRowField !== vm.SCHEDULE_LEVEL.SCHEDULE.KEY &&
                !(vm.selectedRowField === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY &&
                vm.selectedPivot.KEY === vm.SCHEDULE_LEVEL.CREATIVE.KEY);

            reloadGridConfiguration({
                clearSearch: true,
                refreshColumns: true,
                resetSearchFields: true
            });
            vm.searchServerText = null;
            updateModel($selection);
            vm.allMarkedAsChecked = [];
            vm.checkedList = [];

            updateCheckedToChildNode(vm.submodel, false);
            clearBulkEditData();
            clearCheckedItems();
            FlyoutEditionStashService.clear();

            switch ($selection.field) {
                case vm.SCHEDULE_LEVEL.SITE.KEY:
                    vm.propertiesModel = getPropertiesCustom($selection);
                    bulkEditOnSite(vm.submodel[0]);
                    break;
                case vm.SCHEDULE_LEVEL.SECTION.KEY:
                    vm.propertiesModel = getPropertiesCustom($selection);
                    vm.propertiesModel.editLabel = $translate.instant('schedule.editSectionInsertions');
                    bulkEditOnSite(vm.submodel[0]);
                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    vm.propertiesModel = getPlacementProperties($selection);
                    bulkEditOnSite(vm.submodel[0]);
                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    vm.propertiesModel = getCreativeGroupProperties($selection);
                    bulkEditOnSite(vm.submodel[0]);
                    break;
                case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                    vm.propertiesModel = getCreativeProperties($selection);
                    bulkEditOnSite(vm.submodel[0]);
                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE.KEY:
                    vm.propertiesModel = getCreativeProperties($selection);
                    bulkEditOnSite(vm.submodel[0]);
                    break;
            }

            el.removeClass('schedule-insertions-container-initial');
            if (!editPanelSecond.hasClass('in')) {
                editPanelFirst.removeClass('in');
                editPanelAssoc.removeClass('in');
                editPanelSecond.collapse('show');
            }

            if (openFlyout) {
                //FIXME this function should be called from directive: controller.onOpenFlyout()
                onOpenFlyout();
                openFlyout = false;
                el.trigger('flyout', $coords);
            }
        };

        vm.showBulkEditOptions = function () {
            if (vm.flyoutState === vm.FLYOUT_STATE.HALF_VIEW) {
                vm.full();
            }

            vm.bulkEditControlsCollapsed = !vm.bulkEditControlsCollapsed;
        };

        vm.showFilterOptions = function () {
            if (vm.flyoutState === vm.FLYOUT_STATE.HALF_VIEW) {
                vm.full();
            }

            if (vm.filterControlsCollapsed && vm.isNewItemSelectd) {
                vm.filterLoading = true;
                showFilterOptionAsync().then(function () {
                    vm.filterLoading = false;
                    vm.isNewItemSelectd = false;
                });
            }

            vm.filterControlsCollapsed = !vm.filterControlsCollapsed;
        };

        function showFilterOptionAsync() {
            return $q(function (resolve) {
                $timeout(function () {
                    initializeModelFilter();
                    getItemsFiltersFromModel(vm.submodel);
                    setFilterChecked();
                    resolve();
                });
            });
        }

        function promiseUpdate(promise) {
            vm.promise = {
                promise: promise,
                templateUrl: 'app/cm/campaigns/partials/empty-promise-backdrop.html'
            };
        }

        function updateModelsSchedule(data) {
            updateModel(data);
            if (Utils.isUndefinedOrNull(vm.searchServerText)) {
                $timeout(function () {
                    updateCheckedToModel(vm.submodel);
                    refreshModelGrid();
                });
            }
            else {
                $timeout(function () {
                    updateCheckedInSearchContext(vm.submodel, vm.checkedList);
                    refreshModelGrid();
                });
            }

            bulkEditOnSite(data);
        }

        function addSharedCreativeGroupToLists(creativeGroup,
                                               updateAttributes,
                                               sharedCreativeGroups,
                                               sharedCreativeGroupsModel) {
            if (Utils.isUndefinedOrNull(sharedCreativeGroups[creativeGroup.creativeGroupId])) {
                var creativeGroupLite = {
                    creativeGroupId: creativeGroup.creativeGroupId,
                    creativeGroupLabel: creativeGroup.creativeGroupLabel,
                    newWeight: parseInt(updateAttributes.weight),
                    placementAssociations: creativeGroup.placementAssociations,
                    visible: false,
                    weight: creativeGroup.weight
                };

                sharedCreativeGroupsModel.push(creativeGroupLite);
                sharedCreativeGroups[creativeGroup.creativeGroupId] = creativeGroupLite;
            }
        }

        function applyTo(updateAttributes) {
            if (updateAttributes.field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                processSharedCreativeGroups(updateAttributes);
            }
            else if (updateAttributes.field === vm.SCHEDULE_LEVEL.SCHEDULE.KEY) {
                processEntity(updateAttributes);
            }
        }

        function clearBulkEditData() {
            vm.bulkEditData = {};
        }

        function blurButton(buttonName) {
            angular.element('#' + buttonName).trigger('blur');
        }

        function checkSharedCreativeGroups(entity, updateAttributes, sharedCreativeGroups, sharedCreativeGroupsModel) {
            if (!Utils.isUndefinedOrNull(entity.field) && entity.field === updateAttributes.field) {
                if (!Utils.isUndefinedOrNull(entity.checked) && entity.checked &&
                    (Utils.isUndefinedOrNull(entity._visible) && entity.children[0].parent._visible ||
                    !Utils.isUndefinedOrNull(entity._visible) && entity._visible) &&
                    !Utils.isUndefinedOrNull(entity.placementAssociations) && entity.placementAssociations > 1) {
                    addSharedCreativeGroupToLists(
                        entity,
                        updateAttributes,
                        sharedCreativeGroups,
                        sharedCreativeGroupsModel);
                }
            }
            else if (!Utils.isUndefinedOrNull(entity.expanded) && entity.expanded &&
                !Utils.isUndefinedOrNull(entity.children)) {
                var i, len = entity.children.length;

                for (i = 0; i < len; i++) {
                    checkSharedCreativeGroups(
                        entity.children[i],
                        updateAttributes,
                        sharedCreativeGroups,
                        sharedCreativeGroupsModel);
                }
            }
        }

        function processEntity(updateAttributes, sharedCreativeGroups) {
            var i, len = vm.submodel.length,
                outputModelBackup = [];

            for (i = 0; i < len; i++) {
                updateEntity(vm.submodel[i], updateAttributes, sharedCreativeGroups);
            }

            vm.isGridValid = hasChanges();

            if (vm.isGridValid) {
                angular.copy(vm.submodel, outputModelBackup);
                vm.submodel = [];
                angular.copy(outputModelBackup, vm.submodel);
            }

            FlyoutEditionStashService.uncheckAll();
            vm.allMarkedAsChecked = [];
            vm.checkedList = [];
            clearCheckedItems();
            updateCheckedToChildNode(vm.submodel, false);
        }

        function processSharedCreativeGroups(updateAttributes) {
            var i, len = vm.submodel.length,
                sharedCreativeGroups = [],
                sharedCreativeGroupsModel = [];

            if (updateAttributes.field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                for (i = 0; i < len; i++) {
                    checkSharedCreativeGroups(
                        vm.submodel[i],
                        updateAttributes,
                        sharedCreativeGroups,
                        sharedCreativeGroupsModel);
                }

                if (sharedCreativeGroupsModel.length > 0) {
                    showSharedCreativeGroupsWarning(updateAttributes, sharedCreativeGroups, sharedCreativeGroupsModel);
                }
                else {
                    processEntity(updateAttributes, sharedCreativeGroups);
                }
            }
        }

        function closeFlyout() {
            var el = angular.element('#scheduleInsertionsContainer');

            el.trigger('flyoutClose');
            $scope.$parent.vmList.promise = disableItemSelection();
            vm.modelSearchFlyoutFields = null;
            vm.modelInitial = null;
            openFlyout = true;
            // clear out holding variables
            updatedCreativeGroups = {};
            updatedCreativeInsertions = {};
            vm.allMarkedAsChecked = [];
            vm.isGridValid = false;
            vm.flyoutState = vm.FLYOUT_STATE.HALF_VIEW;
            vm.isExpandedCreativeGroup = false;
            $scope.$parent.vmList.flyoutState = vm.FLYOUT_STATE.HIDDEN;
            clearCheckedItems();
            vm.bulkEditControlsCollapsed = true;
            vm.filterControlsCollapsed = true;
            vm.checkedList = [];
            FlyoutEditionStashService.clear();
        }

        function disableItemSelection() {
            var deferred = $q.defer();

            $timeout(function () {
                deferred.resolve([]);
            }, 1000);

            return deferred.promise;
        }

        function setCreativeAttributes(creative, updateAttributes) {
            var creativeUpdated = false;

            if (!Utils.isUndefinedOrNull(updateAttributes.weight) && !Utils.isUndefinedOrNull(creative.weight)) {
                creative.weight = updateAttributes.weight;
                creativeUpdated = true;
            }

            if (!Utils.isUndefinedOrNull(updateAttributes.flightDateStart) &&
                !Utils.isUndefinedOrNull(creative.flightDateStart) &&
                !Utils.isUndefinedOrNull(updateAttributes.flightDateEnd) &&
                !Utils.isUndefinedOrNull(creative.flightDateEnd)) {
                creative.flightDateStart = updateAttributes.flightDateStart;
                creative.flightDateEnd = updateAttributes.flightDateEnd;
                creativeUpdated = true;
            }
            else {
                if (!Utils.isUndefinedOrNull(updateAttributes.flightDateStart) &&
                    !Utils.isUndefinedOrNull(creative.flightDateStart)) {
                    creativeUpdated = setDateStart(creative, updateAttributes.flightDateStart) || creativeUpdated;
                }

                if (!Utils.isUndefinedOrNull(updateAttributes.flightDateEnd) &&
                    !Utils.isUndefinedOrNull(creative.flightDateEnd)) {
                    creativeUpdated = setDateEnd(creative, updateAttributes.flightDateEnd) || creativeUpdated;
                }
            }

            if (!Utils.isUndefinedOrNull(updateAttributes.clickThroughUrl) &&
                !Utils.isUndefinedOrNull(creative.clickThroughUrl)) {
                creative.clickThroughUrl[0] = updateAttributes.clickThroughUrl;
                creativeUpdated = true;
            }

            return creativeUpdated;
        }

        function isCreativeGroupUpdated(creativeGroup, updateAttributes, sharedCreativeGroups) {
            var creativeGroupUpdated = false,
                sharedCreativeGroup = sharedCreativeGroups[creativeGroup.creativeGroupId];

            if (!Utils.isUndefinedOrNull(updateAttributes.weight) && !Utils.isUndefinedOrNull(creativeGroup.weight)) {
                if (creativeGroup.checked) {
                    creativeGroup.weight = creativeGroup.placementAssociations > 1 ?
                        sharedCreativeGroup.newWeight : updateAttributes.weight;

                    creativeGroupUpdated = true;
                }
                else if (!Utils.isUndefinedOrNull(sharedCreativeGroup)) {
                    creativeGroup.weight = sharedCreativeGroup.newWeight;

                    creativeGroupUpdated = true;
                }
            }

            return creativeGroupUpdated;
        }

        function setDateEnd(creative, endDate) {
            var creativeUpdated = false;

            if (!Utils.isUndefinedOrNull(creative.flightDateStart) && creative.flightDateStart !== '') {
                if (DateTimeService.isBeforeOrEqual(creative.flightDateStart, endDate, CONSTANTS.DATE.MOMENT.DATE)) {
                    creative.flightDateEnd = endDate;
                    creativeUpdated = true;
                }
            }
            else {
                creative.flightDateEnd = endDate;
                creativeUpdated = true;
            }

            return creativeUpdated;
        }

        function setDateStart(creative, startDate) {
            var creativeUpdated = false;

            if (!Utils.isUndefinedOrNull(creative.flightDateEnd) && creative.flightDateEnd !== '') {
                if (DateTimeService.isBeforeOrEqual(startDate, creative.flightDateEnd, CONSTANTS.DATE.MOMENT.DATE)) {
                    creative.flightDateStart = startDate;
                    creativeUpdated = true;
                }
            }
            else {
                creative.flightDateStart = startDate;
                creativeUpdated = true;
            }

            return creativeUpdated;
        }

        function setEntityAttributes(entity, updateAttributes, sharedCreativeGroups) {
            if (updateAttributes.field === vm.SCHEDULE_LEVEL.SCHEDULE.KEY) {
                if (setCreativeAttributes(entity, updateAttributes)) {
                    setUpdateEntityList(entity);
                }
            }
            else if (updateAttributes.field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                if (isCreativeGroupUpdated(entity, updateAttributes, sharedCreativeGroups)) {
                    setUpdateEntityList(entity);
                }
            }
        }

        function setUpdateEntityList(entity) {
            if (Utils.isUndefinedOrNull(entity.flightDateEnd) || Utils.isUndefinedOrNull(entity.flightDateStart)) {
                var item = {
                    weight: entity.weight,
                    creativeGroupId: entity.creativeGroupId,
                    placementId: entity.placementId,
                    creativeId: entity.creative
                };
                // Found creative group
                updatedCreativeGroups[entity.creativeGroupId] = item;
            }
            else {
                // Found creative
                updatedCreativeInsertions[entity.id] = {
                    weight: entity.weight,
                    startDate: entity.flightDateStart,
                    endDate: entity.flightDateEnd,
                    clickthroughs: entity.clickThroughUrl,
                    clickThroughUrl: entity.clickThroughUrl,
                    id: entity.id
                };
            }

            FlyoutEditionStashService.save(entity);
        }

        function showSharedCreativeGroupsWarning(updateAttributes, sharedCreativeGroups, sharedCreativeGroupsModel) {
            var wURL = 'app/cm/campaigns/schedule-tab/schedule-insertions/shared-creative-groups-warning-dialog.html';

            ScheduleUtilService.setSharedCreativeGroups(sharedCreativeGroupsModel);

            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                partialHTML: wURL,
                buttons: {
                    yes: $translate.instant('global.update'),
                    no: $translate.instant('global.cancel')
                }
            }).result.then(
                function () {
                    processEntity(updateAttributes, sharedCreativeGroups);
                });
        }

        function updateEntity(entity, updateAttributes, sharedCreativeGroups) {
            if (!Utils.isUndefinedOrNull(entity.field) && entity.field === updateAttributes.field) {
                if ((!Utils.isUndefinedOrNull(entity.checked) && entity.checked ||
                    updateAttributes.field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) &&
                    (Utils.isUndefinedOrNull(entity._visible) && entity.children[0].parent._visible ||
                    !Utils.isUndefinedOrNull(entity._visible) && entity._visible)) {
                    setEntityAttributes(entity, updateAttributes, sharedCreativeGroups);
                }
            }
            else if ((!Utils.isUndefinedOrNull(entity.expanded) && entity.expanded || updateAttributes.field ===
                vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) && !Utils.isUndefinedOrNull(entity.children)) {
                var i, len = entity.children.length;

                for (i = 0; i < len; i++) {
                    updateEntity(entity.children[i], updateAttributes, sharedCreativeGroups);
                }
            }
        }

        function buildDeletePayload() {
            var result = [];

            angular.forEach(vm.allMarkedAsChecked, function (value) {
                var elementToDelete = {};

                switch (vm.selectedPivot) {
                    case vm.SCHEDULE_LEVEL.SITE:
                    case vm.SCHEDULE_LEVEL.PLACEMENT:
                        if (!Utils.isUndefinedOrNull(value.creativeId)) {
                            elementToDelete.creativeId = value.creativeId;
                            elementToDelete.groupId = value.creativeGroupId;
                            elementToDelete.placementId = value.placementId;
                            elementToDelete.sectionId = value.siteSectionId;
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.SCHEDULE.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else if (!Utils.isUndefinedOrNull(value.creativeGroupId)) {
                            elementToDelete.groupId = value.creativeGroupId;
                            elementToDelete.placementId = value.placementId;
                            elementToDelete.sectionId = value.siteSectionId;
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else if (!Utils.isUndefinedOrNull(value.placementId)) {
                            elementToDelete.placementId = value.placementId;
                            elementToDelete.sectionId = value.siteSectionId;
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.PLACEMENT.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else if (Utils.isUndefinedOrNull(value.siteSectionId)) {
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.SITE.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else {
                            elementToDelete.sectionId = value.siteSectionId;
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.SECTION.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }

                        break;
                    case vm.SCHEDULE_LEVEL.CREATIVE_GROUP:
                        if (!Utils.isUndefinedOrNull(value.creativeId)) {
                            elementToDelete.creativeId = value.creativeId;
                            elementToDelete.placementId = value.placementId;
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.groupId = value.creativeGroupId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.SCHEDULE.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else if (!Utils.isUndefinedOrNull(value.placementId)) {
                            elementToDelete.placementId = value.placementId;
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.groupId = value.creativeGroupId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.PLACEMENT.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else if (Utils.isUndefinedOrNull(value.siteId)) {
                            elementToDelete.groupId = value.creativeGroupId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else {
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.groupId = value.creativeGroupId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.SITE.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }

                        break;
                    case vm.SCHEDULE_LEVEL.CREATIVE:
                        if (!Utils.isUndefinedOrNull(value.id) && value.field === vm.SCHEDULE_LEVEL.SCHEDULE.KEY) {
                            elementToDelete.groupId = value.creativeGroupId;
                            elementToDelete.placementId = value.placementId;
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.creativeId = value.creativeId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.SCHEDULE.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else if (!Utils.isUndefinedOrNull(value.creativeGroupId)) {
                            elementToDelete.groupId = value.creativeGroupId;
                            elementToDelete.placementId = value.placementId;
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.creativeId = value.creativeId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else if (!Utils.isUndefinedOrNull(value.placementId)) {
                            elementToDelete.placementId = value.placementId;
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.creativeId = value.creativeId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.PLACEMENT.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else if (Utils.isUndefinedOrNull(value.siteId)) {
                            elementToDelete.creativeId = value.creativeId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.CREATIVE.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }
                        else {
                            elementToDelete.siteId = value.siteId;
                            elementToDelete.creativeId = value.creativeId;
                            elementToDelete.type = vm.SCHEDULE_LEVEL.SITE.KEY;
                            elementToDelete.pivotType = vm.selectedPivot.KEY;
                        }

                        break;
                }

                result.push(elementToDelete);
            });

            return result;
        }

        function hasChanges() {
            return Object.getOwnPropertyNames(updatedCreativeGroups).length > 0 ||
                Object.getOwnPropertyNames(updatedCreativeInsertions).length > 0;
        }

        function getRowRenderer(rowData, cellValue) {
            return ScheduleConfigService.getRowRenderer(rowData, cellValue);
        }

        function getCheckboxHeaderRenderer(submodel) {
            return ScheduleConfigService.getCheckboxHeaderRenderer(submodel);
        }

        function getCheckboxRenderer($row, $cellValue) {
            return ScheduleConfigService.getCheckboxRenderer($row, $cellValue);
        }

        function getDateRenderer($cellValue) {
            // Expected format for dates: 'MM/DD/YYYY'
            return ScheduleConfigService.getDateRenderer($cellValue);
        }

        function getCTRenderer(row, cellValue) {
            return ScheduleConfigService.getCTRenderer(row, cellValue);
        }

        function getDateEditorConf(columnName) {
            return ScheduleConfigService.getDateEditorConf(columnName);
        }

        function getWeightEditorConf() {
            return ScheduleConfigService.getWeightEditorConf();
        }

        function getClickThroughEditorConf() {
            return ScheduleConfigService.getClickThroughEditorConf();
        }

        function getPropertiesCustom(selectedModel) {
            var childrenCount = 0,
                properties;

            lodash.forEach(selectedModel.children, function (n) {
                if (!Utils.isUndefinedOrNull(n.children)) {
                    childrenCount += n.children.length;
                }
            });

            properties = {
                mainTitle: selectedModel.siteDetailLabel.split('(')[0],
                editLabel: $translate.instant('schedule.editSiteInsertions'),
                title: selectedModel.siteDetailLabel.split('(')[0],
                groups: [
                    {
                        groupName: '',
                        content: [
                            {
                                field: $translate.instant('global.site'),
                                value: selectedModel.siteDetailLabel.split('(')[0]
                            },
                            {
                                field: $translate.instant('global.placements'),
                                value: childrenCount
                            }
                        ]
                    }
                ]
            };

            return properties;
        }

        function getPlacementProperties(selectedPlacement) {
            var properties = {
                mainTitle: selectedPlacement.placementLabel,
                editLabel: $translate.instant('schedule.editPlacementInsertion'),
                title: selectedPlacement.placementLabel,
                groups: [
                    {
                        groupName: '',
                        content: [
                            {
                                field: $translate.instant('placement.name'),
                                value: selectedPlacement.placementLabel
                            },
                            {
                                field: $translate.instant('global.site'),
                                value: selectedPlacement.siteName
                            },
                            {
                                field: $translate.instant('global.section'),
                                value: selectedPlacement.siteSectionName
                            },
                            {
                                field: $translate.instant('global.size'),
                                value: selectedPlacement.sizeName
                            },
                            {
                                field: $translate.instant('global.status'),
                                value: selectedPlacement.placementStatusLabel
                            }
                        ]
                    }
                ]
            };

            return properties;
        }

        function getCreativeGroupProperties(selectedCreativeGroup) {
            var properties = {
                mainTitle: selectedCreativeGroup.creativeGroupLabel,
                editLabel: $translate.instant('schedule.editCreativeGroupInsertion'),
                title: selectedCreativeGroup.creativeGroupLabel,
                groups: [
                    {
                        groupName: '',
                        content: [
                            {
                                field: $translate.instant('creativeGroup.deliveryOptions'),
                                value: ''
                            },
                            {
                                field: $translate.instant('creativeGroup.weighting'),
                                value: selectedCreativeGroup.weight
                            },
                            {
                                field: $translate.instant('creativeGroup.enableFrequencyCapping'),
                                value: $translate.instant('creativeGroup.frequencyProperties',
                                    {
                                        frequency: selectedCreativeGroup.frequencyCap,
                                        impressions: selectedCreativeGroup.frequencyCapWindow
                                    })
                            },
                            {
                                field: $translate.instant('creativeGroup.priority'),
                                value: selectedCreativeGroup.creativeGroupPriority
                            },

                            {
                                field: $translate.instant('creativeGroup.targetingOptions'),
                                value: ''
                            },
                            {
                                field: $translate.instant('creativeGroup.targeting.cookie'),
                                value: selectedCreativeGroup.creativeGroupDoCookieTarget ?
                                    $translate.instant('global.on') : $translate.instant('global.off')
                            },
                            {
                                field: $translate.instant('creativeGroup.targeting.geo'),
                                value: selectedCreativeGroup.creativeGroupDoGeoTarget ?
                                    $translate.instant('global.on') : $translate.instant('global.off')
                            },
                            {
                                field: $translate.instant('creativeGroup.targeting.dayPart'),
                                value: selectedCreativeGroup.creativeGroupDoDayPartTarget ?
                                    $translate.instant('global.on') : $translate.instant('global.off')
                            }
                        ]
                    }
                ]
            };

            return properties;
        }

        function getCreativeProperties(selectedCreative) {
            var properties = {
                mainTitle: selectedCreative.creativeLabel,
                editLabel: $translate.instant('schedule.editCreativeInsertion'),
                title: selectedCreative.creativeName,
                groups: [
                    {
                        groupName: '',
                        content: [
                            {
                                field: $translate.instant('creative.creativeAlias'),
                                value: selectedCreative.creativeName
                            },
                            {
                                field: $translate.instant('creative.defaultURL'),
                                value: formatClickThroughUrls(selectedCreative.clickThroughUrl)
                            },
                            {
                                field: $translate.instant('global.status'),
                                value: $translate.instant('global.active')
                            },
                            {
                                field: $translate.instant('global.filename'),
                                value: selectedCreative.filename
                            },
                            {
                                field: $translate.instant('global.size'),
                                value: selectedCreative.sizeName
                            },
                            {
                                field: $translate.instant('creative.creativeType'),
                                value: selectedCreative.creativeType
                            }
                        ]
                    }
                ]
            };

            if (selectedCreative.field === vm.SCHEDULE_LEVEL.CREATIVE.KEY) {
                properties.groups[0].content.splice(1, 1);
            }

            return properties;
        }

        function formatClickThroughUrls(clickThroughUrls) {
            var clickThroughs = '<ul class="clickthrough-urls">';

            angular.forEach(clickThroughUrls, function (clickThroughUrl) {
                clickThroughs += '<li>' + clickThroughUrl + '</li>';
            });

            return clickThroughs += '</ul>';
        }

        function formatDate(textDate, type) {
            if (!angular.isString(textDate)) {
                return $translate.instant('validation.error.invalidDate');
            }

            textDate = ScheduleConfigService.getRightDateRendering(textDate);
            var date = DateTimeService.customParse(textDate, DateTimeService.FORMAT.DATE_TIME_US);

            textDate = type === 'end' ?
                DateTimeService.inverseParse(date) :
                DateTimeService.inverseParse(date);

            return textDate;
        }

        function checkSelectedLevel(selection, currentPivot) {
            enableAddScheduleButton(selection, currentPivot);
            if (selection.field === vm.SCHEDULE_LEVEL.SITE.KEY) {
                vm.siteId = selection.siteId;
                vm.creativeGroupId = selection.creativeGroupId;
                vm.creativeId = selection.creativeId;
                vm.field = vm.SCHEDULE_LEVEL.SITE.KEY;
            }
            else if (selection.field === vm.SCHEDULE_LEVEL.PLACEMENT.KEY) {
                vm.creativeGroupId = selection.creativeGroupId;
                vm.placementId = selection.placementId;
                vm.siteId = selection.siteId;
                vm.siteSectionId = selection.siteSectionId;
                vm.creativeId = selection.creativeId;
                vm.field = vm.SCHEDULE_LEVEL.PLACEMENT.KEY;
            }
            else if (selection.field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                vm.creativeGroupId = selection.creativeGroupId;
                vm.placementId = selection.placementId;
                vm.siteId = selection.siteId;
                vm.siteSectionId = selection.siteSectionId;
                vm.creativeId = selection.creativeId;
                vm.field = vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY;
            }
            else if (selection.field === vm.SCHEDULE_LEVEL.CREATIVE.KEY) {
                vm.creativeId = selection.creativeId;
            }
            else {
                vm.siteId = undefined;
                vm.creativeGroupId = undefined;
                vm.placementId = undefined;
                vm.siteSectionId = undefined;
                vm.field = selection.field;
            }
        }

        function creativeGroupPayload() {
            var changes = [],
                update,
                id;

            for (id in updatedCreativeGroups) {
                if (updatedCreativeGroups.hasOwnProperty(id)) {
                    update = updatedCreativeGroups[id];
                    changes.push({
                        id: parseInt(id),
                        weight: update.weight
                    });
                }
            }

            return changes;
        }

        function creativeInsertionPayload() {
            var changes = [],
                update,
                id;

            for (id in updatedCreativeInsertions) {
                if (updatedCreativeInsertions.hasOwnProperty(id)) {
                    update = updatedCreativeInsertions[id];
                    changes.push({
                        id: parseInt(id),
                        weight: update.weight,
                        endDate: formatDate(update.endDate, 'end'),
                        startDate: formatDate(update.startDate, 'start'),
                        clickthrough: lodash.head(update.clickthroughs),
                        clickthroughs: Utils.isUndefinedOrNull(update.clickthroughs) ? undefined :
                            update.clickthroughs.splice(1).map(function (ct, index) {
                                return {
                                    sequence: index + 2,
                                    url: ct
                                };
                            })
                    });
                }
            }

            return changes;
        }

        function enableAddScheduleButton(row, currentPivot) {
            vm.showAddScheduleButton = true;
            vm.showAddGroupCreativeAssocButton = false;
            switch (currentPivot.KEY) {
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    if (row.field === vm.SCHEDULE_LEVEL.PLACEMENT.KEY) {
                        vm.showAddGroupCreativeAssocButton = true;
                        vm.showAddScheduleButton = false;
                    }

                    break;
                case vm.SCHEDULE_LEVEL.SITE.KEY:
                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    if (row.field === vm.SCHEDULE_LEVEL.PLACEMENT.KEY ||
                        row.field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                        vm.showAddGroupCreativeAssocButton = true;
                        vm.showAddScheduleButton = false;
                    }

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE.KEY:
                    if (row.field === vm.SCHEDULE_LEVEL.PLACEMENT.KEY) {
                        vm.showAddGroupCreativeAssocButton = true;
                        vm.showAddScheduleButton = false;
                    }
                    else if (row.field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                        vm.showAddScheduleButton = false;
                    }

                    break;
            }

            if (row.field === vm.SCHEDULE_LEVEL.SCHEDULE.KEY ||
                row.field === vm.SCHEDULE_LEVEL.SECTION.KEY) {
                vm.showAddScheduleButton = false;
            }
        }

        function stripUUIDs(array) {
            if (!array) {
                return undefined;
            }

            return lodash.map(array, function (model) {
                updateEditFields(model);
                model.oldKey = model.$$uuid;
                model.children = stripUUIDs(model.children);

                return lodash.omit(model, '$$uuid');
            });
        }

        function flyoutRowCollapsed($row) {
            $row.expanded = false;
            if ($row) {
                verifyEvent(function () {
                    $('.te-tree-grid-delegate').jqxTreeGrid('collapseRow', $row.oldKey);
                });

                if (Utils.isUndefinedOrNull($row.level)) {
                    $row.level = 0;
                }

                bulkEditOnSite(vm.submodel[0]);
            }
        }

        function flyoutRowExpanded($row) {
            var model;

            if ($row.loadData) {
                verifyEvent(function () {
                    $('.te-tree-grid-delegate').jqxTreeGrid('expandRow', $row.oldKey);
                });
            }
            else {
                model = vm.context === CONTEXT.SEARCH || vm.scheduleTabContext === CONTEXT.SEARCH ? vm.submodel : null;

                $scope.$parent.vmList.loadDataByRow($row, true, model);
            }

            if (Utils.isUndefinedOrNull($row.level)) {
                $row.level = 0;
            }

            $row.expanded = true;
            if ($row) {
                bulkEditOnSite($row);
            }

            $timeout(function () {
                angular.element('#scheduleInsertionsContainerGhost').trigger('rowVisible');
            }, 10);
        }

        function verifyEvent(event) {
            if (vm.context === CONTEXT.DEFAULT) {
                if (vm.scheduleTabContext === CONTEXT.DEFAULT) {
                    promiseUpdate($timeout(event, 0, false));
                }
            }
        }

        function bulkUpdate() {
            var payload = {
                creativeInsertions: creativeInsertionPayload(),
                creativeGroups: creativeGroupPayload()
            };

            vm.promise = CreativeInsertionService.bulkUpdate(payload);
            vm.promise.then(function (response) {
                if (!response) {
                    return;
                }

                $scope.$parent.vmList.changePivot();
                modelChanged(true);
            });
        }

        function getAllChecked($allChecked) {
            updateParentNodeCheckedValue($allChecked);
            getCheckedNodesBasedOnContext($allChecked);
            vm.checkedList = [];
            getLeafNodes($allChecked, vm.checkedList);
            reloadGridConfiguration({
                refreshColumns: true
            });
        }

        function getCheckedNodesBasedOnContext(checkedList) {
            if (Utils.isUndefinedOrNull(vm.searchServerText) && !isInFilterMode()) {
                vm.parentNodeChecked = true;
                vm.allMarkedAsChecked = checkedList;
            }
            else {
                vm.parentNodeChecked = false;
                vm.allMarkedAsChecked = [];
                getLeafNodes(checkedList, vm.allMarkedAsChecked);
            }
        }

        function getLeafNodes(node, resultList) {
            if (Utils.isUndefinedOrNull(node)) {
                return;
            }

            var length = node.length - 1;

            while (length > -1) {
                if (Utils.isUndefinedOrNull(node[length].loadData) || node[length].loadData === false) {
                    resultList.push(node[length]);
                }
                else {
                    vm.parentNodeChecked = true;
                    getLeafNodes(node[length].children, resultList);
                }

                length--;
            }
        }

        function updateParentNodeCheckedValue(checkedList) {
            if (vm.submodel !== null) {
                var result = lodash.find(checkedList, function (value) {
                    return vm.submodel[0].treeRowId === value.treeRowId;
                });

                if (!Utils.isUndefinedOrNull(vm.submodel[0])) {
                    vm.submodel[0].checked = angular.isDefined(result);
                }
            }
        }

        function hierarchicalRemove() {
            if (!Utils.isUndefinedOrNull(vm.allMarkedAsChecked[0].children) &&
                vm.allMarkedAsChecked[0].children.length > 0) {
                confirmDelete();
            }
            else {
                performDelete();
            }
        }

        function confirmDelete() {
            var title = 'DIALOGS_CONFIRMATION_MSG',
                message = 'creativeInsertion.confirmDelete';

            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant(title),
                description: $translate.instant(message,
                    {
                        entities: getCheckedCreativeInsertions()
                    }
                ),
                buttons: {
                    yes: $translate.instant('global.remove'),
                    no: $translate.instant('global.cancel')
                }
            }).result.then(performDelete);
        }

        function performDelete() {
            var nodesToDelete = [],
                promBulkDelete;

            nodesToDelete = buildDeletePayload();
            promBulkDelete = CampaignsService.bulkDeleteCreativeInsertions(
                $scope.$parent.vmList.campaignId, nodesToDelete
            );
            promBulkDelete.then(function () {
                vm.close();
                $scope.$parent.vmList.changePivot();
                vm.allMarkedAsChecked = [];
                modelChanged(true);
            });
        }

        function getCheckedCreativeInsertions() {
            var result = vm.allMarkedAsChecked[0].siteDetailLabel;

            if (vm.allMarkedAsChecked.length >= 2) {
                angular.forEach(vm.allMarkedAsChecked, function (value, index) {
                    if (index !== 0 && vm.allMarkedAsChecked.length !== index + 1) {
                        result += ', ' + value.siteDetailLabel;
                    }
                    else if (vm.allMarkedAsChecked.length === index + 1) {
                        result += ' and ' + value.siteDetailLabel;
                    }
                });
            }

            return result;
        }

        function updateCheckedToChildNode(node, checked) {
            if (node) {
                var length = node.length - 1;

                while (length > -1) {
                    node[length].checked = checked;
                    updateCheckedToChildNode(node[length].children, checked);
                    length--;
                }
            }
        }

        function updateCheckedToModel(model) {
            var checkListLength = vm.allMarkedAsChecked.length - 1,
                row,
                newCheckedList = [];

            while (checkListLength > -1) {
                row = $scope.$parent.vmList.findSelection(model, vm.allMarkedAsChecked[checkListLength]);
                if (Utils.isUndefinedOrNull(row)) {
                    addToCheckedList(vm.allMarkedAsChecked[checkListLength], newCheckedList);
                }
                else {
                    row.checked = true;
                    addToCheckedList(row, newCheckedList);
                    updateCheckedToChildNode(row.children, true);
                    updateCheckedToParentNode(row, newCheckedList);
                }

                checkListLength--;
            }

            if (Utils.isUndefinedOrNull(vm.searchServerText)) {
                vm.checkedList = [];
                getLeafNodes(newCheckedList, vm.checkedList);
            }
            else {
                vm.checkedList = newCheckedList;
            }

            vm.allMarkedAsChecked = newCheckedList;
            updateCheckedItems({
                allMarkedAsChecked: newCheckedList
            });
        }

        function refreshListCheck() {
            updateModel(vm.modelWithoutFilter);
            updateCheckedToChildNode(vm.submodel, false);
            updateCheckedInSearchContext(vm.submodel, vm.checkedList);
        }

        function updateCheckedInSearchContext(model, checkedList) {
            var checkListLength = checkedList.length - 1,
                row,
                newCheckedList = [];

            while (checkListLength > -1) {
                row = $scope.$parent.vmList.findSelection(model, checkedList[checkListLength]);
                if (Utils.isUndefinedOrNull(row)) {
                    addToCheckedList(checkedList[checkListLength], newCheckedList);
                }
                else {
                    row.checked = true;
                    addToCheckedList(row, newCheckedList);
                    updateCheckedToChildNode(row.children, true);
                    updateCheckedToParentNode(row, newCheckedList);
                }

                checkListLength--;
            }

            if (Utils.isUndefinedOrNull(vm.searchServerText)) {
                vm.checkedList = [];
                getLeafNodes(newCheckedList, vm.checkedList);
            }
            else {
                vm.checkedList = newCheckedList;
            }

            vm.allMarkedAsChecked = newCheckedList;
            updateCheckedItems({
                allMarkedAsChecked: newCheckedList
            });
        }

        function updateModel(selection) {
            if (!selection) {
                return;
            }

            vm.submodel = stripUUIDs([lodash.cloneDeep(selection)]);
            vm.modelInitial = lodash.clone(vm.submodel, true);
        }

        function reloadGridConfiguration(params) {
            var args = params || {};

            args.uuid = vm.flyoutUUID;
            args.clearSearch = params ? params.clearSearch : undefined;
            args.refreshColumns = params ? params.refreshColumns : undefined;
            $scope.$broadcast('te-tree-grid:reloadConfiguration', args);
        }

        function getDetailColumnTitle() {
            var title;

            switch (vm.field) {
                case vm.SCHEDULE_LEVEL.SITE.KEY:
                    title = $translate.instant('global.site');
                    break;
                case vm.SCHEDULE_LEVEL.SECTION.KEY:
                    title = $translate.instant('global.section');
                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    title = $translate.instant('global.placement');
                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    title = $translate.instant('creativeGroup.creativeGroup');
                    break;
                case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                    title = $translate.instant('creative.creative');
                    break;
                default:
                    title = $translate.instant('schedule.siteDetail');
                    break;
            }

            return title;
        }

        function getCTUrlColumnTitle() {
            return vm.flyoutState === vm.FLYOUT_STATE.FULL_VIEW ?
                $translate.instant('schedule.clickThroughUrlPrimary') :
                $translate.instant('schedule.clickThroughUrl');
        }

        function getFilterable(field) {
            return $scope.$parent.vmList.getFilterable(field);
        }

        function getPosition(field) {
            return $scope.$parent.vmList.getPosition(field);
        }

        function getColumnWidth(column) {
            var width;

            switch (column) {
                case 'siteDetailLabel':
                    width = '28%';
                    break;
                case 'weight':
                    width = vm.flyoutState === vm.FLYOUT_STATE.FULL_VIEW ? '7%' : '10%';
                    break;
                case 'flightDateStart':
                    width = vm.flyoutState === vm.FLYOUT_STATE.FULL_VIEW ? '15%' : '20%';
                    break;
                case 'flightDateEnd':
                    width = vm.flyoutState === vm.FLYOUT_STATE.FULL_VIEW ? '15%' : '20%';
                    break;
            }

            return width;
        }

        function showAddScheduleDialog() {
            var params = {
                fromFlyout: true,
                siteId: vm.siteId,
                placementId: vm.placementId,
                groupId: vm.creativeGroupId,
                creativeId: vm.creativeId
            };

            $scope.$parent.vmList.showAddScheduleDialog(params).then(
                function () {
                    $scope.$parent.vmList.changePivot();
                    modelChanged(true);
                });
        }

        function showAddGroupCreativeDialog() {
            var params = {
                fromFlyout: true,
                siteId: vm.siteId,
                placementId: vm.placementId,
                groupId: vm.creativeGroupId,
                sectionId: vm.siteSectionId,
                creativeId: vm.creativeId
            };

            $scope.$parent.vmList.showAddGroupCreativeDialog(params).then(
                function () {
                    $scope.$parent.vmList.changePivot();
                    modelChanged(true);
                });
        }

        function expandCollapseRow(row, data) {
            var action = data.expanded === true ? 'expandRow' : 'collapseRow';

            $scope.$parent.vmList.promise = $timeout(function () {
                $('.te-tree-grid-delegate').jqxTreeGrid(action, row.$$uuid);
            }, 0, false);
        }

        function modelChanged(status) {
            $scope.$parent.$broadcast('status.traffic', {
                option: 'modelChanged',
                status: status
            });
        }

        function bulkEditOnSite(data) {
            ScheduleFlyoutUtilService.resetBulkEditOptions();
            switch (data.field) {
                case vm.SCHEDULE_LEVEL.SITE.KEY:
                    if (vm.selectedPivot.KEY !== vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY &&
                        vm.selectedPivot.KEY !== vm.SCHEDULE_LEVEL.SCHEDULE.KEY) {
                        ScheduleFlyoutUtilService.hasSiteVisibleCreatives(data);
                    }
                    else {
                        ScheduleFlyoutUtilService.hasSectionVisibleCreatives(data);
                    }

                    break;

                case vm.SCHEDULE_LEVEL.SECTION.KEY:
                    ScheduleFlyoutUtilService.hasSectionVisibleCreatives(data);
                    break;

                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    ScheduleFlyoutUtilService.hasPlacementVisibleCreatives(data);
                    if (vm.selectedPivot.KEY === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                        vm.bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
                        if (vm.bulkEditOpt.isCreativeGroupVisible) {
                            vm.isCreativeGroupVisible = false;
                            vm.isCreativeVisible = true;
                        }
                        else {
                            vm.isCreativeGroupVisible = false;
                            vm.isCreativeVisible = false;
                        }

                        return;
                    }

                    break;

                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    if (vm.selectedPivot.KEY === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                        ScheduleFlyoutUtilService.hasSectionVisibleCreatives(data);
                    }
                    else {
                        ScheduleFlyoutUtilService.hasCreativeGroupVisibleCreatives(data);
                    }

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE.KEY:
                    if (vm.selectedPivot.KEY === vm.SCHEDULE_LEVEL.SCHEDULE.KEY) {
                        ScheduleFlyoutUtilService.hasSectionVisibleCreatives(data);
                        vm.bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
                        if (vm.bulkEditOpt.isCreativeVisible === true) {
                            vm.isCreativeGroupVisible = true;
                            vm.isCreativeVisible = false;
                        }
                        else {
                            vm.isCreativeGroupVisible = false;
                            vm.isCreativeVisible = true;
                        }

                        return;
                    }
                    else if (vm.selectedPivot.KEY === vm.SCHEDULE_LEVEL.CREATIVE.KEY) {
                        ScheduleFlyoutUtilService.hasSiteVisibleCreatives(data);
                        vm.bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
                    }
                    else {
                        vm.isCreativeGroupVisible = false;
                        vm.isCreativeVisible = true;
                        return;
                    }

                    break;
            }
            vm.bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
            if (data.field === vm.selectedPivot.KEY && vm.selectedPivot.KEY === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY &&
                vm.bulkEditOpt.isCreativeVisible === false) {
                vm.isCreativeGroupVisible = true;
                vm.isCreativeVisible = false;
                return;
            }

            if (data.field === vm.SCHEDULE_LEVEL.SITE.KEY &&
                vm.selectedPivot.KEY === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY &&
                vm.bulkEditOpt.isCreativeGroupVisible === true) {
                vm.isCreativeGroupVisible = false;
                vm.isCreativeVisible = true;
                return;
            }
            else {
                vm.bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
                vm.isCreativeGroupVisible = vm.bulkEditOpt.isCreativeGroupVisible;
                vm.isCreativeVisible = vm.bulkEditOpt.isCreativeVisible;
                return;
            }
        }

        function updateCheckedItems(params) {
            var args = params || {};

            args.uuid = vm.flyoutUUID;
            args.allMarkedAsChecked = params.allMarkedAsChecked ? params.allMarkedAsChecked : vm.allMarkedAsChecked;
            $scope.$broadcast('te-tree-grid:checked:updateAll', args);
        }

        function updateEditFields(row) {
            FlyoutEditionStashService.load(row);
        }

        function onCloseFlyout() {
            vm.mainVmStatus.cm.schedule.isFlyoutVisible = false;
        }

        function onOpenFlyout() {
            vm.mainVmStatus.cm.schedule.isFlyoutVisible = true;
        }

        /**
         * SEARCH SERVER
         * */
        function reloadSearchOptions() {
            var reloadFlyoutSearchOption = $q.defer(),
                promise = reloadFlyoutSearchOption.promise;

            if (Utils.isUndefinedOrNull(vm.modelSearchFlyoutFields)) {
                vm.modelSearchFlyoutFields = lodash.clone($scope.$parent.vmList.searchFields, true);
                lodash.forEach(vm.modelSearchFlyoutFields, function (value) {
                    value.enabled = true;
                });
            }

            reloadFlyoutSearchOption.resolve({
                searchFields: lodash.filter(vm.modelSearchFlyoutFields, function (value) {
                    return value.position > vm.level;
                })
            });

            return promise;
        }

        function serverFlyoutSideClearSearch(cleanSearch) {
            if (cleanSearch) {
                vm.modelWithoutSearch = null;
            }
            else {
                if (!Utils.isUndefinedOrNull(vm.modelWithoutSearch)) {
                    vm.submodel = [];
                    vm.searchServerText = null;
                    vm.context = getContext(vm.searchServerText);
                    angular.copy(vm.modelWithoutSearch, vm.submodel);
                    angular.copy(vm.optionFilterWithoutSearch, $scope.$parent.vmList.filterValues);
                    vm.isNewItemSelectd = true;
                    vm.filterControlsCollapsed = false;
                    showFilterOptionAsync().then(function () {
                        $scope.$emit('schedule.flyout.filter', {
                                field: vm.selectedRowField,
                                model: vm.modelWithoutFilter
                            }
                        );
                        vm.filterControlsCollapsed = true;
                    });
                }

                if (!Utils.isUndefinedOrNull(vm.submodel)) {
                    bulkEditOnSite(vm.submodel[0]);
                    loadModifiedCreativesAndGroups();
                }

                vm.modelWithoutSearch = null;
            }

            updateCheckedToChildNode(vm.submodel, false);
            updateCheckedInSearchContext(vm.submodel, vm.checkedList);
        }

        function serverSideSearch(searchText) {
            var backup = [],
                flyoutSearch = {},
                modelServer;

            if (Utils.isUndefinedOrNull(vm.modelWithoutSearch)) {
                vm.modelWithoutSearch = lodash.clone(vm.submodel, true);
                vm.optionFilterWithoutSearch = lodash.clone($scope.$parent.vmList.filterValues);
            }

            vm.searchServerText = searchText;
            flyoutSearch.searchText = searchText;
            flyoutSearch.soSite = getStatusOptions('siteLabel');
            flyoutSearch.soSection = getStatusOptions('siteSectionLabel');
            flyoutSearch.soPlacement = getStatusOptions('placementLabel');
            flyoutSearch.soGroup = getStatusOptions('creativeGroupLabel');
            flyoutSearch.soCreative = getStatusOptions('creativeLabel');
            flyoutSearch.type = null;
            flyoutSearch.siteId = null;
            flyoutSearch.sectionId = null;
            flyoutSearch.placementId = null;
            flyoutSearch.groupId = null;
            flyoutSearch.creativeId = null;
            flyoutSearch.flyoutContext = null;

            if (Utils.isUndefinedOrNull(vm.submodel[0])) {
                loadFlyoutSearch(flyoutSearch, vm.modelInitial[0]);
                angular.copy(vm.modelInitial, backup);
            }
            else {
                loadFlyoutSearch(flyoutSearch, vm.submodel[0]);
                angular.copy(vm.submodel, backup);
            }

            vm.context = getContext(searchText);
            flyoutSearch.flyoutContext = vm.context;

            modelServer = $scope.$parent.vmList.loadDataServerSearch(flyoutSearch);
            promiseUpdate(modelServer);

            return modelServer.then(function (model) {
                vm.submodel = [];
                getModelFlyout(model[0], flyoutSearch, backup[0]);
                if (Utils.isUndefinedOrNull(vm.submodel[0])) {
                    ScheduleFlyoutUtilService.resetBulkEditOptions();
                    vm.bulkEditOpt = ScheduleFlyoutUtilService.bulkEditOptions();
                    vm.isCreativeGroupVisible = false;
                    vm.isCreativeVisible = false;
                }
                else {
                    bulkEditOnSite(vm.submodel[0]);
                }

                loadModifiedCreativesAndGroups();
                updateCheckedToChildNode(vm.submodel, false);
                $timeout(function () {
                    updateCheckedInSearchContext(vm.submodel, vm.checkedList);
                    refreshModelGrid();
                });

                initializeModelFilter();
                vm.filterControlsCollapsed = true;
                vm.isNewItemSelectd = true;
            });
        }

        function getContext(searchText) {
            if (Utils.isUndefinedOrNull(searchText) || searchText === '') {
                return CONTEXT.DEFAULT;
            }

            return CONTEXT.SEARCH;
        }

        function loadFlyoutSearch(flyoutSearch, rootFlyout) {
            flyoutSearch.type = rootFlyout.field;

            if (!Utils.isUndefinedOrNull(rootFlyout.siteId)) {
                flyoutSearch.siteId = rootFlyout.siteId;
            }

            if (!Utils.isUndefinedOrNull(rootFlyout.siteSectionId)) {
                flyoutSearch.siteSectionId = rootFlyout.siteSectionId;
            }

            if (!Utils.isUndefinedOrNull(rootFlyout.placementId)) {
                flyoutSearch.placementId = rootFlyout.placementId;
            }

            if (!Utils.isUndefinedOrNull(rootFlyout.creativeGroupId)) {
                flyoutSearch.creativeGroupId = rootFlyout.creativeGroupId;
            }

            if (!Utils.isUndefinedOrNull(rootFlyout.creativeId)) {
                flyoutSearch.creativeId = rootFlyout.creativeId;
            }
        }

        function getModelFlyout(model, rootFlyout, parent) {
            if (!Utils.isUndefinedOrNull(model)) {
                if (model.field === rootFlyout.type) {
                    parent.loadData = model.loadData;
                    parent.expanded = model.expanded;
                    parent.children = model.children;
                    angular.copy([parent], vm.submodel);
                }
                else {
                    getModelFlyout(model.children[0], rootFlyout, parent);
                }
            }
        }

        function getStatusOptions(field) {
            var option = lodash.filter(vm.searchFields, function (value) {
                return value.field === field;
            });

            return option.length === 1 ? option[0].enabled : false;
        }

        function loadModifiedCreativesAndGroups() {
            var creativesList = [],
                creativeGroupList = [];

            getCreativesAndGroups(vm.submodel, creativesList, creativeGroupList);
            FlyoutEditionStashService.bulkLoadGroups(creativeGroupList);
            FlyoutEditionStashService.bulkLoadCreatives(creativesList);
        }

        function getCreativesAndGroups(node, creativeList, creativeGroupList) {
            if (Utils.isUndefinedOrNull(node)) {
                return;
            }

            var length = node.length - 1;

            while (length > -1) {
                if (!Utils.isUndefinedOrNull(node[length].id) &&
                    node[length].field === vm.SCHEDULE_LEVEL.SCHEDULE.KEY) {
                    creativeList.push(node[length]);
                }
                else if (!Utils.isUndefinedOrNull(node[length].creativeGroupId) &&
                    node[length].field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                    creativeGroupList.push(node[length]);
                }

                getCreativesAndGroups(node[length].children, creativeList, creativeGroupList);
                length--;
            }
        }

        function updateCheckedToParentNode(node, updatedCheckedList) {
            var parent;

            if (!Utils.isUndefinedOrNull(node.parent)) {
                parent = node.parent;
                while (parent) {
                    checkParentNode(parent, updatedCheckedList);
                    parent = parent.parent;
                }
            }
        }

        function checkParentNode(parent, updatedCheckedList) {
            var checkedCount = 0,
                length = parent.children.length - 1;

            while (length > -1) {
                checkedCount = parent.children[length].checked === true ? ++checkedCount : checkedCount;
                length--;
            }

            if (parent.children.length === checkedCount) {
                if (Utils.isUndefinedOrNull(vm.searchServerText)) {
                    addToCheckedList(parent, updatedCheckedList);
                    lodash.forEach(parent.children, function (child) {
                        removeChildNodes(child, updatedCheckedList);
                    });
                }

                parent.checked = true;
                updateParentNodeCheckedValue([parent]);
            }
        }

        function removeChildNodes(node, updatedCheckedList) {
            lodash.remove(updatedCheckedList, function (value) {
                return value.treeRowId === node.treeRowId;
            });
        }

        function refreshModelGrid() {
            var outputModelBackup = [];

            angular.copy(vm.submodel, outputModelBackup);
            vm.submodel = [];
            angular.copy(outputModelBackup, vm.submodel);
        }

        function addToCheckedList(node, updatedCheckedList) {
            var res;

            res = lodash.find(updatedCheckedList, function (elem) {
                return elem.treeRowId === node.treeRowId;
            });

            if (res === undefined) {
                updatedCheckedList.push(node);
            }
        }

        function getItemsFiltersFromModel(model) {
            for (var i = 0; i < model.length; ++i) {
                setOptionToFilter(model[i]);
                if (model[i].children) {
                    getItemsFiltersFromModel(model[i].children);
                }

                if (model[i].backUpChildren) {
                    getItemsFiltersFromModel(model[i].backUpChildren);
                }
            }
        }

        function setOptionToFilter(model) {
            switch (model.field) {
                case vm.SCHEDULE_LEVEL.SITE.KEY:
                    if (!Utils.isUndefinedOrNull(vm.filterOptions.SITE)) {
                        vm.filterOptions.SITE.value.push({
                            label: model.siteDetailLabel,
                            id: model.siteId,
                            enabled: true
                        });
                    }

                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    if (!Utils.isUndefinedOrNull(vm.filterOptions.PLACEMENT)) {
                        vm.filterOptions.PLACEMENT.value.push({
                            label: model.placementLabel,
                            id: model.placementId,
                            enabled: true
                        });
                    }

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    if (!Utils.isUndefinedOrNull(vm.filterOptions.GROUP)) {
                        vm.filterOptions.GROUP.value.push({
                            label: model.creativeGroupLabel,
                            id: model.creativeGroupId,
                            enabled: true
                        });
                    }

                    break;
                case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                    if (!Utils.isUndefinedOrNull(vm.filterOptions.CREATIVE)) {
                        vm.filterOptions.CREATIVE.value.push({
                            label: model.creativeLabel,
                            id: model.creativeId,
                            enabled: true
                        });
                    }

                    break;
            }
        }

        function setFilterChecked() {
            if (!Utils.isUndefinedOrNull(vm.filterOptions.SITE)) {
                vm.filterOptions.SITE.value = lodash
                    .uniq(vm.filterOptions.SITE.value, function (item) {
                        return item.id;
                    });

                vm.filterOptions.SITE.value = lodash.sortBy(vm.filterOptions.SITE.value, 'label');

                $scope.$parent.vmList.filterValues[0].values = vm.filterOptions.SITE.value.map(function (res) {
                    return res.id;
                });
            }

            if (!Utils.isUndefinedOrNull(vm.filterOptions.PLACEMENT)) {
                vm.filterOptions.PLACEMENT.value = lodash
                    .uniq(vm.filterOptions.PLACEMENT.value, function (item) {
                        return item.id;
                    });

                vm.filterOptions.PLACEMENT.value = lodash.sortBy(vm.filterOptions.PLACEMENT.value, 'label');
                $scope.$parent.vmList.filterValues[1].values = vm.filterOptions.PLACEMENT.value.map(function (res) {
                    return res.id;
                });
            }

            if (!Utils.isUndefinedOrNull(vm.filterOptions.GROUP)) {
                vm.filterOptions.GROUP.value = lodash
                    .uniq(vm.filterOptions.GROUP.value, function (item) {
                        return item.id;
                    });

                vm.filterOptions.GROUP.value = lodash.sortBy(vm.filterOptions.GROUP.value, 'label');
                $scope.$parent.vmList.filterValues[2].values = vm.filterOptions.GROUP.value.map(function (res) {
                    return res.id;
                });
            }

            if (!Utils.isUndefinedOrNull(vm.filterOptions.CREATIVE)) {
                vm.filterOptions.CREATIVE.value = lodash
                    .uniq(vm.filterOptions.CREATIVE.value, function (item) {
                        return item.id;
                    });

                vm.filterOptions.CREATIVE.value = lodash.sortBy(vm.filterOptions.CREATIVE.value, 'label');

                $scope.$parent.vmList.filterValues[3].values = vm.filterOptions.CREATIVE.value.map(function (res) {
                    return res.id;
                });
            }
        }

        function initializeModelFilter() {
            vm.filterOptions.SITE.value = [];
            vm.filterOptions.PLACEMENT.value = [];
            vm.filterOptions.GROUP.value = [];
            vm.filterOptions.CREATIVE.value = [];
        }

        function isInFilterMode() {
            var filteredElementCount = vm.filterOptions.SITE.value.length + vm.filterOptions.PLACEMENT.value.length +
                vm.filterOptions.GROUP.value.length + vm.filterOptions.CREATIVE.value.length;

            return filteredElementCount > 0 ? true : false;
        }
    }
})();
