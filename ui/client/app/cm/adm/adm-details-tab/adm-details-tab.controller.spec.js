'use strict';

describe('Controller: AdmDetailsTabController', function () {
    var API_SERVICE,
        CONSTANTS,
        $controller,
        $httpBackend,
        $q,
        $scope,
        $stateParams,
        AdmService,
        admDetails,
        $state,
        AdmDetailsTabController,
        datasetId;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_,
                                _$q_,
                                _$controller_,
                                _$rootScope_,
                                _$state_,
                                _$stateParams_,
                                _API_SERVICE_,
                                _AdmService_,
                                _DatasetService_,
                                _CONSTANTS_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        CONSTANTS = _CONSTANTS_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        $q = _$q_;
        $scope = _$rootScope_.$new();
        $state = _$state_;
        $stateParams = _$stateParams_;
        datasetId = $stateParams.id = 'data001';
        AdmService = _AdmService_;
        admDetails = $q.defer();

        $scope.$parent = {
            vm: {
                adm: {}
            }
        };

        spyOn($state, 'go');
        spyOn(AdmService, 'getAdmDetails').andReturn(admDetails.promise);

        AdmDetailsTabController = $controller('AdmDetailsTabController', {
            $scope: $scope,
            $state: $state,
            $stateParams: $stateParams,
            admService: AdmService,
            CONSTANTS: CONSTANTS
        });
        installPromiseMatchers();
    }));

    describe('activate()', function () {
        var admDetailsObject;

        beforeEach(function () {
            admDetailsObject = {
                active: true,
                advertiserId: 9073754,
                advertiserName: 'Cookie Targeting Advertiser',
                agencyId: 9073737,
                alias: 'xxxx',
                contentChannels: ['Display', 'Email'],
                cookieExpirationDays: 73342,
                cookiesToCapture: {
                    cookies: [
                        'cap1',
                        'cap2',
                        'cap3',
                        'cap4',
                        'cap5',
                        'cap6!#$@!%08)(*#$)&(%&#___; sadkfj',
                        'new capture cookies'
                    ],
                    enabled: true
                },
                datasetId: 'aed1a3e8-f267-4c76-aa59-8455a8fb9efd',
                domain: 'extdev.adlegend.net',
                durableCookies: {
                    cookies: ['asdkfl5', 'lkjsalf;;lkswie!@$^^__()*', 'my durable cookies', 'new1'],
                    enabled: true
                },
                failThroughDefaults: {
                    defaultKey: 'Some(my-value)',
                    defaultType: 'KeyDefault',
                    enabled: true
                },
                fileNamePrefix: 'my-prefix',
                ftpAccount: 'greg',
                latestUpdate: '2017-08-08T16:56:49.114-07:00',
                matchCookieName: 'matchTest11 - 2',
                path: '',
                ttlExpirationSeconds: 100816
            };
        });

        it('should invoke AdmService.getADMDatasetConfigs()', function () {
            expect(AdmService.getAdmDetails).toHaveBeenCalledWith(datasetId);
        });

        it('should resolve dataset data', function () {
            admDetails.resolve(admDetailsObject);

            $scope.$digest();

            expect(admDetailsObject).toEqual(AdmDetailsTabController.model);
        });
    });

    describe('Update()', function () {
        var deferred,
            adm,
            modelUpdate = {
                active: 'true',
                advertiserId: 9073754,
                advertiserName: 'Cookie Targeting Advertiser',
                agencyId: 9073737,
                alias: 'xxxx2',
                contentChannels: ['Email', 'Display'],
                cookieExpirationDays: '73342',
                cookiesToCapture: {
                    cookies: [
                        'cap1',
                        'cap2',
                        'cap3',
                        'cap4',
                        'cap5',
                        'cap6!#$@!%08)(*#$)&(%&#___; sadkfj',
                        'new capture cookies'
                    ],
                    enabled: true
                },
                datasetId: 'aed1a3e8-f267-4c76-aa59-8455a8fb9efd',
                domain: 'extdev.adlegend.net',
                durableCookies: {
                    cookies: ['asdkfl5', 'lkjsalf;;lkswie!@$^^__()*', 'my durable cookies', 'new1'],
                    enabled: true
                },
                failThroughDefaults: {
                    defaultCookieList: [
                        {
                            name: 'my', value: '1'
                        }
                    ],
                    defaultKey: 'Some(my-value)',
                    defaultType: 'CookieDefault',
                    enabled: true
                },
                fileNamePrefix: 'my-prefix',
                ftpAccount: 'greg',
                latestUpdate: '2017-08-08T16:56:49.114-07:00',
                matchCookieName: 'matchTest11 - 2',
                path: '',
                ttlExpirationSeconds: 100800,
                activeObject: {
                    key: 'true', name: 'global.active'
                },
                checkContentChannels: {
                    display: true,
                    email: true,
                    site: false
                },
                cookieOverwriteExceptionsArray: [
                    {
                        sequence: 0,
                        cookieName: 'asdkfl5'
                    },
                    {
                        sequence: 1,
                        cookieName: 'lkjsalf;;lkswie!@$^^__()*'
                    },
                    {
                        sequence: 2,
                        cookieName: 'my durable cookies'
                    },
                    {
                        sequence: 3,
                        cookieName: 'new1'
                    }
                ],
                extractableCookiesArray: [
                    {
                        sequence: 0, cookieName: 'cap1'
                    },
                    {
                        sequence: 1,
                        cookieName: 'cap2'
                    },
                    {
                        sequence: 2,
                        cookieName: 'cap3'
                    },
                    {
                        sequence: 3,
                        cookieName: 'cap4'
                    },
                    {
                        sequence: 4,
                        cookieName: 'cap5'
                    },
                    {
                        sequence: 5,
                        cookieName: 'cap6!#$@!%08)(*#$)&(%&#___; sadkfj'
                    },
                    {
                        sequence: 6,
                        cookieName: 'new capture cookies'
                    }
                ],
                failThroughDefaultsArray: [
                    {
                        sequence: 0,
                        cookieName: 'my',
                        cookieValue: 'test []{+r++** f,mg ·$%&('
                    }
                ],
                keyTypeOption: 'cookie',
                updateFrequencyObject: '888815'
            };

        beforeEach(inject(function () {
            deferred = $q.defer();
            spyOn(AdmService, 'updateAdmDetails').andReturn(deferred.promise);
            adm = AdmDetailsTabController.model;
        }));

        it('should invoke AdmService.getADMDatasetConfigs()', function () {
            expect(AdmService.getAdmDetails).toHaveBeenCalledWith(datasetId);
        });

        it('should invoke AdmService.updateAdmDetails()', function () {
            AdmDetailsTabController.model = modelUpdate;
            AdmDetailsTabController.submit();
            expect(AdmService.updateAdmDetails).toHaveBeenCalled();
            expect(AdmDetailsTabController.model.failThroughDefaultsArray[0]).toEqual({
                sequence: 0,
                cookieName: 'my',
                cookieValue: 'test []{+r++** f,mg ·$%&('
            });
        });
    });
});
