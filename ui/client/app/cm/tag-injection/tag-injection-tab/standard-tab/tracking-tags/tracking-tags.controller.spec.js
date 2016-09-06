'use strict';

describe('Controller: TrackingTagsController', function () {
    var TrackingTagsController,
        $scope;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope) {
        $scope = $rootScope.$new();
        TrackingTagsController = $controller('TrackingTagsController', {
            $scope: $scope
        });
    }));

    it('should define a default value for pageSize attribute)', function () {
        expect(TrackingTagsController.pageSize).toEqual(30);
    });

    it('should select row for edit)', function () {
        var selectRows = [
            {
                id: 123,
                name: 'Test01'
            },
            {
                id: 456,
                name: 'Test02'
            }
        ];

        expect(TrackingTagsController.selectEditRow).toEqual({});
        TrackingTagsController.selectRows(selectRows);
        expect(TrackingTagsController.selectEditRow).toEqual({});
        TrackingTagsController.selectRows([
            {
                id: 123,
                name: 'Test01'
            }
        ]);
        expect(TrackingTagsController.selectEditRow).toEqual([
            {
                id: 123,
                name: 'Test01'
            }
        ]);
    });
});
