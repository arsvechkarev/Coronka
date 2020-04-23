package com.arsvechkarev.faq.repository

import android.content.res.Resources
import com.arsvechkarev.faq.R.string.a1
import com.arsvechkarev.faq.R.string.a10
import com.arsvechkarev.faq.R.string.a11
import com.arsvechkarev.faq.R.string.a12
import com.arsvechkarev.faq.R.string.a13
import com.arsvechkarev.faq.R.string.a15
import com.arsvechkarev.faq.R.string.a16
import com.arsvechkarev.faq.R.string.a18
import com.arsvechkarev.faq.R.string.a19
import com.arsvechkarev.faq.R.string.a2
import com.arsvechkarev.faq.R.string.a20
import com.arsvechkarev.faq.R.string.a21
import com.arsvechkarev.faq.R.string.a23
import com.arsvechkarev.faq.R.string.a24
import com.arsvechkarev.faq.R.string.a25
import com.arsvechkarev.faq.R.string.a26
import com.arsvechkarev.faq.R.string.a3
import com.arsvechkarev.faq.R.string.a4
import com.arsvechkarev.faq.R.string.a5
import com.arsvechkarev.faq.R.string.a6
import com.arsvechkarev.faq.R.string.a7
import com.arsvechkarev.faq.R.string.a8
import com.arsvechkarev.faq.R.string.a9
import com.arsvechkarev.faq.R.string.q1
import com.arsvechkarev.faq.R.string.q10
import com.arsvechkarev.faq.R.string.q11
import com.arsvechkarev.faq.R.string.q12
import com.arsvechkarev.faq.R.string.q13
import com.arsvechkarev.faq.R.string.q15
import com.arsvechkarev.faq.R.string.q16
import com.arsvechkarev.faq.R.string.q18
import com.arsvechkarev.faq.R.string.q19
import com.arsvechkarev.faq.R.string.q2
import com.arsvechkarev.faq.R.string.q20
import com.arsvechkarev.faq.R.string.q21
import com.arsvechkarev.faq.R.string.q23
import com.arsvechkarev.faq.R.string.q24
import com.arsvechkarev.faq.R.string.q25
import com.arsvechkarev.faq.R.string.q26
import com.arsvechkarev.faq.R.string.q3
import com.arsvechkarev.faq.R.string.q4
import com.arsvechkarev.faq.R.string.q5
import com.arsvechkarev.faq.R.string.q6
import com.arsvechkarev.faq.R.string.q7
import com.arsvechkarev.faq.R.string.q8
import com.arsvechkarev.faq.R.string.q9
import core.model.FAQItem

class FAQRepository(private val resources: Resources) {
  
  fun loadFAQList(): List<FAQItem> {
    val list = ArrayList<FAQItem>()
    list.add(FAQItem(resources.getString(q1), resources.getString(a1)))
    list.add(FAQItem(resources.getString(q2), resources.getString(a2)))
    list.add(FAQItem(resources.getString(q3), resources.getString(a3)))
    list.add(FAQItem(resources.getString(q4), resources.getString(a4)))
    list.add(FAQItem(resources.getString(q5), resources.getString(a5)))
    list.add(FAQItem(resources.getString(q6), resources.getString(a6)))
    list.add(FAQItem(resources.getString(q7), resources.getString(a7)))
    list.add(FAQItem(resources.getString(q8), resources.getString(a8)))
    list.add(FAQItem(resources.getString(q9), resources.getString(a9)))
    list.add(FAQItem(resources.getString(q10), resources.getString(a10)))
    list.add(FAQItem(resources.getString(q11), resources.getString(a11)))
    list.add(FAQItem(resources.getString(q12), resources.getString(a12)))
    list.add(FAQItem(resources.getString(q13), resources.getString(a13)))
    list.add(FAQItem(resources.getString(q15), resources.getString(a15)))
    list.add(FAQItem(resources.getString(q16), resources.getString(a16)))
    list.add(FAQItem(resources.getString(q18), resources.getString(a18)))
    list.add(FAQItem(resources.getString(q19), resources.getString(a19)))
    list.add(FAQItem(resources.getString(q20), resources.getString(a20)))
    list.add(FAQItem(resources.getString(q21), resources.getString(a21)))
    list.add(FAQItem(resources.getString(q23), resources.getString(a23)))
    list.add(FAQItem(resources.getString(q24), resources.getString(a24)))
    list.add(FAQItem(resources.getString(q25), resources.getString(a25)))
    list.add(FAQItem(resources.getString(q26), resources.getString(a26)))
    return list
  }
}