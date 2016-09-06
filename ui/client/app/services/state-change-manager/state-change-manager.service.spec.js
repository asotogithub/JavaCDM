'use strict';

describe('Service: StateChangeManager', function () {
    var stateChangeManager,
        $scope,
        $state,
        $window,
        $q,
        OauthService,
        CampaignsService,
        CreativeGroupService,
        CreativeService,
        InsertionOrderService,
        token;

    beforeEach(function () {
        OauthService = {};
        CampaignsService = jasmine.createSpyObj('CampaignsService', ['getCampaign']);
        CreativeGroupService = jasmine.createSpyObj('CreativeGroupService', ['getCreativeGroup']);
        CreativeService = jasmine.createSpyObj('CreativeService', ['getCreative']);
        InsertionOrderService = jasmine.createSpyObj('InsertionOrderService', ['getInsertionOrder']);
        module('uiApp', function ($provide) {
            $provide.value('OauthService', OauthService);
            $provide.value('CampaignsService', CampaignsService);
            $provide.value('CreativeGroupService', CreativeGroupService);
            $provide.value('CreativeService', CreativeService);
            $provide.value('InsertionOrderService', InsertionOrderService);
        });

        inject(function (_$q_) {
            $q = _$q_;
            token = 'tokenvalid';

            OauthService.verifyToken = {
                post: function () {
                    var defer = $q.defer();

                    defer.resolve(token);
                    defer.$promise = defer.promise;

                    return defer;
                }
            };
        });
    });

    beforeEach(
        inject(function (StateChangeManager, _$state_, _$window_, _$q_, $rootScope) {
            stateChangeManager = StateChangeManager;
            $state = _$state_;
            $window = _$window_;
            $q = _$q_;
            $scope = $rootScope.$new();

            spyOn($state, 'go');
        })

    );

    it('StateManager service initialize correctly.', function () {
        expect(stateChangeManager).not.toBeUndefined();
    });

    it('should verify the safeGo function.', function () {
        var stateName = 'login';

        stateChangeManager.safeGo(stateName);
        expect($state.href(stateName)).toEqual('#/login');
    });

    it('should verify the state change event.', function () {
        var toState = {},
            event = $window.document.createEvent('Event');

        toState.name = 'campaigns-list';
        stateChangeManager.stateChangeStartHandler(event, toState, {}, {});
        expect($state.href(toState.name)).toEqual('#/campaigns/');
    });

    describe('Breadcrumb change handler', function () {
        beforeEach(function () {
            var deferred = $q.defer(),
                deferredAlias = $q.defer();

            deferred.resolve({
                name: 'foobar'
            });

            deferredAlias.resolve({
                alias: 'foobar'
            });

            CampaignsService.getCampaign.andReturn(deferred.promise);
            CreativeGroupService.getCreativeGroup.andReturn(deferred.promise);
            CreativeService.getCreative.andReturn(deferredAlias.promise);
            InsertionOrderService.getInsertionOrder.andReturn(deferred.promise);
        });

        it('should fetch the campaign name when campaign id is in the to parameters', function () {
            var toState = {},
                toParams = {
                    campaignId: 1234
                },
                fromState = {},
                fromParams = {},
                event = $window.document.createEvent('Event');

            stateChangeManager.stateChangeSuccessObjectLoader(event, toState, toParams, fromState, fromParams);

            $scope.$digest();

            expect($scope.campaignId).toEqual(1234);
            expect($scope.campaignName).toEqual('foobar');
        });

        it('should fetch the creative group name when creative group id is in the to parameters', function () {
            var toState = {},
                toParams = {
                    creativeGroupId: 1234
                },
                fromState = {},
                fromParams = {},
                event = $window.document.createEvent('Event');

            stateChangeManager.stateChangeSuccessObjectLoader(event, toState, toParams, fromState, fromParams);

            $scope.$digest();

            expect($scope.creativeGroupId).toEqual(1234);
            expect($scope.creativeGroupName).toEqual('foobar');
        });

        it('should fetch the creative name when creative id is in the to parameters', function () {
            var toState = {},
                toParams = {
                    creativeId: 1234
                },
                fromState = {},
                fromParams = {},
                event = $window.document.createEvent('Event');

            stateChangeManager.stateChangeSuccessObjectLoader(event, toState, toParams, fromState, fromParams);

            $scope.$digest();

            expect($scope.creativeId).toEqual(1234);
            expect($scope.creativeName).toEqual('foobar');
        });

        it('should fetch the IO name when IO id is in the to parameters', function () {
            var toState = {},
                toParams = {
                    ioId: 1234
                },
                fromState = {},
                fromParams = {},
                event = $window.document.createEvent('Event');

            stateChangeManager.stateChangeSuccessObjectLoader(event, toState, toParams, fromState, fromParams);

            $scope.$digest();

            expect($scope.ioId).toEqual(1234);
            expect($scope.ioName).toEqual('foobar');
        });

        it('should set the breadcrumbNameField on rootScope if fromParams is undefined', function () {
            var toState = {},
                toParams = {
                    ioId: 5678
                },
                fromState = {},
                fromParams,
                event = $window.document.createEvent('Event');

            stateChangeManager.stateChangeSuccessObjectLoader(event, toState, toParams, fromState, fromParams);

            $scope.$digest();

            expect($scope.ioId).toEqual(5678);
            expect($scope.ioName).not.toBeUndefined();
        });

        it('should set the breadcrumbNameField on rootScope if fromParams is not the same as toParams', function () {
            var toState = {},
                toParams = {
                    ioId: 5678
                },
                fromState = {},
                fromParams = {
                    ioId: 1234
                },
                event = $window.document.createEvent('Event');

            stateChangeManager.stateChangeSuccessObjectLoader(event, toState, toParams, fromState, fromParams);

            $scope.$digest();

            expect($scope.ioId).toEqual(5678);
            expect($scope.ioName).not.toBeUndefined();
        });

        it('should set the breadcrumbNameField on rootScope if fromParams is not equal to toParams', function () {
            var toState = {},
                toParams = {
                    ioId: 5678
                },
                fromState = {},
                fromParams = {
                    ioId: 5678
                },
                event = $window.document.createEvent('Event');

            stateChangeManager.stateChangeSuccessObjectLoader(event, toState, toParams, fromState, fromParams);

            $scope.$digest();

            expect($scope.ioId).toEqual(5678);
            expect(InsertionOrderService.getInsertionOrder).not.toHaveBeenCalled();
            expect($scope.ioName).toBeUndefined();
        });
    });
});
