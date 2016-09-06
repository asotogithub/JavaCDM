'use strict';

describe('Service: UsernameMemoryService', function () {
    var usernameMemoryService,
        emailAddr = 'art@vandelayindustries.com',
        thePast = new Date();

    beforeEach(module('uiApp'));

    beforeEach(inject(function (UsernameMemoryService) {
        usernameMemoryService = UsernameMemoryService;
        localStorage.removeItem('teRememberUsername');
        localStorage.removeItem('teRememberExpiry');
    }));

    thePast.setDate(thePast.getDate() - 31);

    it('New service instance should be false, empty email', function () {
        expect(usernameMemoryService.isRememberUser()).toBe(false);
        expect(usernameMemoryService.getRememberUser()).toBeNull();
    });

    it('Set user and there should be the same user and the remember flag should auto-toggle', function () {
        usernameMemoryService.setRememberUser(emailAddr);
        expect(usernameMemoryService.getRememberUser()).toBe(emailAddr);
        expect(usernameMemoryService.isRememberUser()).toBe(true);
    });

    it('forget user, make sure flag is false and everything is cleared out', function () {
        usernameMemoryService.forgetUser();
        expect(usernameMemoryService.isRememberUser()).toBe(false);
        expect(usernameMemoryService.getRememberUser()).toBeNull();
    });

    it('backdoor force an expiry, make sure everything is cleared out', function () {
        usernameMemoryService.setRememberUser(emailAddr);
        expect(usernameMemoryService.getRememberUser()).toBe(emailAddr);
        expect(usernameMemoryService.isRememberUser()).toBe(true);
        localStorage.setItem('teRememberExpiry', thePast.getTime());
        expect(usernameMemoryService.isRememberUser()).toBe(false);
        expect(usernameMemoryService.getRememberUser()).toBeNull();
    });

    it('backdoor force an expiry to null, make sure everything is cleared out', function () {
        usernameMemoryService.setRememberUser(emailAddr);
        expect(usernameMemoryService.getRememberUser()).toBe(emailAddr);
        expect(usernameMemoryService.isRememberUser()).toBe(true);
        localStorage.removeItem('teRememberExpiry');
        expect(usernameMemoryService.isRememberUser()).toBe(false);
        expect(usernameMemoryService.getRememberUser()).toBeNull();
    });
});
