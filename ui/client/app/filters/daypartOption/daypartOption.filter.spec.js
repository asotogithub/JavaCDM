'use strict';

describe('Filter: daypartOption', function () {
    var daypartOption;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($filter) {
        daypartOption = $filter('daypartOption');
    }));

    it('should return formatted input if properly parsed', function () {
        var input =
            '"([browserlocalday] = \'mon\' And [browserlocaltime] >= \'0800\' And [browserlocaltime] < \'1700\')';

        expect(daypartOption(input)).toEqual('Mon, 08:00am \u2014 05:00pm');
    });

    it('should return input if not properly parsed', function () {
        expect(daypartOption('foobar')).toEqual('foobar');
    });
});
