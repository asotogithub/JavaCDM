'use strict';

describe('Controller: TagDetailsFlyOutController', function () {
    var $compile,
        $httpBackend,
        $scope,
        EXPECTED_RESPONSE,
        adTagDetails,
        model,
        response,
        PlacementService,
        TagDetailsFlyOutController;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$compile_,
                                $controller,
                                _$httpBackend_,
                                $q,
                                _$rootScope_,
                                $state,
                                _$translate_,
                                _DateTimeService_,
                                _PlacementService_,
                                _CONSTANTS_
                                ) {
        adTagDetails = $q.defer();
        $compile = _$compile_;
        $httpBackend = _$httpBackend_;
        PlacementService = _PlacementService_;

        $scope = _$rootScope_.$new();
        model = {
            enableSendBtn: true,
            id: 11235813
        };
        $scope.vmTeFlyOutController =
        {
            flyOutModel: {
                data: model
            }
        };

        spyOn(PlacementService, 'getAdTags').andReturn(adTagDetails.promise);
        spyOn($state, 'go');

        TagDetailsFlyOutController = $controller('TagDetailsFlyOutController', {
            $scope: $scope,
            $translate: _$translate_,
            DateTimeService: _DateTimeService_,
            PlacementService: _PlacementService_,
            CONSTANTS: _CONSTANTS_
        });

        response =
            {
                campaignName: 'UITEST QA Support-04',
                clickRedirect: 'http://0234758&ml_camp=10234756&ml_crid=10234779&click=',
                endDate: '2016-03-31T00:00:00-07:00',
                fullAdTags: '<!-- iframe/script/href/image tag </NOSCRIPT></IFRAME>',
                impressions: 4353345,
                ioDescription: 'IO_01',
                ioNumber: '1',
                noScriptVersion: '<NOSCRIPT><A Click Here" BORDER=0></A></NOSCRIPT>',
                scriptVersion: '<SCRIPT SRC="http://ui.qa.test.L_NIF=sClick Here" BORDER=0></A></NOSCRIPT>',
                startDate: '2016-03-31T00:00:00-07:00'
            };

        EXPECTED_RESPONSE = {
            campaignName: 'UITEST QA Support-04',
            clickRedirect: 'http://0234758&ml_camp=10234756&ml_crid=10234779&click=',
            endDate: '2016-03-31T00:00:00-07:00',
            fullAdTags: '<!-- iframe/script/href/image tag </NOSCRIPT></IFRAME>',
            impressions: '4353345',
            ioDescription: 'IO_01',
            ioNumber: '1',
            noScriptVersion: '<NOSCRIPT><A Click Here" BORDER=0></A></NOSCRIPT>',
            scriptVersion: '<SCRIPT SRC="http://ui.qa.test.L_NIF=sClick Here" BORDER=0></A></NOSCRIPT>',
            startDate: '2016-03-31T00:00:00-07:00',
            flightDates: '03/31/2016 — 03/31/2016',
            enableSendBtn: true
        };
    }));

    describe('activate()', function () {
        it('should load details value in model', function () {
            adTagDetails.resolve(response);
            $scope.$apply();
            expect(TagDetailsFlyOutController.model).toEqual(EXPECTED_RESPONSE);
            expect(TagDetailsFlyOutController.resource.instructions).toContain('<a href="mailto:');
        });
    });
});
