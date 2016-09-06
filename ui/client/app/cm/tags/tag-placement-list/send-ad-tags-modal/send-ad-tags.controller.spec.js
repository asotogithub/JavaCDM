'use strict';

describe('Controller: SendAdTagsController', function () {
    var $q,
        $scope,
        $uibModalInstance,
        CONSTANTS,
        EMAIL_TAGS_LIST,
        RECIPIENTS_LIST,
        SELECTED_RECIPIENTS_LIST,
        SELECTED_RECIPIENTS_LIST_2,
        SITE_LIST,
        AgencyService,
        SendAdTagsController,
        lodash,
        recipientsList;

    beforeEach(module('uiApp'));

    beforeEach(function () {
        RECIPIENTS_LIST = [
            {
                firstName: 'Ken',
                id: 1,
                lastName: 'Masters',
                realName: 'Ken Masters',
                userName: 'ken.masters@capcom.com'
            },
            {
                firstName: 'Charlie',
                id: 2,
                lastName: 'Nash',
                realName: 'Charlie Nash',
                userName: 'charlie.nash@capcom.com'
            },
            {
                firstName: 'Cammy',
                id: 3,
                lastName: 'White',
                realName: 'Cammy White',
                userName: 'cammy.white@capcom.com'
            }
        ];

        SELECTED_RECIPIENTS_LIST = [
            {
                firstName: 'Ken',
                id: 1,
                lastName: 'Masters',
                realName: 'Ken Masters',
                userName: 'ken.masters@capcom.com'
            }
        ];

        SELECTED_RECIPIENTS_LIST_2 = [
            {
                firstName: 'Charlie',
                id: 2,
                lastName: 'Nash',
                realName: 'Charlie Nash',
                userName: 'charlie.nash@capcom.com'
            },
            {
                firstName: 'Cammy',
                id: 3,
                lastName: 'White',
                realName: 'Cammy White',
                userName: 'cammy.white@capcom.com'
            }
        ];

        EMAIL_TAGS_LIST = [
            {
                text: 'ken.masters@capcom.com'
            }
        ];

        SITE_LIST = [
            {
                site: 'Site_1',
                siteId: 1,
                placements: [1]
            },
            {
                site: 'Site_2',
                siteId: 2,
                placements: [1, 2]
            },
            {
                site: 'Site_3',
                siteId: 3,
                placements: [1, 2, 3]
            }
        ];
    });

    beforeEach(inject(function (_$q_,
                                $controller,
                                $rootScope,
                                $state,
                                _CONSTANTS_,
                                _AgencyService_,
                                _lodash_) {
        $q = _$q_;
        $scope = $rootScope.$new();
        $uibModalInstance = {
            close: jasmine.createSpy('close'),
            dismiss: jasmine.createSpy('dismiss')
        };
        CONSTANTS = _CONSTANTS_;
        AgencyService = _AgencyService_;
        lodash = _lodash_;
        recipientsList = $q.defer();

        spyOn(AgencyService, 'getUsersTrafficking').andReturn(recipientsList.promise);
        spyOn($state, 'go');

        SendAdTagsController = $controller('SendAdTagsController', {
            $scope: $scope,
            $uibModalInstance: $uibModalInstance,
            CONSTANTS: CONSTANTS,
            AgencyService: AgencyService,
            data: {
                siteList: SITE_LIST
            }
        });
    }));

    describe('activate()', function () {
        it('should set default values', function () {
            expect(SendAdTagsController.formatList).toEqual(
                lodash.values(CONSTANTS.TAGS.SEND_AD_TAGS.FORMAT_TYPE.LIST));
            expect(SendAdTagsController.validForm).toEqual(false);
        });

        it('should load list of Recipients and Sites', function () {
            expect(SendAdTagsController.recipientsList.length).toEqual(0);
            recipientsList.resolve(RECIPIENTS_LIST);
            $scope.$apply();
            expect(SendAdTagsController.recipientsList.length).toEqual(3);
            expect(SendAdTagsController.siteList.length).toEqual(SITE_LIST.length);
        });
    });

    describe('clearAll()', function () {
        it('should remove all email recipients', function () {
            expect(SendAdTagsController.emailList.length).toEqual(0);
            SendAdTagsController.emailList = EMAIL_TAGS_LIST;
            expect(SendAdTagsController.emailList.length).toEqual(EMAIL_TAGS_LIST.length);
            SendAdTagsController.clearAll();
            expect(SendAdTagsController.emailList.length).toEqual(0);
        });
    });

    describe('isIndividualRecipientEmpty()', function () {
        it('should check if individual recipients are empty or not', function () {
            expect(SendAdTagsController.isIndividualRecipientEmpty()).toEqual(true);
            SendAdTagsController.tagsList[0] = {
                emailList: []
            };
            SendAdTagsController.tagsList[0].emailList[0] = 'mail1@mail.com';
            SendAdTagsController.tagsList[0].emailList[1] = 'mail2@mail.com';
            expect(SendAdTagsController.isIndividualRecipientEmpty()).toEqual(false);
        });
    });

    describe('onSelectFormat()', function () {
        it('should check if selected formats updates counter', function () {
            var id1 = 111,
                id2 = 222;

            expect(SendAdTagsController.unselectedSiteCount).toEqual(SITE_LIST.length);
            SendAdTagsController.onSelectFormat(id1);
            expect(SendAdTagsController.unselectedSiteCount).toEqual(SITE_LIST.length - 1);
            SendAdTagsController.onSelectFormat(id1);
            expect(SendAdTagsController.unselectedSiteCount).toEqual(SITE_LIST.length - 1);
            SendAdTagsController.onSelectFormat(id2);
            expect(SendAdTagsController.unselectedSiteCount).toEqual(SITE_LIST.length - 2);
            SendAdTagsController.onSelectFormat(id2);
            expect(SendAdTagsController.unselectedSiteCount).toEqual(SITE_LIST.length - 2);
            SendAdTagsController.onSelectFormat(id2);
            expect(SendAdTagsController.unselectedSiteCount).toEqual(SITE_LIST.length - 2);
        });
    });

    it('should add email tag from selected recipients', function () {
        expect(SendAdTagsController.emailList.length).toEqual(0);
        SendAdTagsController.selectedRecipientsCallback(SELECTED_RECIPIENTS_LIST);
        expect(SendAdTagsController.emailList).toEqual([
            {
                text: 'ken.masters@capcom.com'
            }
        ]);
    });

    it('should remove email tag when it is not in selected recipients list', function () {
        expect(SendAdTagsController.emailList.length).toEqual(0);
        SendAdTagsController.emailList = EMAIL_TAGS_LIST;
        SendAdTagsController.currentSelectedRecipients = SELECTED_RECIPIENTS_LIST;
        SendAdTagsController.selectedRecipientsCallback([]);
        expect(SendAdTagsController.emailList).toEqual([]);
    });

    it('should update selected recipients list when manually add an email', function () {
        SendAdTagsController.recipientsList = RECIPIENTS_LIST;
        SendAdTagsController.onEmailTagAdded(EMAIL_TAGS_LIST[0]);
        expect(SendAdTagsController.currentSelectedRecipients).toEqual(SELECTED_RECIPIENTS_LIST);
    });

    it('should update selected recipients list when remove an email tag', function () {
        SendAdTagsController.recipientsList = RECIPIENTS_LIST;
        SendAdTagsController.currentSelectedRecipients = RECIPIENTS_LIST;
        SendAdTagsController.onEmailTagRemoved(EMAIL_TAGS_LIST[0]);
        expect(SendAdTagsController.currentSelectedRecipients).toEqual(SELECTED_RECIPIENTS_LIST_2);
    });
});
