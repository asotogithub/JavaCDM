package trueffect.truconnect.api.crud.service.importexport;

import trueffect.truconnect.api.commons.model.importexport.CreativeInsertionRawDataView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is intended to have all the Auxiliary methods for CreativeInsertion Import
 *
 * Created by richard.jaldin on 7/28/2016.
 */
public class CreativeInsertionImportExportHelper {

    public CreativeInsertionImportExportHelper() {
    }

    public List<CreativeInsertionRawDataView> doCleanup(List<CreativeInsertionRawDataView> records) {
        List<CreativeInsertionRawDataView> result = new ArrayList<>();
        for (CreativeInsertionRawDataView origin : records) {
            CreativeInsertionRawDataView destination = new CreativeInsertionRawDataView();
            destination.setSiteName(origin.getSiteName());
            destination.setPlacementName(origin.getPlacementName());
            destination.setPlacementId(origin.getPlacementId());
            destination.setCreativeGroupName(origin.getCreativeGroupName());
            destination.setGroupWeight(origin.getGroupWeight());
            destination.setPlacementCreativeName(origin.getPlacementCreativeName());
            destination.setCreativeWeight(origin.getCreativeWeight());
            destination.setCreativeStartDate(origin.getCreativeStartDate());
            destination.setCreativeEndDate(origin.getCreativeEndDate());
            destination.setCreativeClickThroughUrl(origin.getCreativeClickThroughUrl());
            destination.setCreativeType(origin.getCreativeType());
            destination.setCreativeInsertionId(origin.getCreativeInsertionId());
            destination.setRowError(origin.getRowError());
            result.add(destination);
        }
        return result;
    }
}
