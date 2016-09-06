(function () {
    'use strict';

    angular
        .module('uiApp')
        .config(CookieBuilderPlugin);

    CookieBuilderPlugin.$inject =
        ['CookieBuilderPluginDefaults', 'QueryBuilder', 'SQLParser', 'lodash'];

    function CookieBuilderPlugin(CookieBuilderPluginDefaults, QueryBuilder, SQLParser, lodash) {
        //jscs:disable requireCamelCaseOrUpperCaseIdentifiers

        /**
         * Replaces {0}, {1}, ... in a string
         * @param str {string}
         * @param args,... {string|int|float}
         * @return {string}
         */
        function fmt(str, args) {
            args = Array.prototype.slice.call(arguments);

            return str.replace(/{([0-9]+)}/g, function (m, i) {
                return args[parseInt(i) + 1];
            });
        }

        /**
         * Output internal error with jQuery.error
         * @see fmt
         */
        function error() {
            $.error(fmt.apply(null, arguments));
        }

        /**
         * Change type of a value to int or float
         * @param value {mixed}
         * @param type {string} 'integer', 'double' or anything else
         * @param boolAsInt {boolean} return 0 or 1 for booleans
         * @return {mixed}
         */
        function changeType(value, type, boolAsInt) {
            switch (type) {
                case 'integer':
                    return parseInt(value);
                case 'double':
                    return parseFloat(value);
                case 'boolean':
                    var bool = value.trim().toLowerCase() === 'true' || value.trim() === '1' || value === 1;

                    return boolAsInt ? bool ? 1 : 0 : bool;
                default:
                    return value;
            }
        }

        function formatValue(rule, sql) {
            var value = '';

            if (!(rule.value instanceof Array)) {
                rule.value = [rule.value];
            }

            rule.value.forEach(function (v, i) {
                if (i > 0) {
                    value += sql.sep;
                }

                if (rule.type === 'integer' || rule.type === 'double' || rule.type === 'boolean') {
                    v = changeType(v, rule.type, true);
                }

                if (sql.fn) {
                    v = sql.fn(v);
                }

                if (typeof v === 'string') {
                    v = '\'' + v + '\'';
                }

                value += v;
            });

            return value;
        }

        function formatField(field) {
            return '[' + field + ']';
        }

        function buildRule(data, that) {
            var value,
                operator,
                field,
                sqlRule,
                rule;

            if (data.operation) {
                if (data.left.value === undefined || data.right.value === undefined) {
                    error('Missing field and/or value');
                }

                if ($.isPlainObject(data.right.value)) {
                    error('Value format not supported for {0}.', data.left.value);
                }

                // convert array
                if ($.isArray(data.right.value)) {
                    value = data.right.value.map(function (v) {
                        return v.value;
                    });
                }
                else {
                    value = data.right.value;
                }

                // convert operator
                operator = data.operation.toUpperCase();
                field = data.left.value;
            }
            else if (data.arguments) {
                operator = data.name.toUpperCase();
                value = data.arguments;
            }

            sqlRule = that.settings.sqlRuleOperators[operator];

            if (sqlRule === undefined) {
                error('Invalid SQL operation {0}.', operator);
            }

            rule = sqlRule(field, operator, value);
            rule.field = formatField(rule.field);
            rule.id = rule.field;

            return rule;
        }

        function validateSqlExpression(data, that) {
            var value,
                operator,
                field,
                validationErrors = [],
                sqlRule,
                filter;

            function find(fieldId) {
                return lodash.find(that.settings.filters, function (f) {
                    return f.id === fieldId;
                });
            }

            if (data.operation) {
                if (data.left.value === undefined || data.right.value === undefined) {
                    validationErrors.push('Missing field and/or value');
                }

                if ($.isPlainObject(data.right.value)) {
                    validationErrors.push('Value format not supported for ' + data.left.value + '.');
                }

                // convert array
                if ($.isArray(data.right.value)) {
                    value = data.right.value.map(function (v) {
                        return v.value;
                    });
                }
                else {
                    value = data.right.value;
                }

                // convert operator
                operator = data.operation.toUpperCase();
                field = data.left.value;
            }
            else if (data.arguments) {
                operator = data.name.toUpperCase();
                value = data.arguments;
            }

            sqlRule = that.settings.sqlRuleOperators[operator];

            if (sqlRule === undefined) {
                validationErrors.push('Invalid SQL operation \'' + operator + '\'.');
            }
            else {
                field = sqlRule(field, operator, value).field;
            }

            field = formatField(field);

            if (field) {
                filter = find(field);

                if (filter === undefined) {
                    validationErrors.push('Invalid field \'' + field + '\'.');
                }
            }

            return validationErrors;
        }

        QueryBuilder.defaults(CookieBuilderPluginDefaults);

        QueryBuilder.extend({
            splitValueToArray: function (value) {
                if (typeof value !== 'string') {
                    return value;
                }

                var valueArray = value.split(/', *'/);

                if (valueArray.length === 1) {
                    valueArray = value.split(/, */);
                }

                return lodash.map(valueArray, function (val) {
                    val = val.replace(/^'/, '').replace(/'$/, '');
                    if (val.indexOf('\'') > -1 && val.indexOf('\'\'') < 0) {
                        val = val.replace(/'/g, '\'\'');
                    }

                    return val;
                });
            },

            rulesToSql: function (rules) {
                var nl = ' ',
                    that = this;

                function parse(data) {
                    if (!data.condition) {
                        data.condition = that.settings.default_condition;
                    }

                    if (['AND', 'OR'].indexOf(data.condition.toUpperCase()) === -1) {
                        error('Unable to build SQL query with condition "{0}"', data.condition);
                    }

                    if (!data.rules) {
                        return '';
                    }

                    var parts = [];

                    data.rules.forEach(function (rule) {
                        if (rule.rules && rule.rules.length > 0) {
                            parts.push('(' + nl + parse(rule) + nl + ')');
                        }
                        else {
                            var sql = that.settings.sqlOperators[rule.operator],
                                ope = that.getOperatorByType(rule.operator),
                                value = '';

                            if (sql === undefined) {
                                error('Unknown SQL operation for operator "{0}"', rule.operator);
                            }

                            if (ope.nb_inputs !== 0) {
                                value = formatValue(rule, sql);
                            }

                            parts.push(sql.op.replace(/\?/, value).replace(/#/, rule.field));
                        }
                    });

                    return parts.join(' ' + data.condition + nl);
                }

                return parse(rules);
            },

            sqlToRules: function (whereClause) {
                var that = this,
                    cleanSql,
                    parsed,
                    out = {
                        condition: that.settings.default_condition,
                        rules: []
                    },
                    curr = out;

                if (whereClause && whereClause.length > 0) {
                    cleanSql = that.cleanSql(whereClause);
                    parsed = SQLParser.parse('SELECT * FROM table WHERE ' + cleanSql);
                }
                else {
                    return out;
                }

                function flatten(data, i) {
                    // it's a node
                    if (data.operation && ['AND', 'OR'].indexOf(data.operation.toUpperCase()) !== -1) {
                        // create a sub-group if the condition is not the same and it's not the
                        // first level
                        if (i > 0 && curr.condition !== data.operation.toUpperCase()) {
                            curr.rules.push({
                                condition: that.settings.default_condition,
                                rules: []
                            });

                            curr = curr.rules[curr.rules.length - 1];
                        }

                        curr.condition = data.operation.toUpperCase();
                        i++;

                        // some magic !
                        var next = curr;

                        flatten(data.left, i);

                        curr = next;

                        flatten(data.right, i);
                    }
                    // it's a leaf
                    else {
                        curr.rules.push(buildRule(data, that));
                    }
                }

                flatten(parsed.where.conditions, 0);

                return out;
            },

            validateBuilder: function () {
                var that = this,
                    isValid;

                try {
                    isValid = that.validate();
                }
                catch (err) {
                    isValid = false;
                }

                return isValid;
            },

            validateSql: function (whereClause) {
                if (!whereClause || whereClause.length === 0) {
                    return [];
                }

                var that = this,
                    cleanSql = that.cleanSql(whereClause),
                    parsed,
                    validationErrors = [];

                function flatten(data, i) {
                    // it's a node
                    if (data.operation && ['AND', 'OR'].indexOf(data.operation.toUpperCase()) !== -1) {
                        i++;

                        flatten(data.left, i);

                        flatten(data.right, i);
                    }
                    // it's a leaf
                    else {
                        validationErrors = validationErrors.concat(validateSqlExpression(data, that));
                    }
                }

                try {
                    parsed = SQLParser.parse('SELECT * FROM table WHERE ' + cleanSql);
                    flatten(parsed.where.conditions, 0);
                }
                catch (err) {
                    validationErrors = [err.message];
                }

                return validationErrors;
            },

            cleanSql: function (sql) {
                // Clear off brackets ([]) from field names
                sql = sql
                    .replace(/\[/g, '')
                    .replace(/]/g, '');
                sql = sql
                    .replace(/Not Contains/ig, 'NotContains')
                    .replace(/Not IsNullOrEmpty/ig, 'NotIsNullOrEmpty');
                sql = sql
                    .replace(/Between\(('?[^']+'?), *('?[^']+'?)\)/gi, 'Between $1 AND $2')
                    .replace(/Not +(\w+) +(Between|In)/gi, '$1 Not $2');
                return sql;
            }
        });

        QueryBuilder.define('cookie_builder', function () {
            /* "this" is the QueryBuilder instance */
            var that = this;

            QueryBuilder.prototype.rulesToSql = that.rulesToSql;
            QueryBuilder.prototype.sqlToRules = that.sqlToRules;

            that.on('getRules.queryBuilder.filter', function (e) {
                function correctRules(rules) {
                    for (var i = 0, rule = rules[i]; i < rules.length; i++) {
                        if (rule.operator === 'is_any_of' || rule.operator === 'is_none_of') {
                            rule.value = that.splitValueToArray(rule.value);
                        }
                    }
                }

                function loop(rules) {
                    if (rules.condition) {
                        loop(rules.rules);
                    }
                    else {
                        correctRules(rules);
                    }
                }

                loop(e.value.rules);
            });
        }, {});
    }
})();
