'use strict';

describe('Controller: ConfigurationController', function () {
    var controller,
        $scope,
        $state,
        model = {
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

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($compile, $controller, $rootScope, _$state_) {
        $scope = $rootScope.$new();
        $state = _$state_;

        $scope.$parent = {
            $parent: {
                admDetails: {
                    model: [],
                    admConfigForm: {
                        extractableCookiesForm: true,
                        cookieOverwriteExceptionsForm: true,
                        formKeyType: true,
                        formKey: true,
                        failThroughDefaultsForm: true
                    },
                    admParentForm: {
                        $setDirty: function () {
                        },

                        $error: ''
                    }
                }
            }
        };

        spyOn($state, 'go');
        spyOn($scope, '$on');
        controller = $controller('ConfigurationController', {
            $scope: $scope
        });
    }));

    describe('Configuration controller', function () {
        var cookieArray = [
                {
                    cookieName: 'testCookie01',
                    sequence: 0
                },
                {
                    cookieName: 'testCookie02',
                    sequence: 1
                },
                {
                    cookieName: 'testCookie03',
                    sequence: 2
                }
            ],
            indexCookieRemove = 1;

        beforeEach(inject(function () {
            $scope.$parent.$parent.admDetails.model = model;
            //controller.admConfig = $scope.$parent.$parent.admDetails.model;
        }));

        it('should invoke activate()', function () {
            expect($scope.$parent.$parent.admDetails.model).toEqual({
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
            });
        });

        it('Should create an instance of the controller.', function () {
            expect(controller).not.toBeUndefined();
        });

        it('Should add a item to cookie array.', function () {
            expect(cookieArray.length).toEqual(3);
            controller.addCookie(cookieArray);
            expect(cookieArray).toEqual([
                {
                    cookieName: 'testCookie01',
                    sequence: 0
                },
                {
                    cookieName: 'testCookie02',
                    sequence: 1
                },
                {
                    cookieName: 'testCookie03',
                    sequence: 2
                },
                {
                    cookieName: '',
                    cookieValue: '',
                    sequence: 3
                }
            ]);
        });

        it('Should remove empty cookie from cookie array.', function () {
            expect(cookieArray.length).toEqual(4);
            controller.removeEmptyCookies(cookieArray, false);
            expect(cookieArray).toEqual([
                {
                    cookieName: 'testCookie01',
                    sequence: 0
                },
                {
                    cookieName: 'testCookie02',
                    sequence: 1
                },
                {
                    cookieName: 'testCookie03',
                    sequence: 2
                }
            ]);
            expect(cookieArray.length).toEqual(3);
        });

        it('Should remove a item from cookie array.', function () {
            expect(cookieArray.length).toEqual(3);
            controller.removeCookie(cookieArray, indexCookieRemove, $scope.$parent.$parent.admDetails.admParentForm);
            expect(cookieArray).toEqual([
                {
                    cookieName: 'testCookie01',
                    sequence: 0
                }, {
                    cookieName: 'testCookie03',
                    sequence: 1
                }
            ]);
        });
    });
});
