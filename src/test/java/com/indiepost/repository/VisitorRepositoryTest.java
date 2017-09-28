package com.indiepost.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.indiepost.NewIndiepostApplication;
import com.indiepost.dto.stat.PeriodDto;
import com.indiepost.dto.stat.ShareStat;
import com.indiepost.enums.Types.ClientType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.indiepost.repository.RepositoryTestUtil.*;

/**
 * Created by jake on 17. 5. 25.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NewIndiepostApplication.class)
@WebAppConfiguration
@Transactional
public class VisitorRepositoryTest {

    @Autowired
    private VisitorRepository visitorRepository;

    @Test
    public void testRetrieveTotalVisitors() throws JsonProcessingException {
        PeriodDto dto = getOneDayPeriod();
        LocalDateTime since = dto.getStartDate().atStartOfDay();
        LocalDateTime until = dto.getEndDate().atTime(23, 59, 59);
        Long result = visitorRepository.getTotalVisitors(since, until);

        printPeriod(dto);
        System.out.println("Total Visitors: " + result);
        Assert.assertNotEquals("Total visitors should not 0", result.longValue(), 0);
    }

    @Test
    public void testRetrieveTotalMobileAppVisitors() throws JsonProcessingException {
        PeriodDto dto = getOneDayPeriod();
        LocalDateTime since = dto.getStartDate().atStartOfDay();
        LocalDateTime until = dto.getEndDate().atTime(23, 59, 59);
        Long result = visitorRepository.getTotalVisitors(since, until, ClientType.INDIEPOST_LEGACY_MOBILE_APP.toString());
        printPeriod(dto);
        System.out.println("Total Mobile App Visitors: " + result);
        Assert.assertNotEquals("Total mobile app visitors should not 0", result.longValue(), 0);
    }


    @Test
    public void testGetTopChannel() throws JsonProcessingException {
        PeriodDto dto = getOneDayPeriod();
        LocalDateTime since = dto.getStartDate().atStartOfDay();
        LocalDateTime until = dto.getEndDate().atTime(23, 59, 59);
        List<ShareStat> share = visitorRepository.getTopChannel(since, until, 10L);
        testSerializeAndPrintStats(share, dto, "Top Channel");
        Assert.assertTrue("Repository should return resultset", share.size() > 0);
    }

    @Test
    public void testGetTopReferrer() throws JsonProcessingException {
        PeriodDto dto = getOneDayPeriod();
        LocalDateTime since = dto.getStartDate().atStartOfDay();
        LocalDateTime until = dto.getEndDate().atTime(23, 59, 59);
        List<ShareStat> share = visitorRepository.getTopReferrers(since, until, 10L);
        testSerializeAndPrintStats(share, dto, "Top Referrer");
        Assert.assertTrue("Repository should return resultset", share.size() > 0);
    }

    @Test
    public void testGetTopOs() throws JsonProcessingException {
        PeriodDto dto = getOneDayPeriod();
        LocalDateTime since = dto.getStartDate().atStartOfDay();
        LocalDateTime until = dto.getEndDate().atTime(23, 59, 59);
        List<ShareStat> share = visitorRepository.getTopOs(since, until, 10L);
        testSerializeAndPrintStats(share, dto, "Top Os");
        Assert.assertTrue("Repository should return resultset", share.size() > 0);
    }

    @Test
    public void testGetTopBrowsers() throws JsonProcessingException {
        PeriodDto dto = getOneDayPeriod();
        LocalDateTime since = dto.getStartDate().atStartOfDay();
        LocalDateTime until = dto.getEndDate().atTime(23, 59, 59);
        List<ShareStat> share = visitorRepository.getTopWebBrowsers(since, until, 10L);
        testSerializeAndPrintStats(share, dto, "Top Web Browsers");
        Assert.assertTrue("Repository should return resultset", share.size() > 0);
    }
}