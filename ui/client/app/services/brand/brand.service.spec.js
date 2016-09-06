'use strict';

describe('Service: PlacementService', function () {
    var API_SERVICE,
        $httpBackend,
        $q,
        ErrorRequestHandler,
        BrandService,
        placementListByTag;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($state,
                                _$httpBackend_,
                                _$log_,
                                _$q_,
                                _API_SERVICE_,
                                _ErrorRequestHandler_,
                                _BrandService_) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        $q = _$q_;
        API_SERVICE = _API_SERVICE_;
        BrandService = _BrandService_;
        placementListByTag = {
            records: {
                PlacementView: [
                    {
                        campaignId: 6485070,
                        campaignName: 'RCD-NISSAN',
                        id: 7300114,
                        isTrafficked: 0,
                        name: 'Test',
                        siteId: 7285210,
                        siteName: 'AOL2',
                        siteSectionId: 7299329,
                        siteSectionName: 'testing1',
                        sizeName: '2x2',
                        status: 'New',
                        statusTraffic: 'Pending'
                    },
                    {
                        campaignId: 6485070,
                        campaignName: 'RCD-NISSAN',
                        id: 7299821,
                        isTrafficked: 0,
                        name: 'Test',
                        siteId: 7285210,
                        siteName: 'AOL2',
                        siteSectionId: 7299329,
                        siteSectionName: 'testing1',
                        sizeName: '2x2',
                        status: 'New',
                        statusTraffic: 'Pending'
                    },
                    {
                        campaignId: 6485070,
                        campaignName: 'RCD-NISSAN',
                        id: 7299824,
                        isTrafficked: 0,
                        name: 'Test',
                        siteId: 7285210,
                        siteName: 'AOL2',
                        siteSectionId: 7299329,
                        siteSectionName: 'testing1',
                        sizeName: '2x2',
                        status: 'New',
                        statusTraffic: 'Pending'
                    }
                ]
            }
        };
        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('getPlacement()', function () {
        it('should get a placements list based on the Brand id', function () {
            var brandId = 6484898,
                promise;

            $httpBackend.expectGET(API_SERVICE + 'Brands/' + brandId + '/placements').respond(placementListByTag);
            promise = BrandService.getPlacements(brandId);
            $httpBackend.flush();

            expect(promise).toBeResolvedWith(placementListByTag);
        });
    });
});
