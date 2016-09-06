(function () {
    'use strict';

    angular
        .module('te.components')
        .directive('teTreeGridSelect', TeTreeGridSelectDirective);

    TeTreeGridSelectDirective.$inject = ['$parse'];

    function TeTreeGridSelectDirective($parse) {
        return {
            require: 'teTreeGrid',
            restrict: 'A',

            compile: function (element, attrs) {
                var fn = $parse(attrs.teTreeGridSelect, null, true);

                return function (scope, _element, _attrs, controller) {
                    _element.on('rowSelect', function (evt) {
                        var row = evt.args.row,
                            id = row[controller.idField],
                            domEl,
                            coords = {};

                        if (id) {
                            domEl = angular.element('*[' + 'data-key' + ' = \'' + id + '\']');
                            if (domEl && domEl.length && domEl.length !== 0) {
                                coords.offsetTop = domEl.offset().top;
                                coords.offsetLeft = domEl.offset().left;
                                coords.top = domEl.position().top;
                                coords.left = domEl.position().left;
                                coords.scrollTop = domEl.scrollTop();
                                coords.scrollLeft = domEl.scrollLeft();
                                coords.height = domEl.height();
                                coords.width = domEl.width();
                            }

                            scope.$apply(function () {
                                fn(scope, {
                                    $selection: controller.getRecord(id),
                                    $level: row.level,
                                    $coords: coords
                                });
                            });
                        }
                    });
                };
            }
        };
    }
})();
