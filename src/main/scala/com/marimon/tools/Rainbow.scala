package com.marimon.tools


class RainbowMaker(values: Seq[Int]) {
  private val min: Int = values.min
  private val percentile60 = values.sorted.drop(values.length * 6 / 10).head
  private val max: Int = values.max

  // The mid-point is on percentile 60 (not 60% of max!)

  import Rainbow._

  def at(point: Int): Color = {
    if (point == min) Lowest
    else if (point == percentile60) Middle
    else if (point == max) Highest
    else {
      if (point < percentile60)
        compute(point, min, percentile60, Lowest, Middle)
      else
        compute(point, percentile60, max, Middle, Highest)
    }
  }

  private def compute(point: Int, min: Int, max: Int, startColor: Color, finishColor: Color): Color = {
    // 16, 10, 30 --> 0.3 = (point-min)/(max-min) = 6/20
    // distance (in %) that we must travel between start and finish
    val distance = (point - min).toDouble / (max - min).toDouble

    def travel(dist: Double, start: Int, finish: Int): Int = {
      val diff = finish - start
      (start.toDouble + (dist * diff)).toInt
    }

    Color(
      travel(distance, startColor.r, finishColor.r),
      travel(distance, startColor.g, finishColor.g),
      travel(distance, startColor.b, finishColor.b),
    )
  }
}

case class Color(r: Int, g: Int, b: Int) {
  val hex = "#" + r.toHexString.toUpperCase() + g.toHexString.toUpperCase() + b.toHexString.toUpperCase()
}

object Rainbow {
  val Lowest = Color(87, 187, 138) // , "#57BB8A") // green
  val Middle = Color(255, 214, 101) // "#FFD665" // yellowish
  val Highest = Color(230, 123, 114) // "#E67B72" // red

  def build(values: Seq[Int]): RainbowMaker =
    new RainbowMaker(values)

}