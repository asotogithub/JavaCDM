'use strict';

describe('Service: SMEventsFilterService', function () {
    var SMEventsFilterService,
        filterValues,
        filterOptions,
        model,
        value1 = ['0_GroupPZ'],
        value2 = ['Standard', 'TruTag'];

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_SMEventsFilterService_) {
        SMEventsFilterService = _SMEventsFilterService_;
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
                eventName: 'QAEvent00',
                eventType: 0,
                eventTypeName: 'Measured',
                groupName: '0_GroupPZ',
                id: 15258,
                isTrafficked: 1,
                smEventType: 3,
                smGroupId: 11663,
                tagTypeName: 'Standard'
            },
            {
                eventName: 'Event_Tru',
                eventType: 1,
                eventTypeName: 'Conversion',
                groupName: '0_GroupPZ',
                id: 15259,
                isTrafficked: 1,
                smEventType: 1,
                smGroupId: 11663,
                tagTypeName: 'TruTag'
            }
        ];
    }));

    describe('loadFilters()', function () {
        it('should extract options and values from the model', function () {
            SMEventsFilterService.loadFilters(model, filterOptions, filterValues);
            expect(filterOptions[0].value).toEqual(['Conversion', 'Measured']);
            expect(filterOptions[1].value).toEqual(value1);
            expect(filterOptions[2].value).toEqual(value2);
            expect(filterValues[0].values).toEqual(['Measured', 'Conversion']);
            expect(filterValues[1].values).toEqual(value1);
            expect(filterValues[2].values).toEqual(value2);
        });
    });

    describe('updateGroupFilter()', function () {
        it('should update the group filter options', function () {
            var oldFilterValues;

            SMEventsFilterService.loadFilters(model, filterOptions, filterValues);
            expect(filterOptions[1].value).toEqual(value1);
            expect(filterValues[1].values).toEqual(value1);
            oldFilterValues = angular.copy(filterValues);
            filterValues[0].values = [];
            SMEventsFilterService.updateGroupFilter(filterOptions, filterValues, oldFilterValues[0].values);
            expect(filterOptions[1].value).toEqual([]);
            expect(filterValues[1].values).toEqual([]);
        });
    });

    describe('updateTagTypeFilter()', function () {
        it('should update the tag type filter options', function () {
            SMEventsFilterService.loadFilters(model, filterOptions, filterValues);
            expect(filterOptions[2].value).toEqual(value2);
            expect(filterValues[2].values).toEqual(value2);
            filterValues[1].values = ['One Group'];
            SMEventsFilterService.updateTagTypeFilter(filterOptions, filterValues);
            expect(filterOptions[2].value).toEqual([]);
            expect(filterValues[2].values).toEqual([]);
        });
    });
});
