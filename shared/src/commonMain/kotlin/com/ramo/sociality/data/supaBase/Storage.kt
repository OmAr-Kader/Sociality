package com.ramo.sociality.data.supaBase

import com.ramo.sociality.data.util.supabase
import com.ramo.sociality.global.base.SUPA_STORAGE_LINK
import com.ramo.sociality.global.base.Supabase
import io.github.jan.supabase.storage.storage
import kotlinx.datetime.Clock
import kotlin.random.Random

suspend fun Supabase.uploadFile(
    bucket: String,
    baseName: String,
    image: ByteArray,
    invoke: suspend (String) -> Unit,
) {
    storage.from(bucket).apply {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            supabase {
                upload(
                    baseName + "_" + Clock.System.now().toEpochMilliseconds() + "_" + Random.nextInt(),
                    data = image,
                    upsert = false
                ).let { link ->
                    SUPA_STORAGE_LINK + link
                }
            } ?: ""
        }.let {
            invoke(it)
        }
    }
}

suspend fun Supabase.uploadListFile(
    bucket: String,
    baseName: String,
    images: List<ByteArray>,
    invoke: suspend (List<String>) -> Unit,
) {
    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
        images.map {
            supabase {
                storage.from(bucket).upload(
                    baseName + "_" + Clock.System.now().toEpochMilliseconds() + "_" + Random.nextInt(),
                    data = it,
                    upsert = false
                ).let { link ->
                    SUPA_STORAGE_LINK + link
                }
            } ?: ""
        }
    }.let {
        invoke(it)
    }
}

suspend fun Supabase.deleteFile(
    bucket: String,
    urls: List<String>,
    invoke: suspend (Unit?) -> Unit,
) {
    storage.from(bucket).apply {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            urls.forEach { url ->
                supabase {
                    url.split("${bucket}/").lastOrNull()?.let {
                        delete(it)
                    }
                }
            }

        }.let {
            invoke(it)
        }
    }
}
