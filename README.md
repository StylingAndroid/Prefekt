#Prefekt
[ ![Latest Version](https://api.bintray.com/packages/stylingandroid/maven/Prefekt/images/download.svg) ](https://bintray.com/stylingandroid/maven/Prefekt/_latestVersion)

Prefekt is an Android SharedPreferences library for Kotlin. It is typesafe, easy to consume, and efficient thanks to in-memory caching. 
You can subscribe for updates so that if the underlying SharedPreference value is changed you receive a callback even if the change was made
directly to the SharedPreference value is changed outside of Prefekt.

###Usage

To use Prefekt in your project add the following dependency:

    dependencies {
        implementation 'com.stylingandroid.prefekt:prefekt:1.0.0'
    }

You can then create a Prefekt instance in any `android.support.v4.app.FragmentActivity` or `android.support.v4.app.Fragment` in the following manner:

    private var stringValue = prefekt(KEY_STRING, DEFAULT_STRING)
    
where KEY_STRING is the key for the SharedPreference;
and DEFAULT_VALUE is the default value if the SharedPreference value does not yet exist.

DEFAULT_VALUE can be an Int, Long, Float, Boolean, or String value and currently only those types are supported.

You can then change the value by calling:
    
    stringValue.setValue(NEW_VALUE)
    
This will both update the in-memory cached value and, if the value has changed it will also save to SharedPreferences.

Getting the value needs to be done asynchronously (for reasons which will be explained later on). By far the easiest way
to get the value is to add a lambda to the Prefekt declaration and this will get called once the underlying value has been retrieved
and any time that the value changes:

    private var stringValue = prefekt(KEY_STRING, DEFAULT_STRING) {
        println("New value: $it")
    }

An alternate way of retrieving the value is to use an asynchronous callback:

    stringValue.getValue() {
        println("Value: $it")
    }
    
This is similar to the callback except it will only get called once - and will not be called each time the value changes.

Alternatively there is a suspend function which can be called from within a coroutine:

    launch(CommonPool) {
        println("Value: ${stringValue.getValue()}")
    }

Take care that you do not call this from the UI thread or it may block.

Finally you can manually subscribe for callback whenever the value changes:

    object StringSubscriber : Subscriber<String> {
        override fun onChanged(newValue: String) {
            println("New Value: $newValue")
        }
    }
    stringValue.subscribe(StringSubscriber)
    
You can unsubscribe, as well:

    stringValue.unsubscribe(StringSubscriber)


###Internals

Internally Prefekt uses the Android Architecture components to tie in to your Activity & Fragment lifecycles, and the in-memory
cached value is actually a LiveData object. This is necessary because SharedPreference operations require a valid Context.

It is for this reason that synchronous calls to retrieve the value are not supported because
there is no guarantee that the value will be available if it is called before the Context is valid, and the value has been loaded in the background.

Rather than risk exceptions being thrown if the value is accessed before it is available, there was a conscious decision to only support asynchronous 
retrieval using the declaration or manual subscription, the lambda callback, or the suspend function.

The advantage of this approach is the cleanliness of the API which only requires you to pass in the key name and default value 
for the underlying SharedPreference value.

###Future

There are some interesting features in the pipeline so watch this space... 
