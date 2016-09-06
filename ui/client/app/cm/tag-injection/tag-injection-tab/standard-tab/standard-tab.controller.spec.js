'use strict';

describe('Controller: StandardTabController', function () {
    var $controller,
        $httpBackend,
        $q,
        $scope,
        AgencyService,
        StandardTabController,
        trackingTags;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (
        _$controller_,
        _$httpBackend_,
        _$q_,
        _$rootScope_,
        _AgencyService_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        $scope = _$rootScope_.$new();
        AgencyService = _AgencyService_;
        trackingTags = $q.defer();
        spyOn(AgencyService, 'getHTMLTagInjections').andReturn(trackingTags.promise);

        $scope.vmTagInjectionTab = {
            promiseTab: {
                $$state: {}
            }
        };

        StandardTabController = $controller('StandardTabController', {
            $scope: $scope,
            AgencyService: AgencyService
        });
        installPromiseMatchers();
    }));

    describe('activate()', function () {
        it('Should create an instance of the controller.', function () {
            expect(StandardTabController).not.toBeUndefined();
        });

        it('should resolve tracking-tags data', function () {
            var trackingTagsObject = {
                HtmlInjectionTags: [
                    {
                        agencyId: 6031295,
                        htmlContent: '<img src=\"http://m.xp1.ru4.com/ta?_o=d_##EPLID##\" height=\"1\" width=\"1\" />',
                        id: 110,
                        isEnabled: 1,
                        isVisible: 0,
                        name: 'us1_tag'
                    },
                    {
                        agencyId: 6031295,
                        htmlContent: '<img src=\"http://m.xp1.ru4.com/ta?_o=d_##EPLID##" height=\"1\" width=\"1\" />',
                        id: 111,
                        isEnabled: 1,
                        isVisible: 0,
                        name: 'us2_tag'
                    }
                ]
            };

            trackingTags.resolve(trackingTagsObject);
            StandardTabController.campaignSelected = {
                id: null
            };

            $scope.$apply();
            expect(trackingTagsObject).toEqual(StandardTabController.trackingTags);
        });
    });
});
