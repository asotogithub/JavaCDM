package trueffect.truconnect.api.publik.client.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

public class MultivaluedHashMap<K, V> extends HashMap<K, List<V>> implements MultivaluedMap<K, V> {

	@Override
	public void add(K arg0, V arg1) {
		List<V> values = this.get(arg0);
		if (values == null) {
			values = new ArrayList<V>();
			this.put(arg0, values);
		}

		values.add(arg1);
	}

	@Override
	public V getFirst(K arg0) {
		List<V> values = this.get(arg0);
		if (values != null && values.size() > 0) {
			return values.get(0);
		}

		return null;
	}

	@Override
	public void putSingle(K arg0, V arg1) {
		this.remove(arg0);
		this.add(arg0, arg1);
	}

}
