package trueffect.truconnect.api.crud.factory;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.htmlinjection.AdChoicesHtmlInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.CustomInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.FacebookCustomTrackingInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.HtmlInjectionType;
import trueffect.truconnect.api.commons.validation.ValidationConstants;


/**
 * @author saulo.lopez
 */
public class HtmlInjectionTagFactory {
    public static HtmlInjectionTags createHtmlInjectionTag(HtmlInjectionType htmlInjectionType) {
        HtmlInjectionTags result = null;

        if (htmlInjectionType == null || htmlInjectionType.getType() == null) {
            return result;
        }

        switch (htmlInjectionType.getType()) {
            case AD_CHOICES:
                result = createFromAdChoicesTag((AdChoicesHtmlInjectionType) htmlInjectionType);
                break;
            case FACEBOOK_CUSTOM_TRACKING:
                result = createFromFacebookTag(
                        (FacebookCustomTrackingInjectionType) htmlInjectionType);
                break;
            case CUSTOM_TAG:
                result = createFromCustomTag((CustomInjectionType) htmlInjectionType);
                break;
        }

        return result;
    }

    private static HtmlInjectionTags createFromFacebookTag(
            FacebookCustomTrackingInjectionType facebookCustomTrackingInjectionType) {
        HtmlInjectionTags result = new HtmlInjectionTags();

        result.setIsVisible(Constants.DISABLED);
        result.setIsEnabled(Constants.ENABLED);

        result.setHtmlContent(createFacebookHtmlContent(
                facebookCustomTrackingInjectionType.getFirstPartyDomainId()));
        result.setSecureHtmlContent(createFacebookSecureHtmlContent(
                facebookCustomTrackingInjectionType.getFirstPartyDomainId()));
        result.setName(createName(facebookCustomTrackingInjectionType.getName()));

        return result;
    }

    private static HtmlInjectionTags createFromAdChoicesTag(
            AdChoicesHtmlInjectionType adChoicesHtmlInjectionType) {
        HtmlInjectionTags result = new HtmlInjectionTags();
        result.setIsVisible(Constants.ENABLED);
        result.setIsEnabled(Constants.ENABLED);

        result.setHtmlContent(
                createAdChoicesHtmlContent(adChoicesHtmlInjectionType.getOptOutUrl()));
        result.setSecureHtmlContent(
                createAdChoicesSecureHtmlContent(adChoicesHtmlInjectionType.getOptOutUrl()));
        result.setName(createName(adChoicesHtmlInjectionType.getName()));

        return result;
    }

    private static HtmlInjectionTags createFromCustomTag(CustomInjectionType customInjectionType) {
        HtmlInjectionTags result = new HtmlInjectionTags();

        result.setIsVisible(Constants.DISABLED);
        result.setIsEnabled(Constants.ENABLED);

        result.setHtmlContent(customInjectionType.getTagContent());
        result.setSecureHtmlContent(customInjectionType.getSecureTagContent());
        result.setName(createName(customInjectionType.getName()));

        return result;
    }
    private static String createAdChoicesHtmlContent(String optOutUrl) {
        return String.format(Constants.HTML_INJECTION_ADCHOICES_HTML_CONTENT, optOutUrl);
    }

    private static String createAdChoicesSecureHtmlContent(String optOutUrl) {
        return String.format(Constants.HTML_INJECTION_ADCHOICES_SECURE_HTML_CONTENT, optOutUrl);
    }

    private static String createFacebookHtmlContent(Long firstPartyDomainId) {
        return String.format(Constants.HTML_INJECTION_FACEBOOK_HTML_CONTENT, firstPartyDomainId);
    }

    private static String createFacebookSecureHtmlContent(Long firstPartyDomainId) {
        return String
                .format(Constants.HTML_INJECTION_FACEBOOK_SECURE_HTML_CONTENT, firstPartyDomainId);
    }

    public static String createName(String name) {
        String result = name;
        if (name != null) {
            result = name.trim().replaceAll(ValidationConstants.HTML_INJECTION_SPACE_REMOVER, " ");
        }
        return result;
    }
}
