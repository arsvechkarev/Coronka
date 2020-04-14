package core.state

abstract class BaseScreenState(internal var isStateRecreated: Boolean = false)

val BaseScreenState.isRecreated get() = isStateRecreated

val BaseScreenState.isFresh get() = !isStateRecreated