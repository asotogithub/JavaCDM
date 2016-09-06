(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('PingBoxController', PingBoxController);

    PingBoxController.$inject = [
        'Utils',
        'lodash'
    ];

    function PingBoxController(
        Utils,
        lodash) {
        var vm = this;

        vm.deletePing = deletePing;
        vm.editPing = editPing;
        vm.enterContainer = enterContainer;
        vm.exitContainer = exitContainer;
        vm.hasFocus = false;
        vm.onBlur = onBlur;
        vm.onSiteChange = onSiteChange;
        vm.onTagTypeChange = onTagTypeChange;
        vm.pingContentPattern = '';
        vm.publisherPattern = '';
        vm.savePing = savePing;
        vm.shouldDisableSiteDropdown = shouldDisableSiteDropdown;

        activate();

        function activate() {
            vm.LENGTH = vm.model.maxLength;
            vm.PING_CARD_TYPES = vm.model.pingCardTypes;
            vm.disableEditMode = vm.model.editMode ? false : true;
            vm.pingTitle = vm.model.pingTypeField;

            vm.tagTypeList = vm.model.pingTagTypeList;

            if (angular.isDefined(vm.model.pingTagType)) {
                vm.selectedTagType = {
                    key: vm.model.pingTagType
                };

                getContentPattern();
            }

            vm.siteList = Utils.isUndefinedOrNull(vm.model.siteList) ? [] : vm.model.siteList;

            if (vm.model.pingId) {
                selectSite();
            }
        }

        function deletePing() {
            vm.onDeletePing({
                pingId: vm.model.pingId
            });
        }

        function editPing() {
            if (!vm.editModeEnabled) {
                vm.disableEditMode = false;
                vm.editModeEnabled = true;
            }
        }

        function enterContainer() {
            vm.hasFocus = true;
        }

        function exitContainer() {
            vm.hasFocus = false;
        }

        function getContentPattern() {
            vm.pingContentPattern = vm.model.pingPatternList[vm.selectedTagType.key];
        }

        function onBlur() {
            if (!vm.hasFocus) {
                savePing();
            }
        }

        function onSiteChange() {
            vm.model.siteId = vm.selectedSite.id;
            vm.model.siteName = vm.selectedSite.name;
        }

        function onTagTypeChange() {
            vm.model.pingTagType = vm.selectedTagType.key;
            vm.model.pingTagTypeField = vm.selectedTagType.name;
            getContentPattern();
            vm.pingBoxForm.pingContent.$dirty = true;
        }

        function selectSite() {
            var result = lodash.find(vm.siteList, function (value) {
                return value.id === vm.model.siteId;
            });

            if (Utils.isUndefinedOrNull(result)) {
                vm.siteList.push({
                    id: vm.model.siteId,
                    name: vm.model.siteName
                });
            }

            vm.selectedSite = {
                id: vm.model.siteId
            };
        }

        function savePing() {
            validate();
            if (vm.pingBoxForm.$valid) {
                vm.onSavePing({
                    pingId: vm.model.pingId
                }).then(function () {
                    vm.disableEditMode = true;
                    vm.editModeEnabled = false;
                });
            }
        }

        function shouldDisableSiteDropdown() {
            return vm.disableEditMode || vm.model.pingId;
        }

        function validate() {
            vm.pingBoxForm.siteSelectionName.$dirty = true;
            vm.pingBoxForm.tagTypeSelectionName.$dirty = true;
            vm.pingBoxForm.pingContent.$dirty = true;
        }
    }
})();
