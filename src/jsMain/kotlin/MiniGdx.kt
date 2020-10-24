import com.github.dwursteisen.minigdx.MiniGdx

@JsExport
@ExperimentalJsExport
fun debugHitbox(debug: Boolean) {
    MiniGdx.debugHitbox = debug
}

@ExperimentalJsExport
@JsExport
fun debugStates(debug: Boolean) {
    MiniGdx.debugStates = debug
}
