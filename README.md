<p align="center"><a href="https://github.com/andrefrsousa/SuperBottomSheet" target="_blank"><img width="250"src="raw/logo.png"></a></p>
<h1 align="center">Super Bottom Sheet</h1>
<p align="center">Android native bottom sheet on steroids 💪</p>
<p align="center">
  <a href="https://github.com/andrefrsousa/SuperBottomSheet/actions?query=workflow%3A%22Android+CI%22"><img src="https://img.shields.io/github/workflow/status/andrefrsousa/SuperBottomSheet/Android%20CI" alt="Build Status"></a>
  <a href="https://jitpack.io/#andrefrsousa/SuperBottomSheet"><img src="https://jitpack.io/v/andrefrsousa/SuperBottomSheet.svg" alt="jitpack"></a>
  <a href="https://android-arsenal.com/api?level=14"><img src="https://img.shields.io/badge/API-14%2B-orange.svg?style=flat" alt="api"></a>
</p>
  
### Summary  

This library allows you to display the bottom sheets in your application with the bonus of **animating the color of the status bar** and the **upper rounded corners** while scrolling. 

  
## Download  
  
This library is available in **jitpack**, so to use it you need to add the above statement to your root *build.gradle*:
   
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
    implementation 'com.github.andrefrsousa:SuperBottomSheet:2.0.0'
}
```  
  
## Sample Project  

We have a sample project in Kotlin that demonstrates the use of the library [here](https://github.com/andrefrsousa/SuperBottomSheet/blob/master/demo/src/main/java/com/andrefrsousa/superbottomsheet/demo/MainActivity.kt).

![](/raw/example.gif)
  
## Use  

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
  
## Adjustments
  
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
<attr name="superBottomSheet_expandedHeight" format="enum">
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

fun getExpandedHeight(): Int {
    // Your code goes here
}


```

## License  
  
```  
Copyright (c) 2018 André Sousa  

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
