package datetime

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class DateTimeTest {
  
  @Test
  fun `difference between times calculated correctly`() {
    val pattern = "yyyy-MM-dd-HH-mm"
    val date1 = DateTime.ofPattern(pattern, "2020-03-22-10-52")
    val date2 = DateTime.ofPattern(pattern, "2020-03-22-10-59")
    
    val difference = date1.differenceWith(date2, TimeUnit.MINUTES)
    val reverseDifference = date2.differenceWith(date1, TimeUnit.MINUTES)
    
    assertEquals(7, difference)
    assertEquals(7, reverseDifference)
  }
}