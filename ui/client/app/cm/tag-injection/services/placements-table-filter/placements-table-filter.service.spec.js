'use strict';

describe('Service: PlacementsTableFilterService', function () {
    var PlacementsTableFilterService,
        filterValues,
        filterOptions,
        model;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_PlacementsTableFilterService_) {
        PlacementsTableFilterService = _PlacementsTableFilterService_;
        filterOptions = [
            {
                value: []
            },
            {
                value: []
            },
            {
                value: []
            }
        ];
        filterValues = [
            {
                values: []
            },
            {
                values: []
            },
            {
                values: []
            }
        ];
        model = [
            {
                campaignId: 91253,
                campaignName: 'My Campaign',
                endDate: '2016-02-05T23:59:59-07:00',
                id: 1334,
                name: 'Placement 1',
                siteId: 62791,
                siteName: 'One Site',
                startDate: '2016-01-06T00:00:00-07:00'
            },
            {
                campaignId: 91253,
                campaignName: 'My Campaign',
                endDate: '2016-02-06T23:59:59-07:00',
                id: 8724,
                name: 'Placement 2',
                siteId: 57894,
                siteName: 'Another Site',
                startDate: '2016-01-07T00:00:00-07:00'
            }
        ];
    }));

    describe('loadFilterOptions()', function () {
        it('should extract options and values from the model', function () {
            PlacementsTableFilterService.loadFilterOptions(model, filterOptions, filterValues);
            expect(filterOptions[0].value).toEqual([
                {
                    id: 91253,
                    label: 'My Campaign',
                    enabled: true
                }
            ]);
            expect(filterOptions[1].value).toEqual([
                {
                    id: 57894,
                    label: 'Another Site',
                    enabled: true
                },
                {
                    id: 62791,
                    label: 'One Site',
                    enabled: true
                }
            ]);
            expect(filterOptions[2].value).toEqual([
                {
                    id: 1334,
                    label: 'Placement 1',
                    enabled: true
                },
                {
                    id: 8724,
                    label: 'Placement 2',
                    enabled: true
                }
            ]);
            expect(filterValues[0].values).toEqual(['My Campaign']);
            expect(filterValues[1].values).toEqual(['Another Site', 'One Site']);
            expect(filterValues[2].values).toEqual(['Placement 1', 'Placement 2']);
        });
    });

    describe('mappingFilters()', function () {
        it('should map the options from a row of the model', function () {
            PlacementsTableFilterService.mappingFilters(model[0]);
            expect(PlacementsTableFilterService.getPlacementsMap()).toEqual({
                'Placement 1': {
                    campaign: ['My Campaign'],
                    site: ['One Site']
                }
            });
            expect(PlacementsTableFilterService.getSitesMap()).toEqual({
                'One Site': {
                    campaign: ['My Campaign'],
                    placement: ['Placement 1']
                }
            });
            PlacementsTableFilterService.mappingFilters(model[1]);
            expect(PlacementsTableFilterService.getPlacementsMap()).toEqual({
                'Placement 1': {
                    campaign: ['My Campaign'],
                    site: ['One Site']
                },
                'Placement 2': {
                    campaign: ['My Campaign'],
                    site: ['Another Site']
                }
            });
            expect(PlacementsTableFilterService.getSitesMap()).toEqual({
                'One Site': {
                    campaign: ['My Campaign'],
                    placement: ['Placement 1']
                },
                'Another Site': {
                    campaign: ['My Campaign'],
                    placement: ['Placement 2']
                }
            });
        });
    });

    describe('siteIdByName()', function () {
        it('should return the site id that matches with a given site name', function () {
            expect(PlacementsTableFilterService.siteIdByName(model, 'One Site')).toEqual(62791);
            expect(PlacementsTableFilterService.siteIdByName(model, 'Another Site')).toEqual(57894);
        });
    });

    describe('placementIdByName()', function () {
        it('should return the placement id that matches with a given placement name', function () {
            expect(PlacementsTableFilterService.placementIdByName(model, 'Placement 1')).toEqual(1334);
            expect(PlacementsTableFilterService.placementIdByName(model, 'Placement 2')).toEqual(8724);
        });
    });

    describe('updateSite()', function () {
        it('should update the site filter options, removing a hidden campaign\'s children', function () {
            var oldFilterValues;

            PlacementsTableFilterService.loadFilterOptions(model, filterOptions, filterValues);
            expect(filterOptions[1].value).toEqual([
                {
                    id: 57894,
                    label: 'Another Site',
                    enabled: true
                },
                {
                    id: 62791,
                    label: 'One Site',
                    enabled: true
                }
            ]);
            expect(filterValues[1].values).toEqual(['Another Site', 'One Site']);
            oldFilterValues = angular.copy(filterValues);
            filterValues[0].values = [];
            PlacementsTableFilterService.updateSite(filterValues, oldFilterValues[0].values, model, filterOptions);
            expect(filterOptions[1].value).toEqual([]);
            expect(filterValues[1].values).toEqual([]);
        });
    });

    describe('updatePlacement()', function () {
        it('should update the placement filter options, removing a hidden site\'s children', function () {
            var oldFilterValues;

            PlacementsTableFilterService.loadFilterOptions(model, filterOptions, filterValues);
            expect(filterOptions[2].value).toEqual([
                {
                    id: 1334,
                    label: 'Placement 1',
                    enabled: true
                },
                {
                    id: 8724,
                    label: 'Placement 2',
                    enabled: true
                }
            ]);
            expect(filterValues[2].values).toEqual(['Placement 1', 'Placement 2']);
            oldFilterValues = angular.copy(filterValues);
            filterValues[1].values = ['One Site'];
            PlacementsTableFilterService.updatePlacement(filterValues, oldFilterValues[1].values, model, filterOptions);
            expect(filterOptions[2].value).toEqual([
                {
                    id: 1334,
                    label: 'Placement 1',
                    enabled: true
                }
            ]);
            expect(filterValues[2].values).toEqual(['Placement 1']);
        });
    });
});
