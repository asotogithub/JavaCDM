(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('DateTimeService', DateTimeService);

    DateTimeService.$inject = [
        '$moment',
        'CONSTANTS'
    ];

    function DateTimeService($moment,
                             CONSTANTS) {
        this.FORMAT = {
            DATE_TIME_US: CONSTANTS.DATE.MOMENT.DATE_TIME_US,
            DATE_TIME_FULL: CONSTANTS.DATE.MOMENT.DATE_FULL,
            DATE: CONSTANTS.DATE.MOMENT.DATE,
            DATE_DAY_TIME_FULL: CONSTANTS.DATE.MOMENT.DATE_DAY_TIME_FULL
        };
        this.TIMEZONE = CONSTANTS.TIMEZONE.DEFAULT;

        var that = this;

        this.applyOffset = function (textDate, customOffset) {
            var currentOffset = $moment().format('Z'),
                dateOffset = $moment(textDate).format('Z'),
                offset = currentOffset;

            if (customOffset) {
                offset = customOffset;
            }
            else if (currentOffset !== dateOffset) {
                offset = dateOffset;
            }

            return textDate.substr(textDate, textDate.length - 6).concat(offset);
        };

        /**
         * Format a text date as another text using input/output formats.
         * @param text A text date that should be formatted.
         * @param inputPattern input pattern of the text date
         * @param outputPatter output pattern of the text date
         * @returns {a text of the Date formatted in the output pattern.}
         */
        this.customFormat = function (text, inputPattern, outputPattern) {
            if (!angular.isString(text) || !angular.isString(inputPattern) ||
                !angular.isString(outputPattern)) {
                return null;
            }

            var moment = getDateFromPattern(text, inputPattern);

            return moment ? moment.format(outputPattern) : null;
        };

        /**
         * Parses text to produce a Date
         * @param text A text that should be parsed.
         * @param pattern the pattern of given text
         * @returns {a Date parsed from the text. In case of error, returns null.}
         */
        this.customParse = function (text, pattern) {
            if (!angular.isString(text) || !angular.isString(pattern)) {
                return null;
            }

            var moment = getDateFromPattern(text, pattern);

            return moment ? moment.toDate() : null;
        };

        /**
         * Format a text as date using given pattern.
         * @param text A text that should be formatted.
         * @param pattern pattern to be used as a format for the date.
         * @returns {a text of the Date formatted. In case of error, returns null.}
         */
        this.format = function (text, pattern) {
            if (!angular.isString(text)) {
                return null;
            }

            var formatPattern = this.FORMAT.DATE,
                date = getDate(text);

            if (angular.isString(pattern)) {
                formatPattern = pattern;
            }

            return date ? date.format(formatPattern) : null;
        };

        /**
         * Format a date object using given pattern.
         * @param date A date object that should be formatted.
         * @param pattern pattern to be used as a format for the date.
         * @param timeZone format a date using timezone (e.g. MST).
         * @returns {a text of the Date formatted. In case of error, returns null.}
         */
        this.formatDate = function (date, pattern, timeZone) {
            var formatPattern = this.FORMAT.DATE;

            if (pattern) {
                formatPattern = pattern;
            }

            if (timeZone) {
                return date ? $moment.tz(date, timeZone).format(formatPattern) : null;
            }

            return date ? $moment(date).format(formatPattern) : null;
        };

        /**
         * Get current date object using default timezone.
         * @param date optional parameter, to be used to create the new date.
         * @returns {a date object. In case of error, returns null.}
         */
        this.getDate = function (date) {
            var momentDate = date ? $moment(date) : $moment();

            if (!momentDate.isValid()) {
                return null;
            }

            return momentDate.tz(that.TIMEZONE).toDate();
        };

        /**
         * Get a moment object
         * @param date optional parameter, to be used to create the new moment object
         * @returns {a moment object. In case of error, returns null}
         */
        this.getMoment = function (date) {
            var momentDate = date ? $moment(date) : $moment();

            if (!momentDate.isValid()) {
                return null;
            }

            return momentDate.tz(that.TIMEZONE);
        };

        this.getStartDate = function (date) {
            if (!angular.isDate(date)) {
                return null;
            }

            date.setHours(0, 0, 0);
            return date;
        };

        this.getEndDate = function (date) {
            if (!angular.isDate(date)) {
                return null;
            }

            date.setHours(23, 59, 59);
            return date;
        };

        this.inverseParse = function (date) {
            if (!angular.isDate(date)) {
                return null;
            }

            var newText = $moment(date).format(that.FORMAT.DATE_TIME_FULL);

            return that.applyOffset(newText, '-07:00');
        };

        this.isBefore = function (startDate, endDate, pattern) {
            var startDateMoment = $moment(startDate, pattern),
                endDateMoment = $moment(endDate, pattern);

            return startDateMoment.isValid() &&
                endDateMoment.isValid() &&
                startDateMoment.isBefore(endDateMoment);
        };

        this.isBeforeOrEqual = function (startDate, endDate, pattern) {
            var startDateMoment = $moment(startDate, pattern),
                endDateMoment = $moment(endDate, pattern);

            return startDateMoment.isValid() &&
                endDateMoment.isValid() &&
                (startDateMoment.isSame(endDateMoment) || startDateMoment.isBefore(endDateMoment));
        };

        this.isBeforeCurrentDate = function (date, pattern) {
            var currentDate = $moment().format(pattern),
                expirationDate = that.format(date, pattern);

            return that.isBefore(currentDate, expirationDate, pattern);
        };

        this.isEqual = function (startDate, endDate, pattern) {
            var startDateMoment = $moment(startDate, pattern),
                endDateMoment = $moment(endDate, pattern);

            return startDateMoment.isValid() &&
                endDateMoment.isValid() &&
                startDateMoment.isSame(endDateMoment);
        };

        /**
         * Parses text to produce a Date
         * @param text A text that should be parsed.
         * @returns {a Date parsed from the text. In case of error, returns null.}
         */
        this.parse = function (text) {
            if (!angular.isString(text)) {
                return null;
            }

            var newDateText = that.applyOffset(text);

            return $moment(newDateText).toDate();
        };

        function getDate(text) {
            if (!angular.isString(text)) {
                return null;
            }

            var moment = null;

            text = that.applyOffset(text);
            moment = $moment(text);

            return moment.isValid() ? moment : null;
        }

        function getDateFromPattern(text, pattern) {
            if (!angular.isString(text) || !angular.isString(pattern)) {
                return null;
            }

            var moment = null;

            moment = $moment(text, pattern);

            return moment.isValid() ? moment : null;
        }

        this.getDateDifference = function (startDate, endDate) {
            return $moment(startDate).diff($moment(endDate), 'days') * -1;
        };
    }
})();
