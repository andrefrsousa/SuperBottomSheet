# Super Bottom Sheet  
Android native a bottom sheet for on steroids 💪  

![](/raw/banner.png)
  
## Specs  
[![](https://jitpack.io/v/andrefrsousa/SuperBottomSheet.svg)](https://jitpack.io/#andrefrsousa/SuperBottomSheet) [![API](https://img.shields.io/badge/API-14%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=14) [![Build Status](https://travis-ci.org/andrefrsousa/SuperBottomSheet.svg?branch=master)](https://travis-ci.org/andrefrsousa/SuperBottomSheet)
  
  
This library allows you to show bottom sheets in your app with the bonus of **animating the status bar** color and the **top rounded corners** as you scroll.   

It has been written **100% in Kotlin**. ❤️  

## Spread Some :heart:  
[![GitHub followers](https://img.shields.io/github/followers/andrefrsousa.svg?style=social&label=Follow)](https://github.com/andrefrsousa)  [![Twitter Follow](https://img.shields.io/twitter/follow/andrefrsousa.svg?style=social)](https://twitter.com/andrefrsousa)
  
## Download  
  
This library is available in **jitpack**, so you need to add this repository to your root build.gradle at the end of repositories:
   
```groovy  
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
	
Add the dependency:

```groovy 
dependencies {
    implementation 'com.github.andrefrsousa:SuperBottomSheet:{latest_version}'
}
```  
  
## Sample Project  

We have a sample project in Kotlin that demonstrates the lib usages [here](https://github.com/andrefrsousa/SuperBottomSheet/blob/master/demo/src/main/java/com/andrefrsousa/superbottomsheet/demo/MainActivity.kt).

![](/raw/example.gif)
  
## Usage  

It is recommended to check the sample project to get a complete understanding of all the features offered by the library.  
In order to create a bottom sheet in your project you just need to extend SuperBottomSheetFragment.

Example:

```kotlin
class MySheetFragment : SuperBottomSheetFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_demo_sheet, container, false)
    }
}
```
  
## Customization
  
The are a group of general properties that you can define. These properties will be applied to all the SuperBottomSheet in your project.  

```xml
// The amount of DIM that will be applied to the background from 0 to 1.
<attr name="superBottomSheet_dim" format="float"/>  

// Sheet background color
<attr name="superBottomSheet_backgroundColor" format="color"/>

// To corner radius to be applied. 0 if you want none.
<attr name="superBottomSheet_cornerRadius" format="dimension"/>  

// Enable or disable the radius animation. Default is true.
<attr name="superBottomSheet_animateCornerRadius" format="float"/>

// Default if false. Enalbe it if you want to skip collapse state
<attr name="superBottomSheet_alwaysExpanded" format="boolean"/>

// Default if true
<attr name="superBottomSheet_cancelableOnTouchOutside" format="boolean"/>

// Default if true
<attr name="superBottomSheet_cancelable" format="boolean"/>

// Status bar color. The default uses the colorDark attribute value.  
<attr name="superBottomSheet_statusBarColor" format="color"/>  

// The height of the bottom sheet when it is collapsed 
<attr name="superBottomSheet_peekHeight" format="dimension"/>
```

If you want to specify properties for a single bottom sheet you can override some methods:

```kotlin
fun getPeekHeight(): Int {
    // Your code goes here
}

fun getDim(): Float {
    // Your code goes here
}

fun getBackgroundColor(): Int {
   // Your code goes here
}

fun getStatusBarColor(): Int {
    // Your code goes here
}

fun getCornerRadius(): Float {
    // Your code goes here
}

fun isSheetAlwaysExpanded(): Boolean {
    // Your code goes here
}

fun isSheetCancelableOnTouchOutside(): Boolean {
    // Your code goes here
}

fun isSheetCancelable(): Boolean {
    // Your code goes here
}

fun animateCornerRadius(): Boolean {
    // Your code goes here
}

```
  
## License  
  
```  
The MIT License (MIT)  
  
Copyright (c) 2018 André Sousa  
  
Permission is hereby granted, free of charge, to any person obtaining a copy  
of this software and associated documentation files (the "Software"), to deal  
in the Software without restriction, including without limitation the rights  
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
copies of the Software, and to permit persons to whom the Software is  
furnished to do so, subject to the following conditions:  
  
The above copyright notice and this permission notice shall be included in all  
copies or substantial portions of the Software.  
  
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  
SOFTWARE.
