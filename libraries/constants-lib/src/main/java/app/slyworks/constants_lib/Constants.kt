package app.slyworks.constants_lib


/**
 * Created by Joshua Sylvanus, 12:01 PM, 12/10/2021.
 */
const val AUTHORITY = "app.slyworks.medix"

//region Events
const val EVENT_GET_NETWORK_UPDATES = "app.slyworks.medix.EVENT_GET_NETWORK_UPDATES"
const val EVENT_USER_REGISTRATION = "app.slyworks.medix.EVENT_USER_REGISTRATION"
const val EVENT_SEND_PASSWORD_RESET_EMAIL = "app.slyworks.medix.EVENT_SEND_PASSWORD_RESET_EMAIL"
const val EVENT_USER_LOGIN = "app.slyworks.medix.EVENT_USER_LOGIN"
const val EVENT_SELECT_PROFILE_IMAGE = "app.slyworks.medix.EVENT_SELECT_PROFILE_IMAGE"
const val EVENT_ROLLBACK_REGISTRATION = "app.slyworks.medix.EVENT_ROLLBACK_REGISTRATION"
const val EVENT_GET_DOCTOR_USERS = "app.slyworks.medix.EVENT_GET_DOCTOR_USERS"
const val EVENT_GET_DISPLAY_VIEW = "app.slyworks.medix.EVENT_GET_DISPLAY_VIEW"
const val EVENT_GET_ALL_MESSAGES = "app.slyworks.medix.EVENT_GET_ALL_MESSAGES"
const val EVENT_SEND_CLOUD_MESSAGE = "app.slyworks.medix.EVENT_SEND_CLOUD_MESSAGE"
const val EVENT_UPDATE_FCM_REGISTRATION_TOKEN = "app.slyworks.medix.EVENT_UPDATE_FCM_REGISTRATION_TOKEN"
const val EVENT_GET_CONSULTATION_REQUEST = "app.slyworks.medix.EVENT_GET_CONSULTATION_REQUEST"
const val EVENT_OPEN_VIEW_PROFILE_FRAGMENT = "app.slyworks.medix.EVENT_OPEN_VIEW_PROFILE"
const val EVENT_OPEN_MESSAGE_ACTIVITY = "app.slyworks.medix.EVENT_OPEN_MESSAGE_FRAGMENT"
const val EVENT_OPEN_MESSAGE_ACTIVITY_2 = "app.slyworks.medix.EVENT_OPEN_MESSAGE_FRAGMENT_2"
const val EVENT_GET_USER_DATA_FOR_UID = "app.slyworks.medix.EVENT_GET_USER_DATA_FOR_UID"
const val EVENT_UPDATE_MESSAGE_COUNT = "app.slyworks.medix.EVENT_UPDATE_MESSAGE_COUNT"
const val EVENT_SEND_REQUEST = "app.slyworks.medix.EVENT_SEND_REQUEST"
const val EVENT_INCOMING_VIDEO_CALL = "app.slyworks.medix.EVENT_INCOMING_VIDEO_CALL"
const val EVENT_NEW_MESSAGE_RECEIVED = "app.slyworks.medix.EVENT_NEW_MESSAGE_RECEIVED"
const val EVENT_LISTEN_FOR_CONSULTATION_REQUESTS = "app.slyworks.medix.EVENT_LISTEN_FOR_CONSULTATION_REQUESTS"
const val EVENT_LISTEN_FOR_CONSULTATION_REQUESTS_ACCEPT = "app.slyworks.medix.EVENT_LISTEN_FOR_CONSULTATION_REQUESTS_ACCEPT"
//endregion

const val DEFAULT_LAST_SIGN_IN_TIME = 100_000_000_000L
const val MAX_LAST_SIGN_IN_TIME = 300_000_000_000L
const val MAX_LAST_SIGN_IN_TIME2 = (3 * 24 * 60 * 60 * 1000).toLong()

const val KEY_LAST_SIGN_IN_TIME = "app.slyworks.medix.KEY_LAST_SIGN_IN_TIME"
const val KEY_UNREAD_MESSAGE_COUNT = "app.slyworks.medix.KEY_UNREAD_MESSAGE_COUNT"
const val KEY_FCM_UPLOAD_TOKEN = "app.slyworks.medix.KEY_FCM_UPLOAD_TOKEN"
const val KEY_FCM_REGISTRATION = "app.slyworks.medix.KEY_FCM_REGISTRATION"
const val KEY_IS_THERE_NEW_FCM_REG_TOKEN = "app.slyworks.medix.KEY_IS_THERE_NEW_FCM_REG_TOKEN"
const val KEY_LOGGED_IN_STATUS = "app.slyworks.medix.KEY_LOGGED_IN_STATUS"
const val KEY_UPLOAD_USER_PROFILE = "app.slyworks.medix.KEY_UPLOAD_USER_PROFILE"
const val KEY_FRAGMENT = "app.slyworks.medix.KEY_FRAGMENT"
const val KEY_EMAIL = "app.slyworks.medix.KEY_EMAIL"
const val KEY_PASS = "app.slyworks.medix.KEY_PASS"


const val OUTGOING_MESSAGE = "MESSAGE_SEND"
const val INCOMING_MESSAGE = "MESSAGE_RECEIVE"
const val HEADER = "MESSAGE_HEADER"

//region Preferences
const val PERMISSION_REQUEST_CODE = 1_000_000 //random number
const val APP_PERMISSION_STATUS = "app.slyworks.medix.APP_PERMISSION_STATUS"
const val LAST_SESSION_SAVE_DATE = "app.slyworks.medix.LAST_SESSION_SAVE_DATE"
const val PROFILE_PHOTO_URI = "app.slyworks.medix.PROFILE_PHOTO_URI"
//endregion


const val IMAGE_FILE_REQUEST_CODE = 1_000
const val CAMERA_FILE_REQUEST_CODE = 1_001

//const val NOT_SET = 0.0
const val NOT_SENT = 1.0
const val SENT = 3.0
const val DELIVERED = 4.0
const val READ = 5.0


const val FCM_NEW_MESSAGE = "NEW_MESSAGE"
const val FCM_REQUEST = "REQUEST"
const val FCM_RESPONSE_ACCEPTED = "RESPONSE_ACCEPTED"
const val FCM_RESPONSE_DECLINED = "RESPONSE_DECLINED"
const val FCM_VOICE_CALL_REQUEST = "FCM_VOICE_CALL_REQUEST"
const val FCM_NEW_UPDATE_MESSAGE = "FCM_NEW_UPDATE_MESSAGE"

const val REQUEST_PENDING = "REQUEST_PENDING"
const val REQUEST_ACCEPTED = "REQUEST_ACCEPTED"
const val REQUEST_DECLINED = "REQUEST_DECLINED"
const val REQUEST_NOT_SENT = "REQUEST_NOT_SENT"
const val REQUEST_ERROR = "REQUEST_ERROR"
const val REQUEST_FAILED = "REQUEST_FAILED"

const val VIDEO_CALL_OUTGOING = "app.slyworks.medix.VIDEO_CALL_OUTGOING"
const val VIDEO_CALL_INCOMING = "app.slyworks.medix.VIDEO_CALL_INCOMING"

const val VOICE_CALL_OUTGOING = "app.slyworks.medix.VOICE_CALL_OUTGOING"
const val VOICE_CALL_INCOMING = "app.slyworks.medix.VOICE_CALL_INCOMING"

const val EXTRA_ACCEPT_REQUEST = "app.slyworks.medix.EXTRA_ACCEPT_REQUEST"
const val EXTRA_CLOUD_MESSAGE_TYPE_ACCEPT = "app.slyworks.medix.EXTRA_CLOUD_MESSAGE_TYPE_ACCEPT"
const val EXTRA_CLOUD_MESSAGE_TYPE_DECLINE = "app.slyworks.medix.EXTRA_CLOUD_MESSAGE_TYPE_DECLINE"
const val EXTRA_CLOUD_MESSAGE_FROM_UID = "app.slyworks.medix.EXTRA_CLOUD_MESSAGE_FROM_UID"
const val EXTRA_CLOUD_MESSAGE_TO_UID = "app.slyworks.medix.EXTRA_CLOUD_MESSAGE_TO_UID"
const val EXTRA_CLOUD_MESSAGE_STATUS = "app.slyworks.medix.EXTRA_CLOUD_MESSAGE_STATUS"
const val EXTRA_CLOUD_MESSAGE_FULLNAME = "app.slyworks.medix.EXTRA_CLOUD_MESSAGE_FULLNAME"
const val EXTRA_CLOUD_MESSAGE_TO_FCMTOKEN = "app.slyworks.medix.EXTRA_CLOUD_MESSAGE_TO_FCMTOKEN"
const val EXTRA_NOTIFICATION_IDENTIFIER = "app.slyworks.medix.EXTRA_NOTIFICATION_IDENTIFIER"

const val EXTRA_USER_PROFILE_ARGS = "app.slyworks.medix.EXTRA_USER_PROFILE_ARGS"
const val EXTRA_USER_PROFILE_MPWM = "app.slyworks.medix.EXTRA_USER_PROFILE_MPWM"
const val EXTRA_USER_PROFILE_FBU = "app.slyworks.medix.EXTRA_USER_PROFILE_FBU"
const val EXTRA_MAIN_FRAGMENT = "app.slyworks.medix.EXTRA_MAIN_FRAGMENT"
const val EXTRA_RECEIVED_MESSAGE = "app.slyworks.medix.EXTRA_RECEIVED_MESSAGE"
const val EXTRA_VIDEO_CALL_TYPE = "app.slyworks.medix.EXTRA_VIDEO_CALL_TYPE"
const val EXTRA_VIDEO_CALL_USER_DETAILS = "app.slyworks.medix.EXTRA_VIDEO_CALL_USER_DETAILS"
const val EXTRA_INCOMING_VIDEO_CALL_FROM_UID = "app.slyworks.medix.EXTRA_INCOMING_VIDEO_CALL_FROM_UID"
const val EXTRA_INCOMING_VIDEO_CALL_RESPONSE_TYPE = "app.slyworks.medix.EXTRA_INCOMING_VIDEO_CALL_RESPONSE_TYPE"

const val EXTRA_VOICE_CALL_TYPE = "app.slyworks.medix.EXTRA_VOICE_CALL_TYPE"
const val EXTRA_VOICE_CALL_USER_DETAILS = "app.slyworks.medix.EXTRA_VOICE_CALL_USER_DETAILS"
const val EXTRA_INCOMING_VOICE_CALL_FROM_UID = "app.slyworks.medix.EXTRA_INCOMING_VOICE_CALL_FROM_UID"
const val EXTRA_INCOMING_VOICE_CALL_RESPONSE_TYPE = "app.slyworks.medix.EXTRA_INCOMING_VOICE_CALL_RESPONSE_TYPE"
const val EXTRA_LOGIN_DESTINATION = "app.slyworks.medix.EXTRA_LOGIN_DESTINATION"
const val EXTRA_IS_ACTIVITY_RECREATED = "app.slyworks.medix.EXTRA_IS_ACTIVITY_RECREATED"

const val EXTRA_ACTIVITY = "app.slyworks.medix.EXTRA_ACTIVITY"

const val GOOGLE_API_SERVICES_ERROR_DIALOG_REQUEST_CODE = 9_000

const val AGORA_APP_ID = "6f659e8d9c6c49eeb9d6043e76c04ef9"
const val VIDEO_CHANNEL_1_TEMP_TOKEN = "0066f659e8d9c6c49eeb9d6043e76c04ef9IADct4QNkc9Lygr/rJ/AKi/swmlwgya9QdG16R9RAJ4yf9CGQ7wAAAAAEAD45Mp205jyYQEAAQDQmPJh"
const val VIDEO_CALL_CHANNEL = "GADS_2021_VC_CHANNEL"

const val APP_SERVICE_ID = 1_000_000

const val NOTIFICATION_CONSULTATION_REQUEST = 9_00
const val NOTIFICATION_CONSULTATION_REQUEST_RESPONSE = 3_00
const val NOTIFICATION_NEW_MESSAGE = 1_500
const val NOTIFICATION_VIDEO_CALL_REQUEST = 10_00
const val NOTIFICATION_VOICE_CALL_REQUEST = 11_00

const val PATIENT = "PATIENT"
const val DOCTOR = "DOCTOR"
const val ACCOUNT_TYPE_NOT_SET = "NOT_SET"

const val NOT_SET = -1
const val MISSED_CALL = 1
const val OUTGOING_CALL = 2
const val INCOMING_CALL = 3

const val VOICE_CALL = 10
const val VIDEO_CALL = 11

const val TYPE_REQUEST = "REQUEST"
const val TYPE_RESPONSE = "RESPONSE"

const val INPUT_ERROR = 1
const val INCOMING_CALL_NOTIFICATION = 2

/*intDef for NetworkStatusView */
const val GENERAL= 0
const val COORDINATOR = 1
const val MAIN = 2

const val DI_ACTIVITY_VIEWMODEL_KEY = "app.slyworks.medix.DI_ACTIVITY_VIEWMODEL_KEY"
const val DI_FRAGMENT_VIEWMODEL_KEY = "app.slyworks.medix.DI_FRAGMENT_VIEWMODEL_KEY"

const val FRAGMENT_CHAT_HOST = "fragment_chat_host"
const val FRAGMENT_PATIENT_HOME = "fragment_patient_home"
const val FRAGMENT_DOCTOR_HOME = "fragment_doctor_home"
const val FRAGMENT_PROFILE_HOST = "fragment_profile_host"
const val FRAGMENT_REG_ZERO = "fragment_registration_0"
const val FRAGMENT_REG_ONE = "fragment_registration_1"
const val FRAGMENT_REG_TWO = "fragment_registration_2"
const val FRAGMENT_REG_PATIENT = "fragment_registration_patient"
const val FRAGMENT_REG_DOCTOR = "fragment_registration_doctor"
const val FRAGMENT_REG_OTP = "fragment_registration_otp"

const val SPLASH_ACTIVITY_INTENT_FILTER = "app.slyworks.auth_feature.splash.SplashActivity.open"
const val ONBOARDING_ACTIVITY_INTENT_FILTER = "app.slyworks.auth_feature.OnBoardingActivity.open"
const val LOGIN_ACTIVITY_INTENT_FILTER = "app.slyworks.auth_feature.LoginActivity.open"
const val VERIFICATION_ACTIVITY_INTENT_FILTER = "app.slyworks.auth_feature.VerificationActivity.open"
const val REGISTRATION_ACTIVITY_INTENT_FILTER = "app.slyworks.auth_feature.RegistrationActivity.open"
const val MAIN_ACTIVITY_INTENT_FILTER = "app.slyworks.core_feature.MainActivity.open"
const val VIEW_REQUESTS_ACTIVITY_INTENT_FILTER = "app.slyworks.core_feature.main.ViewRequestsActivity.open"
const val VOICECALL_ACTIVITY_INTENT_FILTER = "app.slywork.voicecall_feature.VoiceCallActivity.open"
const val VIDEOCALL_ACTIVITY_INTENT_FILTER = "app.slyworks.videocall_feature.VideoCallActivity.open"
const val MESSAGE_ACTIVITY_INTENT_FILTER = "app.slyworks.message_feature.MessageActivity.open"