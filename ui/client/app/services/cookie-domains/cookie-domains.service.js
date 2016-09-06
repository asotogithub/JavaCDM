//jscs:disable requireCamelCaseOrUpperCaseIdentifiers

(function () {
    'use strict';

    angular
        .module('uiApp')
        .service('CookieDomainsService', CookieDomainsService);

    CookieDomainsService.$inject = [
        '$q',
        '$resource',
        'API_SERVICE',
        'CampaignsService',
        'CONSTANTS',
        'ErrorRequestHandler',
        'lodash'
    ];

    function CookieDomainsService($q,
                                  $resource,
                                  API_SERVICE,
                                  CampaignsService,
                                  CONSTANTS,
                                  ErrorRequestHandler,
                                  lodash) {
        var cookieTargetsResource = $resource(API_SERVICE + 'CookieDomains/:cookieDomainId/cookies'),

            errorHandler = function (logMessage, deferred) {
                return ErrorRequestHandler.handleAndReject(logMessage, deferred);
            },

            selectionValidation = function (value, rule) {
                var isValid = true,
                    operator = rule.operator;

                if (!operator.multiple) {
                    isValid = isValid && value && value.length === operator.nb_inputs;
                    if (operator.nb_inputs > 1) {
                        lodash.forEach(value, function (val) {
                            isValid = isValid && val.length === 1;
                        });
                    }
                }

                return isValid;
            },

            isNumber = function () {
                return function (val) {
                    return !isNaN(val);
                };
            },

            inRange = function (validations) {
                return function (val) {
                    return isNumber(validations)(val) && val <= validations.max && val >= validations.min;
                };
            },

            numberValidation = function (value, rule, fn) {
                var isValid = true,
                    operator = rule.operator,
                    validations = rule.filter.validation,
                    valueArray;

                function split(val) {
                    if (typeof val !== 'string') {
                        return val;
                    }

                    var valArray = val.split(/', *'/);

                    if (valArray.length === 1) {
                        valArray = val.split(/, */);
                    }

                    return lodash.map(valArray, function (v) {
                        v = v.replace(/^'/, '').replace(/'$/, '');
                        if (v.indexOf('\'') > -1 && v.indexOf('\'\'') < 0) {
                            v = v.replace(/'/g, '\'\'');
                        }

                        return v;
                    });
                }

                if (operator.multiple) {
                    valueArray = split(value);
                }
                else {
                    if (operator.nb_inputs > 1) {
                        valueArray = value;
                    }
                    else {
                        valueArray = [value];
                    }
                }

                isValid = isValid && lodash.every(valueArray, fn(validations));

                return isValid;
            },

            numberValueValidation = function (value, rule) {
                return numberValidation(value, rule, isNumber);
            },

            numberRangeValidation = function (value, rule) {
                return numberValidation(value, rule, inRange);
            },

            numberInput = function (rule, inputName) {
                return lodash.template('<input class="form-control" name="<%= inputName %>"/>!')({
                    inputName: inputName
                });
            };

        return {
            numberValueValidation: numberValueValidation,
            numberRangeValidation: numberRangeValidation,
            selectionValidation: selectionValidation,
            getCookies: function (campaignId) {
                var cookieTargets = $q.defer();

                CampaignsService.getCampaign(campaignId).then(function (campaign) {
                    if (campaign.cookieDomainId) {
                        return cookieTargetsResource.get({
                            cookieDomainId: campaign.cookieDomainId
                        }).$promise;
                    }
                    else {
                        return $q.defer().promise.resolve;
                    }
                }).then(getCookiesComplete)
                    .catch(errorHandler('Cannot get cookie targets', cookieTargets));

                function getCookiesComplete(response) {
                    var _cookieTargets = [].concat(lodash.result(response, '.records[0].CookieTargetTemplate', [])),
                        mappedCookies,
                        values;

                    mappedCookies = lodash.map(_cookieTargets, function (cookieTarget) {
                        var filter = {};

                        cookieTarget.cookieName = '[' + cookieTarget.cookieName + ']';

                        filter.id = cookieTarget.cookieName;

                        switch (cookieTarget.cookieContentType) {
                            case CONSTANTS.COOKIE_CONTENT_TYPE.STRING:
                                filter.type = 'string';
                                break;
                            case CONSTANTS.COOKIE_CONTENT_TYPE.STRING_LIST:
                                filter.type = 'string';
                                values = cookieTarget.contentPossibleValues.split('`');
                                filter.input = 'checkbox';
                                filter.values = {};
                                lodash.forEach(values, function (value) {
                                    filter.values[value] = value;
                                });

                                filter.validation = {};
                                filter.validation.callback = selectionValidation;
                                break;
                            case CONSTANTS.COOKIE_CONTENT_TYPE.NUMBER:
                                filter.type = 'double';
                                filter.input = numberInput;
                                filter.validation = {};
                                filter.validation.callback = numberValueValidation;
                                break;
                            case CONSTANTS.COOKIE_CONTENT_TYPE.NUMBER_LIST:
                                filter.type = 'double';
                                values = cookieTarget.contentPossibleValues.split('`');
                                filter.input = 'checkbox';
                                filter.values = {};
                                lodash.forEach(values, function (value) {
                                    filter.values[value] = parseInt(value);
                                });

                                filter.validation = {};
                                filter.validation.callback = selectionValidation;
                                break;
                            case CONSTANTS.COOKIE_CONTENT_TYPE.NUMBER_RANGE:
                                filter.type = 'double';
                                values = cookieTarget.contentPossibleValues.split('`');
                                filter.input = numberInput;
                                filter.validation = {};
                                filter.validation.callback = numberRangeValidation;
                                filter.validation.min = parseInt(values[0]);
                                filter.validation.max = parseInt(values[1]);
                                break;
                            default:
                                filter.type = 'string';
                        }

                        return filter;
                    });

                    cookieTargets.resolve(mappedCookies);
                }

                return cookieTargets.promise;
            }
        };
    }
})();
