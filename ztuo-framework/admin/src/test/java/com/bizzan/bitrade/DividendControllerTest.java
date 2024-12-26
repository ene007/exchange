package com.bizzan.bitrade;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.QMemberTransaction;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.OrderDetailAggregationService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.vo.MemberTransactionVO;
import com.querydsl.core.types.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @date 2020年03月22日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AdminApplication.class)
public class DividendControllerTest {
    @Autowired
    private OrderDetailAggregationService orderDetailAggregationService;
    @Autowired
    private MemberTransactionService memberTransactionService;

    @Test
    public void queryStatistics(){
        long start = DateUtil.strToDate("2020-03-01 12:30:30").getTime();
        long end = DateUtil.strToDate("2020-03-22 14:30:30").getTime();
        System.out.println("start:"+start+"-----end:"+end);
        orderDetailAggregationService.queryStatistics(start,end);
    }

    @Test
    public void testExchangeList(){
        PageModel pageModel = new PageModel();
        pageModel.setPageNo(1);
        pageModel.setPageSize(10);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(QMemberTransaction.memberTransaction.type.eq(TransactionType.ACTIVITY_BUY));
        Page<MemberTransactionVO> results = memberTransactionService.joinFind(predicates, pageModel);
        System.out.println("results="+results);
    }
}
