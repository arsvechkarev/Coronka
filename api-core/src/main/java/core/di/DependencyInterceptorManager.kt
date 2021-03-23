package core.di

object DependencyInterceptorManager {
  
  @PublishedApi
  internal val dependencyInterceptors = ArrayList<DependencyInterceptor>()
  
  fun addDependencyInterceptor(dependencyInterceptor: DependencyInterceptor) {
    dependencyInterceptors.add(dependencyInterceptor)
  }
  
  inline fun <reified T : Any> tryInterceptDependency(clazz: Class<T>, defaultValue: () -> T): T {
    var resultDependency: T? = null
    dependencyInterceptors.forEach { interceptor ->
      val dependency = interceptor.getDependency(clazz)
      if (dependency != null) {
        if (resultDependency == null) {
          resultDependency = dependency as? T ?: error(
            "Cannot cast create dependency of class $clazz with interceptor $interceptor")
        } else {
          error(
            "Two interceptors create the same $resultDependency, cannot figure out which to use")
        }
      }
    }
    return resultDependency ?: defaultValue.invoke()
  }
}
