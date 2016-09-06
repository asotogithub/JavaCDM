'use strict';

describe('Controller: AddTrackingTagHtmlTagTypeController', function () {
    var controller,
        $scope,
        $state;

    beforeEach(module('uiApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($controller, $rootScope, _$state_) {
        $rootScope.vmAddTrackingTag = {};
        $rootScope.vmAddTrackingTag.STEP = {
            TAG_TYPE: {
                index: 1,
                isValid: false,
                key: 'addTrackingTag.tagType'
            }
        };
        $rootScope.vmAddTrackingTag.HTML_TAG_TYPE = {
            AD_CHOICES: {
                key: 'adChoices',
                name: 'tagInjection.adChoices'
            },
            FACEBOOK_CUSTOM: {
                key: 'facebookCustom',
                name: 'tagInjection.facebookCustom'
            }
        };
        $scope = $rootScope.$new();
        $state = _$state_;
        spyOn($state, 'go');
        controller = $controller('AddTrackingTagHtmlTagTypeController', {
            $scope: $scope
        });
    }));

    it('Should create an instance of the controller.', function () {
        expect(controller).not.toBeUndefined();
    });
});
