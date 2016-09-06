'use strict';

describe('Service: Sha1', function () {
    var Sha1Var;

    beforeEach(module('te.components'));

    beforeEach(
        inject(function (Sha1) {
            Sha1Var = Sha1;
        })

    );

    it('should return the SHA1 Hash of a string', function () {
        var string = '123456789asdfghjkl',
            result = '9fffa40341f17fb56689a280f819468d8db30ae1';

        expect(Sha1Var.hash(string)).toEqual(result);
    });
});
