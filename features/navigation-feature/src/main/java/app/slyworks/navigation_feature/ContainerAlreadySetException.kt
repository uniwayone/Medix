package app.slyworks.navigation_feature

/**
 * Created by Joshua Sylvanus, 10:57 AM, 26/08/2022.
 */
internal class ContainerAlreadySetException(): Exception(){
    override val message: String
        get() = "a FragmentContainer has already been set for this transaction"
}