/**
 * Created by saul.crespo on 4/18/2016.
 */
'use strict';

describe('Directive: issuesContainer', function () {
    var $compile,
        element,
        scope,
        DATA = {
            config: {
                campaignId: '6158572',
                rowsIssues: 2,
                rowsErrors: 1,
                rowsWarnings: 1,
                totalRows: 3,
                rowsUpdate: 1,
                isValidComplete: false,
                pageSize: 10,
                uuid: 'f95b099c-e451-4036-96f4-bc9560cd3220',
                resources: {
                    completeImport: 'Select "Continue" below to complete your media import...',
                    mainDescription: 'We found 2 issue(s) with your media import sheet...'
                }
            },
            model: {
                errors: [
                    {
                        row: 6,
                        description: 'Creative Weight is invalid. It must be a whole number equal ' +
                            'to or between 0 and 10,000'
                    }
                ],
                warnings: [
                    {
                        row: 10,
                        description: 'Creative Weight is invalid. It must be a whole number equal ' +
                            'to or between 0 and 10,000'
                    }
                ]
            }
        };

    beforeEach(module('uiApp'));

    beforeEach(module('app/directives/issues-container/issues-container.html'));
    beforeEach(module('components/directives/te-table/te-table.html'));

    beforeEach(inject(function ($rootScope, $state, $templateCache, _$compile_) {
        $compile = _$compile_;
        scope = _.extend($rootScope.$new(), {
            vm: DATA
        });
        spyOn($state, 'go');
    }));

    describe('Directive Issues Container rendering', function () {
        it('should render cancel, continue, export buttons and label', function () {
            element = $compile(
                angular.element('<issues-container data-model="vm.model" data-config="vm.config"></issues-container>'
                )
            )(scope);
            scope.$apply();

            expect(element.find('#cancel-import-errors')).toBeTruthy();
            expect(element.find('#complete-import')).toBeTruthy();
            expect(element.find('#export-issues')).toBeTruthy();
            expect(element.find('#rowsErrors')).toBeTruthy();
        });

        it('should show rows in table', inject(function () {
            element = $compile(
                angular.element('<issues-container data-model="vm.model" data-config="vm.config"></issues-container>'
                )
            )(scope);
            scope.$apply();
            expect(element.find('.pb').html()).toEqual('We found 2 issue(s) with your media import sheet...');
            var errorModelTable = element.find('#errorsGrid tbody'),
                errorRows = errorModelTable.children('tr'),
                warningModelTable = element.find('#warningsGrid tbody'),
                warningRows = warningModelTable.children('tr');

            expect(errorRows.length).toBe(1);
            expect(warningRows.length).toBe(1);
        }));
    });
});
