package com.tidal.tidaltask.domain.album.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.tidal.tidaltask.R
import com.tidal.tidaltask.base.BaseFragment
import com.tidal.tidaltask.domain.album.AlbumPresenter
import com.tidal.tidaltask.domain.album.AlbumView
import com.tidal.tidaltask.domain.album.model.Album
import com.tidal.tidaltask.util.Constants
import kotlinx.android.synthetic.main.fragment_album_listing.*
import kotlinx.android.synthetic.main.search_artist_fragment.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class AlbumListingFragment : BaseFragment(), AlbumView, AlbumRecyclerAdapter.OnClickListener {

    @Inject
    lateinit var presenter: AlbumPresenter
    private lateinit var rvAdapter: AlbumRecyclerAdapter
    private var artistId: Int? = null
    private val gridColumns: Int = 2

    override fun getLayoutId(): Int = R.layout.fragment_album_listing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null)
            artistId = arguments!!.getInt(Constants.ARG_PARAM1)

        toolbarTitle = "Albums"

        rvAdapter = AlbumRecyclerAdapter(context = context, clickListener = this)

        presenter.attachView(this)
    }

    override fun initViews(parent: View, savedInstanceState: Bundle?) {
        super.initViews(parent, savedInstanceState)
        setupToolbar()
        rvAlbums.adapter = rvAdapter
        rvAlbums.layoutManager = GridLayoutManager(context, gridColumns)
        artistId?.also { presenter.getAlbums(it) } ?: run { showError(Constants.ERROR_MESSAGE) }
    }

    fun setupToolbar() {
        toolbar?.show()
        toolbar?.title = toolbarTitle
        toolbar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showAlbums(albums: List<Album>) {
        rvAdapter?.setData(albums, true)
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOffProgressBar(toShow: Boolean) {
        if (toShow)
            fragmentHelper.showLoadingDialog()
        else
            fragmentHelper.hideLoadingDialog()
    }

    override fun onClick(album: Album) {
        Toast.makeText(context, "album clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar?.hide()
    }

    /* ----- Fragment Creation Factory / Companion ------ */
    companion object {

        fun newInstance(param1: Int): AlbumListingFragment {
            val fragment = AlbumListingFragment()
            val args = Bundle()
            args.putInt(Constants.ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
}
