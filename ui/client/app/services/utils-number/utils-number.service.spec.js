'use strict';

describe('Service: UtilsNumber', function () {
    var utilsNumber;

    beforeEach(module('uiApp'));

    beforeEach(
        inject(function (UtilsNumber) {
            utilsNumber = UtilsNumber;
        })

    );

    it('should get a unmasked number.', function () {
        var result,
            actual = '123,456,789,012',
            expected = 123456789012;

        result = utilsNumber.unmaskIntegerNumbers(actual);

        expect(result).toEqual(expected);
    });
});
