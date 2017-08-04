package trueffect.truconnect.api.crud.mybatis;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * Created by richard.jaldin on 11/6/2015.
 */
public class C3P0DataSourceFactory extends UnpooledDataSourceFactory {

    public C3P0DataSourceFactory() {
        this.dataSource = new ComboPooledDataSource();
    }
}
