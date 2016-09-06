(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddGroupCreativeAssociationsController', AddGroupCreativeAssociationsController);

    AddGroupCreativeAssociationsController.$inject = [
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

    function AddGroupCreativeAssociationsController(
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
            DEFAULT_TIMEZONE = CONSTANTS.TIMEZONE.DEFAULT;

        vm.addToCreativeInsertionList = addToCreativeInsertionList;
        vm.campaignId = data.campaignId;
        vm.close = close;
        vm.creativeInputList = [];
        vm.creativeInsertionList = [];
        vm.creativeInsertionTemplateObject = null;
        vm.creativeOutputList = [];
        vm.entityName = data.name;
        vm.filterOptions = {
            filterText: [
                {
                    id: 'filterInput',
                    text: '',
                    field: ['creativeGroupName', 'creativeFileName']
                },
                {
                    id: 'filterOutput',
                    text: '',
                    field: ['creativeGroupName', 'creativeFileName']
                }
            ]
        };
        vm.isBasedOnCreativeGroupId = false;
        vm.modalTitle = data.title;
        vm.modalToolTip = data.toolTip;
        vm.pendingCreativeInsertionList = [];
        vm.saveAndClose = saveAndClose;
        vm.templateLeft = 'app/cm/campaigns/schedule-tab/add-group-creative-associations/templates/left-table.html';
        vm.templateRight = 'app/cm/campaigns/schedule-tab/add-group-creative-associations/templates/right-table.html';

        activate();

        function activate() {
            vm.creativeInputList = data.creatives;
            vm.creativeInsertionTemplateObject = buildCreativeInsertionStructure(data.placements);
            angular.forEach(vm.creativeInputList, function (creative) {
                creative.creativeGroupWeight = CONSTANTS.SCHEDULE.WEIGHT.DEFAULT;
            });
        }

        function addToCreativeInsertionList() {
            var newCreativeInsertion,
                newCreativeInsertions;

            newCreativeInsertions = lodash.map(vm.creativeOutputList, function (creative) {
                newCreativeInsertion = angular.copy(vm.creativeInsertionTemplateObject);
                newCreativeInsertion.creativeAlias = creative.creativeAlias;
                newCreativeInsertion.creativeClickthrough = creative.creativeDefaultClickthrough;
                newCreativeInsertion.clickthrough = creative.creativeDefaultClickthrough;
                newCreativeInsertion.clickthroughs = creative.creativeClickthroughs;
                newCreativeInsertion.creativeGroupId = creative.creativeGroupId;
                newCreativeInsertion.creativeGroupName = creative.creativeGroupName;
                newCreativeInsertion.creativeGroupWeight = creative.creativeGroupWeight;
                newCreativeInsertion.weight = creative.creativeGroupWeight;
                newCreativeInsertion.creativeId = creative.creativeId;
                newCreativeInsertion.sizeName = creative.sizeName;
                newCreativeInsertion.allowRemove = true;
                return newCreativeInsertion;
            });

            vm.pendingCreativeInsertionList = vm.pendingCreativeInsertionList.concat(newCreativeInsertions);

            vm.creativeOutputList = [];
        }

        function buildCreativeInsertionStructure(placement) {
            return {
                campaignId: placement.campaignId,
                endDate: placement.endDate,
                placementEndDate: placement.endDate,
                placementId: placement.id,
                placementName: placement.name,
                placementStartDate: placement.startDate,
                placementStatus: placement.status,
                startDate: placement.startDate,
                siteId: placement.siteId,
                siteName: placement.siteName,
                siteSectionId: placement.siteSectionId,
                siteSectionName: placement.sectionName,
                released: 0,
                sequence: 0,
                timeZone: DEFAULT_TIMEZONE
            };
        }

        function close() {
            $uibModalInstance.dismiss();
        }

        function saveAndClose() {
            vm.promise = CampaignsService.bulkSave(vm.campaignId, vm.pendingCreativeInsertionList)
                .then(function (response) {
                    vm.creativeInsertionList = response;
                    $uibModalInstance.close();
                })
                .catch(function (error) {
                    if (error.status === CONSTANTS.HTTP_STATUS.BAD_REQUEST) {
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
                            ErrorRequestHandler.handleAndReject(error.data.error.message)(error);
                        }
                    }
                    else {
                        ErrorRequestHandler.handleAndReject('Cannot add creative association', null, function () {
                            $uibModalInstance.dismiss();
                        })(error);
                    }
                });
        }

        function refreshCreativeModel() {
            var outputModelBackup = [];

            angular.copy(vm.creativeInputList, outputModelBackup);
            vm.creativeInputList = [];
            angular.copy(outputModelBackup, vm.creativeInputList);
        }

        $scope.$on('rootScope:itemsAssociatedRemoved', function (event, creative) {
            creative.creativeDefaultClickthrough = creative.clickthrough;
            creative.creativeClickthroughs = creative.clickthroughs;
            vm.creativeInputList.push(creative);
            refreshCreativeModel();
        });
    }
})();
