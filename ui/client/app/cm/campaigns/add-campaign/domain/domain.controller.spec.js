'use strict';

describe('Controller: AddCampaignDomainController', function () {
    var controller,
        $scope,
        $state,
        mockDomainsObject,
        UserService;

    beforeEach(function () {
        module('uiApp', function ($provide) {
            UserService = jasmine.createSpyObj('UserService', ['getDomains', 'hasPermission']);
            $provide.value('UserService', UserService);
        });

        inject(function ($q) {
            mockDomainsObject = {
                pageSize: 2,
                startIndex: 0,
                totalNumberOfRecords: 2,
                records: [
                {
                    CookieDomain: [
                    {
                        agencyId: 9024559,
                        cookieDomainRootId: 9010397,
                        createdDate: '2015-05-18T13:30:42-04:00',
                        createdTpwsKey: 'd5589621-4bd0-4775-89af-f6fb7d495f67',
                        domain: 'www.tests.qa.ui',
                        id: 9024565,
                        isThirdParty: 'N',
                        logicalDelete: 'N',
                        modifiedDate: '2015-05-18T13:30:42-04:00',
                        modifiedTpwsKey: 'd5589621-4bd0-4775-89af-f6fb7d495f67',
                        type: 'cookieDomainDTO'
                    },
                    {
                        agencyId: 9024559,
                        cookieDomainRootId: 1171366,
                        createdDate: '2015-05-21T08:31:03-04:00',
                        createdTpwsKey: 'b1c28462-ef6f-451a-b18a-31594469a223',
                        domain: 'ad.adlegend.com',
                        id: 9034120,
                        isThirdParty: 'Y',
                        logicalDelete: 'N',
                        modifiedDate: '2015-05-21T08:31:03-04:00',
                        modifiedTpwsKey: 'b1c28462-ef6f-451a-b18a-31594469a223',
                        type: 'cookieDomainDTO'
                    }
                    ]
                }
                ]
            };

            var defer = $q.defer();

            defer.resolve(mockDomainsObject);
            defer.$promise = defer.promise;

            UserService.getDomains.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $rootScope.vmCampaign = {};
        $rootScope.vmCampaign.STEP = {
            NAME: {
                index: 1,
                isValid: false,
                key: 'addCampaign.name'
            },
            DOMAIN: {
                index: 2,
                isValid: false,
                key: 'addCampaign.domain'
            },
            DATES_BUDGET: {
                index: 3,
                isValid: false,
                key: 'addCampaign.dates&Budget'
            }
        };

        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('AddCampaignDomainController', {
            $scope: $scope
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    it('Should resolve the promise.', function () {
        var promise = UserService.getDomains();

        expect(promise).toBeResolved();
    });

    it('Should resolve the promise with all domains data.', function () {
        var promise = UserService.getDomains();

        expect(promise).toBeResolvedWith(mockDomainsObject);
    });
});
