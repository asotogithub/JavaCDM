'use strict';

describe('Controller: AssociatedPlacementsController', function () {
    var $scope,
        controller,
        DialogFactory,
        PLACEMENT_LIST = [
            {
                id: 6101571,
                name: 'Placement 01',
                siteName: 'SITE - QA test',
                sectionName: 'Section name',
                sizeName: '180x150',
                status: 'Rejected',
                allowRemove: true
            },
            {
                id: 6101572,
                name: 'Placement 02',
                siteName: 'SITE - QA test',
                sectionName: 'Section name',
                sizeName: '180x150',
                status: 'Accepted'
            }
        ];

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, $q, $translate, CONSTANTS, CampaignsUtilService, lodash) {
        $scope = $rootScope.$new();
        DialogFactory = jasmine.createSpyObj('DialogFactory', ['showCustomDialog']);
        DialogFactory.showCustomDialog.andCallFake(function () {
            var index = controller.model.indexOf(PLACEMENT_LIST[0]);

            controller.model.splice(index, 1);

            return {
                result: $q.when([])
            };
        });

        controller = $controller('AssociatedPlacementsController', {
            $scope: $scope,
            $translate: $translate,
            CONSTANTS: CONSTANTS,
            CampaignsUtilService: CampaignsUtilService,
            DialogFactory: DialogFactory,
            lodash: lodash
        });
        controller.model = PLACEMENT_LIST;
    }));

    describe('removePlacement', function () {
        it('should remove given placement', inject(function (CONSTANTS, $translate) {
            expect(controller.model.length).toBe(2);
            controller.removePlacement(PLACEMENT_LIST[0]);

            expect(DialogFactory.showCustomDialog).toHaveBeenCalledWith({
                type: CONSTANTS.DIALOG.TYPE.WARNING,
                title: $translate.instant('DIALOGS_WARNING'),
                description: $translate.instant('package.placementAssociatedRemoveWarning'),
                buttons: CONSTANTS.DIALOG.BUTTON_SET.OK_CANCEL,
                dontShowAgainID: CONSTANTS.PACKAGE.PLACEMENT_ASSOCIATED_REMOVE_WARNING_DIALOG_ID
            });

            expect(controller.model.length).toBe(1);
        }));
    });
});
