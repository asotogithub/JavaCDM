(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTreeGridChecked', TeTreeGridCheckedDirective);

    TeTreeGridCheckedDirective.$inject = ['$parse'];

    function TeTreeGridCheckedDirective($parse) {
        return {
            require: 'teTreeGrid',
            restrict: 'A',

            compile: function (element, attrs) {
                var CHECKED = 'checkRow',
                    UNCHECKED = 'uncheckRow',
                    fn = $parse(attrs.teTreeGridChecked, null, true),
                    changeCheckedStatus = function (evt, scope, _element, _attrs, controller, status) {
                        var byHierarchy = _attrs.selectionHierarchy,
                            args = evt.args,
                            row = args.row;

                        if (byHierarchy) {
                            _attrs.selectionHierarchy = false;
                            $(_element).find('.te-tree-grid-delegate').jqxTreeGrid('beginUpdate');

                            if (status === CHECKED) {
                                removeChildrenFromCheckedList(row, controller);
                            }
                            else if (status === UNCHECKED) {
                                controller.removeLeafNodes(row);
                                controller.unsetDataChecked(row);
                            }

                            checkUncheckChildren(row, _element, status, controller);
                            checkUncheckParent(row, _element, status, controller);

                            scope.$apply(function () {
                                fn(scope, {
                                    $allChecked: controller.getAllCheckedData()
                                });
                            });

                            $(_element).find('.te-tree-grid-delegate').jqxTreeGrid('endUpdate');
                            _attrs.selectionHierarchy = true;
                        }
                        else {
                            scope.$apply(function () {
                                fn(scope, {
                                    $allChecked: controller.getAllCheckedData()
                                });
                            });
                        }
                    };

                function checkUncheckChildren(node, _element, status, controller) {
                    var children,
                        length;

                    if (node.children && node.children.length > 0) {
                        children = node.children;
                        length = children.length - 1;
                        while (length > -1) {
                            $(_element).find('.te-tree-grid-delegate')
                                .first().jqxTreeGrid(status, children[length].$$uuid);
                            checkUncheckChildren(children[length], _element, status, controller);
                            length--;
                        }
                    }
                }

                function checkUncheckParent(node, _element, status, controller) {
                    var parent;

                    if (node.parent !== null) {
                        parent = node.parent;
                        while (parent) {
                            checkStatus(status, _element, parent, controller);
                            parent = parent.parent;
                        }
                    }
                }

                function checkStatus(status, _element, parent, controller) {
                    var checkedCount,
                        length;

                    if (status === UNCHECKED) {
                        $(_element).find('.te-tree-grid-delegate')
                            .first().jqxTreeGrid(status, parent.$$uuid);
                        addChildrenToCheckedList(parent, controller);
                    }
                    else {
                        checkedCount = 0;
                        length = parent.children.length - 1;
                        while (length > -1) {
                            checkedCount = parent.children[length].checked === true ? ++checkedCount : checkedCount;
                            length--;
                        }

                        if (parent.children.length === checkedCount) {
                            $(_element).find('.te-tree-grid-delegate')
                                .first().jqxTreeGrid(status, parent.$$uuid);
                            removeChildrenFromCheckedList(parent, controller);
                        }
                    }
                }

                function removeChildrenFromCheckedList(node, controller) {
                    removeChildrenRecursive(node, controller);
                    controller.setDataChecked(node);
                }

                function removeChildrenRecursive(node, controller) {
                    if (node.children) {
                        var length = node.children.length - 1;

                        while (length > -1) {
                            controller.unsetDataChecked(node.children[length]);
                            removeChildrenRecursive(node.children[length], controller);
                            length--;
                        }
                    }
                }

                function addChildrenToCheckedList(node, controller) {
                    var length = node.children.length - 1;

                    controller.unsetDataChecked(node);
                    while (length > -1) {
                        if (node.children[length].checked) {
                            controller.setDataChecked(node.children[length]);
                        }

                        length--;
                    }
                }

                return function (scope, _element, _attrs, controller) {
                    _element.on('rowCheck', function (evt) {
                        changeCheckedStatus(evt, scope, _element, _attrs, controller, CHECKED);
                    });

                    _element.on('rowUncheck', function (evt) {
                        changeCheckedStatus(evt, scope, _element, _attrs, controller, UNCHECKED);
                    });
                };
            }
        };
    }
})();
