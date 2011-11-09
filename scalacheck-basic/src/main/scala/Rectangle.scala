package com.company.scalacheck  
  
/**  
 * Simple case class that will be used as the basis for our examples  
 */  
case class Rectangle(val width:Double, val height:Double) {  
  // when the width is a multiple of 3, this will fail  
  lazy val area =  if(width % 11 ==0) (width * 1.0001 * height) else (width * height)  
  // valid version of the method above  
  lazy val areaCorrect = (width * height)  
  lazy val perimeter = (2*width) + (2*height)  
  def biggerThan(r:Rectangle) = (area > r.area)  
}