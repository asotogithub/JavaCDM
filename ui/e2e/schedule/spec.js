

describe('Schedule', function() {
    var bootstrapData = {
            creatives : ['image-1-350x250.jpg','image-2-350x250.jpg', 'image-3-350x250.jpg',
                'image-4-350x250.jpg', 'creative-gif-350x250.zip', 'third-party-creative.3rd'],
            creativesDisplayName : ['image-1-350x250','image-2-350x250', 'image-3-350x250',
                'image-4-350x250', 'creative-gif-350x250'],
            placements : ['New Placement for Protractor', 'Placement1 for Delete',
                'Placement2 for Delete', 'Third Party Placement'],
            sites : ['Protractor Site'],
            tagInjection: [
                {
                    name: 'TagSchedule 01',
                    optOutUrl: 'http://www.protractor-test.com'
                },
                {
                    name: 'TagSchedule 02',
                    optOutUrl: 'http://www.protractor-test.com'
                }
            ]
        },
        campaignNameSchedule = 'Protractor Test Campaign for Schedule',
        login = require('../utilities/login.spec'),
        logout = require('../utilities/logout.spec'),
        nav = require('../page-object/navigation.po'),
        scheduleBootstrap = require('../utilities/schedule-bootstrap.spec'),
        scheduleBulk = require('./schedule-bulk.spec'),
        scheduleBulkEdit = require('./schedule-bulk-edit.spec.js'),
        scheduleChangePivot = require('./schedule-change-pivot.spec'),
        scheduleCreativeInsertion = require('./schedule-creative-insertion-weight.spec.js'),
        scheduleCreativePivotAssociations = require('./schedule-creative-pivot-associations.spec.js'),
        scheduleDelete = require('./schedule-delete.spec.js'),
        scheduleDomain = require('./schedule-domain.spec'),
        scheduleEditCTUrl = require('./schedule-edit-click-through.spec.js'),
        scheduleFilters = require('./schedule-filters.spec.js'),
        scheduleGrid = require('./schedule-grid.spec'),
        scheduleGroupPivotAssociations = require('./schedule-group-pivot-associations.spec.js'),
        scheduleModal = require('./schedule-modal.spec'),
        scheduleNew = require('./schedule-create.spec'),
        schedulePaging = require('./schedule-paging.spec.js'),
        schedulePlacementPivotAssociations = require('./schedule-placement-pivot-associations.spec.js'),
        scheduleServerSideSearch = require('./schedule-server-side-search.spec.js'),
        scheduleSitePivotAssociations = require('./schedule-site-pivot-associations.spec.js'),
        scheduleTagAssociations = require('./schedule-tag-associations.spec.js'),
        users = require('../utilities/users.spec');

    it('Execute Schedule Suite', function() {
        global.countCampaigns++;
        browser.wait(function() {
            return nav.campaignsItem.isPresent();
        }).then(function(){
            scheduleBootstrap.setupScheduleData(campaignNameSchedule, bootstrapData.creatives, 'New IO for Protractor',
                111111, bootstrapData.placements, bootstrapData.tagInjection);
            login(scheduleBootstrap.getUserCredentials().userName, scheduleBootstrap.getUserCredentials().userPassword);
            scheduleEditCTUrl(campaignNameSchedule);
            scheduleGrid(campaignNameSchedule);
            scheduleChangePivot(campaignNameSchedule);
            scheduleNew(campaignNameSchedule);
            scheduleModal(campaignNameSchedule);
            scheduleDomain(campaignNameSchedule);
            scheduleBulkEdit(campaignNameSchedule);
            scheduleBulk(campaignNameSchedule);
            scheduleCreativeInsertion(campaignNameSchedule);
            scheduleFilters(campaignNameSchedule);
            schedulePaging(campaignNameSchedule);
            scheduleServerSideSearch(campaignNameSchedule, bootstrapData);
            scheduleSitePivotAssociations(campaignNameSchedule, bootstrapData);
            schedulePlacementPivotAssociations(campaignNameSchedule, bootstrapData);
            scheduleGroupPivotAssociations(campaignNameSchedule, bootstrapData);
            scheduleCreativePivotAssociations(campaignNameSchedule, bootstrapData);
            scheduleDelete(campaignNameSchedule, bootstrapData);
            scheduleTagAssociations(campaignNameSchedule, bootstrapData);
            logout();
        });
    });
});
