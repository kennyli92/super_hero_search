package com.example.superherosearch

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.superherosearch.businesslogic.SuperHeroAction
import com.example.superherosearch.businesslogic.SuperHeroEvent
import com.example.superherosearch.businesslogic.SuperHeroState
import com.example.superherosearch.businesslogic.SuperHeroViewModel
import com.example.superherosearch.databinding.ActivityMainBinding
import com.example.superherosearch.extensions.plusAssign
import com.example.superherosearch.extensions.showSnackBar
import com.example.superherosearch.log.Logging
import com.example.superherosearch.recyclerview.SuperHeroAdapter
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private val vm: SuperHeroViewModel by viewModels()

  private lateinit var adapter: SuperHeroAdapter
  private lateinit var binding: ActivityMainBinding
  private val disposables = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // recycler view
    binding.superHeroRecyclerView.layoutManager = LinearLayoutManager(this)
    val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
    itemDecorator.setDrawable(
      ContextCompat.getDrawable(this, R.color.black)!!)
    binding.superHeroRecyclerView.addItemDecoration(itemDecorator)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.search_menu, menu)

    return true
  }

  override fun onStart() {
    super.onStart()
    adapter = SuperHeroAdapter()
    binding.superHeroRecyclerView.adapter = adapter

    // STATE
    disposables += vm.stateObs()
      .subscribe({ state ->
        when (state) {
          is SuperHeroState.Noop -> {}
          is SuperHeroState.ListItem -> onLoadState(state = state)
          is SuperHeroState.ProcessVisibility -> onProgressVisibility(state = state)
        }
      }, Logging.logErrorAndThrow())

    // EVENT
    disposables += vm.eventObs()
      .subscribe({ event ->
        when (event) {
          is SuperHeroEvent.Noop -> {}
          is SuperHeroEvent.Snackbar -> onSnackbarEvent(event = event)
        }
      }, Logging.logErrorAndThrow())

    // Action Signals
    val refreshSignal = binding.superHeroRefresh.refreshes()
      .map { SuperHeroAction.LoadSuperHeroes(isCache = false) }
    val actionSignal = Observable.merge(
      Observable.just(SuperHeroAction.LoadSuperHeroes() as SuperHeroAction),
      refreshSignal
    )

    disposables += vm.actionHandler(actionSignal = actionSignal)
  }

  override fun onDestroy() {
    super.onDestroy()
    disposables.clear()
  }

  /** State Handlers **/

  private fun onLoadState(state: SuperHeroState.ListItem) {
    adapter.items = state.superHeroItems
    adapter.notifyDataSetChanged()

    binding.superHeroRefresh.isRefreshing = false
  }

  private fun onProgressVisibility(state: SuperHeroState.ProcessVisibility) {
    binding.superHeroProgressbar.visibility = state.visibility.value
  }

  /** Event Handlers **/

  private fun onSnackbarEvent(event: SuperHeroEvent.Snackbar) {
    binding.root.showSnackBar(snackbarViewModel = event.vm)
  }
}