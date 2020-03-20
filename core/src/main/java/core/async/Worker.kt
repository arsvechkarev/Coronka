package core.async

interface Worker {
  
  fun submit(block: () -> Unit)
}