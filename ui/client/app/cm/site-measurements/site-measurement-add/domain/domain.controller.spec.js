'use strict';

describe('Controller: SMCampaignDomainController', function () {
    var $filter,
        $q,
        $scope,
        CONSTANTS,
        RESPONSE_DOMAIN_LIST,
        SORTED_DOMAIN_LIST,
        SiteMeasurementsUtilService,
        UserService,
        controller,
        userGetDomainsPromise;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller,
                                _$filter_,
                                _$q_,
                                $rootScope,
                                $state,
                                _CONSTANTS_,
                                _SiteMeasurementsUtilService_,
                                _UserService_) {
        $filter = _$filter_;
        $q = _$q_;
        $scope = $rootScope.$new();
        CONSTANTS = _CONSTANTS_;
        SiteMeasurementsUtilService = _SiteMeasurementsUtilService_;
        UserService = _UserService_;

        $rootScope.vmSMCampaign = {
            STEP: {
                NAME: {
                    index: 1,
                    isValid: false,
                    key: 'addSMCampaign.name'
                },
                DOMAIN: {
                    index: 2,
                    isValid: false,
                    key: 'addSMCampaign.domain'
                }
            },
            campaign: {},
            promise: null
        };

        RESPONSE_DOMAIN_LIST = [
            {
                domain: 'zDomainName',
                isThirdParty: 'Y'
            },
            {
                domain: 'aDomainName',
                isThirdParty: 'N'
            }
        ];

        SORTED_DOMAIN_LIST = [
            {
                domain: 'aDomainName',
                isThirdParty: 'N'
            },
            {
                domain: 'zDomainName',
                isThirdParty: 'Y'
            }
        ];

        userGetDomainsPromise = $q.defer();

        spyOn(UserService, 'getDomains').andReturn(userGetDomainsPromise.promise);
        spyOn($state, 'go');

        controller = $controller('SMCampaignDomainController', {
            $filter: $filter,
            $scope: $scope,
            CONSTANTS: CONSTANTS,
            SiteMeasurementsUtilService: SiteMeasurementsUtilService,
            UserService: UserService
        });
    }));

    describe('activate()', function () {
        it('should create and instance of the controller and initialize variables ', function () {
            expect(controller).not.toBeUndefined();
            expect(controller.cookieDomainList).toEqual([]);
            expect(controller.maxLength).toEqual(200);
            expect(controller.statusList).toEqual([
                {
                    key: true,
                    name: 'global.active'
                },
                {
                    key: false,
                    name: 'global.inactive'
                }
            ]);
        });

        it('should load advertiser list data', function () {
            userGetDomainsPromise.resolve(RESPONSE_DOMAIN_LIST);
            $scope.$apply();

            expect(controller.cookieDomainList).toEqual(SORTED_DOMAIN_LIST);
        });
    });
});
