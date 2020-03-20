package core.async

interface Worker {
  
  fun execute(block: () -> Unit)
}