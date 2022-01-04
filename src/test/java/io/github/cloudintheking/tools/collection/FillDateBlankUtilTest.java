package io.github.cloudintheking.tools.collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class FillDateBlankUtilTest {

    public static class TestFillBlank extends FillDateBlankUtil.FillBlankBase<TestFillBlank> {
        private Integer sum;

        public Integer getSum() {
            return sum;
        }

        public void setSum(Integer sum) {
            this.sum = sum;
        }
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("填充空白测试")
    @Test
    void fillBlank() {
        List<TestFillBlank> testFillBlanks = new ArrayList<>();
        TestFillBlank testFillBlank1 = new TestFillBlank();
        testFillBlank1.setYear(2021);
        testFillBlank1.setMonth(12);
        testFillBlank1.setDay(2);
        testFillBlanks.add(testFillBlank1);
        TestFillBlank testFillBlank2 = new TestFillBlank();
        testFillBlank2.setYear(2021);
        testFillBlank2.setMonth(12);
        testFillBlank2.setDay(12);
        testFillBlanks.add(testFillBlank2);
        TestFillBlank testFillBlank3 = new TestFillBlank();
        testFillBlank3.setYear(2021);
        testFillBlank3.setMonth(11);
        testFillBlank3.setDay(23);
        testFillBlanks.add(testFillBlank3);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, 1, 1);
        Date end = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -49);
        Date start = calendar.getTime();
        try {
            testFillBlanks = FillDateBlankUtil.fillBlank(testFillBlanks, FillDateBlankUtil.FillBlankTypeEnum.DAY, start, end, (fillBlankBase) -> {
                TestFillBlank testFillBlank = (TestFillBlank) fillBlankBase;
                testFillBlank.setSum(1);
            });
            testFillBlanks.forEach(t -> {
                System.out.println(t.getDateStr());
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findDates() {
    }
}