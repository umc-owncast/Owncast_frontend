package kr.dori.android.own_cast.keyword

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import kr.dori.android.own_cast.databinding.FragmentAddCategoryDialogBinding
import kr.dori.android.own_cast.databinding.KeywordLoadingDialogBinding


class KeywordLoadingDialog(context: Context):Dialog(context) {
    private lateinit var binding: KeywordLoadingDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KeywordLoadingDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }
}