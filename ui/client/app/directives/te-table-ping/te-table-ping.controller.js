(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('TeTablePingController', TeTablePingController);

    TeTablePingController.$inject = [
        '$filter',
        '$scope',
        'CONSTANTS',
        'Utils',
        'lodash'
    ];

    function TeTablePingController(
        $filter,
        $scope,
        CONSTANTS,
        Utils,
        lodash
        ) {
        var vm = this;

        vm.CONTAINER_STATE = CONSTANTS.FLY_OUT.STATE;
        vm.activate = activate;
        vm.clearSearch = clearSearch;
        vm.data = [];
        vm.deletePing = deletePing;
        vm.isSearchCollapsed = Utils.isUndefinedOrNull(vm.collapseSearch) ? false : vm.collapseSearch;
        vm.isSearchDisabled = isSearchDisabled;
        vm.toggleSearchCollapse = toggleSearchCollapse;
        vm.toggleSearchField = toggleSearchField;
        vm.savePing = savePing;
        vm.searchFields = vm.teTableCustomSearchEnabled ? vm.searchFields : null;
        vm.hasPings = false;

        vm.getData = getData;

        function activate() {
            vm.hasPings = Utils.isUndefinedOrNull(vm.model) ? false : vm.model.length > 0;

            watch('searchTerm', search);

            watch('model', function (newValue, oldValue) {
                if (newValue.length > oldValue.length) {
                    vm.searchTerm = null;
                    vm.hasPings = true;
                }
            });
        }

        function isSearchDisabled() {
            return vm.model && !vm.model.length || vm.editModeEnabled;
        }

        function deletePing(pingId) {
            vm.onDeletePing(
                {
                    pingId: pingId
                }
            );
        }

        function savePing(pingId) {
            return vm.onSavePing({
                pingId: pingId
            });
        }

        function toggleSearchCollapse() {
            vm.isSearchCollapsed = !vm.isSearchCollapsed;
        }

        function watch(property, listener) {
            $scope.$watch(function () {
                return vm[property];
            }, listener, true);
        }

        //SEARCH INPUT
        function search() {
            getData();
        }

        function getData() {
            if (Utils.isUndefinedOrNull(vm.searchTerm) || !vm.searchTerm.trim()) {
                return vm.model;
            }
            else {
                if (vm.searchTerm.trim()) {
                    return $filter('gridFilter')(
                        $filter('valuesFilter')(vm.model || [], vm.filterValues),
                        {
                            searchVal: vm.searchTerm,
                            searchFields: lodash.chain(vm.searchFields)
                                .filter({
                                    enabled: true
                                })
                                .pluck('field')
                                .value()
                        });
                }
            }
        }

        //CLEAR SEARCH INPUT
        function clearSearch() {
            vm.searchTerm = null;
        }

        //SEARCH FIELDS
        function toggleSearchField(evt, searchField) {
            evt.preventDefault();
            evt.stopImmediatePropagation();

            if (!searchField.enabled ||
                lodash.where(vm.searchFields, {
                    enabled: true
                }).length > 1) {
                searchField.enabled = !searchField.enabled;
            }

            search();
        }
    }
})();
