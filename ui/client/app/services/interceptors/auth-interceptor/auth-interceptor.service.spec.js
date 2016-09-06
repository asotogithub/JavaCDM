'use strict';

describe('Service: AuthInterceptor', function () {
    var API_HOST,
        httpInterceptor,
        $httpBackend,
        result,
        config,
        response,
        $state,
        authService,
        $q,
        base64;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function (
            AuthInterceptor,
            _$httpBackend_,
            _$state_,
            _API_HOST_,
            AuthenticationService,
            _$q_,
            $base64) {
        httpInterceptor = AuthInterceptor;
        $httpBackend = _$httpBackend_;
        $state = _$state_;
        API_HOST = _API_HOST_;
        authService = AuthenticationService;
        $q = _$q_;
        base64 = $base64;
        config = {
            url: ''
        };
        response = {
            status: '',
            data: {
                errors: []
            }
        };
    }));

    it('should go to login page with the correct url request', function () {
        config.url = API_HOST + '/Login';

        result = httpInterceptor.request(config);
        $httpBackend.expectGET('app/cm/login/login.html').respond('Login!');
        $httpBackend.flush();

        expect($state.is('login')).toBe(true);
    });

    it('should redirect to login page when the request is wrong', function () {
        config.url = API_HOST + '/NotFoundPage';

        result = httpInterceptor.request(config);
        $httpBackend.expectGET('app/cm/login/login.html').respond('Login!');
        $httpBackend.flush();

        expect($state.is('login')).toBe(true);
    });

    it('should redirect to forbidden page when response status is 403', function () {
        response.status = 403;
        var token = 'Basic ' + base64.encode('invalid-creds@gmail.com' + ':' +
            'invalid-creds@gmail.com'),
            getToken = {
                promiseRequest: function () {
                    var deferred = $q.defer();

                    deferred.resolve(token);
                    return deferred.promise;
                }
            };

        authService.setToken(getToken.promiseRequest().$$state.value);

        result = httpInterceptor.responseError(response);
        $httpBackend.expectPOST(API_HOST + '/verifytoken').respond(403);
        $httpBackend.expectGET('app/cm/error-pages/page-403/page-403.html').respond(200);

        expect(result.$$state.value.status).toBe(403);
    });

    it('should refresh the token and be redirected to login page', function () {
        response.status = 403;
        var token = 'Basic ' + base64.encode('trueffect.galaxy+test@gmail.com' + ':' +
            'trueffect.galaxy+test@gmail.com'),
            getRefreshedToken = {
                promiseRequest: function () {
                    var deferred = $q.defer();

                    deferred.resolve(token);
                    return deferred.promise;
                }
            };

        authService.setToken(getRefreshedToken.promiseRequest().$$state.value);

        result = httpInterceptor.responseError(response);
        $httpBackend.expectPOST(API_HOST + '/refreshaccesstoken').respond(token);
        $httpBackend.expectGET('app/cm/login/login.html').respond(200);

        expect(authService.getToken()).toBe(token);
    });
});
