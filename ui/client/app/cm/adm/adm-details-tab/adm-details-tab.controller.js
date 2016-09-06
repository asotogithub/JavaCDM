(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('AdmDetailsTabController', AdmDetailsTabController);

    AdmDetailsTabController.$inject = [
        '$scope',
        '$state',
        '$stateParams',
        'CONSTANTS',
        'AdmService',
        'DialogFactory',
        'UserService'
    ];

    function AdmDetailsTabController(
        $scope,
        $state,
        $stateParams,
        CONSTANTS,
        AdmService,
        DialogFactory,
        UserService) {
        var vm = this,
            dataId = $stateParams.id;

        vm.CHANNELS = CONSTANTS.ADM_CONSTANTS.CONTENT_CHANNELS;
        vm.KEY_TYPE_OPTION = CONSTANTS.ADM_CONSTANTS.KEY_TYPE_OPTION;
        vm.admParentForm = {};
        vm.admDetailsForm = {};
        vm.admConfigForm = {
            extractableCookiesForm: true,
            cookieOverwriteExceptionsForm: true,
            formKeyType: true,
            formKey: true,
            failThroughDefaultsForm: true
        };
        vm.close = close;
        vm.submit = submit;
        vm.permission = {
            updateConfig: hasPermission(CONSTANTS.PERMISSION.ADM.UPDATE)
        };

        activate();

        function activate() {
            vm.promise = AdmService.getAdmDetails(dataId).then(function (promises) {
                vm.model = promises;
                $scope.$broadcast('admDetails-ready');
                $scope.$parent.vm.adm.fileNamePrefix = vm.model.alias;
            });
        }

        function submit() {
            vm.model.active = vm.model.activeObject.key;
            vm.model.ttlExpirationSeconds = parseInt(vm.model.updateFrequencyObject.toString()
                .replace(new RegExp(',', 'g'), '')) * (60 * 60);
            vm.model.cookieExpirationDays = parseInt(vm.model.cookieExpirationDays.toString()
                .replace(new RegExp(',', 'g'), ''));
            vm.model.contentChannels = [];
            vm.model.durableCookies.cookies = [];
            vm.model.cookiesToCapture.cookies = [];
            vm.model.failThroughDefaults.defaultCookieList = [];
            angular.forEach(vm.model.cookieOverwriteExceptionsArray, function (cookie) {
                vm.model.durableCookies.cookies.push(cookie.cookieName);
            });

            angular.forEach(vm.model.extractableCookiesArray, function (cookie) {
                vm.model.cookiesToCapture.cookies.push(cookie.cookieName);
            });

            angular.forEach(vm.model.failThroughDefaultsArray, function (cookie) {
                vm.model.failThroughDefaults.defaultCookieList.push({
                    name: cookie.cookieName,
                    value: cookie.cookieValue
                });
            });

            if (vm.model.keyTypeOption === vm.KEY_TYPE_OPTION.URL_PATH) {
                vm.model.matchCookieName = null;
            }

            if (vm.model.checkContentChannels.email) {
                vm.model.contentChannels.push(vm.CHANNELS.EMAIL);
            }

            if (vm.model.checkContentChannels.display) {
                vm.model.contentChannels.push(vm.CHANNELS.DISPLAY);
            }

            if (vm.model.checkContentChannels.site) {
                vm.model.contentChannels.push(vm.CHANNELS.SITE);
            }

            vm.promise = AdmService.updateAdmDetails(vm.model);
            vm.promise.then(
                function (response) {
                    $scope.$parent.vm.adm.fileNamePrefix = response.alias;
                    vm.admParentForm.$setPristine();
                    DialogFactory.showDismissableMessage(DialogFactory.DISMISS_TYPE.SUCCESS, 'info.operationCompleted');
                });
        }

        function close() {
            $state.go('adm-list');
        }

        function hasPermission(permission) {
            return UserService.hasPermission(permission);
        }
    }
})();
