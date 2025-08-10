package com.example.mustafakocer.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import com.example.mustafakocer.domain.util.Resource

/**
 * Offline-First mimarisini uygulamak için merkezi bir yardımcı fonksiyon.
 *
 * MİMARİ NOT: Bu fonksiyon, "Single Source of Truth" (Tek Gerçek Kaynağı) prensibini
 * uygular. Veri her zaman veritabanından gelir. Bu fonksiyonun iş akışı:
 * 1. Önce veritabanından mevcut veriyi (eski olabilir) 'Success' olarak yayınlar.
 * 2. Ağdan yeni veri çekmeye çalışır.
 * 3. Ağdan gelen veri başarılı olursa, eski veritabanı verilerini siler.
 * 4. Yeni veriyi veritabanına kaydeder.
 * 5. Veritabanından gelen Flow, yeni veriyi otomatik olarak UI'a yansıtır.
 * 6. Ağ isteği başarısız olursa, hatayı yakalar ama eski veriyi göstermeye devam eder.
 *
 * @param query Veritabanından veri okuyan fonksiyon. Sürekli olarak veri akışı sağlar.
 * @param fetch Ağdan veri çeken suspend fonksiyon.
 * @param saveFetchResult Ağdan gelen veriyi veritabanına kaydeden suspend fonksiyon.
 * @param shouldFetch Ağdan yeni veri çekilip çekilmeyeceğine karar veren fonksiyon (örn: cache süresi).
 * @return UI'ın gözlemleyeceği, veritabanından gelen veriyi içeren bir Resource Flow'u.
 */

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    // 1. Yüklenme durumunu ve veritabanındaki ilk veriyi yayınla
    val data = query().first()
    emit(Resource.Loading) // Önce yükleniyor durumunu gönder

    // 2. Veritabanındaki veriyi ilk kaynak olarak yayınla
    val flow = if (shouldFetch(data)) {
        // Yeni veri çekilmesi gerekiyorsa
        try {
            // 3. Ağdan yeni veriyi çek ve veritabanına kaydet
            val fetchedResult = fetch()
            saveFetchResult(fetchedResult)

            // 4. Veritabanından güncellenmiş veriyi yayınlamaya devam et
            query().map { Resource.Success(it) }
        } catch (throwable: Throwable) {
            // 5. Ağ hatası durumunda, hatayı logla ve veritabanındaki eski veriyi yayınlamaya devam et
            // Gerçek bir uygulamada burada hata loglama mekanizması olabilir.
            query().map { Resource.Success(it) } // Hata olsa bile eski veriyi göstermeye devam et
        }
    } else {
        // Yeni veri çekilmesi gerekmiyorsa, sadece veritabanını dinle
        query().map { Resource.Success(it) }
    }

    // 6. Sonuç akışını yayınla
    emitAll(flow)
}