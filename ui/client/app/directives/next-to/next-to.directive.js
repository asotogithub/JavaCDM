(function () {
    'use strict';

    angular
        .module('uiApp')
        .directive('nextTo', NextTo);

    NextTo.$inject = [
        '$moment',
        'DateTimeService'
    ];

    function NextTo($moment, DateTimeService) {
        var link = function ($scope, $element, $attrs, ctrl) {
            var validate = function (viewValue) {
                    if (viewValue && $attrs.nextTo) {
                        var endDate = convertToDate(viewValue),
                            startDate = convertToDate($scope.$eval($attrs.nextTo));

                        if (validateDates(endDate) && validateDates(startDate)) {
                            ctrl.$setValidity('nextTo', nextTo(startDate, endDate));
                        }
                    }
                    else {
                        ctrl.$setValidity('nextTo', true);
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

                nextTo = function (startDate, endDate) {
                    if ($moment(startDate).add(1, 'days').format('LL') === $moment(endDate).format('LL')) {
                        return true;
                    }

                    return false;
                };

            ctrl.$parsers.unshift(validate);
            ctrl.$formatters.push(validate);

            $attrs.$observe('nextTo', function () {
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
