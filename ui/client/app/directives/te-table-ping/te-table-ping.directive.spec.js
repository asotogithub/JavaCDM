'use strict';

describe('Directive: te-table-ping', function () {
    var $compile,
        $elementDirective,
        element,
        scope,
        DATA = {
            searchFields: [
                {
                    enabled: true,
                    field: 'Tag Type',
                    position: 1,
                    title: 'Tag Type'
                },
                {
                    enabled: true,
                    field: 'Type',
                    position: 2,
                    title: 'Type'
                },
                {
                    enabled: true,
                    field: 'Site',
                    position: 3,
                    title: 'Site'
                }
            ],
            filterValues: [
                {
                    fieldName: 'publisherName',
                    values: []
                },
                {
                    fieldName: 'pingType',
                    values: []
                }
            ],
            filterOption: {
                PUBLISHER: {
                    text: 'Publisher',
                    value: []
                },
                PING_TYPE: {
                    text: 'Ping Type',
                    value: []
                }
            },
            actionFilter: [
                {
                    onSelectAll: '',
                    onDeselectAll: '',
                    onItemAction: ''
                },
                {
                    onSelectAll: '',
                    onDeselectAll: '',
                    onItemAction: ''
                }
            ],
            model: {
                createdDate: '2015-06-11T10:30:04-07:00',
                createdTpwsKey: 'dac374a0-91e3-4675-a1c8-dd9ad5cb20fd',
                eventName: 'Event01',
                eventType: 1,
                id: 15261,
                isTrafficked: 1,
                location: 'ed laoreet convallis diam. Donec sagittis dolor placerat, finibus leo nec, suscipit lacus.' +
                    ' Vestibulum posuere purus quam, at tempus risus tempus at.',
                logicalDelete: 'N',
                measurementState: 3,
                modifiedDate: '2016-08-03T10:23:36-07:00',
                modifiedTpwsKey: 'dac374a0-91e3-4675-a1c8-dd9ad5cb20fd',
                pingEvents: [],
                smEventType: 3,
                smGroupId: 11664,
                eventTypeSelected: {
                    id: 3,
                    name: 'Measured'
                },
                groupName: 'A_Name_Group',
                isReadOnly: false,
                loadPings: true,
                tagTypeSelected: {
                    id: 1,
                    name: 'TruTag'
                }
            },
            siteList: [
                {
                    eventName: 'Event01',
                    eventType: 1,
                    groupName: '01_PZ_Group',
                    id: 15375,
                    pingContent: 'http://ad.adlegend.com',
                    pingId: 18057,
                    pingTagType: 0,
                    pingType: 1,
                    siteId: 9428174,
                    siteName: 'site name',
                    smEventType: 2,
                    smGroupId: 11665
                },
                {
                    eventName: 'Event01',
                    eventType: 1,
                    groupName: '01_PZ_Group',
                    id: 15375,
                    pingContent: 'http://ad.adlegend.com',
                    pingId: 18058,
                    pingTagType: 0,
                    pingType: 2,
                    siteId: 9428174,
                    siteName: 'site Name ',
                    smEventType: 2,
                    smGroupId: 11665
                }
            ]
        };

    beforeEach(module('uiApp'));
    beforeEach(module('app/directives/te-table-ping/te-table-ping.html'));
    beforeEach(module('app/directives/ping-box/ping-box.html'));
    beforeEach(module('app/directives/dropdown-multicheckbox/dropdown-multicheckbox.html'));
    beforeEach(module('te.components'));
    beforeEach(module('components/directives/te-table/te-table-add-btn/te-table-add-btn.html'));

    beforeEach(inject(function ($rootScope, $state, $templateCache, _$compile_) {
        $compile = _$compile_;
        scope = _.extend($rootScope.$new(), {
            vm: DATA
        });
        spyOn($state, 'go');

        $elementDirective = angular.element(
            '<te-table-ping data-empty-message="someEmptyMessage"' +
            '   data-model="vm.model.pingEvents"' +
            '   data-search-fields="vm.searchFields"' +
            '   data-te-table-custom-search-enabled="true"' +
            '   data-site-values="vm.siteList"' +
            '  <te-table-btns>' +
            '    <te-table-add-btn id="addBtn"></te-table-add-btn>' +
            '  </te-table-btns>' +
                '<te-table-secondary-btns>' +
                '   <dropdown-multicheckbox ' +
                '           data-ng-repeat="option in vm.filterOption"' +
                '           data-input-model="option.value"' +
                '           data-output-model="vm.filterValues[$index].values"' +
                '           data-events="vm.actionFilter[$index]"' +
                '           data-title="option.text">' +
                '   </dropdown-multicheckbox>' +
                    '<button id="filterOptions"' +
                    'data-ng-click="vm.toggleFilter()"' +
                    'class="btn btn-warning">' +
                        '<span class="text-center">' +
                            '<i class="fa fa-filter"></i>' +
                        '</span>' +
                    '</button>' +
                '</te-table-secondary-btns>' +
            '</te-table-ping>'
        );
    }));

    describe('Directive Table Ping rendering', function () {
        it('should render add - search - filters', function () {
            element = $compile($elementDirective)(scope);
            scope.$apply();

            expect(element.find('#addBtn').closest('.te-table-btns')).toBeTruthy();
            expect(element.find('#filterOptions').closest('.te-table-secondary-btns')).toBeTruthy();
            expect(element.find('.te-table-search')).toBeTruthy();
            expect(element.find('.dropdown-toggle')).toBeTruthy();
        });
    });
});
