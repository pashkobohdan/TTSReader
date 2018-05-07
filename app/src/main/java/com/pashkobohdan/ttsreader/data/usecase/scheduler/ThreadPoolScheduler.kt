package com.pashkobohdan.ttsreader.data.usecase.scheduler

import io.reactivex.Scheduler

interface ThreadPoolScheduler {
    fun getScheduler() : Scheduler
}