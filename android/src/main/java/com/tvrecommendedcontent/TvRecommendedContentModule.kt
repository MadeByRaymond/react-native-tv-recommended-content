package com.tvrecommendedcontent

import com.facebook.react.bridge.ReactApplicationContext

class TvRecommendedContentModule(reactContext: ReactApplicationContext) :
  NativeTvRecommendedContentSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeTvRecommendedContentSpec.NAME
  }
}
