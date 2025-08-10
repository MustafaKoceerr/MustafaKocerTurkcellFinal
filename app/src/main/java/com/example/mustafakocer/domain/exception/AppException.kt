package com.example.mustafakocer.domain.exception

/**
 * Uygulama içindeki tüm özel istisnalar için tip-güvenli bir hiyerarşi tanımlar.
 * Bu sınıf, standart 'Exception' sınıfını genişletir ve 'message' özelliğini override eder.
 *
 * MİMARİ NOT: Bu sealed class, tüm katmanlarda hata yönetimi için tek ve iyi tanımlanmış
 * bir sözleşme oluşturur. Kullanıcıya hangi mesajın gösterileceğini değil, teknik olarak
 * *neyin* yanlış gittiğini temsil eder. Bu istisnaları kullanıcı dostu mesajlara
 * çevirmek UI katmanının sorumluluğundadır.
 */
sealed class AppException(
    override val message: String?,
    override val cause: Throwable? = null,
) : Exception(message, cause) {

    /**
     * İnternet bağlantısı, zaman aşımı gibi altyapısal ağ sorunlarını temsil eder.
     */
    sealed class Network(message: String?, cause: Throwable? = null) : AppException(message, cause) {
        data class NoInternet(override val cause: Throwable? = null) : Network("İnternet bağlantısı yok", cause)
        data class Timeout(override val cause: Throwable? = null) : Network("İstek zaman aşımına uğradı", cause)
    }

    /**
     * Sunucunun HTTP yanıtlarından kaynaklanan hataları temsil eder.
     */
    sealed class Api(val httpCode: Int, message: String?, cause: Throwable? = null) : AppException(message, cause) {
        data class Unauthorized(override val cause: Throwable? = null) : Api(401, "Yetkisiz", cause)
        data class NotFound(override val cause: Throwable? = null) : Api(404, "Bulunamadı", cause)
        data class ServerError(val code: Int, override val cause: Throwable? = null) : Api(code, "Sunucu Hatası", cause)
        data class HttpError(val code: Int, override val message: String?, override val cause: Throwable? = null) : Api(code, message, cause)
    }

    /**
     * Veri işleme sırasında oluşan hataları temsil eder (örn: JSON parse hatası, boş yanıt).
     */
    sealed class Data(message: String, cause: Throwable? = null) : AppException(message, cause) {
        data object EmptyResponse : Data("Sunucudan boş yanıt alındı", null)
        data class Parse(override val cause: Throwable?) : Data("Veri ayrıştırma hatası", cause)
        /**
         * İş kurallarına uymayan girdiler (input) için kullanılır.
         * Örn: Boş e-posta, geçersiz şifre formatı.
         */
        data class ValidationError(val validationMessage: String) : Data(validationMessage, null)
    }

    /**
     * Yukarıdaki kategorilere girmeyen, beklenmedik tüm hatalar için kullanılır.
     */
    data class Unknown(override val cause: Throwable? = null) : AppException("Bilinmeyen bir hata oluştu", cause)
}