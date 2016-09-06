'use strict';

describe('Controller: StatusController', function () {
    var $scope,
        $translate,
        StatusService,
        StatusController,
        deferred;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, _$translate_, $q) {
        $scope = $rootScope.$new();
        $translate = _$translate_;
        StatusService = jasmine.createSpyObj('StatusService', [
            'getStatus'
        ]);
        deferred = $q.defer();
        StatusService.getStatus.andReturn(deferred.promise);

        StatusController = $controller('StatusController', {
            StatusService: StatusService
        });
    }));

    it('should load the status when the api is available', function () {
        var sampleStatus = {
            foo: 'bar'
        };

        deferred.resolve(sampleStatus);
        $scope.$digest();

        expect(StatusController.status).toEqual(sampleStatus);
    });

    it('should load the error when the api is unavailable', function () {
        deferred.reject('foo bar');
        $scope.$digest();

        expect(StatusController.error).toEqual($translate.instant('status.error'));
    });
});
