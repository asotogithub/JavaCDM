package trueffect.truconnect.api.commons.model.enums;

/**
 * Rate Types
 * Created by richard.jaldin on 8/12/2015.
 */
public enum RateTypeEnum {

    CPA(1L),
    CPC(2L),
    CPL(3L),
    CPM(4L),
    FLT(5L);

    private Long code;

    private RateTypeEnum(Long code) {
        this.code = code;
    }

    public Long getCode() {
        return code;
    }
    
    public static RateTypeEnum typeOf(Long type) {
        if(type == null){
            throw new IllegalArgumentException("Type cannot be null");
        }
        for(RateTypeEnum rt : values()){
            if(rt.getCode().equals(type)){
                return rt;
            }
        }
        return null;
    }    
}
