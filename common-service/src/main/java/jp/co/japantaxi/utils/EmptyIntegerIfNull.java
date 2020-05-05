package jp.co.japantaxi.utils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class EmptyIntegerIfNull implements TypeHandler<Integer> {

    @Override
    public Integer getResult(ResultSet rs, int columnIndex) throws SQLException {
        return (rs.getString(columnIndex) == null) ? 0 : rs.getInt(columnIndex);
    }

    @Override
    public Integer getResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return (cs.getString(columnIndex) == null) ? 0 : cs.getInt(columnIndex);
    }

	@Override
	public void setParameter(PreparedStatement ps, int i, Integer parameter, JdbcType jdbcType) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getResult(ResultSet rs, String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return (rs.getString(columnName) == null) ? 0 : rs.getInt(columnName);
	}
}; 
