'use strict';

describe('Service: Utils', function () {
    var UtilsVar;

    beforeEach(module('te.components'));

    beforeEach(
        inject(function (Utils) {
            UtilsVar = Utils;
        })

    );

    it('should verify a element if defined or null.', function () {
        var element,
            result;

        result = UtilsVar.isUndefinedOrNull(element);

        expect(result).toBeTruthy();

        element = {
                id: 3,
                name: 'Trafficked'
            };

        result = UtilsVar.isUndefinedOrNull(result.field);

        expect(result).toBeTruthy();
    });

    it('should verify a element when defined', function () {
        var result,
            element = {
            id: 3,
            name: 'Trafficked'
        };

        result = UtilsVar.isUndefinedOrNull(element.name);

        expect(result).toBe(false);
    });

    it('should find a element on array', function () {
        var result,
            element = [
                {
                    user: 'barney',
                    age: 36,
                    active: true
                },
                {
                    user: 'fred',
                    age: 40,
                    active: false
                },
                {
                    user: 'pebbles',
                    age: 1,
                    active: true
                }
            ];

        result = UtilsVar.getElementFromArray(element, 'barney', 'user');
        expect(result.user).toBe('barney');
        expect(result.age).toBe(36);
        expect(result.active).toBe(true);
    });
});
