(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('CampaignReviewController', CampaignReviewController);

    CampaignReviewController.$inject = [
        '$filter',
        '$q',
        '$scope',
        'AgencyService',
        'CampaignsService',
        'CONSTANTS',
        'DialogFactory',
        'UserService',
        'Utils',
        'lodash'
    ];

    function CampaignReviewController(
        $filter,
        $q,
        $scope,
        AgencyService,
        CampaignsService,
        CONSTANTS,
        DialogFactory,
        UserService,
        Utils,
        lodash
        ) {
        var vm = this,
            campaignId = $scope.$parent.$parent.vm.campaignId;

        vm.contact = UserService.getUsername();
        vm.contacts = [];
        vm.updateSelectedDomain = updateSelectedDomain;

        activate();

        function activate() {
            vm.domains = [];

            $scope.$parent.$parent.vm.promise = $q.all([
                CampaignsService.getCampaignDetails(campaignId),
                UserService.getDomains(),
                AgencyService.getUsersTrafficking()
            ]);

            $scope.$parent.$parent.vm.promise.then(function (promise) {
                vm.campaignModel = promise[0].campaign;
                $scope.$parent.$parent.vm.campaignModel = vm.campaignModel;

                loadDomains(promise[1]);
                loadUsers(promise[2]);
            });
        }

        function loadDomains(domains) {
            vm.domains = $filter('orderBy')(domains, 'domain');
            $scope.$parent.$parent.vm.domains = vm.domains;

            var cookieDomainId = vm.campaignModel && vm.campaignModel.cookieDomainId ,
                selectedDomain = cookieDomainId && lodash.findWhere(vm.domains, {
                    id: cookieDomainId
                });

            if (cookieDomainId && !selectedDomain) {
                selectedDomain = {
                    id: cookieDomainId,
                    domain: Utils.isUndefinedOrNull(vm.campaignModel.domain) ? '' : vm.campaignModel.domain
                };
                vm.domains.unshift(selectedDomain);
            }

            vm.selectDomains = selectedDomain;
            updateSelectedDomain();
        }

        function loadUsers(users) {
            var currentUserName = UserService.getUsername(),
                currentContactId = 0,
                currentId = 0,
                currentRealName = '';

            angular.forEach($filter('orderBy')(users, 'realName'), function (contact) {
                if (contact.userName === currentUserName) {
                    currentContactId = contact.contactId;
                    currentId = contact.id;
                    currentRealName = contact.realName;
                }
                else {
                    vm.contacts.push({
                        id: contact.id,
                        contactId: contact.contactId,
                        userName: contact.userName,
                        realName: contact.realName,
                        checked: false,
                        readonly: false
                    });
                }
            });

            vm.contacts.unshift(
                {
                    id: currentId,
                    contactId: currentContactId,
                    userName: currentUserName,
                    realName: currentRealName,
                    checked: true,
                    readonly: true
                }
            );

            $scope.$parent.$parent.vm.contacts = vm.contacts;
        }

        function updateSelectedDomain() {
            $scope.$parent.$parent.vm.selectDomains = vm.selectDomains;
            $scope.$parent.$parent.vm.campaignModel.domain = Utils.isUndefinedOrNull(vm.campaignModel.domain) ?
                '' : vm.campaignModel.domain;
        }
    }
})();
