'use strict';

describe('Service: ErrorHandlingService', function () {
    var errorHandlingService;

    beforeEach(module('uiApp'));

    beforeEach(
        inject(function (ErrorHandlingService) {
            errorHandlingService = ErrorHandlingService;
        })

    );

    it('should get a single message as string', function () {
        var errors, result;

        errors = [
            {
                message: 'Some error here'
            }, {
                message: 'Some other error.'
            }
        ];

        result = errorHandlingService.getErrorMessage(errors);

        expect(result).toEqual('Some error here\nSome other error.');
    });
});
