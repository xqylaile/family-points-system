package com.family.points.service;

import com.family.points.entity.MonthlySettlement;
import java.util.List;

/**
 * 月度结算服务
 */
public interface MonthlySettlementService {

    String getSettlementMonth();

    boolean isSettled(String month);

    MonthlySettlement settle(String expectedMonth);

    List<MonthlySettlement> listSettlements(String month);
}
