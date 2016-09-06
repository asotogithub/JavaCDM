'use strict';

describe('Service: CreativeGroupService', function () {
    var API_SERVICE,
        $httpBackend,
        CreativeGroupService,
        ErrorRequestHandler;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _API_SERVICE_, _CreativeGroupService_, _ErrorRequestHandler_, $state) {
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        CreativeGroupService = _CreativeGroupService_;
        ErrorRequestHandler = _ErrorRequestHandler_;

        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('getCreativeGroup()', function () {
        it('should set enablePriority to 0 if priority is 0', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'CreativeGroups/1337').respond({
                priority: 0
            });
            promise = CreativeGroupService.getCreativeGroup(1337);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith({
                priority: 0,
                enablePriority: 0,
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_zip'
                    }
                }
            });
        });

        it('should set enablePriority to 1 if priority is not 0', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'CreativeGroups/1337').respond({
                priority: 2
            });
            promise = CreativeGroupService.getCreativeGroup(1337);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith({
                priority: 2,
                enablePriority: 1,
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_zip'
                    }
                }
            });
        });

        it('should set originalName', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'CreativeGroups/1337').respond({
                name: 'someCreativeGroup',
                priority: 2
            });
            promise = CreativeGroupService.getCreativeGroup(1337);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith({
                enablePriority: 1,
                name: 'someCreativeGroup',
                originalName: 'someCreativeGroup',
                priority: 2,
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_zip'
                    }
                }
            });
        });

        it('should format geoTargets', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'CreativeGroups/1337').respond({
                name: 'someCreativeGroup',
                priority: 2,
                geoTargets: [
                    {
                        geoTarget: [
                            {
                                antiTarget: 0,
                                values: [
                                    {
                                        value: [
                                            {
                                                id: 29691,
                                                label: '80020 - Broomfield - CO'
                                            },
                                            {
                                                id: 2017,
                                                label: '01027 - Easthampton - MA'
                                            }
                                        ]
                                    }
                                ],
                                typeCode: 'geo_zip'
                            },
                            {
                                antiTarget: 0,
                                values: [
                                    {
                                        value: [
                                            {
                                                id: 24,
                                                label: 'Connecticut'
                                            }
                                        ]
                                    }
                                ],
                                typeCode: 'geo_state'
                            }
                        ]
                    }
                ]
            });
            promise = CreativeGroupService.getCreativeGroup(1337);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith({
                enablePriority: 1,
                name: 'someCreativeGroup',
                originalName: 'someCreativeGroup',
                priority: 2,
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [
                            {
                                id: 24,
                                label: 'Connecticut'
                            }
                        ],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [
                            {
                                id: 29691,
                                label: '80020 - Broomfield - CO'
                            },
                            {
                                id: 2017,
                                label: '01027 - Easthampton - MA'
                            }
                        ],
                        typeCode: 'geo_zip'
                    }
                }
            });
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'CreativeGroups/1337').respond(404);
                CreativeGroupService.getCreativeGroup(1337);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'CreativeGroups/1337').respond(404, 'FAILED');
                promise = CreativeGroupService.getCreativeGroup(1337);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('updateCreativeGroup()', function () {
        var creativeGroup;

        beforeEach(function () {
            creativeGroup = {
                cookieTarget: '[cp_type] = 1',
                daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                doCookieTargeting: 1,
                doDaypartTargeting: 1,
                doGeoTargeting: 1,
                enablePriority: 1,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                id: 9080620,
                impressionCap: 1,
                isDefault: 0,
                priority: 5,
                weight: 100
            };
        });

        it('should PUT w/ priority adjusted up to min', function () {
            var promise;

            creativeGroup.priority = -1;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 1,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ priority adjusted down to max', function () {
            var promise;

            creativeGroup.priority = 99;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 15,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ priority coerced to a number', function () {
            var promise;

            creativeGroup.priority = 'totallyNotANumber';
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 1,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/o priority if enablePriority is disabled', function () {
            var promise;

            creativeGroup.enablePriority = 0;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 0,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/o priority if isDefault is enabled', function () {
            var promise;

            creativeGroup.isDefault = 1;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 0,
                    doDaypartTargeting: 0,
                    doGeoTargeting: 0,
                    doOptimization: 0,
                    enableFrequencyCap: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 1,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 0,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ frequencyCap adjusted up to min', function () {
            var promise;

            creativeGroup.frequencyCap = -1;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ frequencyCap adjusted down to max', function () {
            var promise;

            creativeGroup.frequencyCap = 99999;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 9999,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ frequencyCap coerced to a number', function () {
            var promise;

            creativeGroup.frequencyCap = 'totallyNotANumber';
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ frequencyCapWindow adjusted up to min', function () {
            var promise;

            creativeGroup.frequencyCapWindow = -1;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 1,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ frequencyCapWindow adjusted down to max', function () {
            var promise;

            creativeGroup.frequencyCapWindow = 9999;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 999,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ frequencyCapWindow coerced to a number', function () {
            var promise;

            creativeGroup.frequencyCapWindow = 'totallyNotANumber';
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 1,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ weight adjusted up to min', function () {
            var promise;

            creativeGroup.weight = -1;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 0
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ weight adjusted down to max', function () {
            var promise;

            creativeGroup.weight = 999;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ weight coerced to a number', function () {
            var promise;

            creativeGroup.weight = 'totallyNotANumber';
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 0
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ impressionCap adjusted up to 0', function () {
            var promise;

            creativeGroup.impressionCap = -1;
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 0,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ impressionCap coerced to a number', function () {
            var promise;

            creativeGroup.impressionCap = 'totallyNotANumber';
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 0,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/o originalName', function () {
            var promise;

            creativeGroup.name = 'someCreativeGroup';
            creativeGroup.originalName = 'someCreativeGroup';
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: null,
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    name: 'someCreativeGroup',
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should PUT w/ formatted geoTargets', function () {
            var promise;

            creativeGroup.geoTargets = {
                geoZip: {
                    antiTarget: 0,
                    values: [
                        {
                            id: 29691,
                            label: '80020 - Broomfield - CO',
                            code: '80020',
                            typeId: 4
                        },
                        {
                            id: 2017,
                            label: '01027 - Easthampton - MA',
                            code: '01027',
                            typeId: 4
                        }
                    ],
                    typeCode: 'geo_zip'
                },
                geoState: {
                    antiTarget: 0,
                    values: [
                        {
                            id: 24,
                            label: 'Connecticut',
                            code: 'CT',
                            typeId: 2
                        }
                    ],
                    typeCode: 'geo_state'
                }
            };
            $httpBackend.expect(
                'PUT',
                API_SERVICE + 'CreativeGroups/9080620',
                {
                    clickthroughCap: -1,
                    cookieTarget: '[cp_type] = 1',
                    doOptimization: 0,
                    geoTargets: [
                        {
                            geoTarget: [
                                {
                                    antiTarget: 0,
                                    values: [
                                        {
                                            value: [
                                                {
                                                    id: 29691,
                                                    label: '80020 - Broomfield - CO'
                                                },
                                                {
                                                    id: 2017,
                                                    label: '01027 - Easthampton - MA'
                                                }
                                            ]
                                        }
                                    ],
                                    typeCode: 'geo_zip'
                                },
                                {
                                    antiTarget: 0,
                                    values: [
                                        {
                                            value: [
                                                {
                                                    id: 24,
                                                    label: 'Connecticut'
                                                }
                                            ]
                                        }
                                    ],
                                    typeCode: 'geo_state'
                                }
                            ]
                        }
                    ],
                    daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                    doCookieTargeting: 1,
                    doDaypartTargeting: 1,
                    doGeoTargeting: 1,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    id: 9080620,
                    impressionCap: 1,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    priority: 5,
                    weight: 100
                }
            ).respond({});
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        it('should format response w/ enablePriority set to 0 if priority is 0', function () {
            var promise;

            creativeGroup.priority = 0;
            $httpBackend.expect('PUT', API_SERVICE + 'CreativeGroups/9080620', /.*/)
                .respond(_.omit(creativeGroup, 'enablePriority'));
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith({
                cookieTarget: '[cp_type] = 1',
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_zip'
                    }
                },
                daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                doCookieTargeting: 1,
                doDaypartTargeting: 1,
                doGeoTargeting: 1,
                enablePriority: 0,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                id: 9080620,
                impressionCap: 1,
                isDefault: 0,
                priority: 0,
                weight: 100
            });
        });

        it('should format response w/ enablePriority set to 1 if priority is not 0', function () {
            var promise;

            creativeGroup.priority = 5;
            $httpBackend.expect('PUT', API_SERVICE + 'CreativeGroups/9080620', /.*/)
                .respond(_.omit(creativeGroup, 'enablePriority'));
            promise = CreativeGroupService.updateCreativeGroup(creativeGroup);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith({
                cookieTarget: '[cp_type] = 1',
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_zip'
                    }
                },
                daypartTarget: 'browserlocaltime >= 0600 AND browserlocaltime < 0800',
                doCookieTargeting: 1,
                doDaypartTargeting: 1,
                doGeoTargeting: 1,
                enablePriority: 1,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                id: 9080620,
                impressionCap: 1,
                isDefault: 0,
                priority: 5,
                weight: 100
            });
        });
    });

    describe('getCreativeList()', function () {
        it('should resolve promise with response.records[0].CreativeGroupCreative', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'CreativeGroups/1337/creatives').respond({
                records: [
                    {
                        CreativeGroupCreative: [
                            {
                                alias: 'a'
                            },
                            {
                                alias: 'b'
                            },
                            {
                                alias: 'c'
                            }
                        ]
                    }
                ]
            });
            promise = CreativeGroupService.getCreativeList(1337);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith([
                {
                    alias: 'a'
                },
                {
                    alias: 'b'
                },
                {
                    alias: 'c'
                }
            ]);
        });

        it('should wrap single record response in an array', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'CreativeGroups/1337/creatives').respond({
                records: [
                    {
                        CreativeGroupCreative: {
                            alias: 'a'
                        }
                    }
                ]
            });
            promise = CreativeGroupService.getCreativeList(1337);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith([
                {
                    alias: 'a'
                }
            ]);
        });
    });

    describe('removeCreativeGroup()', function () {
        it('should DELETE creative group', function () {
            var promise;

            $httpBackend.whenDELETE(API_SERVICE + 'CreativeGroups/1337').respond({});
            promise = CreativeGroupService.removeCreativeGroup(1337);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('forceRemoveCreativeGroup()', function () {
        it('should DELETE creative group with recursiveDelete', function () {
            var promise;

            $httpBackend.whenDELETE(API_SERVICE + 'CreativeGroups/1337?recursiveDelete=true').respond({});
            promise = CreativeGroupService.forceRemoveCreativeGroup(1337);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });

    describe('saveCreativeGroup()', function () {
        it('should POST creative group', function () {
            var promise;

            $httpBackend.expect(
                'POST',
                API_SERVICE + 'CreativeGroups',
                {
                    campaignId: 69,
                    clickthroughCap: -1,
                    cookieTarget: null,
                    daypartTarget: null,
                    doCookieTargeting: 0,
                    doDaypartTargeting: 0,
                    doOptimization: 0,
                    enableFrequencyCap: 0,
                    enableGroupWeight: 0,
                    frequencyCap: 1,
                    frequencyCapWindow: 24,
                    geoTargets: [
                        {
                            geoTarget: [
                                {
                                    antiTarget: 0,
                                    values: [
                                        {
                                            value: [
                                                {
                                                    id: 24,
                                                    label: 'Connecticut'
                                                }
                                            ]
                                        }
                                    ],
                                    typeCode: 'geo_state'
                                },
                                {
                                    antiTarget: 0,
                                    values: [
                                        {
                                            value: [
                                                {
                                                    id: 29691,
                                                    label: '80020 - Broomfield - CO'
                                                },
                                                {
                                                    id: 2017,
                                                    label: '01027 - Easthampton - MA'
                                                }
                                            ]
                                        }
                                    ],
                                    typeCode: 'geo_zip'
                                }
                            ]
                        }
                    ],
                    impressionCap: 0,
                    isDefault: 0,
                    isReleased: 0,
                    minOptimizationWeight: -1,
                    name: 'someCreativeGroup',
                    priority: 0,
                    weight: 100
                }
            ).respond({
                campaignId: 69,
                clickthroughCap: -1,
                cookieTarget: null,
                daypartTarget: null,
                doCookieTargeting: 0,
                doDaypartTargeting: 0,
                doOptimization: 0,
                enableFrequencyCap: 0,
                enableGroupWeight: 0,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                geoTargets: [
                    {
                        geoTarget: [
                            {
                                antiTarget: 0,
                                values: [
                                    {
                                        value: [
                                            {
                                                id: 24,
                                                label: 'Connecticut'
                                            }
                                        ]
                                    }
                                ],
                                typeCode: 'geo_state'
                            },
                            {
                                antiTarget: 0,
                                values: [
                                    {
                                        value: [
                                            {
                                                id: 29691,
                                                label: '80020 - Broomfield - CO'
                                            },
                                            {
                                                id: 2017,
                                                label: '01027 - Easthampton - MA'
                                            }
                                        ]
                                    }
                                ],
                                typeCode: 'geo_zip'
                            }
                        ]
                    }
                ],
                id: 1337,
                impressionCap: 0,
                isDefault: 0,
                isReleased: 0,
                minOptimizationWeight: -1,
                name: 'someCreativeGroup',
                priority: 0,
                weight: 100
            });
            promise = CreativeGroupService.saveCreativeGroup({
                campaignId: 69,
                cookieTarget: null,
                daypartTarget: null,
                doCookieTargeting: 0,
                doDaypartTargeting: 0,
                enableFrequencyCap: 0,
                enableGroupWeight: 0,
                enablePriority: 0,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [
                            {
                                id: 24,
                                label: 'Connecticut'
                            }
                        ],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [
                            {
                                id: 29691,
                                label: '80020 - Broomfield - CO'
                            },
                            {
                                id: 2017,
                                label: '01027 - Easthampton - MA'
                            }
                        ],
                        typeCode: 'geo_zip'
                    }
                },
                isDefault: 0,
                name: 'someCreativeGroup',
                priority: 0,
                weight: 100
            });
            $httpBackend.flush();

            expect(promise).toBeResolvedWith({
                campaignId: 69,
                clickthroughCap: -1,
                cookieTarget: null,
                daypartTarget: null,
                doCookieTargeting: 0,
                doDaypartTargeting: 0,
                doOptimization: 0,
                enableFrequencyCap: 0,
                enableGroupWeight: 0,
                enablePriority: 0,
                frequencyCap: 1,
                frequencyCapWindow: 24,
                geoTargets: {
                    geoCountry: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_country'
                    },
                    geoState: {
                        antiTarget: 0,
                        values: [
                            {
                                id: 24,
                                label: 'Connecticut'
                            }
                        ],
                        typeCode: 'geo_state'
                    },
                    geoDma: {
                        antiTarget: 0,
                        values: [],
                        typeCode: 'geo_dma'
                    },
                    geoZip: {
                        antiTarget: 0,
                        values: [
                            {
                                id: 29691,
                                label: '80020 - Broomfield - CO'
                            },
                            {
                                id: 2017,
                                label: '01027 - Easthampton - MA'
                            }
                        ],
                        typeCode: 'geo_zip'
                    }
                },
                id: 1337,
                impressionCap: 0,
                isDefault: 0,
                isReleased: 0,
                minOptimizationWeight: -1,
                name: 'someCreativeGroup',
                originalName: 'someCreativeGroup',
                priority: 0,
                weight: 100
            });
        });
    });

    describe('createAssociations()', function () {
        var promise,
            associations =
            {
                campaignId: '5959474',
                creativeFilenames: ['file1.jpg', 'file2.jpg'],
                creativeGroupIds: ['1337', '1338', '1339']
            };

        it('should associate creatives with creative-groups', function () {
            $httpBackend.whenPUT(API_SERVICE + 'CreativeGroups/createAssociations').respond({});
            promise = CreativeGroupService.createAssociations(associations);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });

        describe('on failure', function () {
            it('should reject promise', function () {
                $httpBackend.whenPUT(API_SERVICE + 'CreativeGroups/createAssociations').respond(404, 'FAILED');
                promise = CreativeGroupService.createAssociations(associations);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });

    describe('searchByCampaignAndName()', function () {
        it('should get a CreativeGroups array based on CampaignId and Name criteria', function () {
            var name = 'Def',
                campaignId = 6031313,
                promise,
                response = {
                    startIndex: 0,
                    pageSize: 1000,
                    totalNumberOfRecords: 11,
                    records: [
                        {
                            CreativeGroup: [
                                {
                                    campaignId: 6031313,
                                    name: 'Default'
                                }
                            ]
                        }
                    ]
                };

            $httpBackend.whenGET(API_SERVICE + 'CreativeGroups?' +
                'query=name+contains+%22Def%22' +
                '+and+campaignId+%3D+6031313+sorting+name+asc')
                .respond(response);
            promise = CreativeGroupService.searchByCampaignAndName(campaignId, name);
            $httpBackend.flush();

            expect(promise).toBeResolved();
        });
    });
});
