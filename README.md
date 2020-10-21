<p align="center"><a href="https://github.com/andrefrsousa/SuperBottomSheet" target="_blank"><img width="250"src="raw/logo.png"></a></p>
<h1 align="center">Super Bottom Sheet</h1>
<p align="center">Android native bottom sheet on steroids 💪</p>
<p align="center">
  <a href="https://travis-ci.org/andrefrsousa/SuperBottomSheet"><img src="https://travis-ci.org/andrefrsousa/SuperBottomSheet.svg?branch=master" alt="Build Status"></a>
  <a href="https://jitpack.io/#andrefrsousa/SuperBottomSheet"><img src="https://jitpack.io/v/andrefrsousa/SuperBottomSheet.svg" alt="jitpack"></a>
  <a href="https://android-arsenal.com/api?level=14"><img src="https://img.shields.io/badge/API-14%2B-orange.svg?style=flat" alt="api"></a>
  <a href="https://android-arsenal.com/details/1/7259"><img src="https://img.shields.io/badge/Android%20Arsenal-SuperBottomSheet-green.svg?style=flat" alt="Android Arsenal"></a>
</p>
  
### Summary  

This library allows you to display the bottom sheets in your application with the bonus of **animating the color of the status bar** and the **upper rounded corners** while scrolling. 

It was written **100% in Kotlin** ❤️.
  
## Download  
  
This library is available in **jitpack**, so to use it you will need to add the above instruction to your root *build.gradle*:
   
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
    implementation 'com.github.andrefrsousa:SuperBottomSheet:1.5.0'
}
```  
  
## Sample Project  

We have a sample project in Kotlin that demonstrates the use of the Lib [here](https://github.com/andrefrsousa/SuperBottomSheet/blob/master/demo/src/main/java/com/andrefrsousa/superbottomsheet/demo/MainActivity.kt).

![](/raw/example.gif)
  
## Usage  

It is recommended that you review the sample project to get a full understanding of all the features offered by the library. 
To create a bottom sheet in your project, you only need to extend *SuperBottomSheetFragment*.

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
  
This is a group of general properties that you can define. These properties are applied to the entire *SuperBottomSheet* in your project.

```xml
// The set of DIM applied to the background from 0 to 1.
<attr name="superBottomSheet_dim" format="float"/>  

// Background color of the sheet.
<attr name="superBottomSheet_backgroundColor" format="color"/>

// To corner radius to be applied. 0 if you want none.
<attr name="superBottomSheet_cornerRadius" format="dimension"/>  

// Enable or disable the status bar animation. Default value is true.
<attr name="superBottomSheet_animateStatusBar" format="boolean"/>

// Enable or disable the radius animation. Default is true.
<attr name="superBottomSheet_animateCornerRadius" format="boolean"/>

// Enable this option if you want to skip the collapse state. Default value is false.
<attr name="superBottomSheet_alwaysExpanded" format="boolean"/>

// Default is true.
<attr name="superBottomSheet_cancelableOnTouchOutside" format="boolean"/>

// Default is true.
<attr name="superBottomSheet_cancelable" format="boolean"/>

//  Color of the status bar. The default uses the attribute value colorDark.
<attr name="superBottomSheet_statusBarColor" format="color"/>  

// The height of the bottom sheet when it is collapsed.
<attr name="superBottomSheet_peekHeight" format="dimension"/>

// The height of the bottom sheet when it is expanded. Default value is match_parent (-1).
<attr name="superBottomSheet_expandedBehaviour" format="enum">
	<enum name="match_parent" value="-1" />
        <enum name="wrap_content" value="-2" />
</attr>
```

If you want to change the properties of a single bottom sheet you can override these methods:

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

fun animateStatusBar(): Boolean {
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
