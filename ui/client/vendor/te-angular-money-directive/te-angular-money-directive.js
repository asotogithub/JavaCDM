/**
 * Heavily adapted from the `type="number"` directive in Angular's
 * /src/ng/directive/input.js
 *
 * This directive is a custom version of 'angular-money-directive'
 * https://github.com/fiestah/angular-money-directive
 */

angular.module('te.vendor.fiestah.money', [])
    .directive('teMoney', function () {
        'use strict';

        var NUMBER_REGEXP = /^\s*(\d+|(\d*(\.\d*)))\s*$/;
        var DEFAULT_PRECISION = 1;

        function link(scope, el, attrs, ngModelCtrl) {
            var min, max, precision, maxPrecisionFocus, maxPrecisionBlur, lastValidValue, preRoundValue, lastPrecision, DEFAULT_VALUE;

            /**
             * Returns a rounded number in the precision setup by the directive
             * @param  {Number} num Number to be rounded
             * @return {Number}     Rounded number
             */
            function round(num) {
                var d = Math.pow(10, precision);

                return Math.round(num * d) / d;
            }

            /**
             * Returns a string that represents the rounded number
             * @param  {Number} value Number to be rounded
             * @return {String}       The string representation
             */
            function formatPrecision(value, customPrecision) {
                var localPrecision = undefined;
                if (angular.isUndefined(customPrecision)) {
                    localPrecision = precision;
                } else {
                    localPrecision = customPrecision;
                }

                var formattedValue = parseFloat(value).toFixed(localPrecision);
                return formattedValue;
            }

            function formatViewValue(value) {
                return ngModelCtrl.$isEmpty(value) ? '' : '' + value;
            }

            function minValidator(value) {
                if (!ngModelCtrl.$isEmpty(value) && value < min) {
                    ngModelCtrl.$setValidity('min', false);
                } else {
                    ngModelCtrl.$setValidity('min', true);
                }
                return value;
            }

            function maxValidator(value) {
                if (!ngModelCtrl.$isEmpty(value) && value > max) {
                    ngModelCtrl.$setValidity('max', false);
                } else {
                    ngModelCtrl.$setValidity('max', true);
                }
                return value;
            }

            function removeDecimalLessSignificativeZeros(n) {
                return parseFloat(n);
            }

            function initialize() {
                ngModelCtrl.$parsers.push(minValidator);
                ngModelCtrl.$formatters.push(minValidator);

                maxPrecisionFocus = scope.$eval(attrs['maxPrecisionFocus']);
                if (angular.isUndefined(maxPrecisionFocus)) {
                    maxPrecisionFocus = DEFAULT_PRECISION;
                }

                maxPrecisionBlur = scope.$eval(attrs['maxPrecisionBlur']);
                if (angular.isUndefined(maxPrecisionBlur)) {
                    maxPrecisionBlur = DEFAULT_PRECISION;
                }

                // By default the precision will be as set by 'precision-blur' parameter
                precision = maxPrecisionBlur;
                ngModelCtrl.$viewValue = formatPrecision(scope.$eval(attrs['ngModel']), maxPrecisionBlur);
                ngModelCtrl.$render();


                DEFAULT_VALUE = scope.$eval(attrs['defaultValue']);
            }

            function changeNotation(n) {
                if(angular.isUndefined(n) || n == '') {
                    return '';
                }

                if (n.toString().indexOf('e') <= -1) {
                    return n;
                }

                var parts = n.split('e+'),
                    first = parts[0].replace('.', ''),
                    zeroes = parseInt(parts[1], 10) - (first.length - 1),
                    i;

                for (i = 0; i < zeroes; i++) {
                    first += '0';
                }

                return first;
            }

            ngModelCtrl.$parsers.push(function (value) {
                if (angular.isUndefined(value)) {
                    value = '';
                }

                // Handle leading decimal point, like ".5"
                if (value.indexOf('.') === 0) {
                    value = '0' + value;
                }

                // Allow "-" inputs only when min < 0
                if (value.indexOf('-') === 0) {
                    if (min >= 0) {
                        value = null;
                        ngModelCtrl.$setViewValue('');
                        ngModelCtrl.$render();
                    } else if (value === '-') {
                        value = '';
                    }
                }

                var empty = ngModelCtrl.$isEmpty(value);

                if (empty || NUMBER_REGEXP.test(value)) {
                    lastValidValue = (value === '') ? null : (empty ? value : parseFloat(value));
                } else {
                    // Render the last valid input in the field
                    ngModelCtrl.$setViewValue(formatViewValue(lastValidValue));
                    ngModelCtrl.$render();
                }

                ngModelCtrl.$setValidity('number', true);

                return lastValidValue;
            });

            ngModelCtrl.$formatters.push(formatViewValue);
            initialize();

            // Min validation
            attrs.$observe('min', function (value) {
                min = parseFloat(value || 0);
                minValidator(ngModelCtrl.$modelValue);
            });

            // Max validation (optional)
            if (angular.isDefined(attrs.max)) {
                attrs.$observe('max', function (val) {
                    max = parseFloat(val);
                    maxValidator(ngModelCtrl.$modelValue);
                });

                ngModelCtrl.$parsers.push(maxValidator);
                ngModelCtrl.$formatters.push(maxValidator);
            }

            // Round off (disabled by "-1")
            if (attrs.precision !== '-1') {
                ngModelCtrl.$parsers.push(function (value) {
                    if (angular.isDefined(value)) {
                        // Save with rounded value
                        lastValidValue = round(value);
                        return lastValidValue;
                    } else {
                        return undefined;
                    }
                });

                ngModelCtrl.$formatters.push(function (value) {
                    return angular.isDefined(value) ? formatPrecision(value) : value;
                });

                el.bind('blur', function () {
                    precision = maxPrecisionBlur;
                    var value = ngModelCtrl.$modelValue;

                    if (angular.isDefined(value)) {
                        value = formatPrecision(value, maxPrecisionBlur);
                    } else {
                        if (angular.isDefined(DEFAULT_VALUE)) {
                            value = formatPrecision(DEFAULT_VALUE, maxPrecisionBlur);
                        } else {
                            value = '';
                        }
                    }

                    ngModelCtrl.$viewValue = changeNotation(value);
                    ngModelCtrl.$render();
                });

                el.bind('focus', function () {
                    precision = maxPrecisionFocus;
                    var value = ngModelCtrl.$modelValue;

                    if (angular.isDefined(value)) {
                        if (value === 0) {
                            value = formatPrecision(value, maxPrecisionBlur);
                        } else {
                            value = removeDecimalLessSignificativeZeros(formatPrecision(value, maxPrecisionFocus));
                        }
                    } else {
                        if (angular.isDefined(DEFAULT_VALUE)) {
                            value = formatPrecision(DEFAULT_VALUE, maxPrecisionBlur);
                        } else {
                            value = 0;
                        }
                    }

                    ngModelCtrl.$viewValue = changeNotation(value);
                    ngModelCtrl.$render();
                });
            }
        }

        return {
            restrict: 'A',
            require: 'ngModel',
            link: link
        };
    });
