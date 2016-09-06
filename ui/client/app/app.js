(function () {
    'use strict';

    angular.module('uiApp', [
        'angle',
        'angular-growl',
        'angular-momentjs',
        'angular-svg-round-progress',
        'angularLocalStorage',
        'base64',
        'cgBusy',
        'constants',
        'dialogs.main',
        'dndLists',
        'highcharts-ng',
        'http-auth-interceptor',
        'localytics.directives',
        'logDecorator',
        'ncy-angular-breadcrumb',
        'ngAnimate',
        'ngCookies',
        'ngDragDrop',
        'ngFileUpload',
        'ngLodash',
        'ngResource',
        'ngSanitize',
        'ngTable',
        'ngTagsInput',
        'te.components',
        'te.vendor.fiestah.money',
        'ui.bootstrap',
        'ui.router',
        'ui.tree',
        'uiApp.translate'
    ]).config(App);

    angular.module('uiApp').run(Run);
    App.$inject = [
        '$httpProvider',
        '$provide',
        '$urlRouterProvider',
        'API_HOST',
        'API_VERSION',
        'CONSTANTS',
        'OAUTH_CONTEXT',
        'PUBLIC_CONTEXT',
        'growlProvider',
        'lodash'
    ];

    function App($httpProvider,
                 $provide,
                 $urlRouterProvider,
                 API_HOST,
                 API_VERSION,
                 CONSTANTS,
                 OAUTH_CONTEXT,
                 PUBLIC_CONTEXT,
                 growlProvider,
                 lodash) {
        $httpProvider.interceptors.push('AuthInterceptor');
        $urlRouterProvider.otherwise(function ($injector) {
            var $state = $injector.get('$state'),
                $rootScope = $injector.get('$rootScope');

            if ($state.current.url === '^') {
                $rootScope.$broadcast('redirect.home-page');
                return;
            }

            $state.go('page-404');
        });

        $provide.constant('API_OAUTH', buildServiceUrl(OAUTH_CONTEXT));
        $provide.constant('API_SERVICE', buildServiceUrl(PUBLIC_CONTEXT));
        $provide.constant('$timezones.definitions.location', '/bower_components/angular-tz-extensions/tz/data');

        growlProvider.globalTimeToLive(CONSTANTS.GROWL.TTL);
        growlProvider.globalDisableCountDown(true);
        growlProvider.globalPosition(CONSTANTS.GROWL.POSITION);

        function buildServiceUrl(context) {
            return lodash.compact([API_HOST, context, API_VERSION]).join('/') + '/';
        }
    }

    Run.$inject = [
        '$rootScope',
        '$state',
        'AuthenticationService',
        'OauthService',
        'StateChangeManager',
        'UserService'
    ];

    function Run($rootScope,
                 $state,
                 AuthenticationService,
                 OauthService,
                 StateChangeManager,
                 UserService) {
        // Global listeners for the application.
        $rootScope.$on('$stateChangeStart', stateChangeStartHandler);
        $rootScope.$on('$stateChangeSuccess', StateChangeManager.stateChangeSuccessObjectLoader);
        $rootScope.$on('$stateChangeError', function (event) {
            event.preventDefault();
            $state.go('page-404');
        });

        $rootScope.$on('authorized', function () {
            }

        );

        /**
         * This event comes from 'http-auth-interceptor' module.
         */
        $rootScope.$on('event:auth-loginRequired', function () {
            refreshToken();
        });

        $rootScope.$on('forbidden', function () {
            forbidden();
        });

        $rootScope.$on('notFound', function () {
            notFound();
        });

        $rootScope.$on('redirect.home-page', function () {
            redirectToHome();
        });

        $rootScope.$on('unauthorized', function () {
            unauthorize();
        });

        $rootScope.$on('update.model.campaign',
            function (event, campaign) {
                $rootScope.campaignName = campaign.name;
            });

        $rootScope.$on('update.model.creativeGroup',
            function (event, creativeGroup) {
                $rootScope.creativeGroupName = creativeGroup.name;
            });

        function forbidden() {
            $state.go('page-403');
        }

        function notFound() {
            $state.go('page-404');
        }

        function redirectToHome() {
            $state.go('campaigns-list');
        }

        function refreshToken() {
            var promise = OauthService.refreshToken({
                verifyToken: true
            });

            if (!promise) {
                return;
            }

            promise.then().catch(function () {
                unauthorize();
            });
        }

        function stateChangeStartHandler(event, toState, toParams, fromState, fromParams) {
            StateChangeManager.stateChangeStartHandler(event, toState, toParams, fromState, fromParams);
        }

        function unauthorize() {
            AuthenticationService.clear();
            UserService.clear();
            StateChangeManager.safeGo('login');
        }
    }
})();
