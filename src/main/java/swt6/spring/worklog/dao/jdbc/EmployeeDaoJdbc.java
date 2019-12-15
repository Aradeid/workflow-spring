package swt6.spring.worklog.dao.jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import swt6.spring.worklog.dao.EmployeeDao;
import swt6.spring.worklog.domain.Employee;

import java.sql.*;
import java.util.List;


public class EmployeeDaoJdbc extends JdbcDaoSupport implements EmployeeDao {
    protected static class EmployeeRowMapper implements RowMapper<Employee> {

        @Override
        public Employee mapRow(ResultSet resultSet, int i) throws SQLException {
            Employee employee = new Employee();
            employee.setId(resultSet.getLong(1));
            employee.setFirstName(resultSet.getString(2));
            employee.setLastName(resultSet.getString(3));
            employee.setDateOfBirth(resultSet.getDate(4).toLocalDate());

            return employee;
        }
    }

    @Override
    public Employee findById(Long id) {
        final String sql = "select id, firstName, lastName, dateOfBirth from Employee "
                + "where id = ?";
        final Object[] params = { id };
        List<Employee> emplList = getJdbcTemplate().query(sql, params, new EmployeeRowMapper());

        if (emplList.isEmpty()) {
            return null;
        } else if (emplList.size() == 1) {
            return emplList.get(0);
        } else {
            throw  new IncorrectResultSizeDataAccessException(1, emplList.size());
        }
    }

    public Employee findById1(Long id) {
        final String sql = "select id, firstName, lastName, dateOfBirth from Employee "
                + "where id = ?";
        final Object[] params = { id };
        try {
            return getJdbcTemplate().queryForObject(sql, params, new EmployeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Employee> findAll() {
        final String sql = "select id, firstName, lastName, dateOfBirth from Employee";
        return  getJdbcTemplate().query(sql, new EmployeeRowMapper());
    }

    @Override
    public void insert(Employee entity) {
        final String sql = "insert into Employee (firstName, lastName, dateOfBirth) "
                + "values(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setDate(3, Date.valueOf(entity.getDateOfBirth()));
            return  ps;
        }, keyHolder);

        entity.setId(keyHolder.getKey().longValue());
    }

    public void insert3(Employee entity) {
        final String sql = "insert into EMployee (firstName, lastName, dateOfBirth) "
                + "values(?,?,?)";
        getJdbcTemplate().update(sql, entity.getFirstName(), entity.getLastName(), Date.valueOf(entity.getDateOfBirth()));
    }

    public void insert2(Employee entity) {
        final String sql = "insert into Employee(firstName, lastName, dateOfBirth) "
            + "values(?,?,?)";
        getJdbcTemplate().update(sql, ps -> {
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setDate(3, Date.valueOf(entity.getDateOfBirth()));
        });
    }

    public void insert1(Employee entity) {
        final String sql = "insert into Employee (firstName, lastName, dateOfBirth) "
                + "values(?,?,?)";
        try(Connection conn = getDataSource().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getFirstName());
            stmt.setString(2, entity.getLastName());
            stmt.setDate(3, Date.valueOf(entity.getDateOfBirth()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    private void update(Employee entity) {
        final String sql = "update Employee set firstName=?, lastName=?, dateOfBirth=? where id = ?";
        getJdbcTemplate().update(sql,
                entity.getFirstName(),
                entity.getLastName(),
                Date.valueOf(entity.getDateOfBirth()),
                entity.getId());
    }

    @Override
    public Employee merge(Employee entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
}
