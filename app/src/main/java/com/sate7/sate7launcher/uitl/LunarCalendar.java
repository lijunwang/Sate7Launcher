package com.sate7.sate7launcher.uitl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LunarCalendar {
    private int lyear;

    private int lmonth;

    private int lday;

    private boolean leap;

    private int yearCyl, monCyl, dayCyl;

    /**
     * 公历节气
     */
    private String solarTerms = "";
    /**
     * 公历节日
     */
    private String solarFestival = "";
    /**
     * 农历节日
     */
    private String lunarFestival = "";
    /**
     * 农历日期
     */
    private String lunarDate = "";

    private Calendar baseDate = Calendar.getInstance();

    private Calendar offDate = Calendar.getInstance();

    private SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    final static long[] lunarInfo = new long[] { 0x04bd8, 0x04ae0, 0x0a570,
            0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0,
            0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50,
            0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
            0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0,
            0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4,
            0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550,
            0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950,
            0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260,
            0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
            0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40,
            0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3,
            0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960,
            0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0,
            0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
            0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
            0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65,
            0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0,
            0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2,
            0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 };

    final static String[] Gan = new String[] { "甲", "乙", "丙", "丁", "戊", "己",
            "庚", "辛", "壬", "癸" };

    final static String[] Zhi = new String[] { "子", "丑", "寅", "卯", "辰", "巳",
            "午", "未", "申", "酉", "戌", "亥" };

    final static String[] Animals = new String[] { "鼠", "牛", "虎", "兔", "龙",
            "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };

    final static String[] SolarTerm = new String[] { "小寒", "大寒", "立春", "雨水",
            "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋",
            "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至" };

    final static long[] STermInfo = new long[] { 0, 21208, 42467, 63836, 85337,
            107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343,
            285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795,
            462224, 483532, 504758 };

    final static String chineseMonthNumber[] = { "正", "二", "三", "四", "五", "六",
            "七", "八", "九", "十", "冬", "腊" };

    final static String chineseNumber[] =
            { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二" };

    final static String[] sFtv = new String[] {"0101*元旦",
            "0214 情人节", "0308 妇女节", "0312 植树节", "0401 愚人节",
            "0501*劳动节", "0504 青年节", "0512 护士节", "0601 儿童节",
            "0626 反毒品日","0701 建党节", "0801 建军节", "0910 教师节",
            "1001*国庆节", "1031 万圣节", "1128 感恩节", "1225 圣诞节" };

    final static String[] lFtv = { "0101*春节", "0115 元宵", "0505 端午",
            "0707 七夕", "0815 中秋", "0909 重阳", "1208 腊八", "1223 小年",
            "0100*除夕" };

    final static String[] wFtv = { "0521 母亲节", "0631 父亲节" };//每年6月第3个星期日是父亲节,5月的第2个星期日是母亲节

    //星期日是一个周的第1天第3个星期日也就是第3个完整周的第一天

    //
    private LunarCalendar() {
        baseDate.setMinimalDaysInFirstWeek(7);//设置一个月的第一个周是一个完整周
    }

    /**
     * 获得实例
     * @return
     */
    public static LunarCalendar getInstance() {
        return new LunarCalendar();
    }

    final private static int lYearDays(int y)//====== 传回农历 y年的总天数
    {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0)
                sum += 1;
        }
        return (sum + leapDays(y));
    }

    final private static int leapDays(int y)//====== 传回农历 y年闰月的天数
    {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else
            return 0;
    }

    final private static int leapMonth(int y)//====== 传回农历 y年闰哪个月 1-12 , 没闰传回 0
    {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }

    final public static int monthDays(int y, int m)//====== 传回农历 y年m月的总天数
    {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
            return 29;
        else
            return 30;
    }

    final private static String AnimalsYear(int y)//====== 传回农历 y年的生肖
    {

        return Animals[(y - 4) % 12];
    }

    final private static String cyclical(int num)//====== 传入 的offset 传回干支,
    // 0=甲子
    {

        return (Gan[num % 10] + Zhi[num % 12]);
    }

    //  ===== 某年的第n个节气为几日(从0小寒起算)
    final private int sTerm(int y, int n) {

        offDate.set(1900, 0, 6, 2, 5, 0);
        long temp = offDate.getTime().getTime();
        offDate.setTime(new Date((long) ((31556925974.7 * (y - 1900) + STermInfo[n] * 60000L) + temp)));

        return offDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 传出y年m月d日对应的农历.
     * @param curDate   例如 2016年12月29日
     */
    private void CalculateLunarCalendar(String curDate) {

        int leapMonth;
        int y = 0;
        int m = 0;
        int d = 0;

        try {
            baseDate.setTime(chineseDateFormat.parse("1900年1月31日"));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long base = baseDate.getTimeInMillis();
        try {
            baseDate.setTime(chineseDateFormat.parse(curDate));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(chineseDateFormat.parse(curDate));
            y = calendar.get(Calendar.YEAR);
            m = calendar.get(Calendar.MONTH) + 1;
            d = calendar.get(Calendar.DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long obj = baseDate.getTimeInMillis();

        int offset = (int) ((obj - base) / 86400000L);
        //求出和1900年1月31日相差的天数
        dayCyl = offset + 40;//干支天
        monCyl = 14;//干支月

        //用offset减去每农历年的天数
        // 计算当天是农历第几天
        //i最终结果是农历的年份
        //offset是当年的第几天
        int iYear, daysOfYear = 0;
        for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
            daysOfYear = lYearDays(iYear);
            offset -= daysOfYear;
            monCyl += 12;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
            monCyl -= 12;
        }
        //农历年份
        lyear = iYear;

        yearCyl = iYear - 1864;//***********干支年**********//

        leapMonth = leapMonth(iYear); //闰哪个月,1-12
        leap = false;

        //用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
        int iMonth, daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
            //闰月
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                --iMonth;
                leap = true;
                daysOfMonth = leapDays(iYear);
            } else
                daysOfMonth = monthDays(iYear, iMonth);

            offset -= daysOfMonth;
            //解除闰月
            if (leap && iMonth == (leapMonth + 1))
                leap = false;
            if (!leap)
                monCyl++;
        }
        //offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (leap) {
                leap = false;
            } else {
                leap = true;
                --iMonth;
                --monCyl;
            }
        }
        //offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
            --monCyl;
        }
        lmonth = iMonth;
        lday = offset + 1;

        /**
         * 计算阴历日期
         */
        lunarDate = getChinaDayString(lday);

        /**
         * 计算24节气
         */
        if (d == sTerm(y, (m - 1) * 2))
            solarTerms = SolarTerm[(m - 1) * 2];
        else if (d == sTerm(y, (m - 1) * 2 + 1))
            solarTerms = SolarTerm[(m - 1) * 2 + 1];
        else
            solarTerms = "";

        /**
         * 计算公历节日
         */
        this.solarFestival = "";
        for (String ftv : sFtv) {
            if (Integer.parseInt(ftv.substring(0, 2)) == m
                    && Integer.parseInt(ftv.substring(2, 4)) == d) {
                solarFestival = ftv.substring(5);
            }
        }

        /**
         * 计算农历节日
         */
        this.lunarFestival = "";
        for (String ftv : lFtv) {
            if (Integer.parseInt(ftv.substring(0, 2)) == lmonth
                    && Integer.parseInt(ftv.substring(2, 4)) == lday) {
                lunarFestival = ftv.substring(5);
            }
        }

        /**
         * 计算公历节日
         */
        for (String ftv : wFtv) {
            if (Integer.parseInt(ftv.substring(0, 2)) == m &&
                    Integer.parseInt(ftv.substring(2, 3)) == baseDate.get(Calendar.WEEK_OF_MONTH) &&
                    Integer.parseInt(ftv.substring(3, 4)) == baseDate.get(Calendar.DAY_OF_WEEK)) {
                solarFestival += ftv.substring(5);
            }
        }

    }

    /**
     * 根据阴历日期计算阴历
     * @param day
     * @return
     */
    private String getChinaDayString(int day) {
        String chineseTen[] =
                { "初", "十", "廿", "卅" };
        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day > 30)
            return "";
        if (day == 10)
            return "初十";
        else
            return chineseTen[day / 10] + chineseNumber[n];
    }

    public void set(String curDate) {
        CalculateLunarCalendar(curDate);
    }


    public String getSolarTerms() {
        return solarTerms;
    }

    public String getSolarFestival() {
        return solarFestival;
    }

    public String getLunarFestival() {
        return lunarFestival;
    }

    public String getLunarDate() {
        return lunarDate;
    }
    public String getLunarMonth(){

        if(lmonth==1||lmonth==11||lmonth==12)   return chineseNumber[lmonth-1]+"月("+ chineseMonthNumber[lmonth-1]+")";

        return chineseNumber[lmonth-1]+"月 ";
    }
}

