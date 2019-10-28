package com.tidal.tidaltask.domain.album.listing.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager

import com.tidal.tidaltask.R
import com.tidal.tidaltask.base.BaseFragment
import com.tidal.tidaltask.domain.album.detail.ui.AlbumDetailFragment
import com.tidal.tidaltask.domain.album.listing.AlbumPresenter
import com.tidal.tidaltask.domain.album.listing.AlbumView
import com.tidal.tidaltask.domain.album.model.Album
import com.tidal.tidaltask.util.Constants
import kotlinx.android.synthetic.main.fragment_album_listing.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class AlbumListingFragment : BaseFragment(), AlbumView,
    AlbumRecyclerAdapter.OnClickListener {

    @Inject
    lateinit var presenter: AlbumPresenter
    private var rvAdapter: AlbumRecyclerAdapter? = null
    private var artistId: Int? = null
    private val gridColumns: Int = 2

    override fun getLayoutId(): Int = R.layout.fragment_album_listing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null)
            artistId = arguments!!.getInt(Constants.ARG_PARAM1)

        toolbarTitle = "Albums"

        rvAdapter = AlbumRecyclerAdapter(
            context = context,
            clickListener = this
        )

        presenter.attachView(this)
    }

    override fun initViews(parent: View, savedInstanceState: Bundle?) {
        super.initViews(parent, savedInstanceState)
        setupToolbar()
        rvAlbums.adapter = rvAdapter
        rvAlbums.layoutManager = GridLayoutManager(context, gridColumns)
        presenter.getAlbums(artistId)
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
        album.id?.let {
            fragmentHelper.replaceFragment(
                AlbumDetailFragment.newInstance(
                    album.id!!,
                    album.title,
                    album.cover_xl,
                    album.label,
                    album.artist?.name
                ), false, true
            )
        } ?: kotlin.run { showError(Constants.ERROR_MESSAGE)}
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
