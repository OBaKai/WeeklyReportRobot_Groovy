package llk.send

import java.text.SimpleDateFormat

class Utils {

    private static def weekChangeMap = [
            '星期一' : 'Mon',
            '星期二' : 'Tues',
            '星期三' : 'Wed',
            '星期四' : 'Thu',
            '星期五' : 'Fri',
            '星期六' : 'Sat',
            '星期天' : 'Sun'
    ]

    static def weekChange(def w){
        if (w){
            def changeWeek = weekChangeMap[w]
            if (changeWeek){
                return changeWeek
            }
        }

        return w
    }

    /**
     * 当前月份
     * @return
     */
    static def currentMonth(){
        def df = new SimpleDateFormat("MM")
        def month = df.format(new Date())
        if (month.indexOf("0") != -1){
            return month.replace("0", "")
        }

        return month
    }

    /**
     * 当前是本月的第几周
     * @return
     */
    static def currentWeekInMonth(){
        def calendar = Calendar.getInstance()
        def week = calendar.get(Calendar.WEEK_OF_MONTH) //获取是本月的第几周
        //def day = calendar.get(Calendar.DAY_OF_WEEK) //获致是本周的第几天地, 1代表星期天...7代表星期六
        return week.toString()
    }

    /**
     * 当前日期
     * @return
     */
    static def currentDate(){
        def df = new SimpleDateFormat("yyyy-MM-dd")
        return df.format(new Date())
    }

    /**
     * 当前时间
     * @return
     */
    static def currentTime(){
        def df = new SimpleDateFormat("HH-mm-ss")
        return df.format(new Date())
    }

    /**
     * 当前日期和时间
     * @return
     */
    static def currentDateAndTime(){
        def df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
        return df.format(new Date())
    }
}
