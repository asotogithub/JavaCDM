'use strict';

describe('Service: publisherService', function () {
    var API_SERVICE,
        $httpBackend,
        ErrorRequestHandler,
        PublisherService;

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, _API_SERVICE_, _ErrorRequestHandler_, _PublisherService_, $state) {
        ErrorRequestHandler = _ErrorRequestHandler_;
        spyOn(ErrorRequestHandler, 'handleAndReject').andCallThrough();
        $httpBackend = _$httpBackend_;
        API_SERVICE = _API_SERVICE_;
        PublisherService = _PublisherService_;

        spyOn($state, 'go');

        installPromiseMatchers();
    }));

    describe('getList()', function () {
        it('should resolve promise with response.records[0].Publisher', function () {
            var promise,
                response = {
                    startIndex: 0,
                    pageSize: 1000,
                    totalNumberOfRecords: 2,
                    records: [
                        {
                            Publisher: [
                                {
                                    address1: 'Highway',
                                    address2: 'to hell',
                                    agencyId: 6031295,
                                    agencyNotes: 'Automated generated UI Big publisher',
                                    city: 'Seattle',
                                    country: 'USA',
                                    createdDate: '2015-07-29T17:20:37-07:00',
                                    createdTpwsKey: 'f9f79df3-b340-4db1-a521-70266e30c679',
                                    dupName: 'ui test - doctor gus',
                                    id: 5012492,
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-07-29T17:20:37-07:00',
                                    modifiedTpwsKey: 'f9f79df3-b340-4db1-a521-70266e30c679',
                                    name: 'UI Test - Doctor Gus',
                                    phoneNumber: '678.731.6283',
                                    state: 'WS',
                                    url: 'www.yahoo.com',
                                    zipCode: '12345'
                                },
                                {
                                    address1: 'Highway',
                                    address2: 'to hell',
                                    agencyId: 6031295,
                                    agencyNotes: 'Automated generated Big publisher',
                                    city: 'Seattle',
                                    country: 'USA',
                                    createdDate: '2015-07-15T12:38:56-07:00',
                                    createdTpwsKey: '414acab0-26fd-4969-ad66-49e715ca913c',
                                    dupName: 'uitest - automated generated',
                                    id: 5011330,
                                    logicalDelete: 'N',
                                    modifiedDate: '2015-07-15T12:38:56-07:00',
                                    modifiedTpwsKey: '414acab0-26fd-4969-ad66-49e715ca913c',
                                    name: 'UITEST - Automated generated',
                                    phoneNumber: '678.731.6283',
                                    state: 'WS',
                                    url: 'www.yahoo.com',
                                    zipCode: '12345'
                                }
                            ]
                        }
                    ]
                };

            $httpBackend.whenGET(API_SERVICE + 'Publishers').respond(response);
            promise = PublisherService.getList();
            $httpBackend.flush();

            expect(promise).toBeResolved();
            expect(promise).toBeResolvedWith(response.records.Publisher);
        });

        describe('on failure', function () {
            it('should show generic error dialog', inject(function () {
                $httpBackend.whenGET(API_SERVICE + 'Publishers').respond(404);
                PublisherService.getList();
                $httpBackend.flush();

                expect(ErrorRequestHandler.handleAndReject).toHaveBeenCalled();
            }));

            it('should reject promise', function () {
                var promise;

                $httpBackend.whenGET(API_SERVICE + 'Publishers').respond(404, 'FAILED');
                promise = PublisherService.getList();
                $httpBackend.flush();

                expect(promise).toBeRejectedWith(jasmine.objectContaining({
                    data: 'FAILED',
                    status: 404
                }));
            });
        });
    });
});
