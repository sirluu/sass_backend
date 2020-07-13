package jp.co.japantaxi.utils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class ConvertDouble implements TypeHandler<Double> {

  @Override
  public Double getResult(ResultSet rs, String columnName) throws SQLException {
    return (rs.getString(columnName) == null || rs.getString(columnName).isEmpty()) ? null
        : rs.getDouble(columnName);
  }

  @Override
  public Double getResult(ResultSet rs, int columnIndex) throws SQLException {
    return (rs.getString(columnIndex) == null || rs.getString(columnIndex).isEmpty()) ? null
        : rs.getDouble(columnIndex);
  }

  @Override
  public Double getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return (cs.getString(columnIndex) == null || cs.getString(columnIndex).isEmpty()) ? null
        : cs.getDouble(columnIndex);
  }

  @Override
  public void setParameter(PreparedStatement ps, int arg1, Double str, JdbcType jdbcType)
      throws SQLException {}
};
