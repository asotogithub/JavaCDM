'use strict';

describe('Directive: ping-box', function () {
    var $compile,
        $elementDirective,
        $q,
        element,
        scope,
        sites,
        DATA = {
            model:
            {
                eventName: 'Event02T',
                eventType: 1,
                groupName: 'Group02',
                id: 15388,
                location: 'description ',
                pingContent: 'http://ad.adlegend.com',
                pingId: 18068,
                pingTagType: 0,
                pingTagTypeField: 'Img',
                pingTagTypeList: [
                    {
                        key: 0,
                        name: 'Img'
                    },
                    {
                        key: 1,
                        name: 'Iframe'
                    },
                    {
                        key: 2,
                        name: 'Tag'
                    }
                ],
                pingType: 1,
                pingTypeField: 'Broadcast',
                siteId: 9428174,
                siteName: 'Site Name',
                smEventType: 0,
                smGroupId: 11732,
                description: 'description Name'
            }
        };

    sites = [
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
    ];

    beforeEach(module('uiApp'));
    beforeEach(module('app/directives/ping-box/ping-box.html'));

    beforeEach(inject(function (_$q_, $rootScope, $state, $templateCache, _$compile_, CONSTANTS) {
        $q = _$q_;

        DATA.model.pingPatternList = [
            new RegExp(CONSTANTS.REGEX.URL, 'i'),
            new RegExp(CONSTANTS.REGEX.URL, 'i'),
            new RegExp(CONSTANTS.REGEX.CONTENT_HTML, 'i')
        ];

        $compile = _$compile_;
        scope = _.extend($rootScope.$new(), {
            vm: DATA
        });
        spyOn($state, 'go');

        $elementDirective = angular.element(
                '<ping-box ' +
                '   data-model="vm.model"' +
                '   data-site-promise="vm.sitePromise">' +
                '</ping-box>'
        );
    }));

    describe('Directive Ping Box rendering', function () {
        beforeEach(function () {
            element = $compile($elementDirective)(scope);
            scope.$apply();
        });

        it('should render title of ping', function () {
            expect(element.find('h4').text()).toEqual('Broadcast');
            expect(element.find('.ping-box-broadcast-color')).toBeTruthy();
        });

        it('should render buttons delete - edit', function () {
            expect(element.find('.fa-pencil')).toBeTruthy();
            expect(element.find('.fa-trash')).toBeTruthy();
        });

        it('should render input publisher - description  ping', function () {
            expect(element.find('input')).toBeTruthy();
            expect(element.find('textarea')).toBeTruthy();
        });
    });
});
