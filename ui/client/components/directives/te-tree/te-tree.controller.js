(function () {
    'use strict';

    angular
        .module('te.components')
        .controller('TeTreeController', TeTreeController);

    TeTreeController.$inject = [
        '$scope',
        'Utils',
        'lodash'
    ];

    function TeTreeController(
        $scope,
        Utils,
        lodash) {
        var vm = this,
            SEARCH_INPUT_MAX_LENGTH = 250,
            KEY_CODE_ENTER = 13;

        vm.activate = activate;
        vm.applySearch = applySearch;
        vm.clearSearch = clearSearch;
        vm.onKeyPress = onKeyPress;
        vm.onOut = onOut;
        vm.onOver = onOver;
        vm.toggleSearchField = toggleSearchField;
        vm.searchInputMaxLength = SEARCH_INPUT_MAX_LENGTH;
        vm.selectedNode = selectedNode;

        function activate() {
        }

        function applySearch() {
            if (vm.customSearchEnabled) {
                vm.customSearchTerm = vm.searchTerm;
            }

            filterChildren(vm.model);
        }

        function filterChildren(element) {
            var hasFilterVisibleChildren = false;

            angular.forEach(element, function (children) {
                if (children.name) {
                    if (Utils.isUndefinedOrNull(children.isFilterVisible) || children.isFilterVisible === true) {
                        children.isFilterVisible = filterChildren(children.children) ||
                            !(vm.searchTerm && vm.searchTerm.length > 0 &&
                            children.name.search(new RegExp(vm.searchTerm, 'i')) === -1) || vm.customSearchEnabled;
                    }
                }
                else {
                    children.isFilterVisible = filterChildren(children.children);
                }

                hasFilterVisibleChildren = hasFilterVisibleChildren || children.isFilterVisible;
            });

            return hasFilterVisibleChildren;
        }

        function clearSearch() {
            vm.searchTerm = null;
            applySearch();
        }

        function onKeyPress($event) {
            if (vm.customSearchEnabled && $event.keyCode === KEY_CODE_ENTER &&
                !Utils.isUndefinedOrNull(vm.searchTerm) && vm.searchTerm !== '') {
                vm.onCustomSearch(vm.searchTerm);
            }
        }

        function onOut() {
            vm.itemOverRowUuid = undefined;
            $scope.$apply();
        }

        function onOver(event, ui, node) {
            vm.itemOverRowUuid = node.uuidRow;
            $scope.$apply();
        }

        function toggleSearchField(evt, searchField) {
            if (!Utils.isUndefinedOrNull(evt)) {
                evt.preventDefault();
                evt.stopImmediatePropagation();
            }

            if (!searchField.enabled ||
                lodash.where(vm.searchFields, {
                    enabled: true
                }).length > 1) {
                searchField.enabled = !searchField.enabled;
            }
        }

        function selectedNode(node) {
            vm.rowSelectedUuid = node.uuidRow;
            vm.itemOverRowUuid = undefined;
        }

        $scope.$watch('vm.model', function () {
            applySearch();
        });
    }
})();
