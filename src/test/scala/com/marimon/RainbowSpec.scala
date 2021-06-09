package com.marimon

import com.marimon.tools.Rainbow
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RainbowSpec extends AnyFlatSpec with Matchers {

  behavior of "The Rainbow"

  val samples = Seq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
  it should "use green for lowest value" in {
    Rainbow.build(samples).at(0) should be(Rainbow.Lowest)
  }

  it should "use red for highest value" in {
    Rainbow.build(samples).at(9) should be(Rainbow.Highest)
  }

  it should "use yellow for values at 60 percentile" in {
    Rainbow.build(samples).at(6).hex should be("#FFD665")
  }

  it should "use orange-ish for values over 60 percentile" in {
    Rainbow.build(samples).at(7).hex should be("#F6B769")
  }

  it should "use brownish-ish for values under 60 percentile" in {
    Rainbow.build(samples).at(3).hex should be("#ABC877")
  }

}
