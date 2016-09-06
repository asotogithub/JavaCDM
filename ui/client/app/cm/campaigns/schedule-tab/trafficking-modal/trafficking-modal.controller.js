(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('TraffickingModalController', TraffickingModalController);

    TraffickingModalController.$inject = [
        '$modalInstance',
        '$moment',
        '$scope',
        '$translate',
        'CONSTANTS',
        'DialogFactory',
        'ErrorRequestHandler',
        'TraffickingService',
        'UserService',
        'data',
        'lodash'
    ];

    function TraffickingModalController(
        $modalInstance,
        $moment,
        $scope,
        $translate,
        CONSTANTS,
        DialogFactory,
        ErrorRequestHandler,
        TraffickingService,
        UserService,
        data,
        lodash) {
        var vm = this;

        vm.campaignId = data.campaignId;
        vm.campaignModel = [];
        vm.contacts = [];
        vm.errorMessage = null;
        vm.siteContacts = [];
        vm.traffic = traffic;
        vm.closeModal = closeModal;
        vm.setTab = setTab;
        vm.tab = {
            campaignReview: {
                active: true
            },
            siteTraffickingContacts: {
                active: false,
                disabled: true
            }
        };
        vm.TRAFFIC = {
            CAMPAIGN: {
                key: 'campaign.traffic'
            },
            SITE: {
                key: 'site.traffic'
            }
        };

        activate();

        function activate() {
            validateCampaign();
        }

        function validateCampaign() {
            vm.promise = TraffickingService.validateCampaign(vm.campaignId);
            vm.promise.then(function () {
                vm.trafficDisabled = false;
            }).catch(function (error) {
                var errorMessage = error && error.data && error.data.errors;

                vm.trafficDisabled = true;
                vm.errorMessage = errorMessage;
                ErrorRequestHandler.handle('Cannot validate campaign for trafficking', error, function () {
                    $modalInstance.dismiss();
                });
            });
        }

        function setTab(tab) {
            if (vm.TRAFFIC.SITE.key === tab.key) {
                vm.tab.siteTraffickingContacts.active = true;
                vm.tab.campaignReview.active = false;
            }
            else {
                vm.tab.siteTraffickingContacts.active = false;
                vm.tab.campaignReview.active = true;
            }
        }

        function traffic() {
            vm.promise = TraffickingService.trafficking(getTrafficPayload());
            vm.promise.then(function () {
                vm.trafficDisabled = false;
                DialogFactory.showDismissableMessage(
                    DialogFactory.DISMISS_TYPE.SUCCESS,
                    'DIALOGS_CONFIRMATION_TRAFFICKED');
                $modalInstance.close();
            }).catch(function (error) {
                var errorMessage = error && error.data && error.data.errors;

                vm.trafficDisabled = true;
                vm.errorMessage = errorMessage;
                ErrorRequestHandler.handle('Cannot traffic campaign', error, function () {
                    $modalInstance.dismiss();
                });
            });
        }

        function closeModal() {
            DialogFactory.showCustomDialog({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant('DIALOGS_CONFIRMATION_MSG'),
                description: $translate.instant('traffic.confirm.discard'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.OK_CANCEL
            }).result.then(
                function () {
                    $modalInstance.dismiss();
                });
        }

        function getContactIDs(contacts, currentContactId) {
            var filtered = lodash.filter(contacts, function (contact) {
                return contact.checked && contact.contactId !== currentContactId;
            });

            return lodash.pluck(filtered, 'contactId');
        }

        function getTrafficPayload() {
            var currentContactId = UserService.getSavedUser().contactId,
                trafficPayload = {
                currentContactId: currentContactId,
                campaignId: vm.campaignId,
                timeZoneOffset: $moment().utcOffset() / 60,
                domain: vm.selectDomains.domain,
                agencyContacts: getContactIDs(vm.contacts, currentContactId),
                siteContacts: getContactIDs(vm.siteContacts, currentContactId)
            };

            if (vm.campaignModel.statusId === CONSTANTS.SITE_MEASUREMENT.STATES.NEW) {
                trafficPayload.cookieDomainId = vm.selectDomains.id;
            }

            return trafficPayload;
        }
    }
})();
