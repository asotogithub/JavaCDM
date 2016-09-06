(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('SiteTraffickingContactsController', SiteTraffickingContactsController);

    SiteTraffickingContactsController.$inject = [
        '$filter',
        '$scope',
        'CampaignsService',
        'UserService'
    ];

    function SiteTraffickingContactsController(
        $filter,
        $scope,
        CampaignsService,
        UserService) {
        var vm = this;

        vm.allSelected = false;
        vm.checkAllChecked = checkAllChecked;
        vm.checkboxAll = checkboxAll;
        vm.contacts = [];
        vm.$table = {
            filtered: []
        };

        activate();

        function activate() {
            loadContacts($scope.$parent.$parent.vm.campaignId);
        }

        function checkAllChecked(visibleRows) {
            vm.allSelected = visibleRows.length !== 0;

            angular.forEach(visibleRows, function (contact) {
                if (vm.allSelected) {
                    if (!contact.checked) {
                        vm.allSelected = false;
                    }
                }
            });
        }

        function checkboxAll(visibleRows) {
            angular.forEach(visibleRows, function (contact) {
                if (!contact.readonly) {
                    contact.checked = vm.allSelected;
                }
            });
        }

        function loadContacts(campaignId) {
            $scope.$parent.$parent.vm.promise = CampaignsService.getSiteContacts(campaignId);

            $scope.$parent.$parent.vm.promise.then(function (contacts) {
                vm.contacts = [];

                angular.forEach($filter('orderBy')(contacts, 'contactEmail'), function (contact) {
                    vm.contacts.push({
                        contactEmail: contact.contactEmail,
                        contactId: contact.contactId,
                        contactName: contact.contactName,
                        publisherId: contact.publisherId,
                        publisherName: contact.publisherName,
                        siteId: contact.siteId,
                        siteName: contact.siteName,
                        checked: false,
                        readonly: false
                    });
                });

                vm.contacts.unshift(
                    {
                        contactEmail: UserService.getUsername(),
                        contactId: UserService.getSavedUser().contactId,
                        contactName: '---',
                        publisherId: 0,
                        publisherName: '---',
                        siteId: 0,
                        siteName: '---',
                        checked: true,
                        readonly: true
                    }
                );

                $scope.$parent.$parent.vm.siteContacts = vm.contacts;

                registerWatchRows();
            });
        }

        function registerWatchRows() {
            if (angular.isDefined(angular.element('#contactTable').isolateScope())) {
                vm.$table = angular.element('#contactTable').isolateScope().$table;

                angular.element('#contactTable').isolateScope().$watch('$table.filtered.length', function () {
                    checkAllChecked(vm.$table.filtered);
                });
            }
        }
    }
})();

