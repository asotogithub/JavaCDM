(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('ScheduleTabController', ScheduleTabController);

    ScheduleTabController.$inject = [
        '$interpolate',
        '$q',
        '$rootScope',
        '$scope',
        '$stateParams',
        '$templateCache',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'CampaignsUtilService',
        'CreativeUtilService',
        'DateTimeService',
        'DialogFactory',
        'FlyoutEditionStashService',
        'PlacementService',
        'ScheduleUtilService',
        'UserService',
        'Utils',
        'dialogs',
        'lodash',
        'uuid'
    ];

    function ScheduleTabController(
        $interpolate,
        $q,
        $rootScope,
        $scope,
        $stateParams,
        $templateCache,
        $translate,
        CONSTANTS,
        CampaignsService,
        CampaignsUtilService,
        CreativeUtilService,
        DateTimeService,
        DialogFactory,
        FlyoutEditionStashService,
        PlacementService,
        ScheduleUtilService,
        UserService,
        Utils,
        dialogs,
        lodash,
        uuid) {
        var DIALOG = DialogFactory.DIALOG,
            ICONS = CONSTANTS.SCHEDULE.ICONS,
            STATUS_LIST = CampaignsUtilService.getIOStatusList(),
            TRAFFIC_BUTTON_LABEL = $translate.instant('global.traffic'),
            children = [
                {
                    id: null
                }
            ],
            vm = this;

        vm.CONTEXT = {
            DEFAULT: {
                id: 'default-context'
            },
            SEARCH: {
                id: 'search-context'
            }
        };

        vm.FLYOUT = {
            FULL_VIEW: {
                id: 'full-view'
            },
            HALF_VIEW: {
                id: 'half-view'
            },
            HIDDEN: {
                id: 'hidden'
            }
        };

        vm.PAGE_SIZE = CONSTANTS.TE_TREE_GRID.PAGE_SIZE.SMALL;
        vm.SCHEDULE_LEVEL = CONSTANTS.SCHEDULE.LEVEL;
        vm.SERVER_SIDE_SEARCH_OPTIONS = {
            searchInterval: CONSTANTS.TE_TREE_GRID.SEARCH_INTERVAL,
            searchMinLength: CONSTANTS.TE_TREE_GRID.SEARCH_MIN_LENGTH
        };
        vm.addScheduleAssignments = addScheduleAssignments;
        vm.associationsCellsRenderer = associationsCellsRenderer;
        vm.campaignId = $stateParams.campaignId;
        vm.changePivot = changePivot;
        vm.clickThroughUrlCellsRenderer = clickThroughUrlCellsRenderer;
        vm.context = vm.CONTEXT.DEFAULT;
        vm.downloadSuccessful = false;
        vm.expanded = [];
        vm.findSelection = findSelection;
        vm.flightDatesCellsRenderer = flightDatesCellsRenderer;
        vm.flyoutContext = vm.CONTEXT.DEFAULT;
        vm.flyoutState = vm.FLYOUT.HIDDEN;
        vm.getCreativeInsertions = getCreativeInsertions;
        vm.getDetailColumnTitle = getDetailColumnTitle;
        vm.getExportAll = getExportAll;
        vm.getFilterable = getFilterable;
        vm.getPosition = getPosition;
        vm.getRowRenderer = getRowRenderer;
        vm.hasExportCreativeInsertionsPermission = UserService.hasPermission(
            CONSTANTS.PERMISSION.SCHEDULE.EXPORT_CREATIVE_INSERTIONS);
        vm.hasImportCreativeInsertionsPermission = UserService.hasPermission(
            CONSTANTS.PERMISSION.SCHEDULE.IMPORT_CREATIVE_INSERTIONS);
        vm.isSearch = false;
        vm.isServerSearchFlyout = false;
        vm.loadDataByRow = loadDataByRow;
        vm.loadDataByRowSearch = loadDataByRowSearch;
        vm.loadDataServerSearch = loadDataServerSearch;
        vm.mainVmStatus = $scope.mainVm.status;
        vm.model = null;
        vm.modelWithoutSearch = null;
        vm.openImport = openImport;
        vm.pivotList = lodash.values(getAvailablePivot());
        vm.placementStatusCellsRenderer = placementStatusCellsRenderer;
        vm.promise = null;
        vm.rowCollapsed = rowCollapsed;
        vm.rowExpanded = rowExpanded;
        vm.save = save;
        vm.searchFields = [];
        vm.seedModel = {};
        vm.selectedPivot = vm.SCHEDULE_LEVEL.SITE;
        vm.selectionData = [];
        vm.serverSearchText = null;
        vm.serverSearchTextFlyout = null;
        vm.serverSideClearSearch = serverSideClearSearch;
        vm.serverSideSearch = serverSideSearch;
        vm.showAddGroupCreativeDialog = showAddGroupCreativeDialog;
        vm.showAddScheduleDialog = showAddScheduleDialog;
        vm.toSiteSectionRows = toSiteSectionRows;
        vm.trafficButtonLabel = TRAFFIC_BUTTON_LABEL;
        vm.traffickingWarning = traffickingWarning;
        vm.treeUUID = uuid.v4();
        vm.updatedModel = false;
        vm.filterValues = [
            {
                fieldName: vm.SCHEDULE_LEVEL.SITE.KEY,
                values: [],
                key: CONSTANTS.SCHEDULE.FILTER_FLYOUT_VIEW.KEY.SITE
            },
            {
                fieldName: vm.SCHEDULE_LEVEL.PLACEMENT.KEY,
                values: [],
                key: CONSTANTS.SCHEDULE.FILTER_FLYOUT_VIEW.KEY.PLACEMENT
            },
            {
                fieldName: vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY,
                values: [],
                key: CONSTANTS.SCHEDULE.FILTER_FLYOUT_VIEW.KEY.CREATIVE_GROUP
            },
            {
                fieldName: vm.SCHEDULE_LEVEL.SCHEDULE.KEY,
                values: [],
                key: CONSTANTS.SCHEDULE.FILTER_FLYOUT_VIEW.KEY.CREATIVE
            }
        ];

        vm.filterOptions = {
            SITE: {
                text: $translate.instant('global.sites'),
                value: [],
                index: 0,
                fieldName: vm.SCHEDULE_LEVEL.SITE.KEY
            },
            PLACEMENT: {
                text: $translate.instant('global.placements'),
                value: [],
                index: 1,
                fieldName: vm.SCHEDULE_LEVEL.PLACEMENT.KEY
            },
            GROUP: {
                text: $translate.instant('global.groups'),
                value: [],
                index: 2,
                fieldName: vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY
            },
            CREATIVE: {
                text: $translate.instant('creative.creative'),
                value: [],
                index: 3,
                fieldName: vm.SCHEDULE_LEVEL.SCHEDULE.KEY
            }
        };

        activate();

        function activate() {
            ScheduleUtilService.resetStatusTraffic();
            getCreativeInsertions();
        }

        function getAvailablePivot() {
            var listPivot = lodash.filter(CONSTANTS.SCHEDULE.LEVEL, function (item) {
                return UserService.hasPermission(item.PERMISSION);
            });

            return listPivot;
        }

        $scope.$on('status.traffic', function (event, data) {
            if (data.option === 'modal') {
                if (!ScheduleUtilService.getStatusTraffic().isOpenWarning) {
                    traffickingWarning(data);
                }
            }
            else if (data.option === 'modelChanged') {
                modelChanged(data.status);
            }
        });

        vm.onSelect = function ($selection, $level, $coords) {
            var settingScheduleTab = {},
                model;

            if (vm.context === vm.CONTEXT.DEFAULT) {
                model = $selection;
                vm.selectionData = $selection;
            }
            else {
                model = lodash.clone($selection, true);
                model.loadData = false;
                model.expanded = false;
                model.children = null;
                getChildrenContextSearch(model);
                vm.selectionData = model;
            }

            vm.flyoutState = vm.FLYOUT.HALF_VIEW;
            settingScheduleTab.currentPivot = vm.selectedPivot;
            settingScheduleTab.context = vm.context;
            vm.onSelectItem(model, $level, $coords, settingScheduleTab);
        };

        function getChildrenContextSearch(model) {
            if (model.field !== 'schedule') {
                model.children = lodash.map(children, getChildren({
                    siteLabel: '',
                    siteSectionLabel: '',
                    placementLabel: '',
                    creativeGroupLabel: '',
                    creativeLabel: ''
                }));
            }
        }

        function addScheduleAssignments(siteId) {
            var dialogParams = {
                fromFlyout: false,
                siteId: siteId
            };

            showAddScheduleDialog(dialogParams).then(
                function (params) {
                    changePivot();
                    modelChanged(params.isUpdate);
                });
        }

        function showAddGroupCreativeDialog(contextParams) {
            var dialogProperties = {
                controller: 'AddGroupCreativeAssociationsController',
                size: DIALOG.SIZE.LARGE,
                template: 'app/cm/campaigns/schedule-tab/add-group-creative-associations/' +
                'add-group-creative-associations.html',
                type: DIALOG.TYPE.CUSTOM,
                windowClass: 'modal-' + DIALOG.SIZE.EXTRA_LARGE,
                data: {
                    campaignId: null,
                    name: '',
                    title: '',
                    toolTip: '',
                    creatives: [],
                    placement: {}
                }
            };

            return showCommonAddScheduleDialog(dialogProperties, contextParams, true);
        }

        function showAddScheduleDialog(contextParams) {
            var dialogProperties = {
                controller: 'AddScheduleAssignmentsController',
                size: DIALOG.SIZE.LARGE,
                template: 'app/cm/campaigns/schedule-tab/add-schedule-assignments/add-schedule-assignments.html',
                type: DIALOG.TYPE.CUSTOM,
                windowClass: 'modal-' + DIALOG.SIZE.EXTRA_LARGE,
                data: {
                    campaignId: null,
                    name: '',
                    title: '',
                    toolTip: '',
                    creatives: [],
                    placements: []
                }
            };

            return showCommonAddScheduleDialog(dialogProperties, contextParams, false);
        }

        function showCommonAddScheduleDialog(dialogProperties, contextParams, useGetPlacementService) {
            var result = $q.defer(),
                dialogParams = getScheduleAssociationDialogParams(contextParams);

            vm.promise = $q.all([
                CampaignsService.getCreativeGroupCreatives(vm.campaignId, dialogParams.queryParameters),
                useGetPlacementService ? PlacementService.getPlacement(contextParams.placementId) :
                    CampaignsService.getPlacements(vm.campaignId, dialogParams.queryParameters)
            ]).then(function (promise) {
                var creatives = processCreatives(promise[0]),
                    placements = useGetPlacementService ? promise[1] :
                        lodash.map(promise[1], function (placement) {
                            return lodash.extend({
                                placementEndDate: placement.endDate,
                                placementId: placement.id,
                                placementName: placement.name,
                                placementStartDate: placement.startDate
                            }, lodash.pick(
                                placement,
                                'height',
                                'siteId',
                                'siteName',
                                'siteSectionId',
                                'siteSectionName',
                                'sizeName',
                                'width'
                            ));
                        });

                dialogProperties.data.campaignId = vm.campaignId;
                dialogProperties.data.name = dialogParams.name;
                dialogProperties.data.title = dialogParams.title;
                dialogProperties.data.toolTip = dialogParams.toolTip;
                dialogProperties.data.creatives = creatives;
                dialogProperties.data.placements = placements;

                DialogFactory.showCustomDialog(dialogProperties).result.then(function (data) {
                    result.resolve(data);
                });
            });

            return result.promise;
        }

        function processCreatives(creatives) {
            return lodash.map(creatives, function (creative) {
                var height = creative.creativeHeight,
                    width = creative.creativeWidth;

                return lodash.extend({
                    height: height,
                    sizeName: width + 'x' + height,
                    width: width,
                    clickthroughs: creative.creativeClickthroughs,
                    clickthrough: creative.creativeDefaultClickthrough

                }, lodash.omit(creative, 'creativeHeight', 'creativeWidth'));
            });
        }

        function getScheduleAssociationDialogParams(contextParams) {
            var result = {};

            if (!contextParams.fromFlyout) {
                result.name = $translate.instant('schedule.availablePlacementsAndCreative');
                result.title = $translate.instant('schedule.addScheduleAssignments');

                return result;
            }

            switch (vm.selectedPivot) {
                case vm.SCHEDULE_LEVEL.SITE:
                    result.name = vm.selectionData.siteLabel;

                    if (vm.selectionData.field === vm.SCHEDULE_LEVEL.SITE.KEY) {
                        result.toolTip = $translate.instant('schedule.association.sitePivot.siteToolTip');
                        result.title = $translate.instant('schedule.association.sitePivot.siteTitle', {
                            name: vm.selectionData.siteLabel
                        });
                        result.queryParameters = {
                            pivotType: 'site',
                            type: 'site',
                            siteId: contextParams.siteId
                        };
                    }
                    else if (vm.selectionData.field === vm.SCHEDULE_LEVEL.PLACEMENT.KEY) {
                        result.toolTip = $translate.instant('schedule.association.sitePivot.placementToolTip');
                        result.title = $translate.instant('schedule.association.sitePivot.placementTitle', {
                            name: vm.selectionData.placementLabel
                        });
                        result.queryParameters = {
                            pivotType: 'site',
                            type: 'placement',
                            siteId: contextParams.siteId,
                            placementId: contextParams.placementId,
                            sectionId: contextParams.sectionId
                        };
                    }
                    else if (vm.selectionData.field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                        result.toolTip = $translate.instant('schedule.association.sitePivot.groupToolTip');
                        result.title = $translate.instant('schedule.association.sitePivot.groupTitle', {
                            name: vm.selectionData.placementLabel
                        });
                        result.queryParameters = {
                            pivotType: 'site',
                            type: 'group',
                            siteId: contextParams.siteId,
                            placementId: contextParams.placementId,
                            groupId: contextParams.groupId,
                            sectionId: contextParams.sectionId
                        };
                    }

                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT:
                    result.name = vm.selectionData.placementLabel;
                    if (vm.selectionData.field === vm.SCHEDULE_LEVEL.PLACEMENT.KEY) {
                        result.toolTip = $translate.instant('schedule.association.placementPivot.placementToolTip');
                        result.queryParameters = {
                            pivotType: 'placement',
                            type: 'placement',
                            placementId: contextParams.placementId
                        };
                    }
                    else {
                        result.toolTip = $translate.instant('schedule.association.placementPivot.groupToolTip');
                        result.queryParameters = {
                            pivotType: 'placement',
                            type: 'group',
                            placementId: contextParams.placementId,
                            groupId: contextParams.groupId
                        };
                    }

                    result.title = $translate.instant('schedule.association.placementPivotTitle', {
                        name: vm.selectionData.placementLabel
                    });

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP:
                    result.name = vm.selectionData.creativeGroupLabel;
                    if (vm.selectionData.field === vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY) {
                        result.toolTip = $translate.instant('schedule.association.groupPivot.groupToolTip');
                        result.title = $translate.instant('schedule.association.groupPivot.groupTitle', {
                            name: vm.selectionData.creativeGroupLabel
                        });
                        result.queryParameters = {
                            pivotType: 'group',
                            type: 'group',
                            groupId: contextParams.groupId
                        };
                    }
                    else if (vm.selectionData.field === vm.SCHEDULE_LEVEL.SITE.KEY) {
                        result.toolTip = $translate.instant('schedule.association.groupPivot.siteToolTip');
                        result.title = $translate.instant('schedule.association.groupPivot.siteTitle', {
                            name: vm.selectionData.siteLabel
                        });
                        result.queryParameters = {
                            pivotType: 'group',
                            type: 'site',
                            siteId: contextParams.siteId,
                            groupId: contextParams.groupId
                        };
                    }
                    else {
                        result.toolTip = $translate.instant('schedule.association.groupPivot.placementToolTip');
                        result.title = $translate.instant('schedule.association.groupPivot.placementTitle', {
                            name: vm.selectionData.placementLabel
                        });
                        result.queryParameters = {
                            pivotType: 'group',
                            type: 'placement',
                            siteId: contextParams.siteId,
                            placementId: contextParams.placementId,
                            groupId: contextParams.groupId
                        };
                    }

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE:
                    result.name = vm.selectionData.siteDetailLabel;
                    if (vm.selectionData.field === vm.SCHEDULE_LEVEL.CREATIVE.KEY) {
                        result.toolTip = $translate.instant('schedule.association.creativePivot.creativeToolTip');
                        result.title = $translate.instant('schedule.association.creativePivot.creativeTitle');
                        result.queryParameters = {
                            pivotType: 'creative',
                            type: 'creative',
                            creativeId: contextParams.creativeId //TODO: Update once creative pivot is done
                        };
                    }
                    else if (vm.selectionData.field === vm.SCHEDULE_LEVEL.SITE.KEY) {
                        result.toolTip = $translate.instant('schedule.association.creativePivot.siteToolTip', {
                            name: vm.selectionData.creativeLabel
                        });
                        result.title = $translate.instant('schedule.association.creativePivot.siteTitle', {
                            name: vm.selectionData.siteLabel
                        });
                        result.queryParameters = {
                            pivotType: 'creative',
                            type: 'site',
                            siteId: contextParams.siteId,
                            creativeId: contextParams.creativeId //TODO: Update once creative pivot is done
                        };
                    }
                    else {
                        if (vm.selectionData.field === vm.SCHEDULE_LEVEL.PLACEMENT.KEY) {
                            result.toolTip = $translate.instant('schedule.association.creativePivot.placementToolTip', {
                                name: vm.selectionData.creativeLabel
                            });
                            result.queryParameters = {
                                pivotType: 'creative',
                                type: 'placement',
                                siteId: contextParams.siteId,
                                placementId: contextParams.placementId,
                                creativeId: contextParams.creativeId //TODO: Update once creative pivot is done
                            };
                        }
                        else {
                            result.toolTip = $translate.instant('schedule.association.creativePivot.groupToolTip', {
                                creativeName: vm.selectionData.creativeLabel,
                                groupName: vm.selectionData.creativeGroupLabel
                            });
                            result.queryParameters = {
                                pivotType: 'creative',
                                type: 'group',
                                siteId: contextParams.siteId,
                                placementId: contextParams.placementId,
                                groupId: contextParams.groupId,
                                creativeId: contextParams.creativeId //TODO: Update once creative pivot is done
                            };
                        }

                        result.title = $translate.instant('schedule.association.creativePivot.placementOrGroupTitle', {
                            name: vm.selectionData.placementLabel
                        });
                    }

                    break;
            }

            return result;
        }

        function associationsCellsRenderer(rowData) {
            switch (rowData.field) {
                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    return '<span class="association">' +
                        '<i class="creative-group fa ' + ICONS.CREATIVE_GROUP + '"></i>' +
                        rowData.creativeGroupAssociations +
                        '</span>';

                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    return '<span class="association">' +
                        '<i class="placement fa ' + ICONS.PLACEMENT + '"></i>' +
                        rowData.placementAssociations +
                        '</span>' +
                        '<span class="association">' +
                        '<i class="creative fa ' + ICONS.CREATIVE + '"></i>' +
                        rowData.creativeAssociations +
                        '</span>';

                case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                    if (vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE) {
                        return '';
                    }
                    else {
                        return '<span class="association">' +
                            '<i class="placement fa ' + ICONS.PLACEMENT + '"></i>' +
                            rowData.placementAssociations +
                            '</span>' +
                            '<span class="association">' +
                            '<i class="creative-group fa ' + ICONS.CREATIVE_GROUP + '"></i>' +
                            rowData.creativeGroupAssociations +
                            '</span>';
                    }

                    break;

                case vm.SCHEDULE_LEVEL.CREATIVE.KEY:
                    return '<span class="association">' +
                        '<i class="placement fa ' + ICONS.PLACEMENT + '"></i>' +
                        rowData.placementAssociations +
                        '</span>' +
                        '<span class="association">' +
                        '<i class="creative-group fa ' + ICONS.CREATIVE_GROUP + '"></i>' +
                        rowData.creativeGroupAssociations +
                        '</span>';
            }
        }

        function clickThroughUrlCellsRenderer(cellValue) {
            var ret;

            if (cellValue && cellValue.length) {
                ret = '<ul class="clickthrough-urls">' +
                    lodash.map(cellValue, function (value) {
                        return '<li title="' + value + '">' + value + '</li>';
                    }).join('') +
                    '</ul>';
            }

            return ret;
        }

        function flightDatesCellsRenderer(cellText) {
            return '<span class="wrapped-text" title="' + cellText + '">' + cellText + '</span>';
        }

        function getCreativeInsertions() {
            vm.seedModel = {};
            vm.expanded = [];

            vm.promise = CampaignsService.getCreativeInsertions(
                vm.campaignId,
                vm.SCHEDULE_LEVEL.SITE.KEY,
                vm.SCHEDULE_LEVEL.SITE.KEY
            );
            return vm.promise.then(function (promise) {
                vm.model = lodash.map(promise, toSiteRows(vm.seedModel));
                if (vm.model.length === 0) {
                    resetStatusTraffic();
                }
            });
        }

        function getCreativeInsertionsOnExpand(row) {
            vm.promise = CampaignsService.getCreativeInsertions(
                vm.campaignId,
                vm.selectedPivot.KEY,
                row.nextLevelName,
                row.siteId,
                row.siteSectionId,
                row.placementId,
                row.creativeGroupId,
                row.creativeId
            );

            $scope.$broadcast('schedule.flyout.promiseUpdated', {
                promise: vm.promise
            });
            return vm.promise.then(function (response) {
                appendToModel(row, response);
            }).catch(function () {
                rowCollapsed(row);
            });
        }

        function appendToModel(row, elements) {
            vm.seedModel = getSeedModel(row);

            row.loadData = true;

            switch (getNextLevelName(row.field)) {
                case vm.SCHEDULE_LEVEL.SITE.KEY:
                    row.children = lodash.map(elements, toSiteRows(vm.seedModel));
                    break;
                case vm.SCHEDULE_LEVEL.SECTION.KEY:
                    row.children = lodash.map(elements, toSiteSectionRows(vm.seedModel));
                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    row.children = lodash.map(elements, toPlacementRows(vm.seedModel));
                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    row.children = lodash.map(elements, toCreativeGroupRows(vm.seedModel));
                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE.KEY:
                    vm.seedModel.creativesPromise = null;
                    row.children = lodash.map(elements, toCreativeRows(vm.seedModel));
                    break;
                case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                    vm.seedModel.creativesPromise = null;
                    row.children = lodash.map(elements, toScheduleRows(vm.seedModel));
                    break;
                default :
                    row.children = lodash.map(elements, toSiteSectionRows(vm.seedModel));
            }
        }

        function refreshModelGrid() {
            var outputModelBackup = [];

            angular.copy(vm.model, outputModelBackup);
            vm.model = [];
            angular.copy(outputModelBackup, vm.model);
        }

        function getExportAll() {
            vm.promise = CampaignsService.exportResource(vm.campaignId, CONSTANTS.SCHEDULE.FILE_TYPE.XLSX,
                CONSTANTS.SCHEDULE.FILE_TYPE.CREATIVE_INSERTION);
            return vm.promise.then(function (response) {
                var blob = new Blob([response.data],
                    {
                        type: response.type
                    });

                saveAs(blob, response.name);
                vm.downloadSuccessful = true;
            });
        }

        function openImport() {
            var template = 'app/cm/campaigns/schedule-tab/schedule-import/schedule-import.html',
                dlg = dialogs.create(template,
                    'ScheduleImportController as vm',
                    {
                        campaignId: vm.campaignId
                    },
                    {
                        size: 'md',
                        keyboard: true,
                        key: false,
                        backdrop: 'static'
                    });

            dlg.result.then(
                function (response) {
                    if (response.info && response.info.importSuccess === true) {
                        modelChanged(response.info.rowsUpdated > 0);
                        changePivot();
                    }
                    else {
                        openImportErrors(response);
                    }
                });

            return dlg;
        }

        function openImportErrors(listErrors) {
            var template = 'app/cm/campaigns/schedule-tab/schedule-import-errors/schedule-import-errors.html',
                dlg = dialogs.create(template,
                    'ScheduleImportErrorsController as vm',
                    {
                        campaignId: vm.campaignId,
                        response: listErrors.data,
                        uuid: listErrors.info.uuid
                    },
                    {
                        size: 'md',
                        keyboard: true,
                        key: false,
                        backdrop: 'static'
                    });

            dlg.result.then(
                function (response) {
                    if (response.status) {
                        modelChanged(response.rowsUpdated > 0);
                        changePivot();
                    }
                });

            return dlg;
        }

        function placementStatusCellsRenderer(rowData, cellText) {
            var cssClass;

            switch (rowData.placementStatus) {
                case STATUS_LIST.ACCEPTED.key:
                    cssClass = 'status-active';
                    break;

                case STATUS_LIST.NEW.key:
                    cssClass = 'status-planning';
                    break;

                case STATUS_LIST.REJECTED.key:
                    cssClass = 'status-inactive';
                    break;

                default:
                    cssClass = '';
                    break;
            }

            return '<span class="' + cssClass + '">' + cellText + '</span>';
        }

        function getStatusOptions(field) {
            var option = lodash.filter(vm.searchFields, function (value) {
                return value.field === field;
            });

            return option.length === 1 ? option[0].enabled : false;
        }

        function serverSideSearch(searchText) {
            var setting = {},
                serverSearch;

            if (!vm.modelWithoutSearch) {
                vm.modelWithoutSearch = angular.copy(vm.model);
            }

            vm.serverSearchText = searchText;
            $scope.$broadcast('schedule.flyout.close');
            setting.searchText = searchText;
            setting.soSite = getStatusOptions('siteLabel');
            setting.soSection = getStatusOptions('siteSectionLabel');
            setting.soPlacement = getStatusOptions('placementLabel');
            setting.soGroup = getStatusOptions('creativeGroupLabel');
            setting.soCreative = getStatusOptions('creativeLabel');
            setting.type = null;
            setting.siteId = null;
            setting.sectionId = null;
            setting.placementId = null;
            setting.groupId = null;
            setting.creativeId = null;

            vm.context = getContext(searchText);
            serverSearch = loadDataServerSearch(setting);

            return serverSearch.then(function (model) {
                vm.model = model;
                if (vm.model.length === 0) {
                    resetStatusTraffic();
                }

                return searchText;
            });
        }

        function loadDataServerSearch(setting) {
            vm.seedModel = {};

            vm.flyoutContext = Utils.isUndefinedOrNull(setting.flyoutContext) ?
                vm.CONTEXT.DEFAULT : setting.flyoutContext;
            vm.serverSearchTextFlyout = Utils.isUndefinedOrNull(setting.flyoutContext) ? null : setting.searchText;

            vm.promise = CampaignsService.searchCreativeInsertions(
                vm.campaignId,
                setting.searchText,
                vm.selectedPivot.KEY,
                {
                    site: setting.soSite,
                    section: setting.soSection,
                    placement: setting.soPlacement,
                    group: setting.soGroup,
                    creative: setting.soCreative
                },
                setting.type,
                {
                    site: setting.siteId,
                    section: setting.siteSectionId,
                    placement: setting.placementId,
                    group: setting.creativeGroupId,
                    creative: setting.creativeId
                }
            );
            return vm.promise.then(function (promise) {
                var flyout = [],
                    group;

                switch (vm.selectedPivot) {
                    case vm.SCHEDULE_LEVEL.SITE:
                        flyout = lodash.map(lodash.groupBy(promise, 'siteId'), toSiteRows(vm.seedModel));
                        break;
                    case vm.SCHEDULE_LEVEL.PLACEMENT:
                        flyout = lodash.map(lodash.groupBy(promise, 'placementId'), toPlacementRows(vm.seedModel));
                        break;
                    case vm.SCHEDULE_LEVEL.CREATIVE_GROUP:
                        flyout = lodash.map(lodash.groupBy(promise, 'creativeGroupId'),
                            toCreativeGroupRows(vm.seedModel));
                        break;
                    case vm.SCHEDULE_LEVEL.CREATIVE:
                        if (vm.flyoutContext === vm.CONTEXT.DEFAULT) {
                            vm.seedModel.creativesPromise = lodash.clone(promise, true);
                            flyout = lodash.map(lodash.groupBy(promise, 'creativeId'),
                                toScheduleCreativeRows(vm.seedModel, vm.SCHEDULE_LEVEL.CREATIVE));
                        }
                        else {
                            group = lodash.filter(lodash.groupBy(promise, 'creativeId'),
                                function (value, index) {
                                    return parseInt(index) === parseInt(setting.creativeId);
                                });

                            promise = group[0];
                            vm.seedModel.creativesPromise = lodash.clone(promise, true);
                            flyout = lodash.map(lodash.groupBy(promise, 'creativeId'),
                                toScheduleCreativeRows(vm.seedModel, vm.SCHEDULE_LEVEL.CREATIVE));
                        }

                        break;
                }

                return flyout;
            });
        }

        function serverSideClearSearch(cleanSearch) {
            vm.serverSearchText = null;
            vm.context = getContext(vm.serverSearchText);

            $scope.$broadcast('schedule.flyout.close');
            if (cleanSearch) {
                vm.modelWithoutSearch = null;
            }
            else {
                if (vm.modelWithoutSearch) {
                    angular.copy(vm.modelWithoutSearch, vm.model);
                }

                vm.modelWithoutSearch = null;
                refreshModelGrid();
            }
        }

        function getContext(searchText) {
            if (Utils.isUndefinedOrNull(searchText) || searchText === '') {
                return vm.CONTEXT.DEFAULT;
            }

            return vm.CONTEXT.SEARCH;
        }

        function rowCollapsed(row) {
            row.expanded = false;

            if (Utils.isUndefinedOrNull(vm.serverSearchText)) {
                if (vm.flyoutState !== vm.FLYOUT.HIDDEN) {
                    $scope.$broadcast('schedule.flyout.rowExpandedOrCollapsed', row);
                }
            }
        }

        function loadDataByRow(row, loadFromFlyout, model) {
            var rowUpdate = {};

            rowUpdate.isModelFlyout = !Utils.isUndefinedOrNull(model);
            model = Utils.isUndefinedOrNull(model) ? vm.model : model;

            if (loadFromFlyout) {
                row = findSelection(model, row);
                row.loadData = true;
                row.expanded = true;
            }

            getCreativeInsertionsOnExpand(row).then(function () {
                refreshModelGrid();
                if (vm.flyoutState !== vm.FLYOUT.HIDDEN) {
                    rowUpdate.selectionRow = findSelection(model, vm.selectionData);
                    $scope.$broadcast('schedule.flyout.modelUpdated', rowUpdate);
                    $scope.$emit('schedule.flyout.filter', {
                            field: row.field,
                            model: rowUpdate.selectionRow
                        }
                    );
                }
            });
        }

        function loadDataByRowSearch(row, loadFromFlyout, submodel) {
            vm.isServerSearchFlyout = true;
            loadDataRow(row, loadFromFlyout, submodel);
        }

        function loadDataRow(row, loadFromFlyout, model) {
            var rowUpdate = {};

            if (loadFromFlyout) {
                row = findSelection(model, row);
                row.loadData = true;
                row.expanded = true;
            }

            getCreativeInsertionsOnExpand(row).then(function () {
                refreshModelGrid();

                if (vm.flyoutState !== vm.FLYOUT.HIDDEN) {
                    rowUpdate.selectionRow = findSelection(model, vm.selectionData);
                    rowUpdate.isServerSearchFlyout = vm.isServerSearchFlyout;
                    $scope.$broadcast('schedule.flyout.modelUpdated', rowUpdate);
                }
            });
        }

        function rowExpanded(row) {
            var isLoadByFlyout;

            row.expanded = true;
            if (vm.context === vm.CONTEXT.DEFAULT) {
                isLoadByFlyout = false;
                rowExpandedOutSearch(row, isLoadByFlyout);
            }
            else {
                if (!row.loadData) {
                    isLoadByFlyout = false;
                    loadDataByRow(row, isLoadByFlyout);
                }
            }
        }

        function rowExpandedOutSearch(row, isLoadByFlyout) {
            if (row.loadData) {
                if (vm.flyoutState !== vm.FLYOUT.HIDDEN) {
                    $scope.$broadcast('schedule.flyout.rowExpandedOrCollapsed', row);
                }
            }
            else {
                loadDataByRow(row, isLoadByFlyout);
            }
        }

        function conditionFilter(item, row) {
            var responseCondition = false,
                isSite,
                isId,
                isPlacement,
                isCreativeGroup;

            switch (item.field) {
                case vm.SCHEDULE_LEVEL.SITE.KEY:
                    isSite = item.siteId === row.siteId;
                    isCreativeGroup = angular.isUndefined(item.creativeGroupId) ||
                        angular.isUndefined(row.creativeGroupId) ?
                    true : item.creativeGroupId === row.creativeGroupId;
                    isId = angular.isUndefined(row.id) || angular.isUndefined(item.id) ? true : item.id === row.id;
                    responseCondition = isSite && isCreativeGroup && isId;
                    break;
                case vm.SCHEDULE_LEVEL.SECTION.KEY:
                    responseCondition = item.siteSectionId === row.siteSectionId;
                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    responseCondition = item.placementId === row.placementId;
                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    isPlacement = angular.isUndefined(item.placementId) ? true : item.placementId === row.placementId;
                    responseCondition = item.creativeGroupId === row.creativeGroupId && isPlacement;
                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE.KEY:
                    isId = angular.isUndefined(row.id) ? true : item.id === row.id;
                    responseCondition = item.creativeId === row.creativeId && isId;
                    break;
                case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                    isId = angular.isUndefined(row.id) ? true : item.id === row.id;
                    responseCondition = item.creativeId === row.creativeId && isId;
                    break;
            }

            return responseCondition;
        }

        function findSelection(model, element) {
            var result = lodash.find(model, function (item) {
                return conditionFilter(item, element);
            });

            if (!angular.isUndefined(result)) {
                if (result.field !== element.field) {
                    return findSelection(result.children, element);
                }
            }

            return result;
        }

        function save() {
            var template = 'app/cm/campaigns/schedule-tab/trafficking-modal/trafficking-modal.html',
                dlg;

            dlg = dialogs.create(template, 'TraffickingModalController as vm',
                {
                    campaignId: vm.campaignId
                },
                {
                    size: 'xl',
                    keyboard: true,
                    key: false,
                    backdrop: 'static'
                });

            dlg.result.then(
                function () {
                    resetStatusTraffic();
                });

            return dlg;
        }

        function getRowRenderer(rowData, cellText) {
            var template = $templateCache.get('schedule-row-renderer.html'),
                scope = $rootScope.$new(true),
                iconClass;

            switch (rowData.field) {
                case vm.SCHEDULE_LEVEL.SITE.KEY:
                    iconClass = 'site fa ' + ICONS.SITE;
                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    iconClass = 'placement fa ' + ICONS.PLACEMENT;
                    break;

                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    iconClass = 'creative-group fa ' + ICONS.CREATIVE_GROUP;
                    break;

                case vm.SCHEDULE_LEVEL.CREATIVE.KEY:
                case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                    iconClass = 'creative fa ' + ICONS.CREATIVE;
                    break;

                default:
                    iconClass = '';
                    break;
            }

            scope.vm = {
                field: rowData.field,
                label: cellText,
                class: iconClass
            };

            return $interpolate(template)(scope);
        }

        function updateTrafficButtonLabel() {
            vm.trafficButtonLabel = vm.updatedModel ? '(*) ' + TRAFFIC_BUTTON_LABEL : TRAFFIC_BUTTON_LABEL;
        }

        function toSiteRows(seedModel) {
            return function (rows) {
                var firstRow = angular.isArray(rows) ? rows[0] : rows,
                    siteId = firstRow.siteId,
                    siteLabel = formatLabel(firstRow.siteName, siteId),
                    nextLevel = getNextLevelName(vm.SCHEDULE_LEVEL.SITE.KEY),
                    siteDetails = {};

                siteDetails.children = Utils.isUndefinedOrNull(nextLevel) ? null : lodash.map(children,
                    getChildren({
                        siteLabel: siteLabel,
                        siteSectionLabel: null,
                        placementLabel: null,
                        creativeGroupLabel: seedModel.creativeGroupLabel,
                        creativeLabel: seedModel.creativeLabel
                    }));

                siteDetails.loadData = false;
                siteDetails.expanded = false;
                siteDetails.uuid = null;
                siteDetails.siteId = siteId;
                siteDetails.field = vm.SCHEDULE_LEVEL.SITE.KEY;

                if (vm.context === vm.CONTEXT.SEARCH || vm.flyoutContext === vm.CONTEXT.SEARCH) {
                    siteDetails.firstRow = firstRow;
                    siteDetails.rows = rows;
                    siteDetails.siteId = siteId;
                    siteDetails.siteLabel = siteLabel;
                    siteDetails.nextLevel = nextLevel;
                    siteDetails.creativeId = seedModel.creativeId;
                    siteDetails.creativeLabel = seedModel.creativeLabel;
                    siteDetails.creativeGroupId = seedModel.creativeGroupId;
                    siteDetails.creativeGroupLabel = seedModel.creativeGroupLabel;
                    siteDetails.id = seedModel.id;

                    getSiteDetails(siteDetails);
                }

                siteDetails.uuid = FlyoutEditionStashService.buildKey(siteDetails);
                addItemToFilter(vm.filterOptions.SITE,
                    siteLabel,
                    siteId,
                    vm.SCHEDULE_LEVEL.SITE.KEY);

                return {
                    id: seedModel.id,
                    siteId: siteId,
                    creativeId: seedModel.creativeId,
                    creativeLabel: seedModel.creativeLabel,
                    creativeGroupId: seedModel.creativeGroupId,
                    loadData: siteDetails.loadData,
                    field: vm.SCHEDULE_LEVEL.SITE.KEY,
                    nextLevelName: nextLevel,
                    siteLabel: siteLabel,
                    siteName: firstRow.siteName,
                    siteDetailLabel: siteLabel,
                    creativeGroupLabel: seedModel.creativeGroupLabel,
                    expanded: siteDetails.expanded,
                    children: siteDetails.children,
                    treeRowId: siteDetails.uuid,
                    backUpChildren: []
                };
            };
        }

        function toSiteSectionRows(seedModel) {
            return function (rows) {
                var row = angular.isArray(rows) ? rows[0] : rows,
                    siteSectionId = row.siteSectionId,
                    siteSectionLabel = formatLabel(row.siteSectionName, siteSectionId),
                    nextLevel = getNextLevelName(vm.SCHEDULE_LEVEL.SECTION.KEY),
                    sectionDetails = {};

                sectionDetails.children = Utils.isUndefinedOrNull(nextLevel) ? null : lodash.map(children,
                    getChildren({
                        siteLabel: seedModel.siteLabel,
                        siteSectionLabel: siteSectionLabel,
                        placementLabel: null,
                        creativeGroupLabel: null,
                        creativeLabel: null
                    }));
                sectionDetails.loadData = false;
                sectionDetails.expanded = false;
                sectionDetails.uuid = null;
                sectionDetails.field = vm.SCHEDULE_LEVEL.SECTION.KEY;
                sectionDetails.siteSectionId = siteSectionId;

                if (vm.context === vm.CONTEXT.SEARCH || vm.flyoutContext === vm.CONTEXT.SEARCH) {
                    sectionDetails.rows = rows;
                    sectionDetails.siteId = seedModel.siteId;
                    sectionDetails.siteLabel = seedModel.siteLabel;
                    sectionDetails.siteSectionId = siteSectionId;
                    sectionDetails.siteSectionLabel = siteSectionLabel;
                    sectionDetails.siteSectionName = row.siteSectionName;
                    sectionDetails.creativeId = row.creativeId;
                    sectionDetails.creativeLabel = row.creativeAlias;
                    sectionDetails.creativeGroupId = row.creativeGroupId;
                    sectionDetails.creativeGroupLabel = row.creativeGroupName;
                    sectionDetails.id = row.Id;

                    getSectionDetails(sectionDetails);
                }

                sectionDetails.uuid = FlyoutEditionStashService.buildKey(sectionDetails);

                return {
                    siteId: seedModel.siteId,
                    loadData: sectionDetails.loadData,
                    field: vm.SCHEDULE_LEVEL.SECTION.KEY,
                    nextLevelName: nextLevel,
                    siteSectionId: siteSectionId,
                    siteLabel: seedModel.siteLabel,
                    siteName: seedModel.siteName,
                    siteSectionLabel: siteSectionLabel,
                    siteSectionName: row.siteSectionName,
                    siteDetailLabel: siteSectionLabel,
                    expanded: sectionDetails.expanded,
                    children: sectionDetails.children,
                    treeRowId: sectionDetails.uuid,
                    backUpChildren: []
                };
            };
        }

        function toPlacementRows(seedModel) {
            return function (rows) {
                var row = angular.isArray(rows) ? rows[0] : rows,
                    placementId = row.placementId,
                    placementLabel = formatLabel(row.placementName, placementId),
                    placementStatus = row.placementStatus,
                    nextLevel = getNextLevelName(vm.SCHEDULE_LEVEL.PLACEMENT.KEY),
                    placementDetails = {};

                placementDetails.children = Utils.isUndefinedOrNull(nextLevel) ? null :
                    lodash.map(children, getChildren({
                        siteLabel: seedModel.siteLabel,
                        siteSectionLabel: seedModel.siteSectionLabel,
                        placementLabel: placementLabel,
                        creativeGroupLabel: seedModel.creativeGroupLabel,
                        creativeLabel: seedModel.creativeLabel
                    }));
                placementDetails.loadData = false;
                placementDetails.expanded = false;
                placementDetails.row = null;
                placementDetails.uuid = null;

                placementDetails.field = vm.SCHEDULE_LEVEL.PLACEMENT.KEY;
                placementDetails.placementId = placementId;
                placementDetails.siteId = seedModel.siteId;
                placementDetails.siteSectionId = seedModel.siteSectionId;

                if (vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE_GROUP) {
                    validateDataToCreative(placementDetails, rows);
                    row = Utils.isUndefinedOrNull(placementDetails.row) ? row : placementDetails.row;
                }

                if (vm.context === vm.CONTEXT.SEARCH || vm.flyoutContext === vm.CONTEXT.SEARCH) {
                    placementDetails.rows = rows;
                    placementDetails.siteId = seedModel.siteId;
                    placementDetails.siteLabel = seedModel.siteLabel;
                    placementDetails.siteName = seedModel.siteName;
                    placementDetails.siteSectionId = seedModel.siteSectionId;
                    placementDetails.siteSectionLabel = seedModel.siteSectionLabel;
                    placementDetails.siteSectionName = seedModel.siteSectionName;
                    placementDetails.placementId = placementId;
                    placementDetails.placementLabel = placementLabel;
                    placementDetails.creativeId = row.creativeId;
                    placementDetails.creativeLabel = row.creativeAlias;
                    placementDetails.creativeGroupId = row.creativeGroupId;
                    placementDetails.creativeGroupLabel = row.creativeGroupName;
                    getPlacementDetails(placementDetails);
                }

                placementDetails.uuid = FlyoutEditionStashService.buildKey(placementDetails);
                addItemToFilter(vm.filterOptions.PLACEMENT,
                    placementLabel,
                    placementId,
                    vm.SCHEDULE_LEVEL.PLACEMENT.KEY);

                return {
                    id: seedModel.id,
                    siteId: seedModel.siteId,
                    loadData: placementDetails.loadData,
                    field: vm.SCHEDULE_LEVEL.PLACEMENT.KEY,
                    nextLevelName: nextLevel,
                    siteSectionId: seedModel.siteSectionId,
                    creativeId: seedModel.creativeId,
                    creativeGroupId: seedModel.creativeGroupId,
                    creativeGroupLabel: seedModel.creativeGroupLabel,
                    creativeLabel: seedModel.creativeLabel,
                    placementId: placementId,
                    siteLabel: seedModel.siteLabel,
                    siteName: seedModel.siteName,
                    siteSectionLabel: seedModel.siteSectionLabel,
                    siteSectionName: seedModel.siteSectionName,
                    placementLabel: placementLabel,
                    siteDetailLabel: placementLabel,
                    placementStatus: placementStatus,
                    placementStatusLabel: CampaignsUtilService.getStatusName(placementStatus),
                    creativeGroupAssociations: row.placementAssociationsWithCreativeGroups,
                    flightDates: undefined,
                    flightDateStart: undefined,
                    flightDateEnd: undefined,
                    sizeName: row.sizeName,
                    expanded: placementDetails.expanded,
                    children: placementDetails.children,
                    treeRowId: placementDetails.uuid,
                    backUpChildren: [],
                    placementTagAssociationIds: {
                        siteId: row.siteId,
                        sectionId: row.siteSectionId
                    }
                };
            };
        }

        function toCreativeGroupRows(seedModel) {
            return function (rows) {
                var row = angular.isArray(rows) ? rows[0] : rows,
                    creativeGroupId = row.creativeGroupId,
                    creativeGroupLabel = formatLabel(row.creativeGroupName, creativeGroupId),
                    nextLevel = getNextLevelName(vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY),
                    creativeGroupDetails = {};

                creativeGroupDetails.children = Utils.isUndefinedOrNull(nextLevel) ? null :
                    lodash.map(children, getChildren({
                        siteLabel: seedModel.siteLabel,
                        siteSectionLabel: seedModel.siteSectionLabel,
                        placementLabel: seedModel.placementLabel,
                        creativeGroupLabel: creativeGroupLabel,
                        creativeLabel: seedModel.creativeLabel
                    }));
                creativeGroupDetails.loadData = false;
                creativeGroupDetails.expanded = false;
                creativeGroupDetails.row = null;
                creativeGroupDetails.uuid = null;

                creativeGroupDetails.seedModel = seedModel;

                validateDataToCreative(creativeGroupDetails, rows);
                row = Utils.isUndefinedOrNull(creativeGroupDetails.row) ? row : creativeGroupDetails.row;

                creativeGroupDetails.field = vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY;
                creativeGroupDetails.creativeGroupId = creativeGroupId;
                creativeGroupDetails.siteId = seedModel.siteId;
                creativeGroupDetails.siteSectionId = seedModel.siteSectionId;
                creativeGroupDetails.placementId = seedModel.placementId;

                if (vm.context === vm.CONTEXT.SEARCH || vm.flyoutContext === vm.CONTEXT.SEARCH) {
                    creativeGroupDetails.rows = rows;
                    creativeGroupDetails.creativeId = row.creativeId;
                    creativeGroupDetails.creativeLabel = row.creativeAlias;
                    creativeGroupDetails.creativeGroupId = creativeGroupId;
                    creativeGroupDetails.creativeGroupLabel = creativeGroupLabel;
                    creativeGroupDetails.id = row.id;
                    creativeGroupDetails.siteId = seedModel.siteId;
                    creativeGroupDetails.siteLabel = seedModel.siteLabel;
                    creativeGroupDetails.siteName = seedModel.siteName;
                    creativeGroupDetails.siteSectionId = seedModel.siteSectionId;
                    creativeGroupDetails.siteSectionLabel = seedModel.siteSectionLabel;
                    creativeGroupDetails.siteSectionName = seedModel.siteSectionName;
                    creativeGroupDetails.placementId = seedModel.placementId;
                    creativeGroupDetails.placementLabel = seedModel.placementLabel;
                    creativeGroupDetails.creativeGroupId = creativeGroupId;
                    creativeGroupDetails.creativeGroupLabel = creativeGroupLabel;
                    getCreativeGroupDetails(creativeGroupDetails);
                }

                creativeGroupDetails.uuid = FlyoutEditionStashService.buildKey(creativeGroupDetails);
                addItemToFilter(vm.filterOptions.GROUP,
                    creativeGroupLabel,
                    creativeGroupId,
                    vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY);

                return {
                    siteId: seedModel.siteId,
                    loadData: creativeGroupDetails.loadData,
                    field: vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY,
                    nextLevelName: nextLevel,
                    creativeId: vm.context === vm.CONTEXT.SEARCH || vm.flyoutContext === vm.CONTEXT.SEARCH ?
                        creativeGroupDetails.creativeId : seedModel.creativeId,
                    siteSectionId: seedModel.siteSectionId,
                    placementId: seedModel.placementId,
                    creativeGroupId: creativeGroupId,
                    siteLabel: seedModel.siteLabel,
                    creativeLabel: seedModel.creativeLabel,
                    siteName: seedModel.siteName,
                    siteSectionLabel: seedModel.siteSectionLabel,
                    siteSectionName: seedModel.siteSectionName,
                    placementLabel: seedModel.placementLabel,
                    creativeGroupLabel: creativeGroupLabel,
                    siteDetailLabel: creativeGroupLabel,
                    placementAssociations: row.creativeGroupAssociationsWithPlacements,
                    creativeAssociations: row.creativeGroupAssociationsWithCreatives,
                    weight: row.creativeGroupWeight,
                    frequencyCap: row.creativeGroupFrequencyCap,
                    frequencyCapWindow: row.creativeGroupFrequencyCapWindow,
                    creativeGroupPriority: row.creativeGroupPriority,
                    creativeGroupDoCookieTarget: row.creativeGroupDoCookieTargeting,
                    creativeGroupDoGeoTarget: row.creativeGroupDoGeoTargeting,
                    creativeGroupDoDayPartTarget: row.creativeGroupDoDaypartTarget,
                    flightDates: undefined,
                    flightDateStart: undefined,
                    flightDateEnd: undefined,
                    expanded: creativeGroupDetails.expanded,
                    editSupport: {
                        weight: true
                    },
                    children: creativeGroupDetails.children,
                    treeRowId: creativeGroupDetails.uuid,
                    backUpChildren: []
                };
            };
        }

        function toScheduleRows(seedModel) {
            return toScheduleCreativeRows(seedModel, vm.SCHEDULE_LEVEL.SCHEDULE);
        }

        function toCreativeRows(seedModel) {
            return toScheduleCreativeRows(seedModel, vm.SCHEDULE_LEVEL.CREATIVE);
        }

        function toScheduleCreativeRows(seedModel, level) {
            return function (rows) {
                var row = angular.isArray(rows) ? rows[0] : rows,
                    creativeId = row.creativeId,
                    creativeLabel = formatLabel(row.creativeAlias, creativeId),
                    id = row.id,
                    nextLevel = getNextLevelName(level.KEY),
                    creativeDetails = {};

                creativeDetails.children = Utils.isUndefinedOrNull(nextLevel) ? null : lodash.map(children,
                    getChildren({
                        siteLabel: seedModel.siteLabel,
                        siteSectionLabel: seedModel.siteSectionLabel,
                        placementLabel: seedModel.placementLabel,
                        creativeGroupLabel: seedModel.creativeGroupLabel,
                        creativeLabel: creativeLabel
                    }));
                creativeDetails.loadData = false;
                creativeDetails.expanded = false;
                creativeDetails.uuid = null;

                creativeDetails.field = level.KEY;
                creativeDetails.creativeId = creativeId;
                creativeDetails.creativeGroupId = seedModel.creativeGroupId;
                creativeDetails.siteId = seedModel.siteId;
                creativeDetails.siteSectionId = seedModel.siteSectionId;
                creativeDetails.placementId = seedModel.placementId;

                if (vm.context === vm.CONTEXT.SEARCH || vm.flyoutContext === vm.CONTEXT.SEARCH) {
                    creativeDetails.rows = rows;
                    creativeDetails.creativeId = creativeId;
                    creativeDetails.creativeLabel = row.creativeAlias;
                    creativeDetails.creativeGroupId = seedModel.creativeGroupId;
                    creativeDetails.creativeGroupLabel = seedModel.creativeGroupLabel;
                    creativeDetails.id = row.id;
                    creativeDetails.promise = seedModel.creativesPromise;

                    getCreativeDetails(creativeDetails);
                }

                creativeDetails.uuid = FlyoutEditionStashService.buildKey(creativeDetails);

                addItemToFilter(vm.filterOptions.CREATIVE, creativeLabel, creativeId, vm.SCHEDULE_LEVEL.SCHEDULE.KEY);

                return {
                    id: id,
                    field: level.KEY,
                    loadData: level.KEY === vm.SCHEDULE_LEVEL.CREATIVE.KEY ? creativeDetails.loadData : undefined,
                    nextLevelName: nextLevel,
                    siteId: seedModel.siteId,
                    siteSectionId: seedModel.siteSectionId,
                    placementId: seedModel.placementId,
                    creativeGroupId: seedModel.creativeGroupId,
                    creativeId: creativeId,
                    siteLabel: seedModel.siteLabel,
                    siteName: seedModel.siteName,
                    siteSectionLabel: seedModel.siteSectionLabel,
                    siteSectionName: seedModel.siteSectionName,
                    placementLabel: seedModel.placementLabel,
                    creativeGroupLabel: seedModel.creativeGroupLabel,
                    creativeLabel: creativeLabel,
                    creativeName: row.creativeAlias,
                    creativeType: row.creativeType,
                    siteDetailLabel: creativeLabel,
                    sizeName: row.sizeName,
                    filename: row.filename,
                    placementAssociations: row.creativeAssociationsWithPlacements,
                    creativeGroupAssociations: row.creativeAssociationsWithCreativeGroups,
                    weight: level.KEY === vm.SCHEDULE_LEVEL.CREATIVE.KEY ? undefined : row.weight,
                    expanded: creativeDetails.expanded,
                    flightDates: level.KEY === vm.SCHEDULE_LEVEL.CREATIVE.KEY ? undefined :
                        formatFlightDates(row.startDate, row.endDate),
                    flightDateStart: level.KEY === vm.SCHEDULE_LEVEL.CREATIVE.KEY ? undefined :
                        DateTimeService.format(row.startDate, DateTimeService.FORMAT.DATE_TIME_US),
                    flightDateEnd: level.KEY === vm.SCHEDULE_LEVEL.CREATIVE.KEY ? undefined :
                        DateTimeService.format(row.endDate, DateTimeService.FORMAT.DATE_TIME_US),
                    clickThroughUrl: level.KEY === vm.SCHEDULE_LEVEL.CREATIVE.KEY ? undefined :
                        CreativeUtilService.hasClickThrough(row.creativeType) ? lodash.chain([])
                        .concat(row.primaryClickthrough)
                        .concat(lodash.chain(row.additionalClickthroughs).sortBy('sequence').pluck('url').value())
                        .compact()
                        .value() : undefined,
                    editSupport: {
                        clickThroughUrl: CreativeUtilService.hasClickThrough(row.creativeType) ? true : undefined,
                        flightDateStart: true,
                        flightDateEnd: true,
                        weight: true
                    },
                    children: creativeDetails.children,
                    treeRowId: creativeDetails.uuid,
                    backUpChildren: []
                };
            };
        }

        function getChildren(fields) {
            return function () {
                return {
                    siteLabel: fields.siteLabel,
                    siteSectionLabel: fields.siteSectionLabel,
                    placementLabel: fields.placementLabel,
                    creativeGroupLabel: fields.creativeGroupLabel,
                    creativeLabel: fields.creativeLabel
                };
            };
        }

        function formatLabel(name, id) {
            var _name = name || '',
                _id = '(' + (id || '') + ')';

            return [_name, _id].join(' ').trim();
        }

        function formatFlightDates(startDate, endDate) {
            return DateTimeService.format(startDate, DateTimeService.FORMAT.DATE_TIME_US) + ' \u2014 ' +
                DateTimeService.format(endDate, DateTimeService.FORMAT.DATE_TIME_US);
        }

        function traffickingWarning(data) {
            updateStatusWarningTraffic(true);
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                description: $translate.instant('package.traffickingChangesWarning'),
                buttons: {
                    yes: $translate.instant('global.trafficNow'),
                    no: $translate.instant('global.trafficLater')
                },
                dontShowAgainID: CONSTANTS.DONT_SHOW_AGAIN_IDS.TRAFFICKING.PENDING_CHANGES
            }).result.then(
                function (params) {
                    if (angular.isArray(params)) {
                        //if dont show againg was checked
                        navigateDestination(data);
                    }
                    else {
                        //functionality to OK => Traffic Now Button
                        updateStatusWarningTraffic(false);
                        save();
                    }
                },

                function (params) {
                    //functionality to cancel=>Traffic Later Button
                    if (params === 'no') {
                        navigateDestination(data);
                    }
                    else {
                        updateStatusWarningTraffic(false);
                    }
                });
        }

        function updateStatusWarningTraffic(status) {
            ScheduleUtilService.setStatusWarning(status);
        }

        function modelChanged(status) {
            if (status) {
                ScheduleUtilService.setStatusChange(status);
                vm.updatedModel = status;
                updateTrafficButtonLabel();
            }
        }

        function resetStatusTraffic() {
            ScheduleUtilService.resetStatusTraffic();
            vm.updatedModel = false;
            updateTrafficButtonLabel();
        }

        function navigateDestination(data) {
            ScheduleUtilService.setTrafficLater(true);
            if (data.action) {
                data.action();
            }
            else {
                ScheduleUtilService.goRouteDestiny(data);
            }
        }

        function changePivot() {
            vm.seedModel = {};
            vm.serverSearchText = null;
            vm.context = vm.CONTEXT.DEFAULT;
            vm.flyoutContext = vm.CONTEXT.DEFAULT;

            $scope.$broadcast('schedule.flyout.close');
            $scope.$broadcast('te-tree-grid:reloadConfiguration', {
                uuid: vm.treeUUID,
                resetSearchFields: true,
                resetSearchFieldText: true
            });

            vm.promise = CampaignsService.getCreativeInsertions(
                vm.campaignId,
                vm.selectedPivot.KEY,
                vm.selectedPivot.KEY);
            return vm.promise.then(function (promise) {
                serverSideClearSearch(true);
                switch (vm.selectedPivot) {
                    case vm.SCHEDULE_LEVEL.PLACEMENT:
                        vm.model = lodash.map(promise, toPlacementRows(vm.seedModel));
                        break;
                    case vm.SCHEDULE_LEVEL.CREATIVE_GROUP:
                        vm.model = lodash.map(promise, toCreativeGroupRows(vm.seedModel));
                        break;
                    case vm.SCHEDULE_LEVEL.CREATIVE:
                        vm.model = lodash.map(promise, toCreativeRows(vm.seedModel));
                        break;
                    default:
                        vm.model = lodash.map(promise, toSiteRows(vm.seedModel));
                        break;
                }

                if (vm.model.length === 0) {
                    resetStatusTraffic();
                }
            });
        }

        function getDetailColumnTitle() {
            var title;

            switch (vm.selectedPivot) {
                case vm.SCHEDULE_LEVEL.SITE:
                    title = $translate.instant('schedule.siteDetail');
                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT:
                    title = $translate.instant('global.placement');
                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP:
                    title = $translate.instant('global.group');
                    break;
                case vm.SCHEDULE_LEVEL.SCHEDULE:
                    title = $translate.instant('creative.creative');
                    break;
                default:
                    title = $translate.instant('schedule.siteDetail');
                    break;
            }

            return title;
        }

        function getNextLevelName(level) {
            var nextLevel = null,
                currentLevel;

            currentLevel = lodash.find(vm.selectedPivot.CHILDREN_SEQUENCE, function (item) {
                return item.LEVEL === level;
            });

            if (angular.isUndefined(currentLevel)) {
                nextLevel = lodash.find(vm.selectedPivot.CHILDREN_SEQUENCE, function (item) {
                    return item.SEQUENCE === 0;
                });
            }
            else {
                nextLevel = lodash.find(vm.selectedPivot.CHILDREN_SEQUENCE, function (item) {
                    return item.SEQUENCE === currentLevel.SEQUENCE + 1;
                });
            }

            if (!Utils.isUndefinedOrNull(nextLevel)) {
                return nextLevel.LEVEL;
            }

            return nextLevel;
        }

        function getFilterable(field) {
            if (vm.selectedPivot.KEY === field) {
                return true;
            }

            for (var i = 0; i < vm.selectedPivot.CHILDREN_SEQUENCE.length; i++) {
                if (vm.selectedPivot.CHILDREN_SEQUENCE[i].LEVEL === field) {
                    return true;
                }
            }

            return false;
        }

        function getPosition(field) {
            var result = lodash.find(vm.selectedPivot.CHILDREN_SEQUENCE, function (item) {
                return item.LEVEL === field;
            });

            if (Utils.isUndefinedOrNull(result)) {
                return 1;
            }
            else {
                return result.SEQUENCE + 2;
            }
        }

        function groupByType(data, index) {
            return lodash.filter(lodash.groupBy(data, index), function (value, key) {
                return key !== 'undefined';
            });
        }

        //Pivots functionality Search Server
        function getSiteDetails(siteDetails) {
            var groups,
                rows,
                seedModel = getSeedModel(
                    {
                        siteId: siteDetails.siteId,
                        siteLabel: siteDetails.siteLabel,
                        siteName: siteDetails.firstRow.siteName,
                        siteSectionId: siteDetails.firstRow.siteSectionId,
                        siteSectionLabel: siteDetails.firstRow.siteSectionLabel,
                        siteSectionName: siteDetails.firstRow.siteSectionName,
                        creativeId: siteDetails.creativeId,
                        creativeGroupId: siteDetails.creativeGroupId,
                        creativeGroupLabel: siteDetails.creativeGroupLabel,
                        creativeLabel: siteDetails.creativeLabel,
                        id: siteDetails.id
                    });

            if (vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE_GROUP) {
                groups = groupByType(siteDetails.rows, 'placementId');
                if (groups.length > 0) {
                    siteDetails.children = lodash.map(
                        groups,
                        toPlacementRows(seedModel));
                    siteDetails.loadData = true;
                    siteDetails.expanded = true;
                }
            }
            else if (vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE) {
                rows = lodash.filter(siteDetails.rows, function (value) {
                    return !Utils.isUndefinedOrNull(value.placementName);
                });

                groups = groupByType(rows, 'placementId');
                if (groups.length > 0) {
                    siteDetails.children = lodash.map(
                        groups,
                        toPlacementRows(seedModel));
                    siteDetails.loadData = true;
                    siteDetails.expanded = true;
                }
            }
            else {
                groups = groupByType(siteDetails.rows, 'siteSectionId');

                if (groups.length > 0) {
                    siteDetails.children = lodash.map(
                        groups,
                        toSiteSectionRows(getSeedModel(
                            {
                                siteId: siteDetails.siteId,
                                siteLabel: siteDetails.siteLabel,
                                siteName: siteDetails.firstRow.siteName
                            }
                            )));

                    siteDetails.loadData = true;
                    siteDetails.expanded = true;
                }
            }
        }

        function getSectionDetails(sectionDetails) {
            var groups = groupByType(sectionDetails.rows, 'placementId');

            if (groups.length > 0) {
                sectionDetails.children = lodash.map(
                    groups,
                    toPlacementRows(getSeedModel(
                        {
                            siteId: sectionDetails.siteId,
                            siteLabel: sectionDetails.siteLabel,
                            siteName: sectionDetails.siteName,
                            siteSectionId: sectionDetails.siteSectionId,
                            siteSectionLabel: sectionDetails.siteSectionLabel,
                            siteSectionName: sectionDetails.siteSectionName,
                            creativeId: sectionDetails.creativeId,
                            creativeGroupId: sectionDetails.creativeGroupId,
                            creativeGroupLabel: sectionDetails.creativeGroupLabel,
                            creativeLabel: sectionDetails.creativeLabel,
                            id: sectionDetails.id
                        }
                        )));
                sectionDetails.loadData = true;
                sectionDetails.expanded = true;
            }
        }

        function getPlacementDetails(placementDetails) {
            var rows,
                groups,
                seedModel = getSeedModel(
                    {
                        siteId: placementDetails.siteId,
                        siteLabel: placementDetails.siteLabel,
                        siteName: placementDetails.siteName,
                        siteSectionId: placementDetails.siteSectionId,
                        siteSectionLabel: placementDetails.siteSectionLabel,
                        siteSectionName: placementDetails.siteSectionName,
                        placementId: placementDetails.placementId,
                        placementLabel: placementDetails.placementLabel,
                        creativeLabel: placementDetails.creativeLabel
                    });

            if (vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE_GROUP) {
                if (!Utils.isUndefinedOrNull(placementDetails.creativeId)) {
                    seedModel.creativeGroupId = placementDetails.creativeGroupId;
                    seedModel.creativeGroupLabel = placementDetails.creativeGroupLabel;
                    seedModel.creativeLabel = undefined;

                    groups = lodash.filter(placementDetails.rows, function (value) {
                        return !Utils.isUndefinedOrNull(value.creativeId);
                    });

                    placementDetails.children = lodash.map(
                        groups,
                        toScheduleRows(seedModel));

                    placementDetails.loadData = true;
                    placementDetails.expanded = true;
                }
            }
            else if (vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE) {
                rows = lodash.filter(placementDetails.rows, function (value) {
                    return !Utils.isUndefinedOrNull(value.creativeGroupName);
                });

                groups = groupByType(rows, 'creativeGroupId');
                if (groups.length > 0) {
                    placementDetails.children = lodash.map(
                        groups,
                        toCreativeGroupRows(seedModel));
                    placementDetails.loadData = true;
                    placementDetails.expanded = true;
                }
            }
            else {
                groups = groupByType(placementDetails.rows, 'creativeGroupId');

                if (groups.length > 0) {
                    placementDetails.children = lodash.map(
                        groups,
                        toCreativeGroupRows(seedModel));
                    placementDetails.loadData = true;
                    placementDetails.expanded = true;
                }
            }
        }

        function getCreativeGroupDetails(creativeGroupDetails) {
            var creativeLabel,
                groups,
                searchText;

            if (vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE_GROUP) {
                groups = groupByType(creativeGroupDetails.rows, 'siteId');
                if (groups.length > 0) {
                    creativeGroupDetails.children = lodash.map(
                        groups,
                        toSiteRows(getSeedModel(
                            {
                                creativeId: creativeGroupDetails.creativeId,
                                creativeLabel: creativeGroupDetails.creativeLabel,
                                creativeGroupId: creativeGroupDetails.creativeGroupId,
                                creativeGroupLabel: creativeGroupDetails.creativeGroupLabel,
                                id: creativeGroupDetails.id
                            }
                            )));
                    creativeGroupDetails.loadData = true;
                    creativeGroupDetails.expanded = true;
                }
            }
            else if (!Utils.isUndefinedOrNull(creativeGroupDetails.creativeId) &&
                vm.selectedPivot !== vm.SCHEDULE_LEVEL.CREATIVE) {
                groups = lodash.filter(creativeGroupDetails.rows, function (value) {
                    return !Utils.isUndefinedOrNull(value.creativeId);
                });

                creativeGroupDetails.children = lodash.map(
                    groups,
                    toScheduleRows(getSeedModel(
                        {
                            siteId: creativeGroupDetails.siteId,
                            siteLabel: creativeGroupDetails.siteLabel,
                            siteName: creativeGroupDetails.siteName,
                            siteSectionId: creativeGroupDetails.siteSectionId,
                            siteSectionLabel: creativeGroupDetails.siteSectionLabel,
                            siteSectionName: creativeGroupDetails.siteSectionName,
                            placementId: creativeGroupDetails.placementId,
                            placementLabel: creativeGroupDetails.placementLabel,
                            creativeGroupId: creativeGroupDetails.creativeGroupId,
                            creativeGroupLabel: creativeGroupDetails.creativeGroupLabel
                        }
                        )));
                creativeGroupDetails.loadData = true;
                creativeGroupDetails.expanded = true;
            }
            else if (!Utils.isUndefinedOrNull(creativeGroupDetails.creativeId) &&
                vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE) {
                searchText = vm.flyoutContext === vm.CONTEXT.DEFAULT ?
                    vm.serverSearchText.toLowerCase() : vm.serverSearchTextFlyout.toLowerCase();

                groups = lodash.filter(creativeGroupDetails.rows, function (value) {
                    creativeLabel = formatLabel(value.creativeAlias, value.creativeId);
                    return !Utils.isUndefinedOrNull(value.creativeId) &&
                        !Utils.isUndefinedOrNull(value.creativeAlias) &&
                        creativeLabel.toLowerCase().indexOf(searchText) >= 0;
                });

                if (groups.length > 0) {
                    groups = lodash.filter(groups, function (value) {
                        return !Utils.isUndefinedOrNull(value.startDate) &&
                            !Utils.isUndefinedOrNull(value.endDate);
                    });

                    creativeGroupDetails.children = lodash.map(
                        groups,
                        toScheduleRows(getSeedModel(
                            {
                                siteId: creativeGroupDetails.siteId,
                                siteLabel: creativeGroupDetails.siteLabel,
                                siteName: creativeGroupDetails.siteName,
                                siteSectionId: creativeGroupDetails.siteSectionId,
                                siteSectionLabel: creativeGroupDetails.siteSectionLabel,
                                siteSectionName: creativeGroupDetails.siteSectionName,
                                placementId: creativeGroupDetails.placementId,
                                placementLabel: creativeGroupDetails.placementLabel,
                                creativeGroupId: creativeGroupDetails.creativeGroupId,
                                creativeGroupLabel: creativeGroupDetails.creativeGroupLabel
                            }
                        )));
                    creativeGroupDetails.loadData = true;
                    creativeGroupDetails.expanded = true;
                }
            }
            else if (vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE) {
                creativeGroupDetails.creativeId = creativeGroupDetails.seedModel.creativeId;
            }
        }

        function getCreativeDetails(creativeDetails) {
            if (vm.selectedPivot === vm.SCHEDULE_LEVEL.CREATIVE) {
                var groups = lodash.filter(creativeDetails.promise, function (value) {
                    return value.creativeId === creativeDetails.creativeId && !Utils.isUndefinedOrNull(value.siteName);
                });

                groups = groupByType(groups, 'siteId');

                if (groups.length > 0) {
                    creativeDetails.children = lodash.map(
                        groups,
                        toSiteRows(getSeedModel(
                            {
                                creativeId: creativeDetails.creativeId,
                                creativeLabel: creativeDetails.creativeLabel,
                                creativeGroupId: creativeDetails.creativeGroupId,
                                creativeGroupLabel: creativeDetails.creativeGroupLabel,
                                id: creativeDetails.id
                            }
                            )));
                    creativeDetails.loadData = true;
                    creativeDetails.expanded = true;
                }
            }
        }

        function getSeedModel(row) {
            return {
                id: row.id,
                siteId: row.siteId,
                siteLabel: row.siteLabel,
                siteName: row.siteName,
                siteSectionId: row.siteSectionId,
                siteSectionLabel: row.siteSectionLabel,
                siteSectionName: row.siteSectionName,
                placementId: row.placementId,
                placementLabel: row.placementLabel,
                creativeGroupId: row.creativeGroupId,
                creativeGroupLabel: row.creativeGroupLabel,
                creativeId: row.creativeId,
                creativeLabel: row.creativeLabel
            };
        }

        function validateDataToCreative(creativeGroupDetails, rows) {
            if (angular.isArray(rows)) {
                creativeGroupDetails.row = lodash.find(rows, function (value) {
                    return !Utils.isUndefinedOrNull(value.creativeId);
                });
            }
        }

        function addItemToFilter(listItem, label, id, typeField) {
            var existValue,
                findIndex;

            if (vm.flyoutState === vm.FLYOUT.HALF_VIEW) {
                existValue = lodash.find(listItem.value, function (item) {
                    return item.id === id;
                });

                if (!existValue) {
                    listItem.value.push({
                        label: label,
                        id: id,
                        enabled: true
                    });

                    listItem.value = lodash.sortBy(listItem.value, 'label');

                    findIndex = lodash.findIndex(vm.filterValues, function (item) {
                        return item.fieldName === typeField;
                    });

                    vm.filterValues[findIndex].values.push(id);
                }
            }
        }
    }
})();
