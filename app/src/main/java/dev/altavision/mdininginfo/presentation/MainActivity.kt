/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package dev.altavision.mdininginfo.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import dev.altavision.mdininginfo.R
import dev.altavision.mdininginfo.presentation.theme.MdininginfoTheme
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.Query


// API core:
// https://prod-michigan-dining-services-prod.apps.containersprod.art2.p1.openshiftapps.com/dining/locations?key=093665d6ab069c859267fd4001c3c562ba805539ed852978
const val API_KEY = "093665d6ab069c859267fd4001c3c562ba805539ed852978"

interface LocationsApiService {
    @GET("dining/locations")
    suspend fun getLocations(@Query("key") apiKey : String = API_KEY): List<DiningLocation>
    @GET("dining/menu")
    suspend fun getMenu(@Query("key") apiKey : String = API_KEY,
                        @Query("date") date : String,
                        @Query("meal") meal : String,
                        @Query("location") location : String) : MenuResponseWrapper// "key", "date" (DD-MM-YYYY), "meal"
}

object RetrofitInstance {
    val api : LocationsApiService by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC // prints URL and response code
            })
            .build()


        Retrofit.Builder()
            .baseUrl("https://prod-michigan-dining-services-prod.apps.containersprod.art2.p1.openshiftapps.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationsApiService::class.java)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
    }
}

@Composable
fun WearApp(greetingName: String, viewModel: MyViewModel = viewModel()) {
    val uiState : UiState by viewModel.uiState.collectAsState()
    val listState = rememberScalingLazyListState();
    val navController = rememberSwipeDismissableNavController();


    MdininginfoTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "dining_halls_list"
            ) {
                composable("dining_halls_list") {

                    Scaffold(
                        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
                    ) {
                        when (val state : UiState = uiState) {
                            is UiState.Error -> {
                                Text(text = "Error: ${state.message}")
                            }

                            is UiState.Loading -> {
                                CircularProgressIndicator()
                            }

                            is UiState.Success -> {
//                        Text(text = state.diningLocations[0].displayName)
                                ScalingLazyColumn(state = listState) {
                                    items(state.diningLocations) { item ->
                                        Chip(
                                            label = { Text(item.displayName) },
                                            onClick = {
                                                navController.navigate("dining_hall_details/${item.name}")
                                            }
                                        )

                                    }
                                }
                            }
                        }
                    }
                }
                composable("dining_hall_details/{name}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("name")
//                    val detailsUiState by viewModel.detailsUiState.
                    val detailsViewModel : DetailsViewModel = viewModel(
                        factory = DetailsViewModelFactory(id ?: "None")
                    );
                    val detailsUiState by detailsViewModel.detailsUiState.collectAsState();

                    Log.d("MAIN", "Hey, loading!!")

                    Scaffold(
                    ) {
                        when (val detailsState : DetailsUiState = detailsUiState) {
                            is DetailsUiState.Error -> {
                                Log.d("MAIN", "Error: ${detailsState.message}");
                                Text(text = "Error: ${detailsState.message}")
                            }

                            is DetailsUiState.Loading -> {
                                CircularProgressIndicator()
                            }

                            is DetailsUiState.Success -> {
                                Log.d("MAIN", "detailsState is ${detailsState}");
//                        Text(text = state.diningLocations[0].displayName)
                                ScalingLazyColumn() { // FUTURE: Give this a state!
                                    item {
                                        Text(id ?: "Unknown", textAlign = TextAlign.Center);
                                    }
                                    item {
                                        Text(detailsViewModel.meal);
                                    }
                                    items(detailsState.responseWrapper.menu.categories) { item ->

                                        Card(
                                            onClick = {  },
                                        ) {
                                            Column() {
                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(item.categoryName, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                                                Spacer(modifier = Modifier.height(8.dp))
                                                item.menuItems.forEach { menuItem ->
                                                    Text("• " + menuItem.name, fontWeight = FontWeight.Normal)
                                                }
                                            }
                                        }


//                                        Chip(
//                                            label = { Text(item.categoryName) },
//                                            onClick = {
//
//                                            }
//                                        )

                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}