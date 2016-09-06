'use strict';

describe('Service: CookieDomainsService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        CookieDomainsService,
        CampaignsService;

    beforeEach(module('uiApp', function ($provide) {
        CampaignsService = jasmine.createSpyObj('CampaignsService', ['getCampaign']);

        $provide.value('CampaignsService', CampaignsService);
    }));

    beforeEach(inject(function (_$httpBackend_, _API_SERVICE_, _CookieDomainsService_, $state, _$q_) {
        $httpBackend = _$httpBackend_;
        $q = _$q_;
        API_SERVICE = _API_SERVICE_;
        CookieDomainsService = _CookieDomainsService_;

        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('numberValidation()', function () {
        describe('for rule without multiple options', function () {
            var rule = {
                operator: {
                    type: 'equal',
                    //jscs:disable requireCamelCaseOrUpperCaseIdentifiers
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string', 'number']
                    //jscs:enable
                },
                filter: {
                    type: 'double'
                }
            };

            it('should return true for single input rule and valid value', function () {
                var value = '10',
                    isValid = CookieDomainsService.numberValueValidation(value, rule);

                expect(isValid).toBeTruthy();
            });
        });

        describe('for rule with multiple options', function () {
            var rule = {
                operator: {
                    type: 'is_any_of',
                    //jscs:disable requireCamelCaseOrUpperCaseIdentifiers
                    nb_inputs: 1,
                    multiple: true,
                    apply_to: ['string', 'number']
                    //jscs:enable
                },
                filter: {
                    type: 'double',
                    validation: {
                        min: 0,
                        max: 100
                    }
                }
            };

            it('should return true for single input rule and valid single value', function () {
                var value = '10',
                    isValid = CookieDomainsService.numberValueValidation(value, rule);

                expect(isValid).toBeTruthy();
            });

            it('should return true for single input rule and valid multiple values', function () {
                var value = '10,20',
                    isValid = CookieDomainsService.numberValueValidation(value, rule);

                expect(isValid).toBeTruthy();
            });
        });
    });

    describe('numberRangeValidation()', function () {
        describe('for rule without multiple options', function () {
            var rule = {
                    operator: {
                        type: 'equal',
                        //jscs:disable requireCamelCaseOrUpperCaseIdentifiers
                        nb_inputs: 1,
                        multiple: false,
                        apply_to: ['string', 'number']
                        //jscs:enable
                    },
                    filter: {
                        type: 'double',
                        validation: {
                            min: 0,
                            max: 100
                        }
                    }
                };

            it('should return true for single input rule and valid value', function () {
                var value = '10',
                    isValid = CookieDomainsService.numberRangeValidation(value, rule);

                expect(isValid).toBeTruthy();
            });
        });

        describe('for rule with multiple options', function () {
            var rule = {
                    operator: {
                        type: 'is_any_of',
                        //jscs:disable requireCamelCaseOrUpperCaseIdentifiers
                        nb_inputs: 1,
                        multiple: true,
                        apply_to: ['string', 'number']
                        //jscs:enable
                    },
                    filter: {
                        type: 'double',
                        validation: {
                            min: 0,
                            max: 100
                        }
                    }
                };

            it('should return true for single input rule and valid single value', function () {
                var value = '10',
                    isValid = CookieDomainsService.numberRangeValidation(value, rule);

                expect(isValid).toBeTruthy();
            });

            it('should return true for single input rule and valid multiple values', function () {
                var value = '1,3',
                    isValid = CookieDomainsService.numberRangeValidation(value, rule);

                expect(isValid).toBeTruthy();
            });

            it('should return false for single input rule and invalid single value', function () {
                var value = '1000',
                    isValid = CookieDomainsService.numberRangeValidation(value, rule);

                expect(isValid).toBeFalsy();
            });

            it('should return false for single input rule and at least one invalid of multiple values', function () {
                var value = '1,3000',
                    isValid = CookieDomainsService.numberRangeValidation(value, rule);

                expect(isValid).toBeFalsy();
            });
        });
    });

    describe('getCookies()', function () {
        it('should return an empty list when cookie domain id is not set', function () {
            var promise,
                deferred = $q.defer();

            deferred.resolve({});
            CampaignsService.getCampaign.andReturn(deferred.promise);

            promise = CookieDomainsService.getCookies();
            expect(promise).toBeResolvedWith([]);
        });

        it('should map cookie types correctly', function () {
            var promise,
                deferred = $q.defer();

            deferred.resolve({
                cookieDomainId: 1337
            });
            CampaignsService.getCampaign.andReturn(deferred.promise);

            $httpBackend.whenGET(API_SERVICE + 'CookieDomains/1337/cookies').respond({
                startIndex: 0,
                pageSize: 3,
                totalNumberOfRecords: 3,
                records: [
                    {
                        CookieTargetTemplate: [
                            {
                                cookieContentType: 1,
                                cookieDomainId: 9075186,
                                cookieName: 'should_be_string',
                                cookieTargetTemplateId: 9077550,
                                createdDate: '2015-06-17T12:46:57Z',
                                logicalDelete: 'N',
                                modifiedDate: '2015-06-17T12:46:57Z',
                                trackingCookie: '78'
                            },
                            {
                                contentPossibleValues: 'value1`value2`value3',
                                cookieContentType: 2,
                                cookieDomainId: 9075186,
                                cookieName: 'should_be_string_list',
                                cookieTargetTemplateId: 9138836,
                                createdDate: '2015-07-27T15:24:01Z',
                                logicalDelete: 'N',
                                modifiedDate: '2015-07-27T15:24:01Z',
                                trackingCookie: '78'
                            },
                            {
                                cookieContentType: 3,
                                cookieDomainId: 9075186,
                                cookieName: 'should_be_number',
                                cookieTargetTemplateId: 9077551,
                                createdDate: '2015-06-17T12:46:57Z',
                                logicalDelete: 'N',
                                modifiedDate: '2015-06-17T12:46:57Z',
                                trackingCookie: '78'
                            },
                            {
                                contentPossibleValues: '1`2`3',
                                cookieContentType: 4,
                                cookieDomainId: 9075186,
                                cookieName: 'should_be_number_list',
                                cookieTargetTemplateId: 9077552,
                                createdDate: '2015-06-17T12:46:57Z',
                                logicalDelete: 'N',
                                modifiedDate: '2015-06-17T12:46:57Z',
                                trackingCookie: '78'
                            },
                            {
                                contentPossibleValues: '0`48',
                                cookieContentType: 5,
                                cookieDomainId: 9075186,
                                cookieName: 'should_be_number_range',
                                cookieTargetTemplateId: 9077553,
                                createdDate: '2015-06-17T12:46:57Z',
                                logicalDelete: 'N',
                                modifiedDate: '2015-06-17T12:46:57Z',
                                trackingCookie: '78'
                            }
                        ]
                    }
                ]
            });

            promise = CookieDomainsService.getCookies(1337);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith([
                {
                    id: '[should_be_string]',
                    type: 'string'
                },
                {
                    id: '[should_be_string_list]',
                    type: 'string',
                    input: 'checkbox',
                    values: {
                        value1: 'value1',
                        value2: 'value2',
                        value3: 'value3'
                    },
                    validation: {
                        callback: CookieDomainsService.selectionValidation
                    }
                },
                {
                    id: '[should_be_number]',
                    type: 'double',
                    input: CookieDomainsService.numberInput,
                    validation: {
                        callback: CookieDomainsService.numberValueValidation
                    }
                },
                {
                    id: '[should_be_number_list]',
                    type: 'double',
                    input: 'checkbox',
                    values: {
                        1: 1,
                        2: 2,
                        3: 3
                    },
                    validation: {
                        callback: CookieDomainsService.selectionValidation
                    }
                },
                {
                    id: '[should_be_number_range]',
                    type: 'double',
                    input: CookieDomainsService.numberInput,
                    validation: {
                        min: 0,
                        max: 48,
                        callback: CookieDomainsService.numberRangeValidation
                    }
                }
            ]);
        });
    });
});
