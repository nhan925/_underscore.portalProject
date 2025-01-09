import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class AppLifecycleListener : LifecycleObserver {

    companion object {
        var isAppInForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        isAppInForeground = true
        Log.d("AppLifecycleListener", "App entered foreground")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        isAppInForeground = false
        Log.d("AppLifecycleListener", "App entered background")
        Log.d("AppLifecycleListener", "App is: $isAppInForeground")
    }
}
