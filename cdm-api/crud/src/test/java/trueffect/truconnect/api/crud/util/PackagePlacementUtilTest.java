package trueffect.truconnect.api.crud.util;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNotNull;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.crud.EntityFactory;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author marleny.patsi
 */
public class PackagePlacementUtilTest {
    
    private Double rate;
    private Double adSpend;
    private String rateTypeString;
    private Long inventoryPackage;
    private Double adSpendPackage;
    private Double margin;
    private int numberPlacements;
   
    @Before
    public void init() {
        //set default values
        rate =  0D;
        adSpend = 0D;
        rateTypeString = RateTypeEnum.CPM.toString();
        inventoryPackage = Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY;
        adSpendPackage = Constants.PACKAGE_PLACEMENT_DEFAULT_AD_SPEND;
        numberPlacements = 1;
        margin = Constants.COST_DETAIL_DEFAULT_MARGIN;
    }
    
    @Test
    public void testCalculateInventoryWithDefaultValues() {
        
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
        assertThat(inventory, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
    }

    @Test
    public void testCalculateInventoryWithSpecificValuesAndCPA() {
        rate = 100.0D;
        adSpend = 200.0D;
        rateTypeString = RateTypeEnum.CPA.toString();
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
        assertThat(inventory, is(equalTo(2L)));
    }

    @Test
    public void testCalculateInventoryWithSpecificValuesAndCPC() {
        rate = 100.0D;
        adSpend = 200.0D;
        rateTypeString = RateTypeEnum.CPC.toString();
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
        assertThat(inventory, is(equalTo(2L)));
    }

    @Test
    public void testCalculateInventoryWithSpecificValuesAndCPL() {
        rate = 100.0D;
        adSpend = 200.0D;
        rateTypeString = RateTypeEnum.CPL.toString();
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
        assertThat(inventory, is(equalTo(2L)));
    }

    @Test
    public void testCalculateInventoryWithSpecificValuesAndCPM() {
        rate = 100.0D;
        adSpend = 200.0D;
        rateTypeString = RateTypeEnum.CPM.toString();
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
        assertThat(inventory, is(equalTo(2000L)));
    }

    @Test
    public void testCalculateInventoryWithSpecificValuesAndFLT() {
        rate = 100.0D;
        adSpend = 200.0D;
        rateTypeString = RateTypeEnum.FLT.toString();
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
        assertThat(inventory, is(equalTo(1L)));
    }

    @Test
    public void testCalculateInventoryWithAllValuesNull() {
        //set values
        rate =  null;
        adSpend = null;
        rateTypeString = null;
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
        assertThat(inventory, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
    }

    @Test
    public void testCalculateInventoryWithAdSpendNull() {
        //set values
        rate =  EntityFactory.random.nextDouble();
        adSpend = null;
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
        assertThat(inventory, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
    }

    @Test
    public void testCalculateInventoryWithRateTypeNull() {
        //set values
        rate =  EntityFactory.random.nextDouble();
        adSpend = EntityFactory.random.nextDouble();
        rateTypeString = null;
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
        assertThat(inventory, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
    }
    
    @Test
    public void testCalculateInventoryWithValues() {
        //set values
        rate =  EntityFactory.random.nextDouble();
        adSpend = EntityFactory.random.nextDouble();
        rateTypeString = RateTypeEnum.CPM.toString();
        //call Method to test
        Long inventory = PackagePlacementUtil.calculateInventory(rate, adSpend, rateTypeString);
        assertNotNull(inventory);
    }

    @Test
    public void testInventoryPlacementsWithDefaultValue() {
        //call Method to test
        Long inventoryPlacements = PackagePlacementUtil.inventoryPlacement(inventoryPackage, numberPlacements);
        assertNotNull(inventoryPlacements);
        assertThat(inventoryPlacements, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
    }

    @Test
    public void testInventoryPlacementsWithNumberPlacementsZero() {
        //set values
        numberPlacements = 0;
        
        //call Method to test
        Long inventoryPlacements = PackagePlacementUtil.inventoryPlacement(inventoryPackage, numberPlacements);
        assertNotNull(inventoryPlacements);
        assertThat(inventoryPlacements, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
    }

    @Test
    public void testInventoryPlacementsWithInventoryPackageNull() {
        //set values
        inventoryPackage = null;
        
        //call Method to test
        Long inventoryPlacements = PackagePlacementUtil.inventoryPlacement(inventoryPackage, numberPlacements);
        assertNotNull(inventoryPlacements);
        assertThat(inventoryPlacements, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY)));
    }

    @Test
    public void testAdSpendPlacementsWithDefaultValue() {
        //call Method to test
        Double adSpendPlacements = PackagePlacementUtil.adSpendPlacement(adSpendPackage, numberPlacements);
        assertNotNull(adSpendPlacements);
        assertThat(adSpendPlacements, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_AD_SPEND)));
    }

    @Test
    public void testAdSpendPlacementsWithWithNumberPlacementsZero() {
        //set values
        numberPlacements = 0;

        //call Method to test
        Double adSpendPlacements = PackagePlacementUtil.adSpendPlacement(adSpendPackage, numberPlacements);
        assertNotNull(adSpendPlacements);
        assertThat(adSpendPlacements, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_AD_SPEND)));
    }

    @Test
    public void testAdSpendPlacementsWithWithAdSpendPackageNull() {
        //set values
        adSpendPackage = null;

        //call Method to test
        Double adSpendPlacements = PackagePlacementUtil.adSpendPlacement(adSpendPackage, numberPlacements);
        assertNotNull(adSpendPlacements);
        assertThat(adSpendPlacements, is(equalTo(Constants.PACKAGE_PLACEMENT_DEFAULT_AD_SPEND)));
    }

    @Test
    public void testCalculatePlannedNetAdSpendWithDefaultValues() {
        
        //call Method to test
        Double netAdSpend = PackagePlacementUtil.calculatePlannedNetAdSpend(margin, adSpend);
        assertNotNull(netAdSpend);
        assertThat(netAdSpend, is(equalTo(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE)));
    }
    
    @Test
    public void testCalculatePlannedNetAdSpendWithAllValuesNull() {
        //set values
        margin =  null;
        adSpend = null;
        //call Method to test
        Double netAdSpend = PackagePlacementUtil.calculatePlannedNetAdSpend(margin, adSpend);
        assertNotNull(netAdSpend);
        assertThat(netAdSpend, is(equalTo(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE)));
    }
    
    @Test
    public void testCalculatePlannedNetAdSpendWithAdSpendNull() {
        //set values
        double minMargin = Constants.COST_DETAIL_MARGIN_MIN;
        double maxMargin = Constants.COST_DETAIL_MARGIN_MAX;
        int value = (int) minMargin + EntityFactory.random.nextInt((int) (maxMargin - minMargin + 1));
        
        margin = (double) value;
        adSpend = null;
        //call Method to test
        Double netAdSpend = PackagePlacementUtil.calculatePlannedNetAdSpend(margin, adSpend);
        assertNotNull(netAdSpend);
        assertThat(netAdSpend, is(equalTo(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE)));
    }

    @Test
    public void testCalculatePlannedNetAdSpendWithMarginNull() {
        //set values
        margin = null;
        //call Method to test
        Double netAdSpend = PackagePlacementUtil.calculatePlannedNetAdSpend(margin, adSpend);
        assertNotNull(netAdSpend);
        assertThat(netAdSpend, is(equalTo(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE)));
    }

    @Test
    public void testCalculatePlannedNetAdSpendWithMarginMaxValue() {
        //set values
        margin = Constants.COST_DETAIL_MARGIN_MAX;
        adSpend = EntityFactory.random.nextDouble();

        //call Method to test
        Double netAdSpend = PackagePlacementUtil.calculatePlannedNetAdSpend(margin, adSpend);
        assertNotNull(netAdSpend);
        assertThat(netAdSpend, is(equalTo(0D)));
    }

    @Test
    public void testCalculatePlannedNetRateWithDefaultValues() {
        
        //call Method to test
        Double netGrossRate = PackagePlacementUtil.calculatePlannedNetRate(margin, rate);
        assertNotNull(netGrossRate);
        assertThat(netGrossRate, is(equalTo(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE)));
    }
    
    @Test
    public void testCalculatePlannedNetRateWithAllValuesNull() {
        //set values
        margin =  null;
        rate = null;
        //call Method to test
        Double netGrossRate = PackagePlacementUtil.calculatePlannedNetRate(margin, rate);
        assertNotNull(netGrossRate);
        assertThat(netGrossRate, is(equalTo(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE)));
    }
    
    @Test
    public void testCalculatePlannedNetRateWithAdSpendNull() {
        //set values
        double minMargin = Constants.COST_DETAIL_MARGIN_MIN;
        double maxMargin = Constants.COST_DETAIL_MARGIN_MAX;
        int value = (int) minMargin + EntityFactory.random.nextInt((int) (maxMargin - minMargin + 1));
        
        margin = (double) value;
        rate = null;
        //call Method to test
        Double netGrossRate = PackagePlacementUtil.calculatePlannedNetRate(margin, rate);
        assertNotNull(netGrossRate);
        assertThat(netGrossRate, is(equalTo(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE)));
    }

    @Test
    public void testCalculatePlannedNetRateWithMarginNull() {
        //set values
        margin = null;
        //call Method to test
        Double netGrossRate = PackagePlacementUtil.calculatePlannedNetRate(margin, rate);
        assertNotNull(netGrossRate);
        assertThat(netGrossRate, is(equalTo(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE)));
    }

    @Test
    public void testCalculatePlannedNetRateWithMarginMaxValue() {
        //set values
        margin = Constants.COST_DETAIL_MARGIN_MAX;
        rate = EntityFactory.random.nextDouble();

        //call Method to test
        Double netGrossRate = PackagePlacementUtil.calculatePlannedNetRate(margin, rate);
        assertNotNull(netGrossRate);
        assertThat(netGrossRate, is(equalTo(0D)));
    }
}
