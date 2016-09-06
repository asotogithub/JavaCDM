'use strict';

describe('Controller: PropertiesAddController', function () {
    var $q,
        $scope,
        $modalInstance,
        $filter,
        $timeout,
        $translate,
        controller,
        CampaignsService,
        Constants,
        data,
        DialogFactory,
        lodash,
        modelPublisher,
        modelSection,
        modelSite,
        modelSize,
        modelSizeOnly,
        mockObject,
        PublisherService,
        SectionService,
        SiteService;

    data = {
        site: {
            id: 123,
            publisherId: 321
        }
    };

    modelPublisher = {
        publisher: {
            name: 'test publisher'
        },
        site: {
            name: undefined
        },
        section: {
            name: undefined
        },
        size: {
            name: undefined
        }
    };

    modelSite = {
        publisher: {
            name: 'test publisher'
        },
        site: {
            name: 'test site'
        },
        section: {
            name: undefined
        },
        size: {
            name: undefined
        }
    };

    modelSection = {
        publisher: {
            name: 'test publisher'
        },
        site: {
            name: 'test site'
        },
        section: {
            name: 'test section'
        },
        size: {
            name: undefined
        }
    };

    modelSize = {
        publisher: {
            name: 'test publisher'
        },
        site: {
            name: 'test site'
        },
        section: {
            name: 'test section'
        },
        size: {
            name: '800x600'
        }
    };

    modelSizeOnly = {
        publisher: {
            name: undefined
        },
        site: {
            name: undefined
        },
        section: {
            name: undefined
        },
        size: {
            name: '800x600'
        }
    };

    mockObject = {
        publisherId: '80001',
        publisherName: 'publisher_34',
        siteName: 'site_99',
        siteId: '90001'
    };

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(function () {
        module('uiApp', function ($provide) {
            CampaignsService = jasmine.createSpyObj('CampaignsService', ['bulkSaveSiteSectionSize']);
            $provide.value('CampaignsService', CampaignsService);
        });

        inject(function (_$q_) {
            $q = _$q_;
            var defer = $q.defer();

            defer.resolve(mockObject);
            //defer.reject("Duplicate");
            defer.$promise = defer.promise;

            CampaignsService.bulkSaveSiteSectionSize.andReturn(defer.promise);
        });

        installPromiseMatchers();
    });

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller,
                                _$q_,
                                $rootScope,
                                _$filter_,
                                _$timeout_,
                                _$translate_,
                                _DialogFactory_,
                                _lodash_,
                                _PublisherService_,
                                _SectionService_,
                                _SiteService_) {
        $q = _$q_;
        $modalInstance = {
            close: jasmine.createSpy('close'),
            dismiss: jasmine.createSpy('dismiss')
        };
        $filter = _$filter_;
        $timeout = _$timeout_;
        $translate = _$translate_;
        CampaignsService = CampaignsService;
        Constants = {
            PLACEMENT: {
                PROPERTIES: {
                    PUBLISHER_INPUT_MAX_LENGTH: 200,
                    SECTION_INPUT_MAX_LENGTH: 200,
                    SITE_INPUT_MAX_LENGTH: 256
                }
            }
        };
        DialogFactory = _DialogFactory_;
        lodash = _lodash_;
        SectionService = _SectionService_;
        SiteService = _SiteService_;
        PublisherService = _PublisherService_;
        $scope = $rootScope.$new();
        spyOn(PublisherService, 'getList').andReturn(PublisherService.promise);
        spyOn(SiteService, 'getList').andReturn(SiteService.promise);

        controller = $controller('PropertiesAddController', {
            $filter: $filter,
            $modalInstance: $modalInstance,
            $q: $q,
            $scope: $scope,
            $translate: $translate,
            CONSTANTS: Constants,
            CampaignsService: CampaignsService,
            DialogFactory: DialogFactory,
            PublisherService: PublisherService,
            SectionService: SectionService,
            SiteService: SiteService,
            data: data,
            lodash: lodash
        });

        spyOn(controller, 'cancel');
    }));

    describe('Section Add Site:', function () {
        it('Add a new Publisher', function () {
            controller.model = modelPublisher;
            controller.promise = null;
            controller.add();
            $timeout(function () {
                expect(controller.promise).not.toBeNull();
            }, 300);
        });

        it('Add a new Publisher and new Site', function () {
            controller.model = modelSite;
            controller.promise = null;
            controller.add();
            $timeout(function () {
                expect(controller.promise).not.toBeNull();
            }, 300);
        });

        it('Add a new Publisher, new Site and new section', function () {
            controller.model = modelSection;
            controller.promise = null;
            controller.add();
            $timeout(function () {
                expect(controller.promise).not.toBeNull();
            }, 300);
        });

        it('Add a new Publisher, new Site, new section and new size', function () {
            controller.model = modelSize;
            controller.promise = null;
            controller.add();
            $timeout(function () {
                expect(controller.promise).not.toBeNull();
            }, 300);
        });

        it('Add only new size', function () {
            controller.model = modelSizeOnly;
            controller.promise = null;
            controller.add();
            $timeout(function () {
                expect(controller.promise).not.toBeNull();
            }, 300);
        });
    });
});
