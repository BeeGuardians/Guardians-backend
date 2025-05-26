package com.guardians.service.dashboard;

import com.guardians.dto.dashboard.ResRadarChartDto;
import com.guardians.domain.wargame.entity.SolvedWargame;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.repository.SolvedWargameRepository;
import com.guardians.domain.wargame.repository.WargameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SolvedWargameRepository solvedRepo;
    private final WargameRepository wargameRepo;

    @Override
    public List<ResRadarChartDto.CategoryScore> calculateRadarChart(Long userId) {
        List<String> categories = List.of("Web", "Crypto", "Forensic", "BruteForce", "SourceLeak");
        List<ResRadarChartDto.CategoryScore> scores = new ArrayList<>();

        for (String category : categories) {
            List<Wargame> allProblems = wargameRepo.findByCategoryName(category);
            List<SolvedWargame> userSolved = solvedRepo.findByUserIdAndCategoryName(userId, category);

            int totalScore = allProblems.stream().mapToInt(Wargame::getScore).sum();
            int totalCount = allProblems.size();

            int userScore = userSolved.stream().mapToInt(sw -> sw.getWargame().getScore()).sum();
            int userCount = userSolved.size();

            double raw = userScore * (1 + Math.log(Math.max(1, userCount)));
            double maxRaw = totalScore * (1 + Math.log(Math.max(1, totalCount)));

            double normalized = maxRaw == 0 ? 0 : (raw / maxRaw) * 100;

            scores.add(
                    ResRadarChartDto.CategoryScore.builder()
                            .category(category)
                            .normalizedScore(normalized)
                            .build()
            );
        }

        return scores;
    }
}
