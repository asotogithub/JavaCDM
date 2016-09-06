'use strict';

describe('Service: siteService', function () {
    var API_SERVICE,
        $httpBackend,
        ErrorRequestHandler,
        SiteService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _API_SERVICE_, _ErrorRequestHandler_, _SiteService_, $state) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        SiteService = _SiteService_;

        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('getList()', function () {
        it('should resolve promise with response.records[0].Site', function () {
            var promise,
                response = {
                    startIndex: 0,
                    pageSize: 1000,
                    totalNumberOfRecords: 3,
                    records: [
                        {
                            Site: [
                                {
                                    acceptsFlash: 'true',
                                    agencyNotes: 'Automated generated UI site',
                                    clickTrack: 'true',
                                    createdDate: '2015-07-30T18:36:04-04:00',
                                    createdTpwsKey: '238ad228-3afa-4fbd-81ec-da62bc0c854a',
                                    dupName: 'ui test for m. amed - 03',
                                    encode: 'false',
                                    externalId: 'www.external.id.bo',
                                    id: 6064875,
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-07-30T18:36:04-04:00',
                                    modifiedTpwsKey: '238ad228-3afa-4fbd-81ec-da62bc0c854a',
                                    name: 'UI Test For M. Amed - 03',
                                    preferredTag: 'IFRAME',
                                    publisherId: 5012492,
                                    publisherNotes: 'Automated generated UI site',
                                    richMedia: 'true',
                                    targetWin: '_blank',
                                    url: 'www.aol.tomy.y.com'
                                },
                                {
                                    acceptsFlash: 'true',
                                    agencyNotes: 'Automated generated UI site',
                                    clickTrack: 'true',
                                    createdDate: '2015-07-30T18:35:50-04:00',
                                    createdTpwsKey: '238ad228-3afa-4fbd-81ec-da62bc0c854a',
                                    dupName: 'ui test for m. amed ',
                                    encode: 'false',
                                    externalId: 'www.external.id.bo',
                                    id: 6064873,
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-07-30T18:35:50-04:00',
                                    modifiedTpwsKey: '238ad228-3afa-4fbd-81ec-da62bc0c854a',
                                    name: 'UI Test For M. Amed ',
                                    preferredTag: 'IFRAME',
                                    publisherId: 5012492,
                                    publisherNotes: 'Automated generated UI site',
                                    richMedia: 'true',
                                    targetWin: '_blank',
                                    url: 'www.aol.tomy.y.com'
                                },
                                {
                                    acceptsFlash: 'true',
                                    agencyNotes: 'Automated generated UI site',
                                    clickTrack: 'true',
                                    createdDate: '2015-07-30T18:36:00-04:00',
                                    createdTpwsKey: '238ad228-3afa-4fbd-81ec-da62bc0c854a',
                                    dupName: 'ui test for m. amed - 02',
                                    encode: 'false',
                                    externalId: 'www.external.id.bo',
                                    id: 6064874,
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-07-30T18:36:00-04:00',
                                    modifiedTpwsKey: '238ad228-3afa-4fbd-81ec-da62bc0c854a',
                                    name: 'UI Test For M. Amed - 02',
                                    preferredTag: 'IFRAME',
                                    publisherId: 5012492,
                                    publisherNotes: 'Automated generated UI site',
                                    richMedia: 'true',
                                    targetWin: '_blank',
                                    url: 'www.aol.tomy.y.com'
                                }
                            ]
                        }
                    ]
                };

            $httpBackend.whenGET(API_SERVICE + 'Sites').respond(response);
            promise = SiteService.getList();
            $httpBackend.flush();

            expect(promise).toBeResolved();
            expect(promise).toBeResolvedWith(response.records.Site);
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Sites').respond(404);
                SiteService.getList();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Sites').respond(404, 'FAILED');
                promise = SiteService.getList();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
