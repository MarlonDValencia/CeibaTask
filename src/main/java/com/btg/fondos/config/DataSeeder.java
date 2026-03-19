package com.btg.fondos.config;

import com.btg.fondos.enums.FundCategory;
import com.btg.fondos.model.Fund;
import com.btg.fondos.repository.FundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final FundRepository fundRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (fundRepository.count() > 0) {
            return;
        }

        List<Fund> funds = List.of(
                Fund.builder().id("1").name("FPV_BTG_PACTUAL_RECAUDADORA").minimumAmount(75000).category(FundCategory.FPV).build(),
                Fund.builder().id("2").name("FPV_BTG_PACTUAL_ECOPETROL").minimumAmount(125000).category(FundCategory.FPV).build(),
                Fund.builder().id("3").name("DEUDAPRIVADA").minimumAmount(50000).category(FundCategory.FIC).build(),
                Fund.builder().id("4").name("FDO-ACCIONES").minimumAmount(250000).category(FundCategory.FIC).build(),
                Fund.builder().id("5").name("FPV_BTG_PACTUAL_DINAMICA").minimumAmount(100000).category(FundCategory.FPV).build()
        );

        fundRepository.saveAll(funds);
    }
}
