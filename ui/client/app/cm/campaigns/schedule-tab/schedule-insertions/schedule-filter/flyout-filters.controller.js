(function () {
    'use strict';

    angular
        .module('uiApp')
        .controller('FlyoutFiltersController', FlyoutFiltersController);

    FlyoutFiltersController.$inject = [
        '$q',
        '$scope',
        '$timeout',
        'CONSTANTS',
        'Utils',
        '$rootScope',
        'lodash'
    ];

    function FlyoutFiltersController($q,
                                     $scope,
                                     $timeout,
                                     CONSTANTS,
                                     Utils,
                                     $rootScope,
                                     lodash) {
        var parent = $scope.$parent.$parent.vm,
            vm = this;

        vm.SCHEDULE_LEVEL = CONSTANTS.SCHEDULE.LEVEL;
        vm.filterValues = $scope.$parent.$parent.$parent.vmList.filterValues;
        vm.filterOptions = lodash.clone($scope.$parent.$parent.$parent.vmList.filterOptions);
        vm.getModelFilter = getModelFilter;
        vm.actionFilter = [
            {
                onClose: filter
            }
        ];

        activate();

        function activate() {
            orderByPivot();
        }

        function orderByPivot() {
            switch (parent.selectedPivot) {
                case vm.SCHEDULE_LEVEL.SITE:
                    delete vm.filterOptions.SITE;
                    vm.filterOptions.PLACEMENT.order = 0;
                    vm.filterOptions.GROUP.order = 1;
                    vm.filterOptions.CREATIVE.order = 2;

                    switch (parent.selectedRowField) {
                        case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                            delete vm.filterOptions.PLACEMENT;
                            break;
                        case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                            delete vm.filterOptions.PLACEMENT;
                            delete vm.filterOptions.GROUP;
                            break;
                        case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                            delete vm.filterOptions.PLACEMENT;
                            delete vm.filterOptions.GROUP;
                            delete vm.filterOptions.CREATIVE;
                            break;
                    }

                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT:
                    delete vm.filterOptions.SITE;
                    delete vm.filterOptions.PLACEMENT;

                    vm.filterOptions.GROUP.order = 0;
                    vm.filterOptions.CREATIVE.order = 1;

                    switch (parent.selectedRowField) {
                        case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                            delete vm.filterOptions.GROUP;
                            break;
                        case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                            delete vm.filterOptions.GROUP;
                            delete vm.filterOptions.CREATIVE;
                            break;
                    }

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP:
                    vm.filterOptions.SITE.order = 0;
                    vm.filterOptions.PLACEMENT.order = 1;
                    vm.filterOptions.CREATIVE.order = 2;

                    delete vm.filterOptions.GROUP;
                    switch (parent.selectedRowField) {
                        case vm.SCHEDULE_LEVEL.SITE.KEY:
                            delete vm.filterOptions.SITE;
                            break;
                        case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                            delete vm.filterOptions.SITE;
                            delete vm.filterOptions.PLACEMENT;
                            break;
                        case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                            delete vm.filterOptions.SITE;
                            delete vm.filterOptions.PLACEMENT;
                            delete vm.filterOptions.CREATIVE;
                            break;
                    }

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE:
                    vm.filterOptions.SITE.order = 0;
                    vm.filterOptions.PLACEMENT.order = 1;
                    vm.filterOptions.GROUP.order = 2;

                    delete vm.filterOptions.CREATIVE;
                    switch (parent.selectedRowField) {
                        case vm.SCHEDULE_LEVEL.SITE.KEY:
                            delete vm.filterOptions.SITE;
                            break;
                        case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                            delete vm.filterOptions.SITE;
                            delete vm.filterOptions.PLACEMENT;
                            break;
                        case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                            delete vm.filterOptions.SITE;
                            delete vm.filterOptions.PLACEMENT;
                            delete vm.filterOptions.GROUP;
                            break;
                    }

                    break;
            }

            vm.listFilters = lodash.sortByAll(vm.filterOptions, 'order');
        }

        function filter(element, startFilter) {
            if (startFilter) {
                parent.promise = filterModel(element);
                parent.promise.then(function () {
                    if (!parent.bulkEditControlsCollapsed) {
                        parent.isCreativeVisible = vm.filterValues[2].values.length > 0 &&
                            vm.filterValues[3].values.length > 0;
                        parent.isCreativeGroupVisible = vm.filterValues[2].values.length > 0 &&
                            !parent.isCreativeVisible;
                    }
                });
            }
        }

        function filterModel(element) {
            var deferred = $q.defer();

            $timeout(function () {
                if (Utils.isUndefinedOrNull(parent.submodel)) {
                    deferred.reject();
                }
                else {
                    clearFilterOptions(element);
                    parent.modelWithoutFilter = getModelFilter(parent.modelWithoutFilter);
                    parent.refreshListCheck();
                    deferred.resolve();
                }
            });

            return deferred.promise;
        }

        function clearFilterOptions(element) {
            switch (parent.selectedPivot) {
                case vm.SCHEDULE_LEVEL.SITE:

                    if (vm.SCHEDULE_LEVEL.PLACEMENT.KEY === element) {
                        vm.filterOptions.GROUP.value = [];
                        vm.filterOptions.CREATIVE.value = [];
                    }

                    if (vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY === element) {
                        vm.filterOptions.CREATIVE.value = [];
                    }

                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT:

                    if (vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY === element) {
                        vm.filterOptions.CREATIVE.value = [];
                    }

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP:
                    if (vm.SCHEDULE_LEVEL.SITE.KEY === element) {
                        vm.filterOptions.PLACEMENT.value = [];
                        vm.filterOptions.CREATIVE.value = [];
                    }

                    if (vm.SCHEDULE_LEVEL.PLACEMENT.KEY === element) {
                        vm.filterOptions.CREATIVE.value = [];
                    }

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE:
                    if (vm.SCHEDULE_LEVEL.SITE.KEY === element) {
                        vm.filterOptions.PLACEMENT.value = [];
                        vm.filterOptions.GROUP.value = [];
                    }

                    if (vm.SCHEDULE_LEVEL.PLACEMENT.KEY === element) {
                        vm.filterOptions.GROUP.value = [];
                    }

                    break;
            }
        }

        function getModelFilter(model) {
            var currentFilterValues,
                index;

            if (!Utils.isUndefinedOrNull(model.backUpChildren) && model.backUpChildren.length > 0) {
                model.children.push.apply(model.children, model.backUpChildren);
                model.backUpChildren = [];
            }

            if (!Utils.isUndefinedOrNull(model.children) && model.children.length > 0) {
                currentFilterValues = lodash.filter(vm.filterValues, function (o) {
                    return o.fieldName === model.children[0].field;
                });

                index = model.children.length - 1;

                while (index >= 0) {
                    evaluateChildren(model, currentFilterValues, index);
                    index--;
                }
            }

            return model;
        }

        function evaluateChildren(model, currentFilterValues, index) {
            if (currentFilterValues.length > 0) {
                if (isFilter(currentFilterValues[0].values, model.children[index][currentFilterValues[0].key])) {
                    addOptionsToFilter(model.children[index], true);
                    getModelFilter(model.children[index]);
                }
                else {
                    addOptionsToFilter(model.children[index], false);
                    model.backUpChildren.push(model.children[index]);
                    model.children.splice(index, 1);
                }
            }
            else {
                getModelFilter(model.children[index]);
            }
        }

        function isFilter(values, itemToFilter) {
            return lodash.find(values, function (item) {
                return item === itemToFilter;
            });
        }

        function addOptionsToFilter(model, status) {
            switch (model.field) {
                case vm.SCHEDULE_LEVEL.SITE.KEY:
                    if (!Utils.isUndefinedOrNull(vm.filterOptions.SITE)) {
                        vm.filterOptions.SITE.value.push({
                            label: model.siteDetailLabel,
                            id: model.siteId,
                            enabled: status
                        });

                        vm.filterOptions.SITE.value = lodash
                            .uniq(vm.filterOptions.SITE.value, function (item) {
                                return item.id;
                            });

                        vm.filterOptions.SITE.value = lodash.sortBy(vm.filterOptions.SITE.value, 'label');
                    }

                    break;
                case vm.SCHEDULE_LEVEL.PLACEMENT.KEY:
                    if (!Utils.isUndefinedOrNull(vm.filterOptions.PLACEMENT)) {
                        vm.filterOptions.PLACEMENT.value.push({
                            label: model.placementLabel,
                            id: model.placementId
                        });

                        vm.filterOptions.PLACEMENT.value = lodash
                            .uniq(vm.filterOptions.PLACEMENT.value, function (item) {
                                return item.id;
                            });

                        vm.filterOptions.PLACEMENT.value = lodash.sortBy(vm.filterOptions.PLACEMENT.value, 'label');
                    }

                    break;
                case vm.SCHEDULE_LEVEL.CREATIVE_GROUP.KEY:
                    if (!Utils.isUndefinedOrNull(vm.filterOptions.GROUP)) {
                        vm.filterOptions.GROUP.value.push({
                            label: model.creativeGroupLabel,
                            id: model.creativeGroupId,
                            enabled: status
                        });

                        vm.filterOptions.GROUP.value = lodash
                            .uniq(vm.filterOptions.GROUP.value, function (item) {
                                return item.id;
                            });

                        vm.filterOptions.GROUP.value = lodash.sortBy(vm.filterOptions.GROUP.value, 'label');
                    }

                    break;
                case vm.SCHEDULE_LEVEL.SCHEDULE.KEY:
                    if (!Utils.isUndefinedOrNull(vm.filterOptions.CREATIVE)) {
                        vm.filterOptions.CREATIVE.value.push({
                            label: model.creativeLabel,
                            id: model.creativeId,
                            enabled: status
                        });

                        vm.filterOptions.CREATIVE.value = lodash
                            .uniq(vm.filterOptions.CREATIVE.value, function (item) {
                                return item.id;
                            });

                        vm.filterOptions.CREATIVE.value = lodash.sortBy(vm.filterOptions.CREATIVE.value, 'label');
                    }

                    break;
            }
        }

        $rootScope.$on('schedule.flyout.filter', function (event, field) {
            if (!parent.filterControlsCollapsed) {
                parent.modelWithoutFilter = lodash.clone(field.model);
                filter(field.field, true);
            }
        });
    }
})();
