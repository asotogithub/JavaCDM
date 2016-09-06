'use strict';

describe('Controller: FormController', function () {
    var controller,
        $scope,
        $state;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('FormController', {
            $scope: $scope
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });

    describe('Activate()', function () {
        var model = {
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

        beforeEach(inject(function () {
            $scope.$parent = {
                $parent: {
                    admDetails: {
                        model: model
                    }
                }
            };
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
    });
});
