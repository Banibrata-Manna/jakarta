package com.manna;

import jakarta.transaction.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BankingService {
    public void executeTransfer(String fromAccountId, String toAccountId, BigDecimal amount) throws Exception {
        UserTransaction utx = com.arjuna.ats.jta.UserTransaction.userTransaction();
        utx.begin();
        try {
            transfer(fromAccountId, toAccountId, amount);
            log(fromAccountId, toAccountId, amount);
            utx.commit();
        } catch (Exception e) {
            utx.rollback();
            throw e;
        }
    }

    private void transfer(String fromAccountId, String toAccountId, BigDecimal amount) throws SQLException {
        try (Connection conn = new ConnectionProvider("jdbc:mysql://localhost:3306/bank", "", "").getConnection()) {
            try (PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE ACCOUNT SET BALANCE = BALANCE + ? WHERE ACCOUNT_ID = ?"
            )) {
                ps1.setBigDecimal(1, amount);
                ps1.setString(2, toAccountId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement("UPDATE ACCOUNT SET BALANCE = BALANCE - ? WHERE ACCOUNT_ID = ?")) {
                ps2.setBigDecimal(1, amount);
                ps2.setString(2, fromAccountId);
                ps2.executeUpdate();
            }
        } catch (SystemException | RollbackException e) {
            throw new RuntimeException(e);
        }
    }

    private void log(String fromAccountId, String toAccountId, BigDecimal amount) throws SQLException {
        try (Connection conn = new ConnectionProvider("jdbc:mysql://localhost:3306/bank_audit", "", "").getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO TRANSFER_AUDIT (ACCOUNT_ID, ACCOUNT_ID_TO, TRANSFERRED_BALANCE) VALUES (?, ?, ?)")) {
                ps.setString(1, fromAccountId);
                ps.setString(2, toAccountId);
                ps.setBigDecimal(3, amount);
                ps.executeUpdate();
            }
        } catch (SystemException | RollbackException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Test Exception");
    }
}