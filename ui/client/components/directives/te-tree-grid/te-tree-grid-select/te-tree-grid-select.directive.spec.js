'use strict';

describe('Directive: teTreeGridSelect', function () {
    var $scope,
        element;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-tree-grid/te-tree-grid.html'));

    beforeEach(inject(function ($rootScope) {
        $scope = $rootScope.$new();
    }));

    it('should invoke expression on `rowSelect` event', inject(function ($compile) {
        var
            foo = {
                id: 1,
                name: 'foo'
            },
            bar = {
                id: 2,
                name: 'bar',
                children: [foo]
            },
            foobar = {
                id: 3,
                name: 'foobar'
            };

        _.extend($scope, {
            onSelect: jasmine.createSpy('onSelect'),
            model: [bar, foobar]
        });
        element = $compile(angular.element(
            '<te-tree-grid data-model="model"' +
            '              data-te-tree-grid-select="onSelect($selection, $level)">' +
            '  <te-columns>' +
            '    <te-column data-field="name"></te-column>' +
            '  </te-columns>' +
            '</te-tree-grid>'
        ))($scope);
        $scope.$apply();

        element.trigger(_.extend(new $.Event('rowSelect'), {
            args: {
                row: {
                    $$uuid: foo.$$uuid,
                    level: 1
                }
            }
        }));
        expect($scope.onSelect).toHaveBeenCalledWith(foo, 1);

        element.trigger(_.extend(new $.Event('rowSelect'), {
            args: {
                row: {
                    $$uuid: bar.$$uuid,
                    level: 0
                }
            }
        }));
        expect($scope.onSelect).toHaveBeenCalledWith(bar, 0);

        element.trigger(_.extend(new $.Event('rowSelect'), {
            args: {
                row: {
                    $$uuid: foobar.$$uuid,
                    level: 0
                }
            }
        }));
        expect($scope.onSelect).toHaveBeenCalledWith(foobar, 0);
    }));
});
