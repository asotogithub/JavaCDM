'use strict';

describe('Service: UtilsCDMService', function () {
    var utilsCDMService;

    beforeEach(module('uiApp'));

    beforeEach(
        inject(function (UtilsCDMService) {
            utilsCDMService = UtilsCDMService;
        })

    );

    it('UtilsCDM service initialize correctly.', function () {
        expect(utilsCDMService).not.toBeUndefined();
    });

    it('should get a state by id.', function () {
        var states, result;

        states = {
                id: 3,
                name: 'Trafficked'
            };

        result = utilsCDMService.getEnum(states.id, utilsCDMService.states);

        expect(result).toEqual({
            id: 3,
            name: 'Trafficked'
        });
    });

    it('should get a eventType by id.', function () {
        var eventType, result;

        eventType = {
            id: 0,
            name: 'Standard'
        };

        result = utilsCDMService.getEnum(eventType.id, utilsCDMService.eventType);

        expect(result).toEqual({
            id: 0,
            name: 'Standard'
        });
    });

    it('should get a smEventType by id.', function () {
        var smEventType, result;

        smEventType = {
            id: 3,
            name: 'Measured'
        };

        result = utilsCDMService.getEnum(smEventType.id, utilsCDMService.smEventType);

        expect(result).toEqual({
            id: 3,
            name: 'Measured'
        });
    });
});
