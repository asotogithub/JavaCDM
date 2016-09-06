'use strict';

describe('Controller: SiteTraffickingContactsController', function () {
    var $filter,
        $q,
        $scope,
        $state,
        CampaignsService,
        UserService,
        campaignId,
        campaignsPromise,
        controller;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, _$filter_, _$q_, $rootScope, _$state_) {
        $filter = _$filter_;
        $q = _$q_;
        $scope = $rootScope.$new();
        $state = _$state_;
        campaignId = 123456;
        CampaignsService = jasmine.createSpyObj('CampaignsService', ['getSiteContacts']);
        UserService = jasmine.createSpyObj('UserService', ['getUsername', 'getSavedUser']);

        campaignsPromise = $q.defer();
        campaignsPromise.resolve([
            {
                contactEmail: 'alpha@betha.com',
                contactId: 111,
                contactName: 'Alpha Betha',
                publisherId: 222,
                publisherName: 'Publisher 1',
                siteId: 333,
                siteName: 'Site 1',
                checked: false,
                readonly: false
            },
            {
                contactEmail: 'gamma@betha.com',
                contactId: 1111,
                contactName: 'Gamma Betha',
                publisherId: 2222,
                publisherName: 'Publisher 2',
                siteId: 3333,
                siteName: 'Site 2',
                checked: false,
                readonly: false
            }
        ]);
        CampaignsService.getSiteContacts.andReturn(campaignsPromise.promise);

        UserService.getUsername.andReturn('foo@bar.com');

        UserService.getSavedUser.andReturn({
            contactId: 123
        });

        $scope.$parent = {
            $parent: {
                vm: {
                    campaignId: campaignId
                }
            }
        };

        spyOn($scope, '$on');

        controller = $controller('SiteTraffickingContactsController', {
            $filter: $filter,
            $q: $q,
            $scope: $scope,
            $state: _$state_,
            CampaignsService: CampaignsService,
            UserService: UserService
        });

        $scope.$digest();

        installPromiseMatchers();
    }));

    it('should load needed data on Activate(): traffic user site contacts', function () {
        expect(controller.contacts.length).toBe(3); //Including current logged-in user
    });

    it('should check if all contacts were selected', function () {
        expect(controller.allSelected).toBe(false);

        controller.allSelected = true;
        controller.checkboxAll(controller.contacts);

        for (var i = 0; i < controller.contacts.length; i++) {
            expect(controller.contacts[i].checked).toBe(true);
        }
    });
});
