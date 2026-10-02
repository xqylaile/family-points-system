package com.family.points.util;

import com.family.points.entity.FamilyMember;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 竞赛排名：同分同名次，下一名次按实际位置跳号
 */
public class PointRankingUtil {

    private PointRankingUtil() {
    }

    public static List<FamilyMember> rank(List<FamilyMember> members) {
        List<FamilyMember> ranking = new ArrayList<>(members);
        ranking.sort(Comparator.comparingInt(PointRankingUtil::pointsOf).reversed()
                .thenComparing(FamilyMember::getId));

        int rank = 0;
        Integer previousPoints = null;
        for (int i = 0; i < ranking.size(); i++) {
            FamilyMember member = ranking.get(i);
            int points = pointsOf(member);
            if (previousPoints == null || points != previousPoints) {
                rank = i + 1;
            }
            member.setRankNo(rank);
            previousPoints = points;
        }
        return ranking;
    }

    public static int pointsOf(FamilyMember member) {
        return member.getCurrentPoints() == null ? 0 : member.getCurrentPoints();
    }
}
