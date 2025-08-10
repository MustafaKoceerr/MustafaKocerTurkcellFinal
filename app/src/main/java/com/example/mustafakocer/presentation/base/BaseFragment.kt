package com.example.mustafakocer.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * View Binding'i yönetmek için temel bir Fragment sınıfı.
 * Bu sınıf, binding nesnesinin oluşturulmasını ve yaşam döngüsüne uygun olarak
 * temizlenmesini otomatikleştirir, böylece alt sınıflarda tekrar eden kodları önler.
 *
 * MİMARİ NOT: Bu soyut sınıf, "Don't Repeat Yourself" (DRY) prensibini uygular.
 * View Binding'in güvenli bir şekilde (memory leak olmadan) yönetilmesi için
 * Google tarafından önerilen en iyi pratiği (nullable _binding ve non-nullable binding) içerir.
 *
 * @param VB Bu fragment tarafından kullanılacak olan ViewBinding sınıfının türü (örn: FragmentHomeBinding).
 * @param bindingInflater Binding nesnesini oluşturmak için kullanılacak olan inflate metodu
 *                        (örn: FragmentHomeBinding::inflate).
 */
abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean) -> VB
) : Fragment() {

    // Bu, asıl binding nesnesini tutar ve null olabilir.
    // Sadece onCreateView ve onDestroyView arasında bir değere sahip olacaktır.
    private var _binding: VB? = null

    // Bu, alt sınıfların binding nesnesine güvenli bir şekilde erişmesini sağlar.
    // 'protected' olduğu için sadece bu sınıftan miras alan Fragment'lar tarafından görülebilir.
    // '!!' operatörü burada güvenlidir, çünkü bu özelliğe sadece view'ın var olduğu
    // (onCreateView ve onDestroyView arası) yaşam döngüsü içinde erişeceğiz.
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding nesnesini constructor'dan gelen inflater ile oluştur.
        _binding = bindingInflater.invoke(inflater, container, false)
        // Fragment'ın view'ı olarak binding'in kök (root) view'ını döndür.
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment'ın view'ı yok edildiğinde, memory leak'leri önlemek için
        // binding referansını null yap. Bu çok önemlidir!
        _binding = null
    }
}