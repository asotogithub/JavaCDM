(function () {
    'use strict';

    angular
        .module('uiApp')
        .constant('CookieBuilderPluginDefaults', {
            //jscs:disable requireCamelCaseOrUpperCaseIdentifiers

            /* optional default plugin configuration */
            operators: [
                {
                    type: 'equal',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'not_equal',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'greater',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'greater_or_equal',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'less',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'less_or_equal',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'between',
                    nb_inputs: 2,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'not_between',
                    nb_inputs: 2,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'contain',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string']
                },
                {
                    type: 'not_contain',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string']
                },
                {
                    type: 'begins_with',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string']
                },
                {
                    type: 'ends_with',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string']
                },
                {
                    type: 'is_like',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string']
                },
                {
                    type: 'is_not_like',
                    nb_inputs: 1,
                    multiple: false,
                    apply_to: ['string']
                },
                {
                    type: 'is_null',
                    nb_inputs: 0,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'is_not_null',
                    nb_inputs: 0,
                    multiple: false,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'is_any_of',
                    nb_inputs: 1,
                    multiple: true,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'is_none_of',
                    nb_inputs: 1,
                    multiple: true,
                    apply_to: ['string', 'number']
                },
                {
                    type: 'is_blank',
                    nb_inputs: 0,
                    multiple: false,
                    apply_to: ['string']
                },
                {
                    type: 'is_not_blank',
                    nb_inputs: 0,
                    multiple: false,
                    apply_to: ['string']
                }
            ],
            sqlOperators: {
                equal: {
                    op: '# = ?'
                },
                not_equal: {
                    op: '# <> ?'
                },
                greater: {
                    op: '# > ?'
                },
                greater_or_equal: {
                    op: '# >= ?'
                },
                less: {
                    op: '# < ?'
                },
                less_or_equal: {
                    op: '# <= ?'
                },
                between: {
                    op: '# Between(?)',
                    sep: ', '
                },
                not_between: {
                    op: 'Not # Between(?)',
                    sep: ', '
                },
                contain: {
                    op: 'Contains(#, ?)'
                },
                not_contain: {
                    op: 'Not Contains(#, ?)'
                },
                begins_with: {
                    op: 'StartsWith(#, ?)',
                    fn: null // have to set fn to null because of previous defaults in the plugin
                },
                ends_with: {
                    op: 'EndsWith(#, ?)',
                    fn: null // have to set fn to null because of previous defaults in the plugin
                },
                is_like: {
                    op: '# Like ?'
                },
                is_not_like: {
                    op: '# Not Like ?'
                },
                is_null: {
                    op: '# Is Null'
                },
                is_not_null: {
                    op: '# Is Not Null'
                },
                is_any_of: {
                    op: '# In (?)',
                    sep: ', '
                },
                is_none_of: {
                    op: 'Not # In (?)',
                    sep: ', '
                },
                is_blank: {
                    op: 'IsNullOrEmpty(#)'
                },
                is_not_blank: {
                    op: 'Not IsNullOrEmpty(#)'
                }
            },
            sqlRuleOperators: {
                CONTAINS: function (f, o, v) {
                    return {
                        value: v.value[1].value,
                        field: v.value[0].value,
                        operator: 'contain'
                    };
                },

                NOTCONTAINS: function (f, o, v) {
                    return {
                        value: v.value[1].value,
                        field: v.value[0].value,
                        operator: 'not_contain'
                    };
                },

                '=': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'equal'
                    };
                },

                '<>': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'not_equal'
                    };
                },

                '>': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'greater'
                    };
                },

                '>=': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'greater_or_equal'
                    };
                },

                '<': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'less'
                    };
                },

                '<=': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'less_or_equal'
                    };
                },

                BETWEEN: function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'between'
                    };
                },

                'NOT BETWEEN': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'not_between'
                    };
                },

                STARTSWITH: function (f, o, v) {
                    return {
                        value: v.value[1].value,
                        field: v.value[0].value,
                        operator: 'begins_with'
                    };
                },

                ENDSWITH: function (f, o, v) {
                    return {
                        value: v.value[1].value,
                        field: v.value[0].value,
                        operator: 'ends_with'
                    };
                },

                LIKE: function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'is_like'
                    };
                },

                'NOT LIKE': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'is_not_like'
                    };
                },

                IS: function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'is_null'
                    };
                },

                'IS NOT': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'is_not_null'
                    };
                },

                IN: function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'is_any_of'
                    };
                },

                'NOT IN': function (f, o, v) {
                    return {
                        value: v,
                        field: f,
                        operator: 'is_none_of'
                    };
                },

                ISNULLOREMPTY: function (f, o, v) {
                    return {
                        value: null,
                        field: v.value[0].value,
                        operator: 'is_blank'
                    };
                },

                NOTISNULLOREMPTY: function (f, o, v) {
                    return {
                        value: null,
                        field: v.value[0].value,
                        operator: 'is_not_blank'
                    };
                }
            },
            default_condition: 'AND',
            lang: {
                operators: {
                    equal: 'Equals',
                    not_equal: 'Does not equal',
                    greater: 'Is greater than',
                    greater_or_equal: 'Is greater than or equal to',
                    less: 'Is less than',
                    less_or_equal: 'Is less than or equal to',
                    between: 'Is between',
                    not_between: 'Is not between',
                    contain: 'Contains',
                    not_contain: 'Does not contain',
                    begins_with: 'Begins with',
                    ends_with: 'Ends with',
                    is_like: 'Is like',
                    is_not_like: 'Is not like',
                    is_null: 'Is null',
                    is_not_null: 'Is not null',
                    is_any_of: 'Is any of',
                    is_none_of: 'Is none of',
                    is_blank: 'Is blank',
                    is_not_blank: 'Is not blank'
                }
            }
        });
})();
