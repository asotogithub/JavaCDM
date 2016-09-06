'use strict';

describe('Controller: CampaignCreativeEditClickThroughController', function () {
    var $scope,
        controller,
        invalidClickthroughs,
        validClickthroughs;

    beforeEach(module('uiApp'));

    beforeEach(inject(function ($controller, $rootScope, _lodash_) {
        $scope = $rootScope.$new();
        $rootScope.vmEdit = {
            editCreativeForm: {
                $setDirty: function () {
                }
            },
            clickThroughArray: [
                {
                    sequence: 2,
                    url: 'http://www.google.com'
                }
            ]
        };
        invalidClickthroughs = [
            'I am not a valid URL',
            'www.domain.com',
            'https://',
            'http://',
            'http://a',
            'my@email.com',
            '123',
            'http://!@#$%^&*()=+{}[]|?~<>-_;:.google.com',
            'http://www.!@#$%^&*()=+{}[]|?~<>-_;:.com',
            'http://!@#$%^&*()=+{}[]|?~<>-_;:.!@#$%^&*()=+{}[]|?~<>-_;:.com',
            'http://www.google.com/abc,',
            'http://www.google.com/ab,c',
            'http://www.google.com/,abc',
            'http://www.example.com/wpstyle/?p=36,4',
            'http://www.goo(>gle.com',
            'https://us,er@test.com:test.com.ar/path',
            'https://user@te,st.com:test.com.ar/path',
            'https://us,er@te,st.com:test.com.ar/path'
        ];
        validClickthroughs = [
            'ftp://valid-ftp.com',
            'http://foo.com/blah_blah',
            'http://foo.com/blah_blah/',
            'http://foo.com/blah_blah_(wikipedia)',
            'http://foo.com/blah_blah_(wikipedia)_(again)',
            'http://www.example.com/wpstyle/?p=364',
            'https://www.example.com/foo/?bar=baz&inga=42&quux',
            'http://✪df.ws/123',
            'http://userid:password@example.com:8080',
            'http://userid:password@example.com:8080/',
            'http://userid@example.com',
            'http://userid@example.com/',
            'http://userid@example.com:8080',
            'http://userid@example.com:8080/',
            'http://userid:password@example.com',
            'http://userid:password@example.com/',
            'http://142.42.1.1/',
            'http://142.42.1.1:8080/',
            'http://➡.ws/䨹',
            'http://⌘.ws',
            'http://⌘.ws/',
            'http://foo.com/blah_(wikipedia)#cite-1',
            'http://foo.com/blah_(wikipedia)_blah#cite-1',
            'http://foo.com/unicode_(✪)_in_parens',
            'http://foo.com/(something)?after=parens',
            'http://☺.damowmow.com/',
            'http://code.google.com/events/#&product=browser',
            'http://j.mp',
            'http://foo.bar/?q=Test%20URL-encoded%20stuff',
            'http://مثال.إختبار',
            'http://例子.测试',
            'http://उदाहरण.परीक्षा',
            'http://1337.net',
            'http://a.b-c.de',
            'http://223.255.255.254',
            'http://u--serid:password@example.com/',
            'http://a.b--c.de/',
            'http://www.foo.bar./',
            'http://WWW.foo.bar',
            'http://www.FOO.bar',
            'http://WWW.FOO.BAR',
            'HTTP://www.foo.bar',
            'HTTP://WWW.FOO.BAR?Q=TEST%20URL-ENCODED%20STUFF',
            'http://www.host.domain@@MACRO@@.com/path?param=1#@@x?PlacementID@@',
            'http://www.host@@MACRO@@.domain.com/path?param=1#@@x?PlacementID@@',
            'http://!@#$%^&*=+{}[]|?~-_;:.google.com',
            'http://www.!@#$%^&*=+{}[]|?~-_;:.com',
            'http://!@#$%^&*=+{}[]|?~-_;:.!@#$%^&*=+{}[]|?~-_;:.com',
            'http://www.google.com/abc123!@#$%^&*=+{}[]|?~-_;:'
        ];

        controller = $controller('CampaignCreativeEditClickThroughController', {
            $scope: $scope,
            lodash: _lodash_
        });
    }));

    describe('Clickthrough list', function () {
        it('should add a new click-through element', function () {
            expect(controller.clickthroughs.length).toBe(1);
            controller.add();
            expect(controller.clickthroughs.length).toBe(2);
        });

        it('should NOT add a new click-through element, if some element is empty', function () {
            controller.clickthroughs[0].url = '';
            expect(controller.clickthroughs.length).toBe(1);
            controller.add();
            expect(controller.clickthroughs.length).toBe(1);
        });

        it('should remove element of the specified index', function () {
            controller.add();
            expect(controller.clickthroughs.length).toBe(2);
            controller.remove(0);
            expect(controller.clickthroughs.length).toBe(1);
        });

        it('should NOT remove the element if it is the last one', function () {
            controller.add();
            expect(controller.clickthroughs.length).toBe(2);
            controller.remove(0);
            expect(controller.clickthroughs.length).toBe(1);
        });

        it('should NOT remove the element if the index does not exists', function () {
            controller.add();
            expect(controller.clickthroughs.length).toBe(2);
            controller.remove(100);
            expect(controller.clickthroughs.length).toBe(2);
        });

        it('should test invalid urls', function () {
            var regexp = controller.urlPattern;

            angular.forEach(invalidClickthroughs, function (invalidURL) {
                expect(regexp.test(invalidURL)).toBe(false);
            });
        });

        it('should test valid urls', function () {
            var regexp = controller.urlPattern;

            angular.forEach(validClickthroughs, function (validURL) {
                expect(regexp.test(validURL)).toBe(true);
            });
        });
    });
});
