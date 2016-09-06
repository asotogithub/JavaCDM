(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('StateChangeManager', StateChangeManager);

    StateChangeManager.$inject = [
        '$rootScope',
        '$state',
        'AuthenticationService',
        'CampaignsService',
        'CreativeGroupService',
        'CreativeService',
        'InsertionOrderService',
        'InsertionOrderUtilService',
        'OauthService',
        'PackageService',
        'PlacementService',
        'ScheduleUtilService',
        'SiteMeasurementsUtilService',
        'TagInjectionUtilService',
        'UserService'
    ];

    function StateChangeManager(
        $rootScope,
        $state,
        AuthenticationService,
        CampaignsService,
        CreativeGroupService,
        CreativeService,
        InsertionOrderService,
        InsertionOrderUtilService,
        OauthService,
        PackageService,
        PlacementService,
        ScheduleUtilService,
        SiteMeasurementsUtilService,
        TagInjectionUtilService,
        UserService) {
        var that = this,
            ENTITY = {
                CAMPAIGN: {
                    name: 'campaign'
                }
            };

        this.safeGo = function (state, params) {
            $state.get(state).auth = {
                verifyToken: false
            };
            return $state.go(state, params);
        };

        this.stateChangeSuccessObjectLoader = function (
            event,
            toState,
            toParams,
            fromState,
            fromParams) {
            function setBreadcrumb(
                stateParamsIdField,
                resultNameField,
                getFunction,
                breadcrumbNameField,
                breadcrumbIdField,
                entity) {
                if (toParams && toParams[stateParamsIdField]) {
                    $rootScope[breadcrumbIdField] = toParams[stateParamsIdField];

                    if (!fromParams || !fromParams[stateParamsIdField] ||
                        fromParams[stateParamsIdField] !== toParams[stateParamsIdField]) {
                        getFunction(toParams[stateParamsIdField]).then(function (result) {
                            $rootScope[breadcrumbNameField] = result[resultNameField];
                            if (entity === ENTITY.CAMPAIGN) {
                                $rootScope[entity.name] = {};
                                $rootScope[entity.name].advertiser = {};
                                $rootScope[entity.name].brand = {};
                                $rootScope[entity.name].advertiser.id = result.advertiserId;
                                $rootScope[entity.name].brand.id = result.brandId;
                            }
                        });
                    }
                }
            }

            setBreadcrumb('campaignId', 'name', CampaignsService.getCampaign, 'campaignName', 'campaignId',
                ENTITY.CAMPAIGN);

            setBreadcrumb('creativeGroupId', 'name', CreativeGroupService.getCreativeGroup, 'creativeGroupName',
                'creativeGroupId');

            setBreadcrumb('creativeId', 'alias', CreativeService.getCreative, 'creativeName', 'creativeId');

            setBreadcrumb('ioId', 'name', InsertionOrderService.getInsertionOrder, 'ioName', 'ioId');

            setBreadcrumb('packageId', 'name', PackageService.getPackage, 'packageName', 'packageId');

            setBreadcrumb('placementId', 'name', PlacementService.getPlacement, 'placementName', 'placementId');
        };

        this.stateChangeStartHandler = function (event, toState, toParams, fromState) {
            if (!isStateChangeAllowed(event, toState, toParams, fromState)) {
                return;
            }

            if (toState.noAuth) {
                return;
            }

            handleRedirects(event, toState, toParams);
            if ($state.get(toState.name).redirectTo) {
                return;
            }

            verifyToken(event, toState, toParams);
        };

        function isStateChangeAllowed(event, toState, toParams, fromState) {
            if (fromState.name === 'placement-list') {
                return InsertionOrderUtilService.isStateChangeAllowed(event, toState, toParams);
            }

            if (fromState.name === 'schedule-tab') {
                return ScheduleUtilService.isStateChangeAllowed(event, toState, toParams);
            }

            if (fromState.name === 'tag-injection-standard') {
                return TagInjectionUtilService.isStateChangeAllowed(event, toState, toParams);
            }

            if (fromState.name === 'sm-details' || fromState.name === 'events-list') {
                return SiteMeasurementsUtilService.isStateChangeAllowed(event, toState, toParams);
            }

            return true;
        }

        function forbidden() {
            $rootScope.$broadcast('forbidden');
        }

        function handleRedirects(event, toState, toParams) {
            if (hasPermission(toState) === false) {
                event.preventDefault();
                forbidden();
                return;
            }

            if (toState.redirectTo) {
                var stateChild = $state.get(toState.redirectTo);

                if (hasPermission(stateChild)) {
                    event.preventDefault();
                    $state.go(toState.redirectTo, toParams);
                }
            }
        }

        function hasPermission(toState) {
            if (toState.name !== 'page-403' && toState.permission) {
                if (!UserService.hasPermission(toState.permission)) {
                    return false;
                }
            }

            return true;
        }

        function verifyToken(event, toState, toParams) {
            if (toState.name === 'login') {
                if (AuthenticationService.getToken() === null) {
                    return;
                }
                else {
                    event.preventDefault();
                }
            }

            if (AuthenticationService.getToken() === null) {
                event.preventDefault();
                unauthorize();
                return;
            }

            var auth = $state.get(toState.name).auth;

            if (auth && !auth.verifyToken) {
                $state.get(toState.name).auth = null;
                return;
            }

            verifyTokenByAPI(event, toState, toParams);
        }

        function redirectToHome() {
            $rootScope.$broadcast('redirect.home-page');
        }

        function unauthorize() {
            $rootScope.$broadcast('unauthorized');
        }

        function verifyTokenByAPI(event, toState, toParams) {
            var promise = OauthService.verifyToken();

            promise.then(function () {
                if (toState.name === 'login') {
                    redirectToHome();
                    return;
                }

                that.safeGo(toState.name, toParams);
            }).catch(function () {
                var promiseRefresh = OauthService.refreshToken();

                if (!promiseRefresh) {
                    return;
                }

                promiseRefresh.then(function () {
                    that.safeGo(toState.name, toParams);
                }).catch(function () {
                    unauthorize();
                });
            });
        }
    }
})();

