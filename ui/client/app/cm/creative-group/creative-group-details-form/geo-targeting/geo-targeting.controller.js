(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('GeoTargetingController', GeoTargetingController);

    GeoTargetingController.$inject = ['$scope', '$timeout', 'CONSTANTS', 'GeoTargetingService', 'lodash'];

    function GeoTargetingController($scope, $timeout, CONSTANTS, GeoTargetingService, lodash) {
        var vm = this,
            reconcileCountries = reconcileSelections('countries', 'model.geoCountry.values'),
            reconcileStates = reconcileSelections('states', 'model.geoState.values'),
            reconcileDMAs = reconcileSelections('dmas', 'model.geoDma.values'),
            reconcileZips = reconcileSelections('zips', 'model.geoZip.values');

        vm.GEO_TARGETING_PAGE_SIZE = CONSTANTS.CREATIVE_GROUP.GEO_TARGETING_PAGE_SIZE;
        vm.countries = null;
        vm.dmas = null;
        vm.getDMAs = getDMAs;
        vm.getStates = getStates;
        vm.getZips = getZips;
        vm.promise = null;
        vm.removeGeo = removeGeo;
        vm.setDirty = setDirty;
        vm.states = null;
        vm.visible = false;
        vm.zips = null;

        activate();

        function activate() {
            $scope.$watch('vm.model', function () {
                reconcileCountries();
                reconcileStates();
                reconcileDMAs();
                reconcileZips();
            });

            $scope.$watch('vm.visible', function (visible) {
                if (visible) {
                    getCountries();
                }
            });
        }

        function getCountries() {
            if (!vm.countries) {
                var promise = vm.promise = GeoTargetingService.getCountries();

                promise.then(function (data) {
                    vm.countries = data;
                    reconcileCountries();
                });
            }
        }

        function getStates() {
            if (!vm.states) {
                var promise = vm.promise = GeoTargetingService.getStates();

                promise.then(function (data) {
                    vm.states = data;
                    reconcileStates();
                });
            }
        }

        function getDMAs() {
            if (!vm.dmas) {
                var promise = vm.promise = GeoTargetingService.getDMAs();

                promise.then(function (data) {
                    vm.dmas = data;
                    reconcileDMAs();
                });
            }
        }

        function getZips() {
            if (!vm.zips) {
                var promise = vm.promise = GeoTargetingService.getZips();

                promise.then(function (data) {
                    vm.zips = data;
                    reconcileZips();
                });
            }
        }

        function removeGeo(evt, values, geo) {
            if (!angular.element(evt.currentTarget).closest('fieldset').is(':disabled')) {
                lodash.pull(values, geo);
                setDirty();
            }
        }

        function setDirty() {
            vm.$form.$setDirty();
        }

        function reconcileSelections(data, selection) {
            return function () {
                var $form = vm.$form,
                    mapped = lodash.indexBy(lodash.result(vm, data), 'id');

                lodash.set(vm, selection, lodash
                    .chain(lodash.result(vm, selection))
                    .map(function (value) {
                        return mapped[value.id] || value;
                    })
                    .compact()
                    .value());

                if ($form.$pristine) {
                    $timeout(lodash.bind($form.$setPristine, $form));
                }
            };
        }
    }
})();
