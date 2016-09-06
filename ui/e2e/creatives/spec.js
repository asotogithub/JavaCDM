describe('Creative', function () {
    var dataSetup = require('./setup-creative'),
        creativeUpload = require('./creative-upload.spec'),
        creativeVideoCreative = require('./creative-video-creative.spec'),
        creativeUploadPagination = require('./creative-upload-pagination.spec'),
        campaignName = 'Protractor Creatives Campaign',
        nav = require('../page-object/navigation.po'),
        creativesBootstrap = require('../utilities/creatives-bootstrap.spec'),
        files = {
            gifFile: {
                name: 'pilot-GB_lifestyle_300by250.gif',
                width: '300',
                height: '250',
                type: 'gif',
                path: ''
            },
            html5file: {
                name: 'Leaderboard-MultiClick-Swiffy.zip',
                width: '728',
                height: '90',
                type: 'html5',
                path: ''
            },
            thirdPartyFile: {
                name: 'third-party-creative.3rd',
                width: '350',
                height: '250',
                type: '3rd',
                path: ''
            },
            txtFile: {
                txt: {
                    name: 'creativeTxt.txt',
                    width: '1',
                    height: '1',
                    type: 'txt',
                    path: ''
                },
                emptyTxt: {
                    name: 'creativeEmptyTxt.txt',
                    width: '1',
                    height: '1',
                    type: 'txt',
                    path: ''
                }
            },
            xmlFile: {
                vast: {
                    name: 'video-vast-creative-1-2x2.xml',
                    displayName: 'video-vast-creative-1-2x2.xml',
                    width: '2',
                    height: '2',
                    type: 'xml',
                    displayType: 'template',
                    src: 'https://truadvertiser.trueffect.com/video/SampleVideo_1080x720_1mb.mp4',
                    path: ''
                },
                vmap: {
                    name: 'video-vmap-creative-1-2x2.xml',
                    displayName: 'video-vmap-creative-1-2x2.xml',
                    width: '2',
                    height: '2',
                    type: 'xml',
                    displayType: 'template',
                    src: 'https://truadvertiser.trueffect.com/video/SampleVideo_1080x720_1mb.mp4',
                    path: ''
                }
            }
        };

    it('should execute Creative tests', function () {
        browser.wait(function() {
        return nav.campaignsItem.isPresent();
            }).then(function () {
                creativesBootstrap.setupCreativesData(campaignName);
                browser.refresh();
                dataSetup(campaignName, files);
                creativeUpload(campaignName, files);
                creativeVideoCreative(campaignName, files.xmlFile);
                creativeUploadPagination(campaignName);
            });
    });
});
