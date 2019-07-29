package com.example.quartz.jobs;

import com.example.quartz.service.BaseJob;
import com.example.quartz.dao.JdbcBeanMapper;
import com.example.quartz.model.JdbcBean;
import com.example.quartz.tool.SpringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//mysqldump -P 3306 -h 192.168.1.125 -uroot -p1111 jxc --default-character-set=utf8 --lock-tables=false --result-file=D:\res\backup\abc\2019_05_29_13_55_36_backup.sql
public class DataBaseBackUp implements BaseJob {

    public static final String SQL_BACKUP_PREFIX_FORMAT = "yyyy_MM_dd_HH_mm_ss";


    private Logger logger = LoggerFactory.getLogger(getClass());

    static String filePath;

    public void backup(JdbcBean jdbcBean){
        try {
            String fileName = new SimpleDateFormat(SQL_BACKUP_PREFIX_FORMAT).format(new Date()) + "_backup";
            filePath = getFilePath(fileName + ".sql", jdbcBean);
            boolean exportFlag = executeExportCommond(jdbcBean,filePath);
            if(exportFlag){
                logger.info("数据库数据备份本地成功！,数据库为【{}】",jdbcBean.getDB());
            }else{
                logger.info("数据库数据备份失败,数据库为【{}】",jdbcBean.getDB());
            }
        } catch (Exception e) {
            logger.info("运行异常，数据库数据备份失败！");
            e.printStackTrace();
            logger.info("本地备份失败,数据库为【{}】", jdbcBean.getDB());
        }
    }


    /**
     * 导出数据(方法一)
     * dos命令执行备份 mysqldump -P port -h ip -u username -ppassWord projectName > d:\db.sql
     * C:\Program Files\MySQL\MySQL Server 5.6\bin\mysqldump -P 3306 -h rm-bp1o0s4ntr2do799nko.mysql.rds.aliyuncs.com -u xwmysqlroot -pXw68789481! xiaowei_zaixian --default-character-set=utf8 --lock-tables=false --result-file=D:/data/design_center/dbfiles/20180817185421_backup.sql
     * @param jdbcBean
     * @param filePath
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    //执行导出命令
    public static boolean executeExportCommond(JdbcBean jdbcBean,String filePath) throws IOException, InterruptedException{


        //静态获取本地MySQL数据库安装目录下bin的目录
//        String C="C:\\Program Files\\MySQL\\MySQL Server 5.6\\bin\\";
//        append(" --column-statistics=0").

                String sql = new StringBuffer("mysqldump").
                append(" -P ").append(jdbcBean.getPort()).
                append(" -h ").append(jdbcBean.getIP()).
                append(" -u").append(jdbcBean.getUserName()).
                append(" -p").append(jdbcBean.getPassword()).
                append(" ").append(jdbcBean.getDB()).
                append(" --default-character-set=utf8 ").
                append("--lock-tables=false --result-file=").append(filePath).toString();

        System.out.println(sql);

        Process process = Runtime.getRuntime().exec(sql);
        if(process.waitFor()==0){//0 表示线程正常终止。
            return true;
        }
        return false;//异常终止
    }


    //获得文件路径
    public static String getFilePath(String fileName,JdbcBean jdbcBean){
        String rootPath;
        String filPath;
        rootPath = jdbcBean.getBackupPath() + jdbcBean.getDB() + "/";
        if(!new File(rootPath).exists()){//判断文件是否存在
            new File(rootPath).mkdirs();//可以在不存在的目录中创建文件夹。诸如：a\\b,既可以创建多级目录。
        }
        if(fileName==null){
            filPath = rootPath+new SimpleDateFormat(SQL_BACKUP_PREFIX_FORMAT).format(new Date())+"_backup.sql";
        }else{
            filPath = rootPath+fileName;
        }
        return filPath;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<JdbcBean> jdbcBeanList = SpringUtil.getBean(JdbcBeanMapper.class).selectlocalhost();
        for (JdbcBean jdbc:jdbcBeanList) {
            backup(jdbc);
        }
    }
}