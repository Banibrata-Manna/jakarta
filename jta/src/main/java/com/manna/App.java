package com.manna;

import jakarta.transaction.*;

import java.math.BigDecimal;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        BankingService bankingService = new BankingService();

        try {
            bankingService.executeTransfer("1000", "1001", BigDecimal.valueOf(5000));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
