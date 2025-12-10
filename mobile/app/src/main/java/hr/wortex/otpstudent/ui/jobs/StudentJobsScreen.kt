package hr.wortex.otpstudent.ui.career

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.model.StudentJob

// ----------------------------------------------------
//  SCREEN ZA APLIKACIJU (koristi ViewModel + REST)
// ----------------------------------------------------

@Composable
fun StudentJobsScreen(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: StudentJobsViewModel = viewModel(
        factory = DependencyProvider.studentJobsViewModelFactory
    )
) {
    val state = viewModel.uiState

    StudentJobsContent(
        navController = navController,
        paddingValues = paddingValues,
        state = state,
        onToggleOpenOnly = { viewModel.toggleOpenOnly() },
        onFilterClick = { /* TODO: filter dialog */ },
        onJobClick = { job ->
            // sada radi s REST ID-jem
            navController.navigate("job_detail/${job.id}")
        }
    )
}

// ----------------------------------------------------
//  UI CONTENT — koristi se za Screen + Preview
// ----------------------------------------------------

@Composable
private fun StudentJobsContent(
    navController: NavController,
    paddingValues: PaddingValues,
    state: StudentJobsUiState,
    onToggleOpenOnly: () -> Unit,
    onFilterClick: () -> Unit,
    onJobClick: (StudentJob) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
    ) {

        // ---------------- Title bar ----------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B6E2A))
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "STUDENTSKI POSLOVI",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        // ---------------- Filter bar ----------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .clickable { onFilterClick() },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    text = "Filteri",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 13.sp,
                    color = Color(0xFF1B6E2A)
                )
            }

            Text(
                text = "Otvorene prijave",
                fontSize = 13.sp,
                modifier = Modifier.clickable { onToggleOpenOnly() }
            )
        }

        // ---------------- Content ----------------
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

        } else if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.jobs) { job ->
                    StudentJobCard(
                        job = job,
                        location = job.city.ifBlank { job.location },
                        onClick = { onJobClick(job) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vrati prilagođeni prikaz",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
//  UI KARTICA JOBA
// ----------------------------------------------------

@Composable
private fun StudentJobCard(
    job: StudentJob,
    location: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF4CAF50)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = job.name.uppercase(),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(location, color = Color.White, fontSize = 13.sp)
                }

                Text(
                    "Saznaj više!",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ----------------------------------------------------
//  PREVIEW — radi bez API-a i bez ViewModel Factoryja
// ----------------------------------------------------

@Preview(showBackground = true)
@Composable
private fun StudentJobsScreenPreview() {
    val navController = rememberNavController()

    val previewState = StudentJobsUiState(
        jobs = listOf(
            StudentJob(
                id = 1,
                name = "ADMINISTRATIVNI POSLOVI",
                startDate = "2025-01-01",
                location = "Adresa 1",
                city = "Zagreb"
            ),
            StudentJob(
                id = 2,
                name = "SAVJETNIK ZA OTPgo",
                startDate = "2025-02-10",
                location = "Adresa 2",
                city = "Split"
            ),
            StudentJob(
                id = 3,
                name = "ASISTENT ZA DIGITALNI MARKETING",
                startDate = "2025-03-15",
                location = "Adresa 3",
                city = "Rijeka"
            ),
            StudentJob(
                id = 4,
                name = "IT PODRŠKA",
                startDate = "2025-04-01",
                location = "Adresa 4",
                city = "Osijek"
            )
        )
    )

    StudentJobsContent(
        navController = navController,
        paddingValues = PaddingValues(0.dp),
        state = previewState,
        onToggleOpenOnly = {},
        onFilterClick = {},
        onJobClick = {}
    )
}