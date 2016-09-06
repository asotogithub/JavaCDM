'use strict';

describe('Service: authentication', function () {
    var authentication,
        _$q,
        _$base64,
        token = '';

    beforeEach(module('uiApp'));

    // instantiate service
    beforeEach(inject(function (AuthenticationService, $q, $base64) {
        authentication = AuthenticationService;
        _$q = $q;
        _$base64 = $base64;
    }));

    it('Authentication service initialize correctly.', function () {
        expect(!!authentication).toBe(true);
    });

    it('should invoke AuthenticationService.clear()', function () {
        authentication.setAuthorization('credentials');
        authentication.clear();
        expect(authentication.getAuthorization()).toBeNull();
    });

    it('Token correctly set', function () {
        token = 'Basic ' + _$base64.encode('trueffect.galaxy+test@gmail.com' + ':' +
            'trueffect.galaxy+test@gmail.com');

        var getToken = {
            promiseRequest: function () {
                var deferred = _$q.defer();

                deferred.resolve(token);
                return deferred.promise;
            }
        };

        authentication.setToken(getToken.promiseRequest().$$state.value);
        expect(authentication.getToken()).toBe(token);
    });
});
