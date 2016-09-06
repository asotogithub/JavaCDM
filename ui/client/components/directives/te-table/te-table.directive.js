(function () {
    'use strict';

    var SELECT_ALL_CHECKBOX_HEADER =
        'components/directives/te-table/te-table-select-all-checkbox/te-table-select-all-checkbox.html';

    angular
        .module('te.components')
        .directive('teTable', TeTableCompile)
        .directive('teTable', TeTableDirective);

    TeTableCompile.$inject = ['CONSTANTS'];
    TeTableDirective.$inject = ['$parse', 'lodash'];

    function TeTableCompile(CONSTANTS) {
        var SELECTION_MODE = CONSTANTS.TE_TABLE.SELECTION_MODE;

        return {
            // IMPORTANT: rlt 20150628 - ng-table directive priority + 3
            //       See: https://github.com/esvit/ng-table/blob/v0.5.4/dist/ng-table.js#L774
            priority: 1004,

            restrict: 'E',

            compile: function ($el) {
                var $table = $el.find('table'),
                    $td = $el.find('td'),
                    $tr = $el.find('tr'),
                    checkboxHeader,

                    searchFields = $td.filter(function () {
                        return !!angular.element(this).data('searchable');
                    }).map(function () {
                        var $this = angular.element(this),
                            sortable = $this.data('sortable'),
                            searchable = $this.data('searchable'),
                            field = searchable === true ? sortable : searchable;

                        return {
                            title: $this.data('title') || field,
                            field: field
                        };
                    }).get(),

                    rowObject = (($tr.data('ng-repeat') || '').match(/(\S+) in \$data/) || []).pop(),
                    sortableDefault = $td.filter('[data-sortable-default="true"]');

                // NOTE: rlt 20150714 - This is part of a hack to resolve a rare issue when data set on $el gets lost
                //                      by the link phase.
                angular
                    .element('<te-table-config></te-table-config>')
                    .attr('data-search-fields', JSON.stringify(searchFields))
                    .attr('data-default-sort', sortableDefault.length ?
                        sortableDefault.data('sortable') : $td.filter('[data-sortable]').data('sortable'))
                    .appendTo($el);

                $table.addClass('table table-bordered table-hover table-striped')
                    .attr('data-ng-table', '$table.tableParams')
                    .attr('data-te-table-body', 'data-te-table-body');

                $td.each(function () {
                    var $this = angular.element(this);

                    $this.attr('data-header-title', $this.data('title') || '\'\'');
                    if ($this.data('minWidth') > 0) {
                        $this.attr('style', 'min-width: ' + $this.data('minWidth') + 'px;');
                    }
                });

                $tr.attr('data-ng-class', '{info: $table.isRowSelected(' + rowObject + ')}')
                    .attr('data-ng-click', '$table.toggleSelection($event, ' + rowObject + ')');

                if ($el.data('selection-mode') === SELECTION_MODE.MULTI ||
                    $el.data('selection-mode') === SELECTION_MODE.SINGLE_CHECKBOX) {
                    checkboxHeader = $el.data('selection-mode') ===
                        SELECTION_MODE.MULTI ? '\'' + SELECT_ALL_CHECKBOX_HEADER + '\'' : '';
                    $tr.prepend(
                        '<td data-header-class="\'te-table-select-column\'"' +
                        '    data-header-title="\'\'"' +
                        '    data-header="' + checkboxHeader + '">' +
                        '  <div class="checkbox c-checkbox">' +
                        '    <label>' +
                        '      <input class="te-table-select-checkbox"' +
                        '             type="checkbox"' +
                        '             data-ng-checked="$table.isRowSelected(' + rowObject + ')"' +
                        '             data-ng-click="$table.toggleSelection($event, ' + rowObject + ')" />' +
                        '      <span class="fa fa-check"></span>' +
                        '    </label>' +
                        '  </div>' +
                        '</td>'
                    );
                }
            }
        };
    }

    function TeTableDirective($parse, lodash) {
        return {
            // IMPORTANT: rlt 20150628 - ng-table directive priority + 2
            //       See: https://github.com/esvit/ng-table/blob/v0.5.4/dist/ng-table.js#L774
            priority: 1003,

            bindToController: true,
            controller: 'TeTableController',
            controllerAs: '$table',
            replace: true,
            restrict: 'E',
            templateUrl: 'components/directives/te-table/te-table.html',
            transclude: true,

            scope: {
                emptyMessage: '=',
                model: '=',
                collapseSearch: '=',
                onCounterSearch: '&',
                pageSize: '@',
                pinnedRows: '=',
                selection: '=?',
                selectionMode: '@',
                filterValues: '=',
                uuid: '=',
                searchFields: '=?',
                teTableCustomSearchEnabled: '=?',
                teTableOnCustomSearch: '=?',
                teTableOnClearSearchField: '=?'
            },

            link: function (scope, element, attrs, controller, transclude) {
                var defaultSort,
                    searchFields;

                transclude(scope.$parent, function (clone) {
                    // NOTE: rlt 20150714 - This is part of a hack to resolve a rare issue when data set on elemnent
                    //                      gets lost by the link phase.
                    var $config = clone.filter('te-table-config');

                    element.find('.te-table-body').append(clone.filter('table'));
                    element.find('.te-table-btns').append(clone.filter('te-table-btns'));
                    element.find('.te-table-secondary-btns').append(clone.filter('te-table-secondary-btns'));
                    element.find('.te-table-thirdly-btns').append(clone.filter('te-table-thirdly-btns'));

                    defaultSort = $config.data('default-sort');
                    searchFields = $config.data('search-fields');
                });

                controller.defaultSort = $parse(defaultSort)(scope);

                if (!controller.teTableCustomSearchEnabled) {
                    controller.searchFields = lodash.map(
                        searchFields,

                        function (field) {
                            return lodash.extend({
                                enabled: true
                            }, lodash.mapValues(field, function (value) {
                                return $parse(value)(scope);
                            }));
                        });
                }

                controller.activate();
            }
        };
    }
})();
