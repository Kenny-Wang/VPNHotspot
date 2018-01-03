package be.mygod.vpnhotspot

import android.content.IntentFilter
import android.util.Log

fun createIntentFilter(vararg actions: String): IntentFilter {
    val result = IntentFilter()
    actions.forEach { result.addAction(it) }
    return result
}

const val NOISYSU_TAG = "NoisySU"
const val NOISYSU_SUFFIX = "SUCCESS\n"
fun noisySu(vararg commands: String): Boolean {
    val process = ProcessBuilder("su", "-c", """function noisy() { "$@" || echo "$@" exited with $?; }
${commands.joinToString("\n") { "noisy $it" }}
echo $NOISYSU_SUFFIX""")
            .redirectErrorStream(true)
            .start()
    process.waitFor()
    var out = process.inputStream.bufferedReader().use { it.readLine() }
    val result = out != NOISYSU_SUFFIX
    out = out.removeSuffix(NOISYSU_SUFFIX)
    if (!out.isNullOrBlank()) Log.i("noisySu", out)
    val err = process.errorStream.bufferedReader().use { it.readLine() }
    if (!err.isNullOrBlank()) Log.e("noisySu", err)
    return result
}