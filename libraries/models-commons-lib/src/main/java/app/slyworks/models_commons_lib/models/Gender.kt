package app.slyworks.models_commons_lib.models

enum class Gender {
    MALE{
        override fun toString(): String {
            return "male"
        }
    },
    FEMALE{
        override fun toString(): String {
            return "female"
        }
    },
    NOT_SET{
        override fun toString(): String {
            return "not_set"
        }
    }
}