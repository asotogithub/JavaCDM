'use strict';

describe('Directive: teTreeGridRowCollapse', function () {
    var $scope,
        element;

    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-tree-grid/te-tree-grid.html'));

    beforeEach(inject(function ($rootScope) {
        $scope = $rootScope.$new();
    }));

    it('should invoke expression on `rowCollapse` event', inject(function ($compile) {
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
            onRowCollapse: jasmine.createSpy('onRowCollapse'),
            model: [bar, foobar]
        });
        element = $compile(angular.element(
            '<te-tree-grid data-model="model"' +
            '              data-te-tree-grid-row-collapse="onRowCollapse($row)">' +
            '  <te-columns>' +
            '    <te-column data-field="name"></te-column>' +
            '  </te-columns>' +
            '</te-tree-grid>'
        ))($scope);
        $scope.$apply();

        element.trigger(_.extend(new $.Event('rowCollapse'), {
            args: {
                row: {
                    $$uuid: foo.$$uuid
                }
            }
        }));
        expect($scope.onRowCollapse).toHaveBeenCalledWith(foo);

        element.trigger(_.extend(new $.Event('rowCollapse'), {
            args: {
                row: {
                    $$uuid: bar.$$uuid
                }
            }
        }));
        expect($scope.onRowCollapse).toHaveBeenCalledWith(bar);

        element.trigger(_.extend(new $.Event('rowCollapse'), {
            args: {
                row: {
                    $$uuid: foobar.$$uuid
                }
            }
        }));
        expect($scope.onRowCollapse).toHaveBeenCalledWith(foobar);
    }));
});
