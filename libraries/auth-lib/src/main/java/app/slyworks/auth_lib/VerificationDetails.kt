package app.slyworks.auth_lib

enum class VerificationDetails{
    OTP{
        private lateinit var phoneNumber:String
        override fun setDetails(details: String) { phoneNumber = details }
        override fun getDetails(): String = phoneNumber
    },
    EMAIL{
        private lateinit var email:String
        override fun setDetails(details: String) { email = details }
        override fun getDetails(): String = email
    };
    abstract fun getDetails():String
    abstract fun setDetails(details:String)
}