'use strict';

describe('Service: SectionService', function () {
    var API_SERVICE,
        $httpBackend,
        ErrorRequestHandler,
        SectionService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _API_SERVICE_, _ErrorRequestHandler_, _SectionService_, $state) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        SectionService = _SectionService_;

        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('getList()', function () {
        it('should resolve promise with response.records[0].Section', function () {
            var promise,
                response = {
                    startIndex: 0,
                    pageSize: 1000,
                    totalNumberOfRecords: 3,
                    records: [
                        {
                            SiteSection: [
                                {
                                    createdDate: '2015-07-30T18:41:26-04:00',
                                    id: 6064880,
                                    modifiedDate: '2015-07-30T18:41:26-04:00',
                                    name: 'UI test - Placement M. Amed - 011438292487583',
                                    siteId: 6064873
                                },
                                {
                                    createdDate: '2015-07-30T18:41:19-04:00',
                                    id: 6064876,
                                    modifiedDate: '2015-07-30T18:41:19-04:00',
                                    name: 'UI test - Placement M. Amed - 031438292480829',
                                    siteId: 6064873
                                },
                                {
                                    createdDate: '2015-07-30T18:41:24-04:00',
                                    id: 6064878,
                                    modifiedDate: '2015-07-30T18:41:24-04:00',
                                    name: 'UI test - Placement M. Amed - 021438292485152',
                                    siteId: 6064873
                                }
                            ]
                        }
                    ]
                };

            $httpBackend.expectGET(API_SERVICE + 'SiteSections?query=' + encodeURIComponent('siteId=6064873'))
                .respond(response);
            promise = SectionService.getList(6064873);
            $httpBackend.flush();

            expect(promise).toBeResolved();
            expect(promise).toBeResolvedWith(response.records.SiteSection);
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'SiteSections?query=' + encodeURIComponent('siteId=6064873'))
                    .respond(404);
                SectionService.getList(6064873);
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'SiteSections?query=' + encodeURIComponent('siteId=6064873'))
                    .respond(404, 'FAILED');
                promise = SectionService.getList(6064873);
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
