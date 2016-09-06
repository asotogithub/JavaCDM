'use strict';

describe('Service: StatusService', function () {
    var API_SERVICE,
        StatusService,
        $httpBackend,
        $q;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_API_SERVICE_, _StatusService_, _$httpBackend_, _$q_, $state) {
        $httpBackend = _$httpBackend_;
        $q = _$q_;
        API_SERVICE = _API_SERVICE_;
        StatusService = _StatusService_;

        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    it('should get the api status', function () {
        var statusFromServer = {
                foo: 'bar'
            },
            url = API_SERVICE + 'Status',
            promise;

        $httpBackend.whenGET(url).respond(statusFromServer);
        promise = StatusService.getStatus();
        $httpBackend.flush();

        expect(promise).toBeResolvedWith(statusFromServer);
    });
});

