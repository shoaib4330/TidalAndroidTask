package com.tidal.tidaltask.domain.artist.ui

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.tidal.tidaltask.R
import com.tidal.tidaltask.base.BaseFragment
import com.tidal.tidaltask.domain.album.listing.ui.AlbumListingFragment
import com.tidal.tidaltask.domain.artist.ArtistPresenter
import com.tidal.tidaltask.domain.artist.ArtistView
import com.tidal.tidaltask.domain.artist.model.Artist
import com.tidal.tidaltask.util.Constants
import kotlinx.android.synthetic.main.search_artist_fragment.*
import javax.inject.Inject

class SearchArtistFragment : BaseFragment(), ArtistView, ArtistRecyclerAdapter.OnClickListener {

    override fun getLayoutId(): Int = R.layout.search_artist_fragment

    @Inject
    lateinit var presenter: ArtistPresenter

    private lateinit var rvAdapter: ArtistRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rvAdapter = ArtistRecyclerAdapter(context = context, clickListener = this)

        presenter.attachView(this)
    }

    override fun initViews(parent: View, savedInstanceState: Bundle?) {
        super.initViews(parent, savedInstanceState)
        rv_artists.adapter = rvAdapter
        configureArtistSearchView()
    }

    override fun onStart() {
        super.onStart()
        toolbar?.hide() // hide the toolbar
    }

    private fun clearArtistsList() {
        this.rvAdapter?.clearData()
    }

    override fun showArtists(artists: List<Artist>) {
        this.rvAdapter.setData(artists, true)
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

    override fun onClick(artist: Artist) {
        artist.id?.let {
            fragmentHelper.replaceFragment(
                AlbumListingFragment.newInstance(artist.id, artist.name!!),
                clearBackStack = false,
                addToBackstack = true
            )
        }
    }

    private fun configureArtistSearchView() {
        svArtistSearch?.setOnClickListener { svArtistSearch?.onActionViewExpanded() }

        svArtistSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            private val handler: Handler = Handler()

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { presenter.findArtists(query) }
                return true // report action handled
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    when {
                        it.length > 1 -> {
                            handler.removeCallbacks(null) // cancel previously sent messages
                            handler.postDelayed({ presenter.findArtists(it) }, 3000L)
                        }
                        it.isBlank() -> clearArtistsList()
                        else -> {
                            // when length is one, lets not do anything
                        }
                    }
                } ?: clearArtistsList()
                return true // report action handled
            }
        })
    }

    /* ----- Fragment Creation Factory / Companion ------ */
    companion object {
        fun newInstance(): SearchArtistFragment {
            return SearchArtistFragment()
        }
    }
}