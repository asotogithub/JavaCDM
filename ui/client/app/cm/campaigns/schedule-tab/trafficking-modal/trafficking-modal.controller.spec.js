'use strict';

describe('Controller: TraffickingModalController', function () {
    var traffickingModalController,
        $httpBackend,
        $q,
        $scope,
        $state,
        CreativeService,
        creatives,
        modalInstance;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, $controller, _$q_, $rootScope) {
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = jasmine.createSpyObj('$state', ['go']);
        creatives = $q.defer();

        CreativeService = jasmine.createSpyObj('CreativeService', ['searchCreativeNotAssociatedByCampaign']);
        CreativeService.searchCreativeNotAssociatedByCampaign.andReturn(creatives.promise);

        modalInstance = {
            close: jasmine.createSpy('modalInstance.close'),
            dismiss: jasmine.createSpy('modalInstance.dismiss'),
            result: {
                then: jasmine.createSpy('modalInstance.result.then')
            }
        };

        traffickingModalController = $controller('TraffickingModalController', {
            $scope: $scope,
            $modalInstance: modalInstance,
            $state: $state,
            CreativeService: CreativeService,
            data: {
                campaignId: 568456
            }
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(traffickingModalController).not.toBeUndefined();
    });

    it('should start with undefined data', function () {
        expect(traffickingModalController.campaignId).toBeDefined();
        expect(traffickingModalController.tab).toBeDefined();
    });
});
