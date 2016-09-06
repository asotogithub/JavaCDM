(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('FormController', FormController);

    FormController.$inject = [
        '$scope',
        'CONSTANTS',
        'CampaignsUtilService',
        'Utils',
        'UtilsNumber',
        'lodash'
    ];

    function FormController(
        $scope,
        CONSTANTS,
        CampaignsUtilService,
        Utils,
        UtilsNumber,
        lodash) {
        var vm = this;

        vm.isValidContentChannel = isValidContentChannel;
        vm.validateNumeric = validateNumeric;
        vm.ADM_CONSTANTS = CONSTANTS.ADM_CONSTANTS.NUMBER;
        vm.CHANNELS = CONSTANTS.ADM_CONSTANTS.CONTENT_CHANNELS;
        vm.maxLength = CONSTANTS.INPUT_MAX_LENGTH;
        vm.statusList = lodash.values(CampaignsUtilService.getADMStatusList());
        vm.admDetailsForm = {
            checkContentChannels: {
                display: false,
                email: false,
                site: false
            }
        };

        $scope.$on('admDetails-ready', function () {
            vm.admDetailsForm = $scope.$parent.$parent.admDetails.model;
            activate();
        });

        function activate() {
            formatProperties();
        }

        function formatProperties() {
            var status = filterById(vm.admDetailsForm.active.toString(), vm.statusList);

            vm.admDetailsForm.updateFrequencyObject = Math.floor(vm.admDetailsForm.ttlExpirationSeconds / (60 * 60));
            vm.admDetailsForm.activeObject = status ? status : vm.statusList[0];
            vm.admDetailsForm.checkContentChannels = {
                display: checkContentChannels(vm.CHANNELS.DISPLAY, vm.admDetailsForm.contentChannels),
                email: checkContentChannels(vm.CHANNELS.EMAIL, vm.admDetailsForm.contentChannels),
                site: checkContentChannels(vm.CHANNELS.SITE, vm.admDetailsForm.contentChannels)
            };

            if (Utils.isUndefinedOrNull(vm.admDetailsForm.alias)) {
                vm.admDetailsForm.alias = vm.admDetailsForm.fileNamePrefix;
            }
        }

        function filterById(id, arrayElements) {
            if (arrayElements.length < 1) {
                return null;
            }

            var res = lodash.find(arrayElements, function (item) {
                if (item.id) {
                    return item.id === id;
                }

                if (item.key) {
                    return item.key === id;
                }
            });

            if (!res) {
                return null;
            }

            return res;
        }

        function checkContentChannels(key, arrayElements) {
            return lodash.include(arrayElements, key);
        }

        function isValidContentChannel() {
            if (!vm.admDetailsForm.checkContentChannels.display &&
                !vm.admDetailsForm.checkContentChannels.email &&
                !vm.admDetailsForm.checkContentChannels.site) {
                return true;
            }

            return false;
        }

        function validateNumeric(element) {
            switch (element) {
                case $scope.$parent.$parent.admDetails.admDetailsForm.fileFrequency.$name:
                    $scope.$parent.$parent.admDetails.admDetailsForm.fileFrequency.$setValidity(
                        'minValue', UtilsNumber.unmaskIntegerNumbers(
                            vm.admDetailsForm.updateFrequencyObject) >= vm.ADM_CONSTANTS.MIN);
                    break;

                case $scope.$parent.$parent.admDetails.admDetailsForm.matchCookie.$name:
                    $scope.$parent.$parent.admDetails.admDetailsForm.matchCookie.$setValidity(
                        'minValue', UtilsNumber.unmaskIntegerNumbers(
                            vm.admDetailsForm.cookieExpirationDays) >= vm.ADM_CONSTANTS.MIN);

                    $scope.$parent.$parent.admDetails.admDetailsForm.matchCookie.$setValidity(
                        'maxValue', UtilsNumber.unmaskIntegerNumbers(
                            vm.admDetailsForm.cookieExpirationDays) <= vm.ADM_CONSTANTS.MAX);
                    break;
            }
        }
    }
})();
