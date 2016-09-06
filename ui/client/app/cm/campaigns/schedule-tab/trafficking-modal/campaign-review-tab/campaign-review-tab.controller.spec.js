'use strict';

describe('Controller: CampaignReviewController', function () {
    var $filter,
        $q,
        $scope,
        $state,
        AgencyService,
        CampaignsService,
        UserService,
        agencyPromise,
        campaignId,
        campaignsPromise,
        controller,
        userPromise;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, _$filter_, _$q_, $rootScope, _$state_) {
        $filter = _$filter_;
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        campaignId = 123456;
        AgencyService = jasmine.createSpyObj('AgencyService', ['getUsersTrafficking']);
        CampaignsService = jasmine.createSpyObj('CampaignsService', ['getCampaignDetails']);
        UserService = jasmine.createSpyObj('UserService', ['getDomains', 'getUsername']);

        agencyPromise = $q.defer();
        agencyPromise.resolve([
            {
                id: 1,
                contactId: 1,
                userName: 'foo@bar.com',
                realName: 'Foo Bar'
            },
            {
                id: 2,
                contactId: 2,
                userName: 'one@bar.com',
                realName: 'One Bar'
            }
        ]);
        AgencyService.getUsersTrafficking.andReturn(agencyPromise.promise);

        campaignsPromise = $q.defer();
        campaignsPromise.resolve({
                campaign: {
                    cookieDomainId: 1,
                    id: campaignId,
                    name: 'Campaign 1',
                    domain: 'Domain 1'
                }
            });
        CampaignsService.getCampaignDetails.andReturn(campaignsPromise.promise);

        userPromise = $q.defer();
        userPromise.resolve([
            {
                id: 1,
                domain: 'Domain 1'
            },
            {
                id: 2,
                domain: 'Domain 2'
            },
            {
                id: 3,
                domain: 'Domain 3'
            }
        ]);
        UserService.getDomains.andReturn(userPromise.promise);

        UserService.getUsername.andReturn('foo@bar.com');

        $scope.$parent = {
            $parent: {
                vm: {
                    campaignId: campaignId
                }
            }
        };

        spyOn($scope, '$on');

        controller = $controller('CampaignReviewController', {
            $filter: $filter,
            $q: $q,
            $scope: $scope,
            $state: _$state_,
            AgencyService: AgencyService,
            CampaignsService: CampaignsService,
            UserService: UserService
        });

        $scope.$digest();

        installPromiseMatchers();
    }));

    it('should load needed data on Activate(): traffic user contacts, domains, campaign', function () {
        expect(controller.contacts.length).toBe(2);

        expect(controller.domains.length).toBe(3);

        expect(controller.selectDomains).toEqual({
            id: 1,
            domain: 'Domain 1'
        });

        expect(controller.campaignModel).toEqual({
            cookieDomainId: 1,
            id: 123456,
            name: 'Campaign 1',
            domain: 'Domain 1'
        });
    });
});
