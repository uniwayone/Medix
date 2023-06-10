package app.slyworks.utils_lib

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 *Created by Joshua Sylvanus, 11:25 AM, 1/13/2022.
 */
class TimeHelper {
    //region Vars
    private val A_DAY = 10_000_000_000L
    private val MORE_THAN_YESTERDAY  = 20_000_000_000L
    //endregion

    fun checkIfDateIsSameDay(t1:String, t2:String):Boolean{
        val sdf:SimpleDateFormat = SimpleDateFormat("dd", Locale.getDefault())

        val dateA:Int = sdf.format(Date(t1.toLong())).toInt()
        val dateB:Int = sdf.format(Date(t2.toLong())).toInt()

        return (dateB - dateA == 0)
    }

    fun convertTimeToDuration(duration:String): String {
       /*would be in the form of System.currentTimeInMillis()*/
       // val d:Duration = Duration.ofMillis(duration.toLong())
        val durationLong:Date = Date(duration.toLong())
        val sdf:SimpleDateFormat = SimpleDateFormat("HHmmss", Locale.getDefault())
        val sdf1:SimpleDateFormat = SimpleDateFormat("HH", Locale.getDefault())
        val sdf2:SimpleDateFormat = SimpleDateFormat("mm", Locale.getDefault())
        val sdf3:SimpleDateFormat = SimpleDateFormat("ss", Locale.getDefault())

        val s:StringBuilder = StringBuilder()
        val duration1:Long = sdf1.format(durationLong).toLong()
        val duration2:Long = sdf2.format(durationLong).toLong()
        val duration3:Long = sdf3.format(durationLong).toLong()
        if(duration1 >= 1L)
            s.append("$duration1:")

        if(duration2 >= 1L)
            s.append("$duration2:")
        else
            s.append("00:")

        s.append("$duration3")

        return s.toString()
    }

    fun convertTimeToString(timeStamp:String):String{
        val time: Date = Date(timeStamp.toLong())
        if(checkIfTimeStampIsYesterday(timeStamp)) {
            val sdf:DateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val currentDate:String = sdf.format(time)
            return "$currentDate, yesterday"
        }

        if(checkIfTimeStampIsToday(timeStamp)){
            val sdf: DateFormat = SimpleDateFormat("h:mm a",Locale.getDefault())
            val currentDate: String = sdf.format(time)
            return currentDate
        }

        val sdf: DateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
        val currentDate: String = sdf.format(time)

        return currentDate;
    }

    private fun checkIfTimeStampIsYesterday(timeStamp:String):Boolean{
        val __currentDate:Date = Calendar.getInstance().time
        val sdf: DateFormat = SimpleDateFormat("ddMMyyyyHHmm", Locale.getDefault())
        val _currentDate:String = sdf.format(__currentDate)
        val currentDate:Long = _currentDate.toLong()

        val _timeStamp:Long = timeStamp.toLong()
        val condition_1 = currentDate - _timeStamp >= A_DAY
        val condition_2 = currentDate - _timeStamp < MORE_THAN_YESTERDAY
        if(condition_1 && condition_2)
            return true

        return false
    }

    private fun checkIfTimeStampIsToday(timeStamp: String):Boolean{
        val __currentDate:Date = Calendar.getInstance().time
        val sdf: DateFormat = SimpleDateFormat("ddMMyyyyHHmm",Locale.getDefault())
        val _currentDate:String = sdf.format(__currentDate)
        val currentDate:Long = _currentDate.toLong()

        val _timeStamp:Long = timeStamp.toLong()
        val condition_1 = currentDate - _timeStamp < A_DAY
        if(condition_1)
            return true

        return false
    }

    fun getCurrentTime():Long = System.currentTimeMillis()

    fun getCurrentDate():Long{
        val sdf: DateFormat = SimpleDateFormat("ddMMyyyyHHmm", Locale.getDefault())
        return sdf.format(Calendar.getInstance().time).toLong()
    }

    fun isWithin3DayPeriod(lastSignInTime: Long): Boolean {
        val threeDays:Long = 3 * 24 * 60 * 60 * 1_000
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastSignInTime) < threeDays
    }

    /** timePeriod should be the number of days */
    fun isWithinTimePeriod(timeToBeChecked:Long, timePeriod:Long):Boolean{
        val numOfDays:Long = timePeriod * 24 * 60 * 1_000
        val currentTime = System.currentTimeMillis()
        return (currentTime - timeToBeChecked) < numOfDays
    }

    fun isWithinTimePeriod(timeToBeChecked:Long, timePeriod:Long, timeUnit: TimeUnit):Boolean{
        val currentTime:Long = System.currentTimeMillis()
        val _timePeriod:Long =
            when(timeUnit){
               TimeUnit.DAYS -> timePeriod  * 24 * 60 * 60 * 1_000
               TimeUnit.HOURS -> timePeriod * 60 * 60 * 1_000
               TimeUnit.MINUTES -> timePeriod * 60 * 1_000
               TimeUnit.SECONDS -> timePeriod * 1_000
               TimeUnit.MILLISECONDS -> timePeriod
               else -> throw IllegalArgumentException()
            }

        return (currentTime - timeToBeChecked) < _timePeriod
    }

}
