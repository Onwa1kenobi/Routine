package com.ugochukwu.routine.viewmodel


sealed class RoutineViewContract {
    // Contract class to display message to the user
    class MessageDisplay(val message: String) : RoutineViewContract()

    // Contract object to notify a routine save success
    object SaveSuccess : RoutineViewContract()
}