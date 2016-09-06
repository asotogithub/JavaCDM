(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AddCampaignDomainController', AddCampaignDomainController);

    AddCampaignDomainController.$inject = [
        '$log',
        '$scope',
        'CONSTANTS',
        'DialogFactory',
        'UserService',
        'lodash'
    ];

    function AddCampaignDomainController(
        $log,
        $scope,
        CONSTANTS,
        DialogFactory,
        UserService,
        lodash) {
        $log.debug('Running AddCampaignDomainController');
        var vm = this;

        vm.OPTION = {
            FIRST: {
                key: 'firstParty'
            },
            THIRD: {
                key: 'thirdParty'
            }
        };

        vm.campaign = $scope.$parent.vmCampaign.campaign;
        vm.domainForm = {};
        vm.option = null;
        vm.selectOption = selectOption;
        vm.step = $scope.$parent.vmCampaign.STEP.DOMAIN;

        function activate() {
            getDomains();
        }

        function doAutoSelection(firstPartyDomains, thirdPartyDomain) {
            if (firstPartyDomains && !vm.thirdPartyDomain) {
                selectOption(vm.OPTION.FIRST);
            }
            else if (thirdPartyDomain && !vm.firstPartyDomains) {
                selectOption(vm.OPTION.THIRD);
            }
        }

        function getDomains() {
            $scope.$parent.vmCampaign.promiseRequest = UserService.getDomains();
            $scope.$parent.vmCampaign.promiseRequest.then(
                function (domains) {
                    setDomains(domains);
                },

                function (error) {
                    DialogFactory.showDialog(CONSTANTS.DIALOG.TYPE.ERROR);
                    $log.error('Cannot get domains: ' + angular.toJson(error));
                    vm.domains.reject(error);
                });
        }

        function getFirstPartyDomains(domains) {
            if (domains.length < 1) {
                return null;
            }

            var res = [];

            angular.forEach(domains, function (item) {
                if (item.isThirdParty === 'N') {
                    res.push(item);
                }
            });

            if (res.length < 1) {
                return null;
            }

            return res;
        }

        function getThirdPartyDomain(domains) {
            if (domains.length < 1) {
                return null;
            }

            var res = lodash.find(domains, function (item) {
                return item.isThirdParty === 'Y' && item.domain === CONSTANTS.COOKIE_DOMAIN.THIRD_PARTY_DOMAIN;
            });

            if (!res) {
                return null;
            }

            return res;
        }

        function selectOption(option) {
            if (vm.option === option) {
                return;
            }

            vm.option = option;
            if (option === vm.OPTION.FIRST) {
                vm.campaign.domain = null;
                vm.domainForm.firstPartyDropdown.$viewValue = null;
                vm.step.isValid = false;
            }
            else if (option === vm.OPTION.THIRD) {
                vm.campaign.domain = vm.thirdPartyDomain;
                vm.step.isValid = true;
            }
        }

        function setDomains(domains) {
            vm.domains = domains;
            vm.firstPartyDomains = getFirstPartyDomains(domains);
            vm.thirdPartyDomain = getThirdPartyDomain(domains);
            if (vm.campaign.domain) {
                var index = lodash.indexOf(lodash.pluck(domains, 'id'), vm.campaign.domain.id);

                if (index < domains.length) {
                    vm.campaign.domain = domains[index];
                }
                else {
                    vm.step.isValid = false;
                }
            }

            doAutoSelection(vm.firstPartyDomains, vm.thirdPartyDomain);
        }

        $scope.$on(vm.step.key, function (event, data) {
            if (data.activate) {
                activate();
            }
        });

        $scope.$watch('vm.domainForm.firstPartyDropdown.$viewValue', function (newVal) {
            if (newVal) {
                vm.step.isValid = true;
            }
        }, true);
    }
})();
