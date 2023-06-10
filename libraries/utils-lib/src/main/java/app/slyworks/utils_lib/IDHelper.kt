package app.slyworks.utils_lib

import java.lang.StringBuilder
import java.util.concurrent.Callable


/**
 * Created by Joshua Sylvanus, 8:47 PM, 1/3/2022.
 */
class IDHelper {

    companion object {
        //region Vars
        private val selectionArray: IntArray = intArrayOf(0, 1, 2)
        private val numberArray: CharArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        private val lowercaseLettersArray: CharArray =
            charArrayOf(
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
        private val uppercaseLettersArray: CharArray =
            charArrayOf(
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
        //endregion

        @JvmStatic
        fun generateNewVideoCallUserID(limit: Int = 6): Int {
            val numberArray_limit = numberArray.size - 1
            val numberArray_range = (0..numberArray_limit)

            val _ID: StringBuilder = StringBuilder()
            for (index in 0..limit) {
                _ID.append(numberArray.get(numberArray_range.random()))
            }

            return _ID.toString().toInt()
        }

        /*TODO:measure performance of these 2 methods,the one using an Executor and the one without*/
        @JvmStatic
        fun generateNewUserID(): String {
            return TaskManager.runOnSingleThread(Callable<String> { generateRandomID(12) })
        }

        @JvmStatic
        fun generateNewMessageID(): String {
            return TaskManager.runOnSingleThread(Callable<String> { generateRandomID(15) })
        }

        @JvmStatic
        private fun generateRandomID(limit: Int): String {
            val selctionArray_limit = selectionArray.size - 1
            val numberArray_limit = numberArray.size - 1
            val lowercaseArray_limit = lowercaseLettersArray.size - 1
            val uppercaseArray_limit = uppercaseLettersArray.size - 1

            val selctionArray_range = (0..selctionArray_limit)
            val numberArray_range = (0..numberArray_limit)
            val lowercaseArray_range = (0..lowercaseArray_limit)
            val uppercaseArray_range = (0..uppercaseArray_limit)


            var ID: String = ""
            for (index in 0..limit) {
                when (selctionArray_range.random()) {
                    0 -> ID = ID.plus(numberArray.get(numberArray_range.random()))
                    1 -> ID = ID.plus(lowercaseLettersArray.get(lowercaseArray_range.random()))
                    2 -> ID = ID.plus(uppercaseLettersArray.get(uppercaseArray_range.random()))
                }
            }

            return ID
        }

    }
}