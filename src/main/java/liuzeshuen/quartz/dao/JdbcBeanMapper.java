package liuzeshuen.quartz.dao;

import liuzeshuen.quartz.model.JdbcBean;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface JdbcBeanMapper {

    public List<JdbcBean> selectlocalhost();

    public List<JdbcBean> selectDistance();
}
