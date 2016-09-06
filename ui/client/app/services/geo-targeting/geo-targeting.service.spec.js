'use strict';

describe('Service: GeoTargetingService', function () {
    var API_SERVICE,
        $httpBackend,
        GeoTargetingService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _API_SERVICE_, _GeoTargetingService_, $state) {
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        GeoTargetingService = _GeoTargetingService_;

        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('getCountries()', function () {
        it('should get list of countries', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'GeoLocations/countries').respond({
                records: [
                    {
                        GeoLocation: [
                            {
                                code: 'CA',
                                id: 114,
                                label: 'Canada',
                                typeId: 2
                            },
                            {
                                code: 'MX',
                                id: 229,
                                label: 'Mexico',
                                typeId: 2
                            },
                            {
                                code: 'US',
                                id: 302,
                                label: 'United States',
                                typeId: 2
                            }
                        ]
                    }
                ]
            });

            promise = GeoTargetingService.getCountries();
            $httpBackend.flush();

            expect(promise).toBeResolvedWith([
                {
                    code: 'CA',
                    id: 114,
                    label: 'Canada',
                    typeId: 2
                },
                {
                    code: 'MX',
                    id: 229,
                    label: 'Mexico',
                    typeId: 2
                },
                {
                    code: 'US',
                    id: 302,
                    label: 'United States',
                    typeId: 2
                }
            ]);
        });
    });

    describe('getStates()', function () {
        it('should get list of states', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'GeoLocations/states').respond({
                records: [
                    {
                        GeoLocation: [
                            {
                                code: 'CO',
                                id: 23,
                                label: 'Colorado',
                                typeId: 1
                            },
                            {
                                code: 'TX',
                                id: 67,
                                label: 'Texas',
                                typeId: 1
                            }
                        ]
                    }
                ]
            });

            promise = GeoTargetingService.getStates();
            $httpBackend.flush();

            expect(promise).toBeResolvedWith([
                {
                    code: 'CO',
                    id: 23,
                    label: 'Colorado',
                    typeId: 1
                },
                {
                    code: 'TX',
                    id: 67,
                    label: 'Texas',
                    typeId: 1
                }
            ]);
        });
    });

    describe('getDMAs()', function () {
        it('should get list of DMAs', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'GeoLocations/dmas').respond({
                records: [
                    {
                        GeoLocation: [
                            {
                                code: '751',
                                id: 1088,
                                label: 'Denver, CO',
                                typeId: 3
                            },
                            {
                                code: '752',
                                id: 1090,
                                label: 'Colorado Springs, CO',
                                typeId: 3
                            },
                            {
                                code: '773',
                                id: 1122,
                                label: 'Grand Junction, CO',
                                typeId: 3
                            }
                        ]
                    }
                ]
            });

            promise = GeoTargetingService.getDMAs();
            $httpBackend.flush();

            expect(promise).toBeResolvedWith([
                {
                    code: '751',
                    id: 1088,
                    label: 'Denver, CO',
                    typeId: 3
                },
                {
                    code: '752',
                    id: 1090,
                    label: 'Colorado Springs, CO',
                    typeId: 3
                },
                {
                    code: '773',
                    id: 1122,
                    label: 'Grand Junction, CO',
                    typeId: 3
                }
            ]);
        });
    });

    describe('getZips()', function () {
        it('should get list of zips', function () {
            var promise;

            $httpBackend.whenGET(API_SERVICE + 'GeoLocations/zips').respond({
                records: [
                    {
                        GeoLocation: [
                            {
                                code: '80021',
                                id: 29692,
                                label: '80021 - Broomfield - CO',
                                typeId: 4
                            },
                            {
                                code: '80002',
                                id: 29675,
                                label: '80002 - Arvada - CO',
                                typeId: 4
                            }
                        ]
                    }
                ]
            });

            promise = GeoTargetingService.getZips();
            $httpBackend.flush();

            expect(promise).toBeResolvedWith([
                {
                    code: '80021',
                    id: 29692,
                    label: '80021 - Broomfield - CO',
                    typeId: 4
                },
                {
                    code: '80002',
                    id: 29675,
                    label: '80002 - Arvada - CO',
                    typeId: 4
                }
            ]);
        });
    });
});
