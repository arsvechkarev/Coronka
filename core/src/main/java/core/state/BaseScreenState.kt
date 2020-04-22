package core.state

/**
 * Base screen state for every state in view model
 */
abstract class BaseScreenState(internal var isStateRecreated: Boolean = false)

/**
 * Shows whether state was sent to observer due to fragment/activity recreation
 *
 * @see update
 * @see updateSelf
 */
val BaseScreenState.isRecreated get() = isStateRecreated

/**
 * Shows whether state is changed because of explicit request
 *
 * @see update
 * @see updateSelf
 */
val BaseScreenState.isFresh get() = !isStateRecreated