package hr.wortex.otpstudent.ui.sustav_nagardivanja.informativni_sadrzaj

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Person
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

@Composable
fun InfoContentScreen(paddingValues: PaddingValues, navController: NavController) {
    val getInfoContentUseCase = remember { GetInfoContent(DependencyProvider.infoContentRepository) }
    val viewModel = remember { InfoContentViewModel(getInfoContentUseCase) }
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is InfoContentUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is InfoContentUiState.Error -> {
            Text(text = state.message, color = Color.Red, modifier = Modifier.padding(paddingValues))
        }
        is InfoContentUiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(state.infoContents) { item ->
                    InfoContentCard(
                        item = item,
                        onReadMoreClick = { clickedId ->
                            navController.navigate("info_detail_screen/$clickedId")
                        }
                    )
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
fun InfoContentCard(item: InfoContent, onReadMoreClick: (Int) -> Unit) {
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
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(128.dp)
                    .padding(start = 8.dp),
                tint = Color.White
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
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onReadMoreClick(item.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf2701b))
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
    Scaffold { padding ->
        InfoContentScreen(paddingValues = padding, navController = navController)
    }
}