package com.micoredu.reader.ui.fragment

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.*
import com.micoredu.reader.BaseFragment
import com.micoredu.reader.bean.Book
import com.micoredu.reader.bean.BookChapter
import com.micoredu.reader.ui.models.itemChapterMenu
import com.microedu.lib.reader.R
import com.microedu.lib.reader.databinding.FragmentBookchapterlistBinding

data class BookChapterState(
    val queryBook: Async<Book?> = Uninitialized,
    val queryChapter: Async<List<BookChapter>> = Uninitialized,
    val reverseMenu: Async<List<BookChapter>> = Uninitialized,
    val chapterList: List<BookChapter> = listOf()
) :
    MavericksState

class BookSourceController(private var viewModel: BookChapterViewModel) :
    TypedEpoxyController<BookChapterState>() {
    override fun buildModels(data: BookChapterState?) {
        data?.chapterList?.forEachIndexed { index, bookChapter ->
            itemChapterMenu {
                id("chapterId_${index}")
                currentChapterIndex(this@BookSourceController.viewModel.chapterIndex)
                source(bookChapter)
            }
        }
    }
}

class BookChapterListFragment : BaseFragment(R.layout.fragment_bookchapterlist), MavericksView {

    private var mBookUrl: String? = null
    val binding: FragmentBookchapterlistBinding by viewBinding(FragmentBookchapterlistBinding::bind)
    private val viewModel: BookChapterViewModel by fragmentViewModel()

    private val controller: BookSourceController by lazy {
        BookSourceController(viewModel)
    }

    override fun invalidate() = withState(viewModel) {
        controller.setData(it)
        binding.recyclerView.scrollToPosition(viewModel.chapterIndex ?: 0)
    }

    override fun init(savedInstanceState: Bundle?) {

        mBookUrl = arguments?.getString("bookUrl")

        viewModel.onAsync(BookChapterState::queryBook, deliveryMode = uniqueOnly(), onSuccess = {

        })

        viewModel.onAsync(BookChapterState::queryChapter, onSuccess = {

        })

        binding.recyclerView.setController(controller)
        viewModel.queryBook(mBookUrl!!)
        viewModel.queryChapter(mBookUrl!!)
    }

    fun reversionMenu() {
        viewModel.reverseMenu(mBookUrl!!)
    }


    companion object {
        @JvmStatic
        fun getInstance(bookUrl: String): BookChapterListFragment {
            val instance = BookChapterListFragment()
            val bundle = Bundle()
            bundle.putString("bookUrl", bookUrl)
            instance.arguments = bundle
            return instance
        }
    }
}