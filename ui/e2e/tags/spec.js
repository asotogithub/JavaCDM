

describe('Tags', function() {
    var adTagsSend = require('./ad-tags-send.spec'),
        login = require('../utilities/login.spec'),
        logout = require('../utilities/logout.spec'),
        nav = require('../page-object/navigation.po'),
        tagsGrid = require('./tags-grid.spec'),
        users = require('../utilities/users.spec'),
        user = users.auto01;

    it('Execute Tag Suite', function() {
        browser.wait(function() {
            return nav.campaignsItem.isPresent();
        }).then(function(){
            login(user.email, user.password);
            tagsGrid();
            adTagsSend();
            logout();
        });
    });
});
