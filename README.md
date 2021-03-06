# Color

> A simple approach to colors with String interpolators

[![GitLab CI](https://gitlab.com/taig-github/color/badges/master/pipeline.svg?style=flat-square)](https://gitlab.com/taig-github/color/pipelines)
[![Maven Central](https://img.shields.io/maven-central/v/io.taig/color-core_2.13.svg?style=flat-square)](https://search.maven.org/search?q=g:io.taig%20AND%20a:color-*)
[![License](https://img.shields.io/github/license/taig/color?style=flat-square)](LICENSE)

## Dependency

_Color_ is available for Scala(.js) 3, 2.13 and 2.12 and has no external dependencies.

```scala
libraryDependencies ++=
  "io.taig" %%% "color-core" % "x.y.z" ::
  Nil
```

## Usage

```scala
import io.taig.color.Color
import io.taig.color.implicits._

val red = hex"FF000"
// error: Color value can only have 6 (rgba), 8 (rgba), 3 (rgb shorthand) or 4 (rgba shorthand) digits

val red: Color = hex"FF0000"
// red: io.taig.color.Color = Color(255,0,0,None)

red.toHex
// res0: String = #ff0000

red.toRgb
// res1: String = rgb(255, 0, 0)

red.toRgbScaled
// res2: String = rgb(1.0, 0.0, 0.0)

red.toRgb_%
// res3: String = rgb(100.0%, 0.0%, 0.0%)

val reddish = red.alpha(Channel.fromScale(.5f))
// reddish: io.taig.color.Color = Color(255,0,0,Some(127))

reddish.toHex
// res4: String = #ff00007f

reddish.toRgb
// res5: String = rgba(255, 0, 0, 127)
```