(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('PlacementsController', PlacementsController);

    PlacementsController.$inject = [
        '$log',
        '$q',
        '$scope',
        '$translate',
        'CONSTANTS',
        'AgencyService',
        'DialogFactory',
        'TagInjectionAssociation',
        'TagInjectionTree',
        'Utils',
        'lodash',
        'uuid'
    ];

    function PlacementsController($log,
                                  $q,
                                  $scope,
                                  $translate,
                                  CONSTANTS,
                                  AgencyService,
                                  DialogFactory,
                                  TagInjectionAssociation,
                                  TagInjectionTree,
                                  Utils,
                                  lodash,
                                  uuid) {
        var vm = this,
            parentTagInjection = $scope.vmTagInjectionTab,
            parentStandardTab = $scope.vmTIStandard,
            rowSelectedCampaignId,
            searchContext = false;

        vm.pageSize = CONSTANTS.TE_TREE_GRID.PAGE_SIZE.SMALL;
        vm.treeUUID = uuid.v4();
        vm.onDrop = onDrop;
        vm.onExpandCollapse = onExpandCollapse;
        vm.searchTree = searchTree;
        vm.searchClear = searchClear;
        vm.onSelect = onSelect;
        vm.header = $translate.instant('global.placements');
        vm.backUpModel = {};
        vm.searchFields = [
            {
                enabled: true,
                field: 'campaign',
                position: 1,
                title: 'Campaign'
            },
            {
                enabled: true,
                field: 'site',
                position: 2,
                title: 'Site'
            },
            {
                enabled: true,
                field: 'section',
                position: 3,
                title: 'Section'
            },
            {
                enabled: true,
                field: 'placement',
                position: 4,
                title: 'Placement'
            }
        ];

        vm.filterValues = [
            {
                fieldName: 'campaignName',
                values: []
            }
        ];

        vm.filterOptions = [
            {
                text: $translate.instant('global.campaign'),
                value: [],
                index: 0,
                fieldName: 'campaignName'
            }
        ];

        vm.actionFilter = [
            {
                onSelectAll: onCampaignSelectAll,
                onDeselectAll: onCampaignDeselectAll,
                onItemAction: onCampaignItemAction
            }
        ];

        function onCampaignItemAction(id) {
            var campaignSelected = lodash.find(vm.model, {
                campaignId: id
            });

            campaignSelected.isFilterVisible = !campaignSelected.isFilterVisible;
        }

        function onCampaignSelectAll() {
            filterRows(vm.model);
        }

        function onCampaignDeselectAll() {
            filterRows(vm.model);
        }

        function onExpandCollapse(expanded, element, $event, node) {
            if (expanded === true) {
                onExpand($event, node);
            }
            else {
                onCollapse(node);
            }
        }

        function onDrop(event, ui, placementRow, callback) {
            var promise = onSelect(placementRow),
                placementAction;

            promise.then(function (associations) {
                placementAction =
                    TagInjectionAssociation.buildPlacementActionCreate(placementRow, parentStandardTab.dragDropTag);

                if (TagInjectionAssociation.isAlreadyAssociated(associations, placementAction)) {
                    $log.debug('Association already exist.');
                    return;
                }

                promise = parentTagInjection.promiseTab = parentStandardTab.performAction(placementAction);
                promise.then(function () {
                    promise = onSelect(placementRow);
                    promise.then(function () {
                        DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                            'tagInjection.association.successCreation');
                        callback(placementRow);
                    });
                });
            });
        }

        $scope.$on('tagInjection.reloadPlacementsTree', function (event, data) {
            // Stores advertiser & brand
            vm.params = data;
            rowSelectedCampaignId = null;
            activate(data);

            clearAssociationsAndActions();
        });

        $scope.$on('tagInjection.loadCampaigns', function (event, data) {
            rowSelectedCampaignId = data.campaignId;
            activate(data);
        });

        $scope.$on('tagInjection.clearPlacementsTree', function () {
            clearPlacementsTree();
            clearAssociationsAndActions();
        });

        $scope.$on('tagInjection.removeTagsFromAssociations', function () {
            if (vm.itemSelectedFromPlacementGrid) {
                parentTagInjection.promiseTab = onSelect(vm.itemSelectedFromPlacementGrid);
            }
        });

        $scope.$on('tagInjection.afterRemoveAssociation', function (event, params) {
            var promise = parentTagInjection.promiseTab = onSelect(params.placementRow);

            promise.then(function () {
                DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS,
                    'tagInjection.association.successDeletion');
            });
        });

        function clearAssociationsAndActions() {
            parentStandardTab.clearAssociations();
        }

        function activate(data) {
            loadFirstLevel(data);
        }

        function clearPlacementsTree() {
            vm.model = [];
        }

        function getPlacementRowAssociations(row) {
            return AgencyService.getHtmlInjectionTagAssociation(
                parentTagInjection.advertiser.agencyId,
                row.field.key,
                row.campaignId,
                row.siteId,
                row.sectionId,
                row.placementId
            );
        }

        function getLevelData(level, params) {
            return AgencyService.getPlacements({
                levelType: level.key,
                advertiserId: params.advertiserId,
                brandId: params.brandId,
                campaignId: params.campaignId,
                siteId: params.siteId,
                sectionId: params.sectionId
            });
        }

        function loadFirstLevel(data) {
            if (data.brand.id < 0) {
                updateModel([]);
                return;
            }

            if (Utils.isPromiseInProgress(parentTagInjection.promiseTab)) {
                parentTagInjection.promiseTab.then(function () {
                    resolveLoadFirstLevelPromise(data);
                });
            }
            else {
                resolveLoadFirstLevelPromise(data);
            }
        }

        function resolveLoadFirstLevelPromise(data) {
            var level = TagInjectionAssociation.LEVEL.CAMPAIGN,
                promise = parentTagInjection.promiseTab = getLevelData(level, {
                    advertiserId: data.advertiser.id,
                    brandId: data.brand.id
                }),
                model;

            promise.then(function (result) {
                model = TagInjectionTree.getModel(level, result, {
                    advertiserId: data.advertiser.id,
                    brandId: data.brand.id
                });
                vm.backUpModel = model;

                vm.filterOptions[0].value = [];
                vm.filterValues[0].values = [];
                vm.filterOptions[0].value = lodash.map(model, function (res) {
                    vm.filterValues[0].values.push(res.campaignId);
                    return {
                        enabled: true,
                        id: res.campaignId,
                        label: res.name
                    };
                });

                updateModel(model);
                if (!Utils.isUndefinedOrNull(rowSelectedCampaignId)) {
                    selectedCampaign(model);
                }
            });
        }

        function onExpand($event, row) {
            if (Utils.isUndefinedOrNull(row)) {
                $log.warn('onExpand. rowPlacement is undefined.');
                return;
            }

            if (row.childrenLoaded === true) {
                row.expanded = true;
                return;
            }

            var level = TagInjectionTree.getNextLevel(row.field),
                promiseNextLevel = parentTagInjection.promiseTab = getLevelData(level, row);

            promiseNextLevel.then(function (nextLevelData) {
                row.children = TagInjectionTree.getModel(level, nextLevelData, row);
                row.expanded = true;
                row.childrenLoaded = true;
                if (row.children.length === 0) {
                    row.expanded = false;
                    row.children = [{}];
                }

                refreshModelGrid();
            });
        }

        function selectedCampaign(campaigs) {
            var campaignSelected = lodash.find(campaigs, {
                campaignId: rowSelectedCampaignId
            });

            vm.rowSelectedUuid = campaignSelected.uuidRow;
            onSelect(campaignSelected);
        }

        //TODO fix this behavior inside te-tree directive
        function onCollapse(row) {
            row.expanded = false;
        }

        function refreshModelGrid() {
            var outputModelBackup = [];

            angular.copy(vm.model, outputModelBackup);
            vm.model = [];
            angular.copy(outputModelBackup, vm.model);
        }

        function onSelect(row) {
            var promise,
                deferred = $q.defer();

            promise = parentTagInjection.promiseTab = getPlacementRowAssociations(row);
            promise.then(function (associations) {
                setAssociations(row, associations);
                deferred.resolve(associations);
            });

            vm.itemSelectedFromPlacementGrid = row;
            vm.selectedRowKey = TagInjectionAssociation.buildKey(row);

            return deferred.promise;
        }

        function setAssociations(row, associations) {
            parentStandardTab.setAssociations(row, associations);
        }

        function updateModel(model) {
            vm.model = model;
            refreshModelGrid();
        }

        function searchTree(searchTerm) {
            parentStandardTab.clearAssociations();
            parentTagInjection.promiseTab = AgencyService.searchPlacementView(
                parentTagInjection.advertiser.agencyId,
                parentTagInjection.advertiser.id,
                parentTagInjection.brand.id,
                vm.searchFields[0].enabled,
                vm.searchFields[1].enabled,
                vm.searchFields[2].enabled,
                vm.searchFields[3].enabled,
                searchTerm
            ).then(function (response) {
                var model;

                model = TagInjectionTree.getModelSearch(
                    response,
                    parentTagInjection.advertiser.id,
                    parentTagInjection.brand.id
                );

                if (!searchContext) {
                    searchContext = true;
                    vm.backUpModel = vm.model;
                }

                filterRows(model);
                updateModel(model);
            });
        }

        function searchClear() {
            searchContext = false;
            parentStandardTab.clearAssociations();
            filterRows(vm.backUpModel);
            updateModel(vm.backUpModel);
        }

        function filterRows(model) {
            for (var ind = 0; ind < model.length; ++ind) {
                model[ind].isFilterVisible = vm.filterValues[0].values.indexOf(model[ind].campaignId) !== -1;
            }
        }

        $scope.$on('trackingTag.deleteTag', function () {
            if (!Utils.isUndefinedOrNull(vm.itemSelectedFromPlacementGrid)) {
                onSelect(vm.itemSelectedFromPlacementGrid);
            }
        });

        $scope.$watch('vmPlacements.searchTerm', function (newValue, oldValue) {
            if (!Utils.isUndefinedOrNull(oldValue)) {
                if (!Utils.isUndefinedOrNull(newValue) && newValue.length === 0) {
                    searchClear();
                }
            }
        });
    }
})();
