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
            '星期天' : 'Sun',
            '星期日' : 'Sun'
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

    /**
     * 拼接分钟
     * time : 1444 == 14:44
     * jointTime : 20 -> 拼接到1444后变成 15:04
     */
    static def jointTime(def time, def jointTime) {
        def returnValue = time

        def hour = time?.toString().substring(0, 2)
        def minute = time?.toString().substring(2, 4)

        if (!hour || !minute) return returnValue

        if (jointTime >= 60) return returnValue

        def joint_minute = minute.toInteger() + jointTime.toInteger()
        if (joint_minute < 60){
            returnValue =  "${hour}${joint_minute}"
        } else {
            if (joint_minute == 60){
                returnValue = "${hour.toInteger() + 1}00"
            }else {
                def m = (joint_minute - 60) < 10 ? "0${joint_minute - 60}" : "${joint_minute - 60}"
                returnValue = "${hour.toInteger() + 1}$m"
            }
        }

        //println("jointTime方法, 参数time=$time 参数jointTime=$jointTime, 返回值returnValue=$returnValue")
        return returnValue
    }

    /**
     * 对齐分钟
     * 当前1125 目标1700
     * 因为轮询时间是10分钟
     * 所以这里只要有一次轮询的时间 是5跟0的差值就能对齐了
     *
     * todo 轮询时间变更的话，这个方法需要修改逻辑
     */
    static def alignAtMinute(def current_time, def target_Time) {
        def c_minute = current_time?.toString().substring(3, 4)
        def t_minute = target_Time?.toString().substring(3, 4)

        if (!c_minute || !t_minute) return [false, 10]

        if (c_minute == t_minute) return [false, 10]

        return [true, Math.abs(c_minute.toInteger() - t_minute.toInteger())]
    }
}
