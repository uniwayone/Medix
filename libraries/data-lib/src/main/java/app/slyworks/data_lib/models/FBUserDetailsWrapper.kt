package app.slyworks.data_lib.models


/**
 * Created by Joshua Sylvanus, 1:22 PM, 28/11/2022.
 */
data class FBUserDetailsWrapper(
    var details: FBUserDetailsVModel = FBUserDetailsVModel()
){
    constructor():this(FBUserDetailsVModel())
}
