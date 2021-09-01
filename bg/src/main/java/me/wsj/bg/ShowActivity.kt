package me.wsj.bg

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import me.wsj.lib.EffectUtil
import me.wsj.lib.utils.IconUtils

class ShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)

        val code = intent.getIntExtra("code", 0)
//        val dayBack = IconUtils.getDayBg(this, code)
        findViewById<ImageView>(R.id.ivBg).setImageResource(IconUtils.getBg(this, code))

//        val newCode = convert(code)
        findViewById<ImageView>(R.id.ivEffect).setImageDrawable(EffectUtil.getEffect(this, code))

    }

    override fun onDestroy() {
        super.onDestroy()
        findViewById<ImageView>(R.id.ivEffect).drawable?.let {
            (it as Animatable).stop()
        }
    }
}