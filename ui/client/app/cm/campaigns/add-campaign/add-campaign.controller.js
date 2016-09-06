(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddCampaignController', AddCampaignController);

    AddCampaignController.$inject = [
        '$log',
        '$scope',
        '$state',
        '$translate',
        'CONSTANTS',
        'CampaignsService',
        'DateTimeService',
        'DialogFactory',
        'ErrorRequestHandler'
    ];

    function AddCampaignController(
        $log,
        $scope,
        $state,
        $translate,
        CONSTANTS,
        CampaignsService,
        DateTimeService,
        DialogFactory,
        ErrorRequestHandler) {
        var vm = this;

        vm.STEP = {
            NAME: {
                index: 1,
                isValid: false,
                key: 'addCampaign.name'
            },
            DOMAIN: {
                index: 2,
                isValid: false,
                key: 'addCampaign.domain'
            },
            DATES_BUDGET: {
                index: 3,
                isValid: false,
                key: 'addCampaign.dates&Budget'
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

        function cancel() {
            $log.debug('Cancel');
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('campaign.confirm.discard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.KEEP_DISCARD
            }).result.then(
                function () {
                    $log.debug('Discard');
                    $state.go('campaigns');
                },

                function () {
                    $log.debug('Keep');
                });
        }

        function formatCampaign(campaign) {
            var newCampaign = {};

            newCampaign.advertiserId = campaign.advertiser.id;
            newCampaign.agencyId = campaign.advertiser.agencyId;
            newCampaign.brandId = campaign.brand.id;
            newCampaign.cookieDomainId = campaign.domain.id;
            newCampaign.description = '';
            newCampaign.endDate = DateTimeService.inverseParse(campaign.endDate);
            newCampaign.isActive = 'Y';
            newCampaign.isHidden = 'N';
            newCampaign.logicalDelete = 'N';
            newCampaign.name = campaign.name;
            newCampaign.overallBudget = campaign.budget;
            newCampaign.startDate = DateTimeService.inverseParse(campaign.startDate);

            return newCampaign;
        }

        function save() {
            $log.debug('Save');
            var newCampaign = formatCampaign(vm.campaign);

            vm.promiseRequest = CampaignsService.saveCampaignDetails(newCampaign);
            processSavePromise(vm.promiseRequest);
        }

        function processSavePromise(savePromise) {
            savePromise.then(
                function (campaign) {
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'campaigns.successful');
                    $state.go('campaign-details', {
                        campaignId: campaign.id
                    });
                }).catch(function (error) {
                    if (error.status === 409) {
                        DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.ERROR, 'campaigns.error');
                    }
                    else {
                        ErrorRequestHandler.handle('Cannot Create Campaign', error);
                    }
                });
        }
    }
})();
