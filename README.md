# Energy consumption

This is a public, un maintained version of a custom tool I made to myself. Use at your own risk.

1. use `$ sbt run` to run (you may need to [install `sbt`](https://www.scala-sbt.org/1.x/docs/Setup.html))

## Use your data

1. download hourly metrics from https://www.edistribucion.com/ or your provider website (somenergia, energia XXI, ...)
2. store the CSV file into `src/main/resources/your-key/all.csv`
3. edit `Main.scala` to add a new `Project` for `your-key`
  1. if your CSV format is not yet supported (see `MetricsReaders` and `LineParsers`) you will need to implement it yourself.