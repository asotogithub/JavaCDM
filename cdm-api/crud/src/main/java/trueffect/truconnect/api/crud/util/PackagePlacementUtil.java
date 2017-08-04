package trueffect.truconnect.api.crud.util;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.validation.CostDetailValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

/**
 *
 * @author marleny.patsi
 */
public class PackagePlacementUtil {
    
    public static Long calculateInventory(Double rate, Double adSpend, String rateTypeString) {
        Long inventory = Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY;
        // Check rate and adSpend to be different from zero
        if (rate != null && rate != 0.0) {
            if (adSpend != null && adSpend != 0.0) {
                if (rateTypeString != null) {
                    RateTypeEnum rateType = RateTypeEnum.valueOf(rateTypeString);
                    double decimalInventory = 0.0;
                    if (rateType == RateTypeEnum.CPM) {
                        decimalInventory = Math.ceil((adSpend / rate) * 1000.0);
                    } else if (rateType == RateTypeEnum.CPC || rateType == RateTypeEnum.CPA || rateType == RateTypeEnum.CPL) {
                        decimalInventory = Math.ceil(adSpend / rate);
                    }
                    if (decimalInventory <= Constants.MAX_SAFE_INTEGER) {
                        inventory = (long) decimalInventory;
                    } else {
                        throw BusinessValidationUtil.buildSystemException(
                                new ValidationError(),
                                ValidationCode.INVALID,
                                ResourceBundleUtil.getString("packagePlacement.error.inventoryTooLong"));
                    }
                }
            }
        }
        return inventory == 0L ? Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY : inventory;
    }

    public static Long inventoryPlacement(Long inventoryPackage, int numberPlacements) {

        Long inventory = Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY;

        if(numberPlacements > 0){
            if(inventoryPackage != null && inventoryPackage >= Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY) {
                inventory = (long) Math.ceil(inventoryPackage / (double) numberPlacements);
            }
        }
        return inventory == 0L ? Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY : inventory;
    }
    
    public static Double adSpendPlacement(Double adSpendPackage, int numberPlacements) {
        
        Double adSpend = Constants.PACKAGE_PLACEMENT_DEFAULT_AD_SPEND;

        if( numberPlacements > 0){
            if(adSpendPackage != null && adSpendPackage != 0.0) {
                adSpend = adSpendPackage / numberPlacements;
            }
        }
        return adSpend == 0L ? Constants.PACKAGE_PLACEMENT_DEFAULT_AD_SPEND : adSpend;
    }

    /**
     * Calculates the Planned Net Ad Spend using the following formula:
     *
     * <pre>
     *     PlannedNetAdSpend = PlannedGrossAdSpend - (PlannedGrossAdSpend * margin/100)
     * </pre>
     * Where:
     * <ul>
     *     <li>{@code PlannedGrossAdSpend = adSpend}</li>
     *     <li>{@code margin = margin}</li>
     * </ul>
     * @param margin
     * @param adSpend
     * @return
     */
    public static Double calculatePlannedNetAdSpend(Double margin, Double adSpend) {
        Double netAdSpend;
        margin = CostDetailValidator.getSafeMargin(margin);
        adSpend = CostDetailValidator.getSafePlannedGrossAdSpend(adSpend);
        netAdSpend = adSpend - (adSpend * (margin / 100.0));
        return netAdSpend;
    }

    /**
     * Calculates the Planned Net Gross Rate using the following formula:
     *
     * <pre>
     *     PlannedNetGrossRate = PlannedGrossRate - (PlannedGrossRate * margin/100)
     * </pre>
     * Where:
     * <ul>
     *     <li>{@code PlannedGrossRate = netRate}</li>
     *     <li>{@code margin = margin}</li>
     * </ul>
     * @param margin
     * @param netRate
     * @return
     */
    public static Double calculatePlannedNetRate(Double margin, Double netRate) {
        Double netGrossRate;
        margin = CostDetailValidator.getSafeMargin(margin);
        netRate = CostDetailValidator.getSafePlannedGrossRate(netRate);
        netGrossRate = netRate - (netRate * ( margin / 100.0));
        return netGrossRate;        
    }    

}
