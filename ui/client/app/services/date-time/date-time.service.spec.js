'use strict';

describe('Service: DateTimeService', function () {
    var $moment = null,
        dateTimeService = null,
        FULL_FORMAT = null,
        DEFAULT_FORMAT_DATE = null,
        DATE_TIME_US_FORMAT = null;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$moment_, DateTimeService) {
        $moment = _$moment_;
        dateTimeService = DateTimeService;
        FULL_FORMAT = dateTimeService.FORMAT.DATE_TIME_FULL;
        DEFAULT_FORMAT_DATE = dateTimeService.FORMAT.DATE;
        DATE_TIME_US_FORMAT = dateTimeService.FORMAT.DATE_TIME_US;
    }));

    describe('customFormat()', function () {
        it('should format using an input and output formats', function () {
            var date = '2015-09-28T00:00:00-07:00',
                inputFormat = dateTimeService.FORMAT.DATE_TIME_FULL,
                outputFormat = 'YYYY-MM-DD';

            expect(dateTimeService.customFormat(date, inputFormat, outputFormat)).toEqual('2015-09-28');

            date = '2015-09-28';
            inputFormat = 'YYYY-MM-DD';
            outputFormat = 'MM/DD/YYYY';
            expect(dateTimeService.customFormat(date, inputFormat, outputFormat)).toEqual('09/28/2015');

            date = '2015-09-28T00:00:00-07:00';
            inputFormat = dateTimeService.FORMAT.DATE_TIME_FULL;
            outputFormat = 'YYYY-MM-DD';
            expect(dateTimeService.customFormat(date, inputFormat, outputFormat)).toEqual('2015-09-28');
        });

        it('should return null for invalid parameters', function () {
            expect(dateTimeService.customFormat(null, 'YYYY-MM-DD', 'MM/DD/YYYY')).toBeNull();
            expect(dateTimeService.customFormat('2015-09-28', null, null)).toBeNull();
            expect(dateTimeService.customFormat(null, null, null)).toBeNull();
        });
    });

    describe('format()', function () {
        it('should format a text date using a default pattern', function () {
            expect(dateTimeService.format('2015-09-28T00:00:00-07:00')).toEqual('09/28/2015');
        });

        it('should format a text date using a pattern', function () {
            var date = '2015-09-28T00:00:00-07:00',
                dateOffset = dateTimeService.applyOffset(date);

            expect(dateTimeService.format(date, DEFAULT_FORMAT_DATE)).toEqual('09/28/2015');
            expect(dateTimeService.format(dateOffset, FULL_FORMAT)).toEqual(dateTimeService.applyOffset(date));
        });

        it('should return null for invalid dates', function () {
            expect(dateTimeService.format('2015-09-32T00:00:00-07:00')).toBeNull();
            expect(dateTimeService.format('2015-15-01T00:00:00-07:00', FULL_FORMAT)).toBeNull();
        });
    });

    describe('formatDate()', function () {
        var date = null,
            dateMoment = null;

        beforeEach(function () {
            date = new Date();
            dateMoment = $moment(date).tz(dateTimeService.TIMEZONE);
        });

        it('should format a date object using a default pattern', function () {
            var dateFormatted = dateMoment.format(DEFAULT_FORMAT_DATE);

            expect(dateTimeService.formatDate(date)).toEqual(dateFormatted);
        });

        it('should format a date object using a pattern', function () {
            var customFormat = 'YYYY-MM-DD';

            expect(dateTimeService.formatDate(date, DEFAULT_FORMAT_DATE))
                .toEqual(dateMoment.format(DEFAULT_FORMAT_DATE));
            expect(dateTimeService.formatDate(date, customFormat)).toEqual(dateMoment.format(customFormat));
        });

        it('should return null for invalid dates', function () {
            expect(dateTimeService.formatDate(null)).toBeNull();
        });
    });

    describe('getDate()', function () {
        var momentFromString = null,
            dateString = '2015-09-30T00:00:00-04:00';

        beforeEach(function () {
            momentFromString = $moment(dateString);
        });

        it('should return a date from an input date(using input string)', function () {
            var moment = $moment('2015-09-30T00:00:00-04:00'),
                date = dateTimeService.getDate(moment.toDate());

            expect(date).toEqual(moment.tz(dateTimeService.TIMEZONE).toDate());
        });

        it('should return a date from an input date(using input date)', function () {
            var moment = $moment(new Date()),
                date = dateTimeService.getDate(moment.toDate());

            expect(date).toEqual(moment.tz(dateTimeService.TIMEZONE).toDate());
        });

        it('should return current date', function () {
            expect(dateTimeService.getDate()).not.toBeNull();
        });
    });

    describe('parse()', function () {
        it('should return a date from text', function () {
            var todayString = '2015-10-02T20:58:24-07:00',
                date = dateTimeService.parse(todayString);

            expect($moment(date).format(DEFAULT_FORMAT_DATE)).toEqual('10/02/2015');
        });

        it('should return null for invalid text', function () {
            expect(dateTimeService.parse(null)).toBeNull();
        });
    });

    describe('getStartDate()', function () {
        it('should set hour to 00:00:00', function () {
            var startDate = dateTimeService.parse('2015-09-05T12:11:11-07:00');

            startDate = dateTimeService.getStartDate(startDate);

            expect(dateTimeService.inverseParse(startDate))
                .toEqual('2015-09-05T00:00:00-07:00');
        });
    });

    describe('getEndDate()', function () {
        it('should set hour to 23:59:59', function () {
            var endDate = dateTimeService.parse('2015-09-15T13:22:22-07:00');

            endDate = dateTimeService.getEndDate(endDate);

            expect(dateTimeService.inverseParse(endDate))
                .toEqual('2015-09-15T23:59:59-07:00');
        });
    });

    describe('isBefore()', function () {
        var startDate = '01/01/2001',
            endDate = '01/02/2001',
            startDateTime = '01/01/2001 10:00:00 AM',
            endDateTime = '01/01/2001 10:00:00 PM';

        it('should compare if a date1 is before than date2', function () {
            expect(dateTimeService.isBefore(startDate, startDate, DEFAULT_FORMAT_DATE))
                .toBe(false);

            expect(dateTimeService.isBefore(endDate, startDate, DEFAULT_FORMAT_DATE))
                .toBe(false);

            expect(dateTimeService.isBefore(startDate, endDate, DEFAULT_FORMAT_DATE))
                .toBe(true);
        });

        it('should compare if first date time is before than second date time', function () {
            expect(dateTimeService.isBefore(startDateTime, startDateTime, DATE_TIME_US_FORMAT))
                .toBe(false);

            expect(dateTimeService.isBefore(endDateTime, startDateTime, DATE_TIME_US_FORMAT))
                .toBe(false);

            expect(dateTimeService.isBefore(startDateTime, endDateTime, DATE_TIME_US_FORMAT))
                .toBe(true);
        });
    });

    describe('isBeforeOrEqual()', function () {
        var startDate = '01/01/2001',
            endDate = '01/02/2001',
            startDateTime = '01/01/2001 10:00:00 AM',
            endDateTime = '01/01/2001 10:00:00 PM';

        it('should compare if a date1 is before or equal to date2', function () {
            expect(dateTimeService.isBeforeOrEqual(startDate, endDate, DEFAULT_FORMAT_DATE))
                .toBe(true);

            expect(dateTimeService.isBeforeOrEqual(startDate, startDate, DEFAULT_FORMAT_DATE))
                .toBe(true);

            expect(dateTimeService.isBeforeOrEqual(endDate, endDate, DEFAULT_FORMAT_DATE))
                .toBe(true);

            expect(dateTimeService.isBeforeOrEqual(endDate, startDate, DEFAULT_FORMAT_DATE))
                .toBe(false);
        });

        it('should compare if first date time is before or equal to second date time', function () {
            expect(dateTimeService.isBeforeOrEqual(startDateTime, endDateTime, DATE_TIME_US_FORMAT))
                .toBe(true);

            expect(dateTimeService.isBeforeOrEqual(startDateTime, startDateTime, DATE_TIME_US_FORMAT))
                .toBe(true);

            expect(dateTimeService.isBeforeOrEqual(endDateTime, endDateTime, DATE_TIME_US_FORMAT))
                .toBe(true);

            expect(dateTimeService.isBeforeOrEqual(endDateTime, startDateTime, DATE_TIME_US_FORMAT))
                .toBe(false);
        });
    });

    describe('isEqual()', function () {
        var startDate = '01/01/2001',
            endDate = '01/02/2001',
            startDateTime = '01/01/2001 10:00:00 AM',
            endDateTime = '01/01/2001 10:00:00 PM';

        it('should compare if a date1 is equal to date2', function () {
            expect(dateTimeService.isEqual(startDate, startDate, DEFAULT_FORMAT_DATE))
                .toBe(true);

            expect(dateTimeService.isEqual(endDate, endDate, DEFAULT_FORMAT_DATE))
                .toBe(true);

            expect(dateTimeService.isEqual(startDate, endDate, DEFAULT_FORMAT_DATE))
                .toBe(false);

            expect(dateTimeService.isEqual(endDate, startDate, DEFAULT_FORMAT_DATE))
                .toBe(false);
        });

        it('should compare if first date time is equal to second date time', function () {
            expect(dateTimeService.isEqual(startDateTime, startDateTime, DATE_TIME_US_FORMAT))
                .toBe(true);

            expect(dateTimeService.isEqual(endDateTime, endDateTime, DATE_TIME_US_FORMAT))
                .toBe(true);

            expect(dateTimeService.isEqual(startDateTime, endDateTime, DATE_TIME_US_FORMAT))
                .toBe(false);

            expect(dateTimeService.isEqual(endDateTime, startDateTime, DATE_TIME_US_FORMAT))
                .toBe(false);
        });
    });
});
