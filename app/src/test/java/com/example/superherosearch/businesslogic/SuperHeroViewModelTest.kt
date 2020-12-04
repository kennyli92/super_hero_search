package com.example.superherosearch.businesslogic

import com.example.superherosearch.data.Image
import com.example.superherosearch.data.SuperHeroCharacter
import com.example.superherosearch.data.SuperHeroRepository
import com.example.superherosearch.data.SuperHeroesResponse
import com.example.superherosearch.recyclerview.SuperHeroItem
import com.example.superherosearch.utils.Visibility
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Test

class SuperHeroViewModelTest {
  @Before
  fun before() {
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
  }

  @After
  fun after() {
    RxAndroidPlugins.reset()
    RxJavaPlugins.reset()
  }

  private val superHeroRepository: SuperHeroRepository = mock()

  private fun superHeroViewModel(): SuperHeroViewModel {
    return SuperHeroViewModel(
      superHeroRepository = superHeroRepository
    )
  }

  private val superHeroCharacters = listOf(
    SuperHeroCharacter(name = "name1", image = Image(url = "url1")),
    SuperHeroCharacter(name = "name2", image = Image(url = "url2")),
    SuperHeroCharacter(name = "name3", image = Image(url = "url3"))
  )

  private val superHeroListItem = superHeroCharacters.map { superHeroCharacter ->
    SuperHeroItem.Image(name = superHeroCharacter.name, imageUrl = superHeroCharacter.image.url)
  }

  @Test
  fun on_load_action_returns_super_hero_list_items() {
    // GIVEN
    val superHeroViewModel = superHeroViewModel()
    val actionSignal = PublishSubject.create<SuperHeroAction>()
    superHeroViewModel.actionHandler(actionSignal = actionSignal)

    whenever(superHeroRepository.getSuperHeroes(isCache = false))
      .doReturn(Single.just(SuperHeroesResponse.Characters(characters = superHeroCharacters)))

    // WHEN
    val stateObs = superHeroViewModel.stateObs().test()
    val eventObs = superHeroViewModel.eventObs().test()
    actionSignal.onNext(SuperHeroAction.Load)

    // THEN
    stateObs.assertNotComplete()
      .assertNoErrors()
      .assertValueCount(3)
      .assertValueAt(0, SuperHeroState.ProcessVisibility(visibility = Visibility.VISIBLE))
      .assertValueAt(1, SuperHeroState.ListItem(
        superHeroItems = superHeroListItem
      ))
      .assertValueAt(2, SuperHeroState.ProcessVisibility(visibility = Visibility.GONE))

    eventObs.assertNotComplete()
      .assertNoErrors()
      .assertValueCount(3)
      .assertValueAt(0, SuperHeroEvent.Noop)
      .assertValueAt(1, SuperHeroEvent.Noop)
      .assertValueAt(2, SuperHeroEvent.Noop)
  }
}