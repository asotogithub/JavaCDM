'use strict';

describe('Controller: AddTrackingTagController', function () {
    var $q,
        $scope,
        $state,
        controller,
        mockObject,
        AgencyService;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            AgencyService = jasmine.createSpyObj('AgencyService', ['saveTrackingTag']);
            $provide.value('AgencyService', AgencyService);
        });

        inject(function (_$q_) {
            mockObject = {
                id: 23423,
                name: 'new tracking tag'
            };

            var defer = _$q_.defer();

            defer.resolve(mockObject);
            defer.$promise = defer.promise;

            AgencyService.saveTrackingTag.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, _$q_, $rootScope, _$state_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('AddTrackingTagController', {
            $scope: $scope
        });
    }));

    it('Should resolve the promise to get the tracking-tag to be saved', function () {
        controller.htmlTagType = controller.HTML_TAG_TYPE.AD_CHOICES;
        controller.trackingTagDomain = {
            id: 23423,
            name: 'new tracking tag'
        };

        controller.promise = null;
        controller.save();

        expect(controller.promise).toBeResolved();
    });
});
