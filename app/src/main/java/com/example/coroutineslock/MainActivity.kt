package com.example.coroutineslock

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.random.Random

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var lockViews: List<View>
    private var lockJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setContentView(R.layout.activity_main)

        lockViews = listOf(button_1, button_2, button_3, button_4, button_5, button_6, button_7, button_8, button_9, button_0)
        status_text.text = "Jenny Jenny, who can I turn to?"

        resetLock()
    }

    private fun resetLock() {
        lockJob?.cancel()
        lockJob = launch {
            try {
                while (true) {
                    awaitClicksWithMessages(
                        button_8 to "You give me something I can hold on to",
                        button_6 to "I know you'll think I'm like the others before",
                        button_7 to "Who saw your name and number on the wall",
                        button_5 to "Jenny I've got your number",
                        button_3 to "I need to make you mine",
                        button_0 to "Jenny don't change your number",
                        button_9 to "Eight six seven five three oh nine!"
                    )
                    woggle()
                }
            } catch (e: CancellationException) {
                status_text.setTextColor(getColor(R.color.red))
                status_text.setText("Tommy Tutone is displeased.")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun awaitClicksWithMessages(vararg viewsAndMessages: Pair<View, String>) {
        viewsAndMessages.forEach {
            awaitClickOrReset(it.first)
            status_text.setTextColor(getColor(R.color.black))
            status_text.text = it.second
        }
    }

    private suspend fun awaitClickOrReset(target: View) {
        lockViews.forEach {
            if (it != target) {
                it.setOnClickListener {
                    resetLock()
                }
            }
        }
        awaitClick(target)
        lockViews.forEach { it.setOnClickListener(null) }
    }

    private suspend fun awaitClick(target: View) = suspendCancellableCoroutine<Unit> { cancellableContinuation ->
        target.setOnClickListener {
            cancellableContinuation.resume(Unit)
        }
    }

    private fun woggle() {
        val random = Random(System.currentTimeMillis())
        fun woggleView(target: View) {
            val animator = ValueAnimator.ofInt(0) // Don't care about the values because it's all random
            animator.addUpdateListener {
                val angle = random.nextDouble(Math.PI * 2)
                val length = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    random.nextFloat() * 8f,
                    resources.displayMetrics
                ) * (1f - it.animatedFraction)
                val dx = (Math.cos(angle) * length).toFloat()
                val dy = (Math.sin(angle) * length).toFloat()
                target.translationX = dx
                target.translationY = dy
            }
            animator.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    target.translationX = 0f
                    target.translationY = 0f
                }
            })
            animator.duration = 2000
            animator.start()
        }
        lockViews.forEach { woggleView(it) }
    }

}
