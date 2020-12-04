package com.example.superherosearch.businesslogic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.superherosearch.R
import com.example.superherosearch.data.SuperHeroRepository
import com.example.superherosearch.data.SuperHeroesResponse
import com.example.superherosearch.dialog.SnackbarViewModel
import com.example.superherosearch.log.Logging
import com.example.superherosearch.recyclerview.SuperHeroItem
import com.example.superherosearch.utils.StateEvent
import com.example.superherosearch.utils.Visibility
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class SuperHeroViewModel @ViewModelInject constructor(
  private val superHeroRepository: SuperHeroRepository
) : ViewModel() {
  @Volatile
  private var state: SuperHeroState = SuperHeroState.Noop

  private val stateEventObs =
    PublishSubject.create<StateEvent<SuperHeroState, SuperHeroEvent>>().toSerialized()

  /**
   * emit State that should persist on screen upon foreground from backgrounding
   */
  fun stateObs(): Observable<SuperHeroState> {
    return stateEventObs.map {
      it.state
    }.distinctUntilChanged().observeOn(AndroidSchedulers.mainThread())
  }

  /**
   * Emit single Event that should happen once and not persist on the screen such as dialogs,
   * snackbars, navigations
   */
  fun eventObs(): Observable<SuperHeroEvent> {
    return stateEventObs.map {
      it.event
    }.observeOn(AndroidSchedulers.mainThread())
  }

  /**
   * Observe actions to be handled created from click streams
   */
  fun actionHandler(actionSignal: Observable<SuperHeroAction>): Disposable {
    return actionSignal
      .observeOn(Schedulers.computation())
      .flatMap { action ->
        when (action) {
          is SuperHeroAction.LoadSuperHeroes -> onLoadSuperHeroes(state = state, action = action)
          is SuperHeroAction.Search -> onSearchSuperHeroes(action = action)
        }
      }.subscribe({
        this.state = it.state
        stateEventObs.onNext(it)
      }, Logging.logErrorAndThrow())
  }

  /**
   * Always load super hero characters from cache (db) by default, unless explicitly
   * triggered to get latest from the api
   */
  private fun onLoadSuperHeroes(
    state: SuperHeroState,
    action: SuperHeroAction.LoadSuperHeroes
  ): Observable<StateEvent<SuperHeroState, SuperHeroEvent>> {
    // if fresh screen, get list of super heroes, else recover the last state
    return if (state == SuperHeroState.Noop) {
      superHeroRepository.getSuperHeroes(isCache = action.isCache)
        .observeOn(Schedulers.computation())
        .flatMapObservable { superHeroesResponse ->
          val getSuperHeroStateEvent =
            handleSuperHeroResponse(superHeroesResponse = superHeroesResponse)

          val progressVisibilityState =
            SuperHeroState.ProcessVisibility(visibility = Visibility.GONE)

          return@flatMapObservable Observable.just(
            getSuperHeroStateEvent,
            StateEvent(
              state = progressVisibilityState,
              event = SuperHeroEvent.Noop
            )
          )
        }.startWith(
          StateEvent(
            SuperHeroState.ProcessVisibility(visibility = Visibility.VISIBLE),
            SuperHeroEvent.Noop
          )
        )
    } else {
      Observable.just(StateEvent(state, SuperHeroEvent.Noop))
    }
  }

  private fun handleSuperHeroResponse(
    superHeroesResponse: SuperHeroesResponse
  ): StateEvent<SuperHeroState, SuperHeroEvent> {
    return when (superHeroesResponse) {
      is SuperHeroesResponse.Characters -> {
        val superHeroItems = superHeroesResponse.characters.map { superHeroCharacter ->
          SuperHeroItem.Image(
            name = superHeroCharacter.name,
            imageUrl = superHeroCharacter.image.url
          )
        }
        StateEvent(
          SuperHeroState.ListItem(
            superHeroItems = superHeroItems
          ),
          SuperHeroEvent.Noop
        )
      }
      is SuperHeroesResponse.NotFound ->
        StateEvent(
          SuperHeroState.Noop,
          SuperHeroEvent.Snackbar(
            vm = SnackbarViewModel(
              messageResId = R.string.super_hero_get_not_found,
              duration = Snackbar.LENGTH_LONG
            )
          )
        )
      is SuperHeroesResponse.UnknownError ->
        StateEvent(
          SuperHeroState.Noop,
          SuperHeroEvent.Snackbar(
            vm = SnackbarViewModel(
              messageResId = R.string.super_hero_get_unknown_error,
              duration = Snackbar.LENGTH_LONG
            )
          )
        )
    }
  }

  // do a super hero search from our db cache
  private fun onSearchSuperHeroes(
    action: SuperHeroAction.Search
  ): Observable<StateEvent<SuperHeroState, SuperHeroEvent>> {
    return superHeroRepository.searchSuperHeroes(query = action.query)
      .observeOn(Schedulers.computation())
      .flatMapObservable { superHeroesResponse ->
        val getSuperHeroStateEvent =
          handleSuperHeroResponse(superHeroesResponse = superHeroesResponse)

        return@flatMapObservable Observable.just(getSuperHeroStateEvent)
      }
  }
}