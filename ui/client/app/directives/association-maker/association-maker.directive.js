(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('associationMaker', AssociationMaker);

    AssociationMaker.$inject = [
        'lodash'
    ];

    function AssociationMaker(
        lodash) {
        return {
            restrict: 'E',
            scope: {
                inputModel: '=',
                inputMap: '=',
                outputModel: '=',
                outputMap: '=',
                filterOptions: '=',
                emptyInputMsg: '=',
                emptyOutputMsg: '=',
                placeHolderMsg: '=',
                templateLeftTable: '=',
                templateRightTable: '=',
                onAdd: '&',
                onRemove: '&',
                externalController: '='
            },
            templateUrl: 'app/directives/association-maker/association-maker.html',
            replace: true,
            link: function (scope) {
                scope.allowAdd = false;
                scope.allowAddAll = false;
                scope.allowRemove = false;
                scope.allowRemoveAll = false;
                scope.defaultPagerSize = 5;
                scope.tmpInputModelFiltered = [];
                scope.tmpOutputModelFiltered = [];
                scope.objectSelected = [];
                scope.objectSelectedOut = [];

                scope.$watch('inputModel', function (newValue) {
                    if (newValue) {
                        scope.updateInputModel(newValue);
                    }
                }, true);

                scope.$watch('outputModel', function (newValue) {
                    if (newValue) {
                        scope.updateOutputModel(newValue);
                    }
                }, true);

                scope.updateInputModel = function (model) {
                    scope.allowAdd = verifyIfAnySelected(scope.inputModel);
                    scope.allowAddAll = model.length > 0;
                };

                scope.updateOutputModel = function (model) {
                    scope.allowRemove = verifyIfAnySelected(scope.outputModel);
                    scope.allowRemoveAll = model.length > 0;
                };

                scope.addAssociation = function () {
                    scope.outputModel.push.apply(scope.outputModel, scope.objectSelected);
                    refreshOutputTable();
                    scope.inputModel = lodash.xor(scope.inputModel, scope.objectSelected);

                    angular.forEach(scope.outputModel, function (item) {
                        item.$selected = false;
                    });

                    if (scope.onAdd) {
                        scope.onAdd({
                            addedItems: scope.objectSelected
                        });
                    }
                };

                scope.addAllAssociation = function () {
                    scope.outputModel.push.apply(scope.outputModel, scope.tmpInputModelFiltered);
                    refreshOutputTable();
                    removeFilteredFromInputModel();

                    if (scope.onAdd) {
                        scope.onAdd({
                            addedItems: scope.tmpInputModelFiltered
                        });
                    }

                    scope.tmpInputModelFiltered = [];
                };

                scope.removeAssociation = function () {
                    scope.inputModel.push.apply(scope.inputModel, scope.objectSelectedOut);
                    refreshInputTable();
                    scope.outputModel = lodash.xor(scope.outputModel, scope.objectSelectedOut);

                    angular.forEach(scope.inputModel, function (item) {
                        item.$selected = false;
                    });

                    if (scope.onRemove) {
                        scope.onRemove({
                            removedItems: scope.objectSelected
                        });
                    }
                };

                scope.removeAllAssociation = function () {
                    scope.inputModel.push.apply(scope.inputModel, scope.tmpOutputModelFiltered);
                    refreshInputTable();
                    removeFilteredFromOutputModel();

                    if (scope.onRemove) {
                        scope.onRemove({
                            removedItems: scope.tmpOutputModelFiltered
                        });
                    }

                    scope.tmpOutputModelFiltered = [];
                };

                scope.makeSelectionInputModel = function (rows) {
                    scope.objectSelected = rows;
                    if (rows.length === 0) {
                        angular.forEach(scope.inputModel, function (model) {
                            model.$selected = false;
                        });

                        scope.allowAdd = false;
                    }
                    else {
                        for (var sel = 0; sel < rows.length; sel++) {
                            rows[sel].$selected = true;
                            scope.allowAdd = rows[sel].$selected || verifyIfAnySelected(scope.inputModel);
                        }
                    }
                };

                scope.filteredDataInputModel = function (rows) {
                    angular.copy(rows, scope.tmpInputModelFiltered);
                };

                scope.makeSelectionOutputModel = function (rows) {
                    scope.objectSelectedOut = rows;

                    if (rows.length === 0) {
                        angular.forEach(scope.outputModel, function (model) {
                            model.$selected = false;
                        });

                        scope.allowRemove = false;
                    }
                    else {
                        for (var sel = 0; sel < rows.length; sel++) {
                            rows[sel].$selected = true;
                            scope.allowRemove = rows[sel].$selected || verifyIfAnySelected(scope.outputModel);
                        }
                    }
                };

                scope.filteredDataOutputModel = function (rows) {
                    angular.copy(rows, scope.tmpOutputModelFiltered);
                };

                function verifyIfAnySelected(array) {
                    for (var i = 0; i < array.length; i++) {
                        if (array[i].$selected) {
                            return true;
                        }
                    }

                    return false;
                }

                function refreshOutputTable() {
                    var outputModelBackup = [];

                    angular.copy(scope.outputModel, outputModelBackup);
                    scope.outputModel = [];
                    angular.copy(outputModelBackup, scope.outputModel);
                }

                function refreshInputTable() {
                    var inputModelBackup = [];

                    angular.copy(scope.inputModel, inputModelBackup);
                    scope.inputModel = [];
                    angular.copy(inputModelBackup, scope.inputModel);
                }

                function removeFilteredFromInputModel() {
                    angular.forEach(scope.tmpInputModelFiltered, function (tmpValue) {
                        scope.inputModel = lodash.filter(scope.inputModel, function (item) {
                            return item.id !== tmpValue.id;
                        });
                    });
                }

                function removeFilteredFromOutputModel() {
                    angular.forEach(scope.tmpOutputModelFiltered, function (tmpValue) {
                        scope.outputModel = lodash.filter(scope.outputModel, function (item) {
                            return item.id !== tmpValue.id;
                        });
                    });
                }
            }
        };
    }
})();
