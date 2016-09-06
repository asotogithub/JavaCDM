(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddScheduleAssignmentsController', AddScheduleAssignmentsController);

    AddScheduleAssignmentsController.$inject = [
        '$scope',
        '$translate',
        '$uibModalInstance',
        'CONSTANTS',
        'CampaignsService',
        'DialogFactory',
        'ErrorRequestHandler',
        'Utils',
        'data',
        'lodash'
    ];

    function AddScheduleAssignmentsController(
        $scope,
        $translate,
        $uibModalInstance,
        CONSTANTS,
        CampaignsService,
        DialogFactory,
        ErrorRequestHandler,
        Utils,
        data,
        lodash) {
        var vm = $scope.vm = this,
            selectedCreatives,
            selectedPlacements,
            DIALOG = DialogFactory.DIALOG,
            DEFAULT_TIMEZONE = CONSTANTS.TIMEZONE.DEFAULT;

        vm.campaignId = data.campaignId;
        vm.creatives = null;
        vm.dismiss = dismiss;
        vm.entityName = data.name;
        vm.modalTitle = data.title;
        vm.modalToolTip = data.toolTip;
        vm.pending = [];
        vm.placements = null;
        vm.promise = null;
        vm.removePending = removePending;
        vm.saveAndClose = saveAndClose;
        vm.selectCreatives = selectCreatives;
        vm.selectPlacements = selectPlacements;

        activate();

        function activate() {
            vm.creatives = data.creatives;
            vm.placements = data.placements;
        }

        function dismiss() {
            $uibModalInstance.dismiss();
        }

        function saveAndClose(newAssignments) {
            var pendingList = angular.isDefined(newAssignments) ? newAssignments : vm.pending,
                filteredCreativeInsertionList;

            filteredCreativeInsertionList =
                lodash.map(pendingList, function (pending) {
                        return lodash.extend(
                            {
                                released: 0,
                                sequence: 0,
                                timeZone: DEFAULT_TIMEZONE
                            },
                            lodash.pick(
                                pending,
                                'campaignId',
                                'creativeGroupId',
                                'creativeId',
                                'endDate',
                                'placementId',
                                'startDate',
                                'weight',
                                'clickthroughs',
                                'clickthrough'
                            )
                        );
                    });

            if (filteredCreativeInsertionList.length > 0) {
                vm.promise = CampaignsService.bulkSave(vm.campaignId, filteredCreativeInsertionList);

                vm.promise.then(function () {
                        $uibModalInstance.close({
                            isUpdate: true
                        });
                    }).catch(function (error) {
                        if (error.status === CONSTANTS.HTTP_STATUS.CONFLICT) {
                            var duplicateList = [],
                                duplicateElem = {},
                                duplicateAssignments = [],
                                newScheduleAssignments = [];

                            if (angular.isArray(error.data.error)) {
                                duplicateList = lodash.map(error.data.error, function (errorDetails) {
                                    return {
                                        creativeId: Number(errorDetails.creativeId),
                                        placementId: Number(errorDetails.placementId),
                                        creativeGroupId: Number(errorDetails.groupId)
                                    };
                                });
                            }
                            else {
                                duplicateElem = {
                                    creativeId: Number(error.data.error.creativeId),
                                    placementId: Number(error.data.error.placementId),
                                    creativeGroupId: Number(error.data.error.groupId)
                                };
                                duplicateList.push(duplicateElem);
                            }

                            duplicateAssignments = lodash.filter(pendingList, function (_response) {
                                return !!lodash.findWhere(
                                    duplicateList,
                                    lodash.pick(
                                        _response,
                                        'creativeGroupId',
                                        'creativeId',
                                        'placementId'
                                    ));
                            });

                            newScheduleAssignments = lodash.difference(pendingList, duplicateAssignments);
                            showDuplicateScheduleAssignments(duplicateAssignments, newScheduleAssignments);
                        }
                        else if (error.status === CONSTANTS.HTTP_STATUS.BAD_REQUEST) {
                            if (!Utils.isUndefinedOrNull(error.data) &&
                                !Utils.isUndefinedOrNull(error.data.error) &&
                                !Utils.isUndefinedOrNull(error.data.error.code) &&
                                error.data.error.code.$ === '101') {
                                DialogFactory.showCustomDialog({
                                    type: CONSTANTS.DIALOG.TYPE.ERROR,
                                    title: $translate.instant('global.error'),
                                    description: error.data.error.message
                                });
                            }
                            else {
                                ErrorRequestHandler.handle('Cannot add schedule assignment. ' +
                                    error.data.error.message, error);
                            }
                        }
                        else {
                            ErrorRequestHandler.handle('Cannot add schedule assignment', error, function () {
                                $uibModalInstance.close({
                                    isUpdate: false
                                });
                            });
                        }
                    });
            }
            else {
                $uibModalInstance.close({
                    isUpdate: false
                });
            }
        }

        function showDuplicateScheduleAssignments(duplicateAssignments, newAssignments) {
            DialogFactory.showCustomDialog({
                controller: 'DuplicateScheduleAssignmentsController',
                size: DIALOG.SIZE.LARGE,
                template: 'app/cm/campaigns/schedule-tab/duplicate-schedule-assignments/' +
                    'duplicate-schedule-assignments.html',
                type: DIALOG.TYPE.CUSTOM,
                data: {
                    duplicates: duplicateAssignments
                }
            }).result.then(
                function () {
                    saveAndClose(newAssignments);
                });
        }

        function removePending(pending) {
            vm.pending = lodash.difference(vm.pending, pending);
            $scope.$apply();
        }

        function selectCreatives(creatives) {
            selectedCreatives = creatives;
            updatePending();
        }

        function selectPlacements(placements) {
            selectedPlacements = placements;
            updatePending();
        }

        function updatePending() {
            var creatives = groupByDimensions(selectedCreatives),
                placements = groupByDimensions(selectedPlacements),
                pending = [];

            lodash.chain(lodash.keys(creatives))
                .intersection(lodash.keys(placements))
                .forEach(function (width) {
                    lodash.chain(lodash.keys(creatives[width]))
                        .intersection(lodash.keys(placements[width]))
                        .forEach(function (height) {
                            lodash.forEach(creatives[width][height], function (creative) {
                                var _creative = lodash.pick(
                                    creative,
                                    'campaignId',
                                    'creativeAlias',
                                    'creativeClickthroughs',
                                    'creativeDefaultClickthrough',
                                    'clickthroughs',
                                    'clickthrough',
                                    'creativeGroupId',
                                    'creativeGroupName',
                                    'creativeGroupWeight',
                                    'creativeGroupWeightEnabled',
                                    'creativeId');

                                lodash.forEach(placements[width][height], function (placement) {
                                    pending.push(lodash.extend({
                                        endDate: placement.placementEndDate,
                                        startDate: placement.placementStartDate,
                                        weight: 100
                                    }, lodash.pick(
                                        placement,
                                        'placementEndDate',
                                        'placementId',
                                        'placementName',
                                        'placementStartDate',
                                        'siteId',
                                        'siteName',
                                        'siteSectionId',
                                        'siteSectionName',
                                        'sizeName'
                                    ), _creative));
                                });
                            });
                        })
                        .value();
                })
                .value();

            vm.pending = pending;
        }

        function groupByDimensions(collection) {
            return lodash.mapValues(lodash.groupBy(collection, 'width'), function (item) {
                return lodash.groupBy(item, 'height');
            });
        }
    }
})();
