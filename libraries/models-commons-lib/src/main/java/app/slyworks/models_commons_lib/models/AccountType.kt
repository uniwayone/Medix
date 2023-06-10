package app.slyworks.models_commons_lib.models

import app.slyworks.constants_lib.PATIENT

/**
 * Created by Joshua Sylvanus, 9:20 AM, 12/13/2021.
 */

enum class AccountType{
    PATIENT{
        override fun toString(): String {
            return app.slyworks.constants_lib.PATIENT
        }
    },
    DOCTOR{
        override fun toString(): String {
            return app.slyworks.constants_lib.DOCTOR
        }
    },
    NOT_SET{
        override fun toString(): String {
            return app.slyworks.constants_lib.ACCOUNT_TYPE_NOT_SET
        }
    };

}