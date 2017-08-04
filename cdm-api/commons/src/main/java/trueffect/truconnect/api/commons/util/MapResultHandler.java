package trueffect.truconnect.api.commons.util;

import java.util.HashMap;
import java.util.List;

public interface MapResultHandler<T> {
	public List<T> handleResult(List<HashMap<String, Object>> records);
}
