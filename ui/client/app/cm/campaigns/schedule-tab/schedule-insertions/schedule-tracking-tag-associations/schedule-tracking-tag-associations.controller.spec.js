'use strict';

describe('Controller: ScheduleTrackingTagAssociationsController', function () {
    var $httpBackend,
        $q,
        $scope,
        DialogFactory,
        PlacementService,
        controller,
        dialogDeferred,
        event,
        placementId,
        tagAssociations,
        tagAssocMock = [
            {
                htmlContent: '<script src="@@ML_DOMAIN@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                id: 70864,
                isEnabled: 1,
                name: 'ChoicesLSD',
                secureHtmlContent: '<script src="@@CPSC@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>'
            },
            {
                htmlContent: '<script src="@@ML_DOMAIN@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>',
                id: 10725912,
                isEnabled: 1,
                name: 'TA3628',
                secureHtmlContent: '<script src="@@CPSC@@fetch.cp/te_re/eav.js?x_TXNID=@@TXNID@@"></script>'
            }
        ];

    beforeEach(module('uiApp'));

    beforeEach(inject(function (_$httpBackend_, $controller, $rootScope, _$q_, _PlacementService_) {
        $rootScope.campaign = {
            advertiser: {},
            brand: {}
        };
        $scope = $rootScope.$new();
        $scope.$parent = {
            vm: {
                modelWithoutFilter: {
                    placementTagAssociationIds: {}
                },
                placementId: 9724182
            }
        };
        $q = _$q_;
        PlacementService = _PlacementService_;
        placementId = 9724182;
        tagAssociations = $q.defer();
        $httpBackend = _$httpBackend_;
        $httpBackend.whenGET('app/cm/login/login.html').respond(200, '');
        spyOn(PlacementService, 'getHtmlInjectionTags').andReturn(tagAssociations.promise);
        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showCustomDialog']);
        event = jasmine.createSpyObj('e', ['stopPropagation']);
        dialogDeferred = $q.defer();
        DialogFactory.showCustomDialog.andReturn({
            result: dialogDeferred.promise
        });
        controller = $controller('ScheduleTrackingTagAssociationsController', {
            $scope: $scope,
            PlacementService: PlacementService,
            DialogFactory: DialogFactory
        });
    }));

    describe('activate()', function () {
        it('should invoke PlacementService.getHtmlInjectionTags()', function () {
            $scope.$digest();
            expect(PlacementService.getHtmlInjectionTags).toHaveBeenCalledWith(placementId);
        });

        it('should set creativeList when PlacementService.getHtmlInjectionTags() is resolved', function () {
            tagAssociations.resolve(tagAssocMock);
            $scope.$digest();

            expect(controller.associations).toBe(tagAssocMock);
        });

        it('should initializeClosedPopovers() arrays', function () {
            tagAssociations.resolve(tagAssocMock);
            $scope.$digest();

            expect(controller.contentPopoverIsOpen[0]).toBe(false);
            expect(controller.contentPopoverIsOpen[1]).toBe(false);
            expect(controller.secureContentPopoverIsOpen[0]).toBe(false);
            expect(controller.secureContentPopoverIsOpen[1]).toBe(false);
        });
    });

    describe('viewHTMLContent()', function () {
        it('should verify contentType variable', function () {
            var contentTypes = {
                CONTENT: 'content',
                SECURE_CONTENT: 'secure-content'
            };

            controller.associations = [];
            controller.viewHTMLContent(event, contentTypes.CONTENT);
            expect(controller.contentType).toBe(contentTypes.CONTENT);

            controller.viewHTMLContent(event, contentTypes.SECURE_CONTENT);
            expect(controller.contentType).toBe(contentTypes.SECURE_CONTENT);
        });

        it('should stopPropagation() have been called', function () {
            controller.associations = [];
            controller.viewHTMLContent(event, null);
            expect(event.stopPropagation).toHaveBeenCalled();
        });
    });

    describe('deleteAssociations()', function () {
        it('should show a dialog confirmation', inject(function (CONSTANTS, $translate) {
            var message = 'tagInjection.confirm.removeAssociations',
                title = 'DIALOGS_CONFIRMATION_MSG';

            controller.deleteAssociations();
            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: CONSTANTS.DIALOG.TYPE.CONFIRMATION,
                title: $translate.instant(title),
                description: $translate.instant(message),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.CANCEL_DELETE
            });
        }));
    });
});
