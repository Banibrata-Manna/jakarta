package com.manna;

import com.arjuna.ats.jta.TransactionManager;
import com.mysql.cj.jdbc.MysqlXADataSource;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;

import javax.sql.XAConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionProvider {

    private final String url;
    private final String user;
    private final String password;

    public ConnectionProvider(String url, String user, String password) {
        this.url = Objects.requireNonNull(url, "URL cannot be null");
        this.user = Objects.requireNonNull(user, "User cannot be null");
        this.password = password;
    }

    public Connection getConnection() throws SQLException, SystemException, RollbackException {
        MysqlXADataSource dataSource = new MysqlXADataSource();
        dataSource.setUrl(this.url);
        dataSource.setUser(this.user);
        dataSource.setPassword(this.password);
        XAConnection xaConnection = dataSource.getXAConnection();
        Transaction tx = TransactionManager.transactionManager().getTransaction();
        tx.enlistResource(xaConnection.getXAResource());
        return xaConnection.getConnection();
    }
}