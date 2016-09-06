'use strict';

describe('Service: SiteMeasurementsUtilService', function () {
    var SiteMeasurementsUtilService,
        DateTimeService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_DateTimeService_, _SiteMeasurementsUtilService_) {
        DateTimeService = _DateTimeService_;
        SiteMeasurementsUtilService = _SiteMeasurementsUtilService_;
    }));

    describe('getIOStatusList()', function () {
        it('should return the IOs status list', function () {
            var statusList = [
                {
                    key: true,
                    name: 'global.active'
                },
                {
                    key: false,
                    name: 'global.inactive'
                }
            ];

            expect(SiteMeasurementsUtilService.getStatusList()).toEqual(statusList);
        });
    });

    describe('getExpirationDate()', function () {
        it('should return expiration date based on active/inactive status', function () {
            var currentMomentDate = DateTimeService.getMoment(),
                expectedDate;

            expectedDate = DateTimeService.inverseParse(currentMomentDate.subtract(1, 'day').toDate());
            expect(SiteMeasurementsUtilService.getExpirationDate(false)).toEqual(expectedDate);

            currentMomentDate = DateTimeService.getMoment();
            expectedDate = DateTimeService.inverseParse(currentMomentDate.add(10, 'year').toDate());
            expect(SiteMeasurementsUtilService.getExpirationDate(true)).toEqual(expectedDate);
        });
    });
});
