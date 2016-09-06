'use strict';

describe('Factory: DialogFactory', function () {
    var _dialogFactory,
        _constanst;

    beforeEach(module('uiApp'));

    // instantiate service
    beforeEach(inject(function (DialogFactory, CONSTANTS) {
        _dialogFactory = DialogFactory;
        _constanst = CONSTANTS;
    }));

    it('Should create an instance of the factory.', function () {
        expect(_dialogFactory).not.toBeUndefined();
    });

    it('Should load a error dialog', function () {
        expect(_dialogFactory.showDialog(_constanst.DIALOG.TYPE.ERROR)).not.toBeUndefined();
    });

    it('Should load a custom dialog', function () {
        var
            data = {
                id: 1,
                name: 'test',
                type: 'testType'
            },

            callbacks = {
                callbackConfirm: '$scope.accept',
                callbackCancel: '$scope.cancel',
                callbackError: '$scope.error'
            },

            config = {
                type: _constanst.DIALOG.TYPE.ERROR,
                template: 'fake-dialog.html',
                controller: 'fake-controller',
                data: data,
                callbacks: callbacks
            };

        expect(_dialogFactory.showCustomDialog(config)).not.toBeUndefined();
    });

    it('Should load a confirm changes dialog', function () {
        expect(_dialogFactory.showDialog(_constanst.DIALOG.SPECIFIC_TYPE.CONFIRM_CHANGES)).not.toBeUndefined();
    });
});
