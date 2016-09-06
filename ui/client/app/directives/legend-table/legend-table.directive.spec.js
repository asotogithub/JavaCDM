'use strict';

describe('Directive: legendTable', function () {
    var $compile,
        $translate,
        DATA = {},
        element,
        scope,
        translations = {
            description: '{{value}} Creative Groups'
        };

    beforeEach(module('uiApp'));

    beforeEach(module('app/directives/legend-table/legend-table.html'));

    beforeEach(module(function ($translateProvider) {
        $translateProvider
            .translations('en', translations)
            .preferredLanguage('en');
    }));

    beforeEach(inject(function ($rootScope, $state, _$translate_, _$compile_) {
        $translate = _$translate_;

        DATA.counterRows = $translate.instant('description', {
            value: '5'
        });

        $compile = _$compile_;
        scope = _.extend($rootScope.$new(), {
            vm: DATA
        });
        spyOn($state, 'go');
    }));

    describe('table', function () {
        it('should render label counter ', function () {
            element = $compile(
                angular.element('<legend-table data-legend="vm.counterRows"></legend-table>'
                )
            )(scope);
            scope.$apply();
            expect(element.find('label').text()).toEqual(DATA.counterRows);
        });
    });
});
