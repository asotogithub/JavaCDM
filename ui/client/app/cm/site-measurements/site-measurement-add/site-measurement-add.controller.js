(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddSMCampaignController', AddSMCampaignController);

    AddSMCampaignController.$inject = [
        '$scope',
        '$state',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'SiteMeasurementsService',
        'SiteMeasurementsUtilService',
        'Utils'
    ];

    function AddSMCampaignController(
        $scope,
        $state,
        $translate,
        CONSTANTS,
        DialogFactory,
        SiteMeasurementsService,
        SiteMeasurementsUtilService,
        Utils) {
        var vm = this;

        vm.STEP = {
            NAME: {
                index: 1,
                isValid: false,
                key: 'addSMCampaign.name'
            },
            DOMAIN: {
                index: 2,
                isValid: false,
                key: 'addSMCampaign.domain'
            }
        };
        vm.activateStep = activateStep;
        vm.campaign = {};
        vm.cancel = cancel;
        vm.save = save;

        function activateStep(step) {
            if (!step) {
                return;
            }

            $scope.$broadcast(step.key, {
                activate: true
            });
        }

        function buildPayload() {
            return {
                advertiserId: vm.campaign.advertiser.id,
                brandId: vm.campaign.brand.id,
                cookieDomainId: vm.campaign.cookieDomain.id,
                expirationDate: SiteMeasurementsUtilService.getExpirationDate(vm.campaign.status.key),
                name: vm.campaign.name.trim(),
                notes: Utils.isUndefinedOrNull(vm.campaign.description) ? '' : vm.campaign.description.trim()
            };
        }

        function cancel() {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('siteMeasurement.confirmDiscard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.YES_NO
            }).result.then(
                function () {
                    $state.go('site-measurements-list');
                });
        }

        function save() {
            vm.promise = SiteMeasurementsService.saveCampaign(buildPayload());

            vm.promise.then(
                function (campaign) {
                    DialogFactory.showDismissableMessage(
                        DialogFactory.DISMISS_TYPE.SUCCESS,
                        'info.operationCompleted');
                    $state.go('sm-details', {
                        siteMeasurementId: campaign.id
                    });
                });
        }
    }
})();
