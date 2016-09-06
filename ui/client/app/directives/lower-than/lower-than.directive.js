(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('lowerThan', LowerThan);

    LowerThan.$inject = [
        '$filter',
        'CONSTANTS',
        'DateTimeService'
    ];

    function LowerThan($filter, CONSTANTS, DateTimeService) {
        var link = function ($scope, $element, $attrs, ctrl) {
            var validate = function (viewValue) {
                    if (viewValue) {
                        var endDate = convertToDate(viewValue),
                            startDate;

                        if ($attrs.lowerThan.substring(0, 1) === '"') {
                            startDate = convertToDate($scope.$eval($attrs.lowerThan));
                        }
                        else {
                            startDate = convertToDate($attrs.lowerThan);
                        }

                        if (validateDates(endDate) && validateDates(startDate)) {
                            ctrl.$setValidity('lowerThan', lowerThan(startDate, endDate));
                        }
                    }
                    else {
                        ctrl.$setValidity('lowerThan', true);
                    }

                    return viewValue;
                },

                convertToDate = function (modelDate) {
                    if (!angular.isDate(modelDate)) {
                        var stringDate = modelDate,
                            newDate = DateTimeService.getDate(new Date(stringDate));

                        if (validateDates(newDate)) {
                            modelDate = newDate;
                        }
                    }

                    return modelDate;
                },

                validateDates = function (modelDate) {
                    var date = DateTimeService.getDate(modelDate);

                    return !!(date !== 'Invalid Date' && !isNaN(date));
                },

                lowerThan = function (initDate, endDate) {
                    //TODO review use of input value for $filter and use DateTimeService instead.
                    var initDateFilter = new Date($filter('date')(initDate, CONSTANTS.DATE_FORMAT)),
                        startDateFormat = DateTimeService.getDate(initDateFilter),
                        endDateFilter = new Date($filter('date')(endDate, CONSTANTS.DATE_FORMAT)),
                        endDateFormat = DateTimeService.getDate(endDateFilter);

                    if (startDateFormat > endDateFormat) {
                        return false;
                    }

                    return true;
                };

            ctrl.$parsers.unshift(validate);
            ctrl.$formatters.push(validate);

            $attrs.$observe('lowerThan', function () {
                return validate(ctrl.$viewValue);
            });
        };

        return {
            restrict: 'A',
            require: 'ngModel',
            link: link
        };
    }
})();

