package app.slyworks.data_lib

import app.slyworks.data_lib.models.*
import app.slyworks.models_commons_lib.models.MessageCloudMessage
import app.slyworks.room_lib.room_models.*


/**
 * Created by Joshua Sylvanus, 8:21 PM, 26/11/2022.
 */

fun MessageVModel.toCloudMessage():MessageCloudMessage =
    MessageCloudMessage(
        type = this.type,
        fromUID = this.fromUID,
        toUID = this.toUID,
        senderFullName = this.senderFullName,
        receiverFullName = this.receiverFullName,
        content = this.content,
        timeStamp = this.timeStamp,
        messageID = this.messageID,
        status = this.status,
        senderImageUri = this.senderImageUri,
        accountType = this.accountType,
        FCMRegistrationToken = this.FCMRegistrationToken,
        receiverImageUri = this.receiverImageUri)

fun Array<out PersonVModel>.transform():Array<out Person> =
    this.map(::reverseMapPerson).toTypedArray()

fun Array<out FBUserDetailsVModel>.transform():Array<out FBUserDetails> =
    this.map(::reverseMapFBUserDetails).toTypedArray()

fun Array<out ConsultationRequestVModel>.transform():Array<out ConsultationRequest> =
    this.map(::reverseMapConsultationRequest).toTypedArray()

fun Array<out CallHistoryVModel>.transform():Array<out CallHistory> =
    this.map(::reverseMapCallHistory).toTypedArray()

fun MutableList<Person>.transformPerson():MutableList<PersonVModel> =
    this.map(::mapPerson) as MutableList<PersonVModel>

fun MutableList<FBUserDetails>.transformFBUserDetails():MutableList<FBUserDetailsVModel> =
    this.map(::mapFBUserDetails) as MutableList<FBUserDetailsVModel>

fun MutableList<ConsultationRequest>.transformConsultationRequest():MutableList<ConsultationRequestVModel> =
    this.map(::mapConsultationRequest) as MutableList<ConsultationRequestVModel>

fun MutableList<CallHistory>.transformCallHistory():MutableList<CallHistoryVModel> =
    this.map(::mapCallHistory) as MutableList<CallHistoryVModel>

fun Person.transform(): PersonVModel = mapPerson(this)

fun FBUserDetails.transform(): FBUserDetailsVModel = mapFBUserDetails(this)

fun ConsultationRequest.transform(): ConsultationRequestVModel = mapConsultationRequest(this)

fun CallHistory.transform(): CallHistoryVModel = mapCallHistory(this)

fun mapPerson(persons:MutableList<Person>):MutableList<PersonVModel> =
   persons.map(::mapPerson) as MutableList<PersonVModel>

fun mapFBUserDetails(userDetails:MutableList<FBUserDetails>):MutableList<FBUserDetailsVModel> =
    userDetails.map(::mapFBUserDetails) as MutableList<FBUserDetailsVModel>

fun mapConsultationRequest(requests:MutableList<ConsultationRequest>):MutableList<ConsultationRequestVModel> =
    requests.map(::mapConsultationRequest) as MutableList<ConsultationRequestVModel>

fun mapCallHistory(histories:MutableList<CallHistory>):MutableList<CallHistoryVModel> =
    histories.map(::mapCallHistory) as MutableList<CallHistoryVModel>

fun mapPerson(person: Person): PersonVModel =
    PersonVModel(
        firebaseUID = person.firebaseUID,
        userAccountType = person.userAccountType,
        lastMessageType = person.lastMessageType,
        lastMessageContent = person.lastMessageContent,
        lastMessageStatus = person.lastMessageStatus,
        lastMessageTimeStamp = person.lastMessageTimeStamp,
        imageUri = person.imageUri,
        fullName = person.fullName,
        unreadMessageCount = person.unreadMessageCount,
        FCMRegistrationToken = person.FCMRegistrationToken )

fun mapFBUserDetails(details: FBUserDetails): FBUserDetailsVModel =
    FBUserDetailsVModel(
        accountType =details.accountType,
        firstName = details.firstName,
        lastName = details.lastName,
        fullName = details.fullName,
        email = details.email,
        sex = details.sex,
        age = details.age,
        firebaseUID = details.firebaseUID,
        agoraUID = details.agoraUID,
        FCMRegistrationToken = details.FCMRegistrationToken,
        imageUri = details.imageUri,
        history = details.history,
        specialization = details.specialization )

fun mapConsultationRequest(request: ConsultationRequest): ConsultationRequestVModel =
    ConsultationRequestVModel(
        toUID = request.toUID,
        timeStamp = request.timeStamp,
        details = request.details.transform(),
        status = request.status )

fun mapCallHistory(history: CallHistory): CallHistoryVModel =
    CallHistoryVModel(
        type = history.type,
        status = history.status,
        callerUID = history.callerUID,
        callerName = history.callerName,
        senderImageUri = history.senderImageUri,
        timeStamp = history.timeStamp,
        duration = history.duration)

fun reverseMapPerson(person: PersonVModel):Person =
    Person(
        firebaseUID = person.firebaseUID,
        userAccountType = person.userAccountType,
        lastMessageType = person.lastMessageType,
        lastMessageContent = person.lastMessageContent,
        lastMessageStatus = person.lastMessageStatus,
        lastMessageTimeStamp = person.lastMessageTimeStamp,
        imageUri = person.imageUri,
        fullName = person.fullName,
        unreadMessageCount = person.unreadMessageCount,
        FCMRegistrationToken = person.FCMRegistrationToken )

fun FBUserDetailsVModel.reverseMap():FBUserDetails = reverseMapFBUserDetails(this)

fun reverseMapFBUserDetails(details: FBUserDetailsVModel):FBUserDetails =
    FBUserDetails(
        accountType = details.accountType,
        firstName = details.firstName,
        lastName = details.lastName,
        fullName = details.fullName,
        email = details.email,
        sex = details.sex,
        age =details.age,
        firebaseUID = details.firebaseUID,
        agoraUID = details.agoraUID,
        FCMRegistrationToken = details.FCMRegistrationToken,
        imageUri = details.imageUri,
        history = details.history,
        specialization = details.specialization )

fun reverseMapConsultationRequest(request: ConsultationRequestVModel):ConsultationRequest =
    ConsultationRequest(
        toUID = request.toUID,
        timeStamp = request.timeStamp,
        details = reverseMapFBUserDetails(request.details),
        status = request.status )

fun reverseMapCallHistory(history: CallHistoryVModel):CallHistory =
    CallHistory(
        type = history.type,
        status = history.status,
        callerUID = history.callerUID,
        callerName = history.callerName,
        senderImageUri = history.senderImageUri,
        timeStamp = history.timeStamp,
        duration = history.duration)
