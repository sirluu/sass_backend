package jp.co.japantaxi.utils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class BooleanTypeHandler extends BaseTypeHandler<Object>
{

    @Override
    public Boolean getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return getBoolean(resultSet.getInt(s));
    }
    @Override
    public Boolean getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return getBoolean(resultSet.getInt(i));
    }
    @Override
    public Boolean getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return getBoolean(callableStatement.getInt(i));
    }
    private Boolean getBoolean(Integer s) {
        return (s == 1);
    }
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setBoolean(i, parameter.toString() != "0" ? true : false);
	}

}
