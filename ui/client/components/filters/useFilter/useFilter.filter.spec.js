(function () {
    'use strict';

    describe('Filter: useFilter', function () {
        // load the filter's module
        beforeEach(module('te.components'));

        // initialize a new instance of the filter before each test
        var useFilter;

        beforeEach(inject(function ($filter) {
            useFilter = $filter('useFilter');
        }));

        it('should format a number with commas and no decimal places:"', function () {
            expect(useFilter('1234', 'number')).toBe('1,234');
        });

        it('should format a percentage with decimal places:"', function () {
            expect(useFilter('.2551', 'percentage')).toBe('25.51%');
        });

        it('should format currency with commas, decimal places and currency symbol:"', function () {
            expect(useFilter('1000000.25', 'currency')).toBe('$1,000,000.25');
        });
    });
})();
