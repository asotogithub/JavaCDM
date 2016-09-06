(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('dropdownMulticheckbox', DropdownMulticheckbox);

    DropdownMulticheckbox.$inject = [
        '$document',
        '$filter',
        '$translate',
        'Utils',
        'lodash'
    ];

    function DropdownMulticheckbox(
        $document,
        $filter,
        $translate,
        Utils,
        lodash) {
        return {
            restrict: 'E',
            scope: {
                inputModel: '=',
                outputModel: '=',
                title: '=',
                messages: '=',
                events: '=',
                displayField: '=',
                idField: '=',
                resetSearchOnExpand: '=',
                nameElement: '=?'
            },
            templateUrl: 'app/directives/dropdown-multicheckbox/dropdown-multicheckbox.html',
            replace: true,
            link: function ($scope, $element) {
                var $dropdownTrigger = $element.children()[0],
                    changeSelection = false;

                $scope.toggleDropdown = function () {
                    $scope.open = !$scope.open;
                    if ($scope.resetSearchOnExpand === true) {
                        $scope.searchFilter = undefined;
                    }
                };

                $scope.clearSearch = function ($event) {
                    $scope.searchFilter = undefined;
                    $event.stopImmediatePropagation();
                };

                $scope.checkboxClick = function ($event, row) {
                    $scope.setSelectedItem(row);
                    $event.stopImmediatePropagation();
                };

                $scope.externalEvents = {
                    onDeselectAll: angular.noop,
                    onInitDone: angular.noop,
                    onItemSelect: angular.noop,
                    onItemDeselect: angular.noop,
                    onSelectAll: angular.noop,
                    onItemAction: angular.noop,
                    onOpen: angular.noop,
                    onClose: angular.noop
                };

                $scope.texts = {
                    allSelected: $translate.instant('global.all'),
                    buttonDefaultText: $translate.instant('global.select'),
                    checkAll: $translate.instant('global.selectAll'),
                    searchPlaceholder: $translate.instant('global.searchEllipsis'),
                    uncheckAll: $translate.instant('global.clearSelected')
                };

                if ($scope.displayField && $scope.idField) {
                    $scope.display = $scope.displayField;
                    $scope.id = $scope.idField;
                    $scope.inputIsObject = true;
                }
                else {
                    $scope.display = null;
                    $scope.id = null;
                    $scope.inputIsObject = false;
                }

                angular.extend($scope.externalEvents, $scope.events || []);
                angular.extend($scope.externalEvents, $scope.events || []);
                angular.extend($scope.texts, $scope.messages);
                angular.extend($scope.texts, {
                    buttonDefaultText: $scope.title
                });

                $document.on('click', function (e) {
                    var target = e.target.parentElement,
                        parentFound = false;

                    while (angular.isDefined(target) && target !== null && !parentFound) {
                        if (lodash.contains(target.className.split(' '), 'multiselect-parent') && !parentFound &&
                            target === $dropdownTrigger.parentElement) {
                            parentFound = true;
                        }

                        target = target.parentElement;
                    }

                    if (!parentFound) {
                        $scope.$apply(function () {
                            $scope.open = false;
                        });
                    }
                });

                $scope.getButtonText = function () {
                    var totalSelected;

                    totalSelected = angular.isDefined($scope.outputModel) ? $scope.outputModel.length : 0;
                    if (totalSelected === $scope.inputModel.length && totalSelected > 0) {
                        return $scope.texts.buttonDefaultText + ': ' +
                            $scope.texts.allSelected + ' (' + totalSelected + ')';
                    }
                    else {
                        if ($scope.inputIsObject === true) {
                            totalSelected = lodash.sum($scope.inputModel, function (item) {
                                return item.enabled;
                            });
                        }

                        totalSelected = $scope.inputModel.length < 1 ? '' : totalSelected;
                        return $scope.texts.buttonDefaultText + ': ' + totalSelected;
                    }
                };

                $scope.getPropertyForObject = function (object, property) {
                    if ($scope.inputIsObject && property) {
                        if (angular.isDefined(object) && object.hasOwnProperty(property)) {
                            return object[property];
                        }
                    }
                    else {
                        if (angular.isDefined(object)) {
                            return object;
                        }
                    }

                    return '';
                };

                $scope.selectAll = function () {
                    var auxFilterExpression,
                        auxFilteredModel = lodash.clone($scope.inputModel),
                        auxOutputModel = lodash.clone($scope.outputModel);

                    changeSelection = true;
                    $scope.deselectAll(true, false);

                    if (!Utils.isUndefinedOrNull($scope.searchFilter) && $scope.searchFilter !== '') {
                        if ($scope.inputIsObject === true) {
                            auxFilterExpression = {};
                            auxFilterExpression[$scope.display] = $scope.searchFilter;
                            auxFilteredModel = $filter('filter')($scope.inputModel, auxFilterExpression);
                        }
                        else {
                            auxFilteredModel = $filter('filter')($scope.inputModel, $scope.searchFilter);
                        }
                    }

                    angular.forEach(auxFilteredModel, function (value) {
                        if ($scope.inputIsObject === true) {
                            $scope.setSelectedItem(value, true);
                        }
                        else {
                            $scope.setSelectedItem(value, true);
                        }
                    });

                    $scope.externalEvents.onSelectAll(auxOutputModel);
                };

                $scope.deselectAll = function (isSelectAll, clearSelection) {
                    var auxOutputModel = lodash.clone($scope.outputModel),
                        isPartialDeselect = false;

                    changeSelection = true;
                    if (!Utils.isUndefinedOrNull($scope.searchFilter) && $scope.searchFilter !== '') {
                        isPartialDeselect = true;
                        if ($scope.inputIsObject === true) {
                            angular.forEach($scope.inputModel, function (element) {
                                if (element[$scope.display].toString().toLowerCase()
                                        .indexOf($scope.searchFilter.toString().toLowerCase()) !== -1) {
                                    element.enabled = false;
                                    lodash.remove($scope.outputModel, function (ouputElement) {
                                        return ouputElement === element[$scope.id];
                                    });
                                }
                            });
                        }
                        else {
                            lodash.remove($scope.outputModel, function (value) {
                                return value.toString().toLowerCase()
                                        .indexOf($scope.searchFilter.toString().toLowerCase()) !== -1;
                            });
                        }
                    }
                    else {
                        $scope.outputModel.splice(0, $scope.outputModel.length);
                        if ($scope.inputIsObject && Utils.isUndefinedOrNull(clearSelection)) {
                            $scope.updateStatus($scope.inputModel, false);
                        }
                    }

                    $scope.externalEvents.onDeselectAll(isSelectAll, auxOutputModel, isPartialDeselect);
                };

                $scope.updateStatus = function (model, status) {
                    for (var ind = 0; ind < model.length; ++ind) {
                        model[ind].enabled = status;
                    }
                };

                $scope.setSelectedItem = function (row, isSelectAll) {
                    var id = $scope.getPropertyForObject(row, $scope.idField),
                        exists = $scope.outputModel.indexOf(id) !== -1,
                        auxOutputModel = lodash.clone($scope.outputModel);

                    changeSelection = true;
                    if (exists) {
                        $scope.outputModel.splice($scope.outputModel.indexOf(id), 1);
                        if ($scope.inputIsObject === true) {
                            row.enabled = false;
                        }

                        $scope.externalEvents.onItemDeselect(id);
                    }
                    else {
                        if ($scope.inputIsObject === true) {
                            row.enabled = true;
                        }

                        $scope.outputModel.push(id);
                        $scope.externalEvents.onItemSelect(id);
                    }

                    $scope.externalEvents.onItemAction(id, isSelectAll, auxOutputModel);
                };

                $scope.isChecked = function (id) {
                    return $scope.outputModel.indexOf(id) !== -1;
                };

                $scope.$watch('open', function (newValue, oldValue) {
                    if (!Utils.isUndefinedOrNull(newValue)) {
                        if (newValue) {
                            changeSelection = false;
                            $scope.externalEvents.onOpen();
                        }

                        if (!newValue && oldValue) {
                            $scope.externalEvents.onClose($scope.nameElement, changeSelection);
                        }
                    }
                });

                $scope.externalEvents.onInitDone();
            }
        };
    }
})();
