'use strict';

describe('Controller: CookieTargetingController', function () {
    var $form,
        $q,
        $scope,
        CookieDomainsService,
        CookieTargetingController,
        model,
        cookies,
        $element,
        _cookieTargets = [
            {
                id: '[should_be_string]',
                type: 'string'
            },
            {
                id: '[should_be_string_list]',
                type: 'string',
                input: 'select',
                values: {
                    '----': '----',
                    value1: 'value1',
                    value2: 'value2',
                    value3: 'value3'
                }
            },
            {
                id: '[should_be_number]',
                type: 'double'
            },
            {
                id: '[should_be_number_list]',
                type: 'double',
                input: 'select',
                values: {
                    '----': '----',
                    1: 1,
                    2: 2,
                    3: 3
                }
            },
            {
                id: '[should_be_number_range]',
                type: 'double',
                validation: {
                    min: 0,
                    max: 48,
                    step: 1
                }
            }
        ]
    ;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($compile, $controller, $rootScope, $state, _$q_, _CookieDomainsService_) {
        // Add spy for state.go to prevent the site from redirecting
        spyOn($state, 'go');

        // Create mock objects
        $form = jasmine.createSpyObj('$form', ['$setDirty']);
        $q = _$q_;
        $scope = $rootScope.$new();
        $element = angular.element('<div><div id="cookieBuilder"></div></div>');
        model = {
            cookieTarget: '',
            campaignId: 1337
        };

        // Mock out the CookieDomainsService
        CookieDomainsService = _CookieDomainsService_;
        cookies = $q.defer();
        cookies.resolve(_cookieTargets);
        spyOn(CookieDomainsService, 'getCookies').andReturn(cookies.promise);

        // Create a controller using the mocks
        CookieTargetingController = $controller('CookieTargetingController', {
            $element: $element,
            $scope: $scope,
            CookieDomainsService: CookieDomainsService
        });
        $scope.vm = CookieTargetingController;
        CookieTargetingController.$form = $form;
        CookieTargetingController.model = model;

        $scope.$apply();

        // Create the query builder for testing
        CookieTargetingController.$cookieBuilder.queryBuilder({
            filters: _cookieTargets,
            plugins: {
                //jscs:disable requireCamelCaseOrUpperCaseIdentifiers
                cookie_builder: null
                //jscs:enable
            }
        });
    }));

    describe('getCookieTargets()', function () {
        it('should set vm.cookieTargets', function () {
            CookieTargetingController.getCookieTargets();

            expect(CookieTargetingController.cookieTargets).toBe(_cookieTargets);
        });
    });

    describe('setDirty()', function () {
        beforeEach(function () {
            spyOn(CookieTargetingController, 'getSql').andReturn('name = \'foo\'');
        });

        it('should set $form as dirty', function () {
            CookieTargetingController.setDirty();

            expect($form.$setDirty).toHaveBeenCalled();
        });
    });

    describe('cleanSql()', function () {
        it('should strip brackets from field names', function () {
            var sql = '[jason] = \'bar\'',
                expected = 'jason = \'bar\'';

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('cleanSql', sql)).toBe(expected);
        });

        it('should clean up statements that start with not', function () {
            var sql = 'Not Contains([jason], \'bar\')',
                expected = 'NotContains(jason, \'bar\')';

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('cleanSql', sql)).toBe(expected);
        });

        it('should correct the Between syntax for strings', function () {
            var sql = '[jason] Between(\'bar\', \'foo\')',
                expected = 'jason Between \'bar\' AND \'foo\'';

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('cleanSql', sql)).toBe(expected);
        });

        it('should correct the Not Between syntax for the parser', function () {
            var sql = 'Not [jason] Between(\'bar\', \'foo\')',
                expected = 'jason Not Between \'bar\' AND \'foo\'';

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('cleanSql', sql)).toBe(expected);
        });

        it('should correct the Between syntax for numbers', function () {
            var sql = '[jason] Between(123, 456)',
                expected = 'jason Between 123 AND 456';

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('cleanSql', sql)).toBe(expected);
        });

        it('should be case-insensitive matching', function () {
            var sql = '[jason] between(123, 456)',
                expected = 'jason Between 123 AND 456';

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('cleanSql', sql)).toBe(expected);
        });
    });

    describe('sqlToRules()', function () {
        function checkSingleRule(sql, expected) {
            var out = CookieTargetingController.$cookieBuilder.queryBuilder('sqlToRules', sql),
                rule = out.rules[0];

            expect(out.rules.length).toBe(1);
            expect(rule).toEqual(expected);
        }

        it('should used default operator when only 1 rule', function () {
            var out = CookieTargetingController.$cookieBuilder.queryBuilder('sqlToRules', '[foo] = \'bar\'');

            expect(out.condition).toBe('AND');
        });

        it('should create the rule for equals', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'equal',
                value: 'bar'
            };

            checkSingleRule('[jason] = \'bar\'', expected);
        });

        it('should create the rule for not equals', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'not_equal',
                value: 'bar'
            };

            checkSingleRule('[jason] <> \'bar\'', expected);
        });

        it('should create the rule for greater than', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'greater',
                value: 'bar'
            };

            checkSingleRule('[jason] > \'bar\'', expected);
        });

        it('should create the rule for greater than or equal to', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'greater_or_equal',
                value: 'bar'
            };

            checkSingleRule('[jason] >= \'bar\'', expected);
        });

        it('should create the rule for less than', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'less',
                value: 'bar'
            };

            checkSingleRule('[jason] < \'bar\'', expected);
        });

        it('should create the rule for less than or equal to', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'less_or_equal',
                value: 'bar'
            };

            checkSingleRule('[jason] <= \'bar\'', expected);
        });

        it('should create the rule for between', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'between',
                value: ['bar', 'foo']
            };

            checkSingleRule('[jason] Between(\'bar\', \'foo\')', expected);
        });

        it('should create the rule for not between', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'not_between',
                value: ['bar', 'foo']
            };

            checkSingleRule('Not [jason] Between(\'bar\', \'foo\')', expected);
        });

        it('should create the rule for contains', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'contain',
                value: 'bar'
            };

            checkSingleRule('Contains([jason], \'bar\')', expected);
        });

        it('should create the rule for not contains', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'not_contain',
                value: 'bar'
            };

            checkSingleRule('Not Contains([jason], \'bar\')', expected);
        });

        it('should create the rule for begins with', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'begins_with',
                value: 'bar'
            };

            checkSingleRule('StartsWith([jason], \'bar\')', expected);
        });

        it('should create the rule for ends with', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'ends_with',
                value: 'bar'
            };

            checkSingleRule('EndsWith([jason], \'bar\')', expected);
        });

        it('should create the rule for is like', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_like',
                value: 'bar'
            };

            checkSingleRule('[jason] Like \'bar\'', expected);
        });

        it('should create the rule for is not like', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_not_like',
                value: 'bar'
            };

            checkSingleRule('[jason] Not Like \'bar\'', expected);
        });

        it('should create the rule for is null', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_null',
                value: null
            };

            checkSingleRule('[jason] Is Null', expected);
        });

        it('should create the rule for is not null', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_not_null',
                value: null
            };

            checkSingleRule('[jason] Is Not Null', expected);
        });

        it('should create the rule for is any of with single value', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_any_of',
                value: ['bar']
            };

            checkSingleRule('[jason] In (\'bar\')', expected);
        });

        it('should create the rule for is any of with multiple values', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_any_of',
                value: ['bar', 'foo']
            };

            checkSingleRule('[jason] In (\'bar\', \'foo\')', expected);
        });

        it('should create the rule for is none of with single value', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_none_of',
                value: ['bar']
            };

            checkSingleRule('Not [jason] In (\'bar\')', expected);
        });

        it('should create the rule for is none of with multiple values', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_none_of',
                value: ['bar', 'foo']
            };

            checkSingleRule('Not [jason] In (\'bar\', \'foo\')', expected);
        });

        it('should create the rule for is blank', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_blank',
                value: null
            };

            checkSingleRule('IsNullOrEmpty([jason])', expected);
        });

        it('should create the rule for is not blank', function () {
            var expected = {
                id: '[jason]',
                field: '[jason]',
                operator: 'is_not_blank',
                value: null
            };

            checkSingleRule('Not IsNullOrEmpty([jason])', expected);
        });

        it('should create multiple rules for several conditions', function () {
            var expected = {
                    condition: 'AND',
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'bar'
                        },
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'foo'
                        }
                    ]
                },
                sql = '[jason] = \'bar\' AND [jason] = \'foo\'',
                out = CookieTargetingController.$cookieBuilder.queryBuilder('sqlToRules', sql);

            expect(out).toEqual(expected);
        });

        it('should allow for grouping conditions with same operator', function () {
            var expected = {
                    condition: 'AND',
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'bar'
                        },
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'foo'
                        },
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'blah'
                        }
                    ]
                },
                sql = '[jason] = \'bar\' AND ([jason] = \'foo\' AND [jason] = \'blah\')',
                out = CookieTargetingController.$cookieBuilder.queryBuilder('sqlToRules', sql);

            expect(out).toEqual(expected);
        });

        it('should allow for grouping conditions with different operators', function () {
            var expected = {
                    condition: 'AND',
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'bar'
                        },
                        {
                            condition: 'OR',
                            rules: [
                                {
                                    id: '[jason]',
                                    field: '[jason]',
                                    operator: 'equal',
                                    value: 'foo'
                                },
                                {
                                    id: '[jason]',
                                    field: '[jason]',
                                    operator: 'equal',
                                    value: 'blah'
                                }
                            ]
                        }
                    ]
                },
                sql = '[jason] = \'bar\' AND ([jason] = \'foo\' OR [jason] = \'blah\')',
                out = CookieTargetingController.$cookieBuilder.queryBuilder('sqlToRules', sql);

            expect(out).toEqual(expected);
        });

        it('should allow for OR conditions', function () {
            var expected = {
                    condition: 'OR',
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'bar'
                        },
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'foo'
                        }
                    ]
                },
                sql = '[jason] = \'bar\' OR [jason] = \'foo\'',
                out = CookieTargetingController.$cookieBuilder.queryBuilder('sqlToRules', sql);

            expect(out).toEqual(expected);
        });

        it('should work for very large queries', function () {
            var sql = '[string] = \'abc\' ' +
                      'And [string] <> \'abc\' ' +
                      'And [string] > \'abc\' ' +
                      'And [string] >= \'abc\' ' +
                      'And [string] < \'abc\' ' +
                      'And [string] <= \'abc\' ' +
                      'And [string] Between(\'abc\', \'xyz\') ' +
                      'And Not [string] Between(\'abc\', \'xyz\') ' +
                      'And Contains([string], \'abc\') ' +
                      'And Not Contains([string], \'abc\') ' +
                      'And StartsWith([string], \'abc\') ' +
                      'And EndsWith([string], \'abc\') ' +
                      'And [string] Like \'abc\' ' +
                      'And [string] Not Like \'abc\' ' +
                      'And [string] Is Null ' +
                      'And [string] Is Not Null ' +
                      'And [string] In (\'123\', \'456\') ' +
                      'And Not [string] In (\'123\', \'456\') ' +
                      'And IsNullOrEmpty([string]) ' +
                      'And Not IsNullOrEmpty([string])',
                out = CookieTargetingController.$cookieBuilder.queryBuilder('sqlToRules', sql);

            expect(out.rules.length).toBe(20);
        });
    });

    describe('rulesToSql()', function () {
        it('should create the sql for equal', function () {
            var expected = '[jason] = \'bar\'',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for not equals', function () {
            var expected = '[jason] <> \'bar\'',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'not_equal',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for greater than', function () {
            var expected = '[jason] > \'bar\'',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'greater',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for greater than or equal to', function () {
            var expected = '[jason] >= \'bar\'',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'greater_or_equal',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for less than', function () {
            var expected = '[jason] < \'bar\'',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'less',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for less than or equal to', function () {
            var expected = '[jason] <= \'bar\'',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'less_or_equal',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for between', function () {
            var expected = '[jason] Between(\'bar\', \'foo\')',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'between',
                            value: ['bar', 'foo']
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for not between', function () {
            var expected = 'Not [jason] Between(\'bar\', \'foo\')',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'not_between',
                            value: ['bar', 'foo']
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for contains', function () {
            var expected = 'Contains([jason], \'bar\')',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'contain',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for not contains', function () {
            var expected = 'Not Contains([jason], \'bar\')',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'not_contain',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for begins with', function () {
            var expected = 'StartsWith([jason], \'bar\')',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'begins_with',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for ends with', function () {
            var expected = 'EndsWith([jason], \'bar\')',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'ends_with',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for is like', function () {
            var expected = '[jason] Like \'bar\'',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'is_like',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for is not like', function () {
            var expected = '[jason] Not Like \'bar\'',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'is_not_like',
                            value: 'bar'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for is null', function () {
            var expected = '[jason] Is Null',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'is_null',
                            value: null
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for is not null', function () {
            var expected = '[jason] Is Not Null',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'is_not_null',
                            value: null
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        describe('should create the sql for is any of', function () {
            it('with single value', function () {
                var expected = '[jason] In (\'bar\')',
                    data = {
                        rules: [
                            {
                                id: '[jason]',
                                field: '[jason]',
                                operator: 'is_any_of',
                                value: ['bar']
                            }
                        ]
                    };

                expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
            });

            it('with multiple values', function () {
                var expected = '[jason] In (\'bar\', \'foo\')',
                    data = {
                        rules: [
                            {
                                id: '[jason]',
                                field: '[jason]',
                                operator: 'is_any_of',
                                value: ['bar', 'foo']
                            }
                        ]
                    };

                expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
            });
        });

        describe('should create the sql for is none of', function () {
            it('with single value', function () {
                var expected = 'Not [jason] In (\'bar\')',
                    data = {
                        rules: [
                            {
                                id: '[jason]',
                                field: '[jason]',
                                operator: 'is_none_of',
                                value: ['bar']
                            }
                        ]
                    };

                expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
            });

            it('with multiple values', function () {
                var expected = 'Not [jason] In (\'bar\', \'foo\')',
                    data = {
                        rules: [
                            {
                                id: '[jason]',
                                field: '[jason]',
                                operator: 'is_none_of',
                                value: ['bar', 'foo']
                            }
                        ]
                    };

                expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
            });
        });

        it('should create the sql for is blank', function () {
            var expected = 'IsNullOrEmpty([jason])',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'is_blank',
                            value: null
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create the sql for is not blank', function () {
            var expected = 'Not IsNullOrEmpty([jason])',
                data = {
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'is_not_blank',
                            value: null
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should create a conditional statement from multiple rules', function () {
            var expected = '[jason] = \'bar\' AND [jason] = \'foo\'',
                data = {
                    condition: 'AND',
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'bar'
                        },
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'foo'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should allow for grouping conditions with same operator', function () {
            var expected = '[jason] = \'bar\' AND ( [jason] = \'foo\' AND [jason] = \'blah\' )',
                data = {
                    condition: 'AND',
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'bar'
                        },
                        {
                            condition: 'AND',
                            rules: [
                                {
                                    id: '[jason]',
                                    field: '[jason]',
                                    operator: 'equal',
                                    value: 'foo'
                                },
                                {
                                    id: '[jason]',
                                    field: '[jason]',
                                    operator: 'equal',
                                    value: 'blah'
                                }
                            ]
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should allow for grouping conditions with different operators', function () {
            var expected = '[jason] = \'bar\' AND ( [jason] = \'foo\' OR [jason] = \'blah\' )',
                data = {
                    condition: 'AND',
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'bar'
                        },
                        {
                            condition: 'OR',
                            rules: [
                                {
                                    id: '[jason]',
                                    field: '[jason]',
                                    operator: 'equal',
                                    value: 'foo'
                                },
                                {
                                    id: '[jason]',
                                    field: '[jason]',
                                    operator: 'equal',
                                    value: 'blah'
                                }
                            ]
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });

        it('should allow for OR conditions', function () {
            var expected = '[jason] = \'foo\' OR [jason] = \'blah\'',
                data = {
                    condition: 'OR',
                    rules: [
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'foo'
                        },
                        {
                            id: '[jason]',
                            field: '[jason]',
                            operator: 'equal',
                            value: 'blah'
                        }
                    ]
                };

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('rulesToSql', data)).toEqual(expected);
        });
    });

    describe('splitValueToArray()', function () {
        it('should parse the list', function () {
            var value = 'bar, foo',
                expected = ['bar', 'foo'];

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('splitValueToArray', value)).toEqual(expected);
        });

        it('should allow values to be quoted', function () {
            var value = '\'bar\', \'foo\'',
                expected = ['bar', 'foo'];

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('splitValueToArray', value)).toEqual(expected);
        });

        it('should allow a comma in a value', function () {
            var value = '\'bar\', \'f,oo\'',
                expected = ['bar', 'f,oo'];

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('splitValueToArray', value)).toEqual(expected);
        });

        it('should allow for a sql escaped quote', function () {
            var value = '\'bar\', \'f\'\'oo\'',
                expected = ['bar', 'f\'\'oo'];

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('splitValueToArray', value)).toEqual(expected);
        });

        it('should allow for a quote when values are not quoted', function () {
            var value = 'bar, f\'oo',
                expected = ['bar', 'f\'\'oo'];

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('splitValueToArray', value)).toEqual(expected);
        });

        it('should allow values to contain both comma and quote', function () {
            var value = '\'bar\', \'f\'o,o\'',
                expected = ['bar', 'f\'\'o,o'];

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('splitValueToArray', value)).toEqual(expected);
        });

        it('should only allow commas when values are quoted', function () {
            var value = 'bar, fo,o',
                expected = ['bar', 'fo', 'o'];

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('splitValueToArray', value)).toEqual(expected);
        });

        it('should not affect values that are already arrays', function () {
            var value = ['bar', 'foo'],
                expected = ['bar', 'foo'];

            expect(CookieTargetingController.$cookieBuilder.queryBuilder('splitValueToArray', value)).toEqual(expected);
        });
    });

    describe('validateSql()', function () {
        it('should have no errors for valid sql', function () {
            var sql = '[should_be_string] = \'foo\'',
                validation = CookieTargetingController.$cookieBuilder.queryBuilder('validateSql', sql);

            expect(validation.length).toEqual(0);
        });

        it('should return error when a field is not configured', function () {
            var sql = '[jason] = \'foo\'',
                validation = CookieTargetingController.$cookieBuilder.queryBuilder('validateSql', sql);

            expect(validation.length).toEqual(1);
            expect(validation[0]).toEqual('Invalid field \'[jason]\'.');
        });

        it('should return errors on parsing errors', function () {
            var sql = 'should_be_string =',
                validation = CookieTargetingController.$cookieBuilder.queryBuilder('validateSql', sql);

            expect(validation.length).toEqual(1);
            expect(validation[0]).toEqual('Parse error on line 2: Unexpected \'EOF\'');
        });

        it('should return error when an invalid function is used', function () {
            var sql = 'should_be_string != \'blah\'',
                validation = CookieTargetingController.$cookieBuilder.queryBuilder('validateSql', sql);

            expect(validation.length).toEqual(1);
            expect(validation[0]).toEqual('Invalid SQL operation \'!=\'.');
        });

        it('should not return error on valid functions', function () {
            var sql = 'Not IsNullOrEmpty([should_be_string]) AND [should_be_string] < 3 AND [should_be_string] = 3',
                validation = CookieTargetingController.$cookieBuilder.queryBuilder('validateSql', sql);

            expect(validation.length).toEqual(0);
        });

        it('should catch errors in functions', function () {
            var sql = 'Not IsNullOrEmpty([should_be_string2])',
                validation = CookieTargetingController.$cookieBuilder.queryBuilder('validateSql', sql);

            expect(validation.length).toEqual(1);
            expect(validation[0]).toEqual('Invalid field \'[should_be_string2]\'.');
        });
    });
});
