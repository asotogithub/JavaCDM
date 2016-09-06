(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SiteMeasurementCampaignAssocController', SiteMeasurementCampaignAssocController);

    SiteMeasurementCampaignAssocController.$inject = [
        '$q',
        '$stateParams',
        '$timeout',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'SiteMeasurementsService',
        'Utils',
        'lodash'
    ];

    function SiteMeasurementCampaignAssocController(
        $q,
        $stateParams,
        $timeout,
        $translate,
        CONSTANTS,
        DialogFactory,
        SiteMeasurementsService,
        Utils,
        lodash) {
        var siteMeasurementId = $stateParams.siteMeasurementId,
            statusText = $translate.instant('global.status'),
            vm = this;

        vm.associatedCampaignList = [];
        vm.associatedFilterValues = [
            {
                fieldName: 'status',
                values: []
            }
        ];
        vm.associatedFilterOption = [
            {
                text: statusText,
                value: []
            }
        ];
        vm.associatedActionFilter = [
            {
                onSelectAll: associatedStatusAllAction,
                onDeselectAll: associatedStatusDeselectAll,
                onItemAction: associatedStatusItemAction
            }
        ];
        vm.pageSize = CONSTANTS.SITE_MEASUREMENT.ASSOCIATION.PAGE_SIZE;
        vm.save = save;
        vm.savedChanges = true;
        vm.setChanges = setChanges;
        vm.templateLeft = 'app/cm/site-measurements/site-measurement/campaign-associations/templates/left-table.html';
        vm.templateRight = 'app/cm/site-measurements/site-measurement/campaign-associations/templates/right-table.html';
        vm.unassociatedCampaignList = [];
        vm.unassociatedFilterValues = [
            {
                fieldName: 'status',
                values: []
            }
        ];
        vm.unassociatedFilterOption = [
            {
                text: statusText,
                value: []
            }
        ];
        vm.unassociatedActionFilter = [
            {
                onSelectAll: unassociatedStatusAllAction,
                onDeselectAll: unassociatedStatusDeselectAll,
                onItemAction: unassociatedStatusItemAction
            }
        ];

        activate();

        function activate() {
            loadData();
        }

        function buildPayload() {
            var payload = [];

            lodash.forEach(vm.associatedCampaignList, function (campaign) {
                payload.push({
                    campaignId: campaign.campaignId,
                    measurementId: siteMeasurementId
                });
            });

            return payload;
        }

        function loadData() {
            vm.promise = $q.all([
                SiteMeasurementsService.getCampaigns(siteMeasurementId, true),
                SiteMeasurementsService.getCampaigns(siteMeasurementId, false)
            ]).then(function (promises) {
                vm.associatedCampaignList = promises[0];
                setCampaignStatus(vm.associatedCampaignList);
                loadFilters(vm.associatedCampaignList, vm.associatedFilterOption, vm.associatedFilterValues);

                vm.unassociatedCampaignList = promises[1];
                setCampaignStatus(vm.unassociatedCampaignList);
                loadFilters(vm.unassociatedCampaignList, vm.unassociatedFilterOption, vm.unassociatedFilterValues);
            });
        }

        function loadFilters(model, filterOptions, filterValues) {
            var statusList = [];

            lodash.forEach(model, function (value) {
                if (lodash.indexOf(statusList, value.status) < 0) {
                    statusList.push(value.status);
                }
            });

            filterOptions[0].value = lodash.clone(lodash.sortBy(statusList), true);
            filterValues[0].values = statusList;
        }

        function refreshAssociatedTable() {
            var campaignListBackup = [];

            angular.copy(vm.associatedCampaignList , campaignListBackup);
            vm.associatedCampaignList = [];
            angular.copy(campaignListBackup, vm.associatedCampaignList);
        }

        function refreshUnassociatedTable() {
            var campaignListBackup = [];

            angular.copy(vm.unassociatedCampaignList , campaignListBackup);
            vm.unassociatedCampaignList = [];
            angular.copy(campaignListBackup, vm.unassociatedCampaignList);
        }

        function save() {
            vm.promise = SiteMeasurementsService.updateCampaignAssociation(siteMeasurementId, buildPayload());

            vm.promise.then(
                function () {
                    vm.unsavedChanges = false;

                    DialogFactory.showDismissableMessage(
                        DialogFactory.DISMISS_TYPE.SUCCESS,
                        'info.operationCompleted');
                });
        }

        function setCampaignStatus(campaignList) {
            var activeText = $translate.instant('global.active'),
                inactiveText = $translate.instant('global.inactive');

            lodash.forEach(campaignList, function (campaign) {
                campaign.id = campaign.campaignId;
                campaign.status = campaign.campaignStatus.toUpperCase() === 'NEW' ? inactiveText : activeText;
            });
        }

        function setChanges() {
            vm.savedChanges = false;

            $timeout(function () {
                loadFilters(vm.associatedCampaignList, vm.associatedFilterOption, vm.associatedFilterValues);
                loadFilters(vm.unassociatedCampaignList, vm.unassociatedFilterOption, vm.unassociatedFilterValues);
                refreshAssociatedTable();
                refreshUnassociatedTable();
            });
        }

        function associatedStatusAllAction() {
            refreshAssociatedTable();
        }

        function associatedStatusDeselectAll(isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshAssociatedTable();
            }
        }

        function associatedStatusItemAction(id, isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshAssociatedTable();
            }
        }

        function unassociatedStatusAllAction() {
            refreshUnassociatedTable();
        }

        function unassociatedStatusDeselectAll(isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshUnassociatedTable();
            }
        }

        function unassociatedStatusItemAction(id, isSelectedAll) {
            if (Utils.isUndefinedOrNull(isSelectedAll) || !isSelectedAll) {
                refreshUnassociatedTable();
            }
        }
    }
})();
