package hr.wortex.otpstudent.ui.sustav_nagardivanja.informativni_sadrzaj

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.model.InfoContent
import hr.wortex.otpstudent.domain.usecase.GetInfoContent
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.res.painterResource
import hr.wortex.otpstudent.R
import androidx.compose.runtime.remember
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoContentScreen(paddingValues: PaddingValues, navController: NavController) {
    val getInfoContentUseCase = remember { GetInfoContent(DependencyProvider.infoContentRepository) }
    val viewModel = remember { InfoContentViewModel(getInfoContentUseCase) }
    val uiState by viewModel.uiState.collectAsState()

    val AppGreen = Color(0xFF1B6E2A)
    val AppOrange = Color(0xFFf2701b)
    val White = Color.White
    val Black = Color.Black

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informacije", fontWeight = FontWeight.Bold, color = White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Natrag", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppGreen)
            )
        },
        containerColor = White
    ) { innerPadding ->
        when (val state = uiState) {
            is InfoContentUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppGreen)
                }
            }
            is InfoContentUiState.Error -> {
                Text(text = state.message, color = Color.Red, modifier = Modifier.padding(innerPadding))
            }
            is InfoContentUiState.Success -> {
                val imageResources = remember { listOf(R.drawable.info_1, R.drawable.info_2, R.drawable.info_3) }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(state.infoContents) { item ->
                        val imageIndex = Random.nextInt(imageResources.size)
                        InfoContentCard(
                            item = item,
                            imageResId = imageResources[imageIndex],
                            onReadMoreClick = { clickedId ->
                                navController.navigate("info_detail_screen/$clickedId")
                            },
                            AppOrange = AppOrange,
                            White = White
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun RoundedBoxCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = Color(0xFF7cbe47),
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun InfoContentCard(item: InfoContent, imageResId: Int, onReadMoreClick: (Int) -> Unit, AppOrange: Color, White: Color) {
    RoundedBoxCard(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(200.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = item.name,
                modifier = Modifier
                    .size(128.dp)
                    .padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onReadMoreClick(item.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppOrange, contentColor = White)
                ) {
                    Text("Pročitaj više")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun InfoPreview() {
    val navController = rememberNavController()
    val paddingValues = PaddingValues(0.dp)
    Scaffold { padding ->
        InfoContentScreen(paddingValues = padding, navController = navController)
    }
}