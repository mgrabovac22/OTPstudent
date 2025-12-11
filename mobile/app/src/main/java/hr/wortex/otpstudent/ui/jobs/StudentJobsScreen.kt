package hr.wortex.otpstudent.ui.career

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.model.StudentJob

@Composable
fun StudentJobsScreen(
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: StudentJobsViewModel = viewModel(
        factory = DependencyProvider.studentJobsViewModelFactory
    )
) {
    val state = viewModel.uiState
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadJobs()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    StudentJobsContent(
        paddingValues = paddingValues,
        state = state,
        onFilterChange = viewModel::onFilterSelected,
        onAdditionalFilterClick = { /* TODO: Filter */ },
        onJobClick = { job ->
            navController.navigate("job_detail/${job.id}")
        }
    )
}

@Composable
private fun StudentJobsContent(
    paddingValues: PaddingValues,
    state: StudentJobsUiState,
    onFilterChange: (JobFilterType) -> Unit,
    onAdditionalFilterClick: () -> Unit,
    onJobClick: (StudentJob) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
    ) {

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.clickable { onAdditionalFilterClick() },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Filtriraj",
                        tint = Color(0xFF1B6E2A),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "FILTRIRAJ",
                        fontSize = 13.sp,
                        color = Color(0xFF1B6E2A),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        JobFilterSegment(
            selectedFilter = state.selectedFilter,
            onFilterChange = onFilterChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                val filteredJobs = when (state.selectedFilter) {
                    JobFilterType.ALL -> state.jobs
                    JobFilterType.APPLIED -> state.jobs.filter { it.isApplied }
                    JobFilterType.NOT_APPLIED -> state.jobs.filter { !it.isApplied }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredJobs) { job ->
                        StudentJobCard(
                            job = job,
                            location = job.city.ifBlank { job.location },
                            onClick = { onJobClick(job) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JobFilterSegment(
    selectedFilter: JobFilterType,
    onFilterChange: (JobFilterType) -> Unit
) {
    val label = when (selectedFilter) {
        JobFilterType.APPLIED -> "Prijavljeni poslovi"
        JobFilterType.ALL -> "Sve"
        JobFilterType.NOT_APPLIED -> "Neprijavljeni poslovi"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFE0E0E0), RoundedCornerShape(50)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { onFilterChange(selectedFilter.previous()) }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Prethodni filter"
            )
        }

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = { onFilterChange(selectedFilter.next()) }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Sljedeći filter"
            )
        }
    }
}

private fun JobFilterType.next(): JobFilterType =
    when (this) {
        JobFilterType.APPLIED -> JobFilterType.ALL
        JobFilterType.ALL -> JobFilterType.NOT_APPLIED
        JobFilterType.NOT_APPLIED -> JobFilterType.APPLIED
    }

private fun JobFilterType.previous(): JobFilterType =
    when (this) {
        JobFilterType.APPLIED -> JobFilterType.NOT_APPLIED
        JobFilterType.ALL -> JobFilterType.APPLIED
        JobFilterType.NOT_APPLIED -> JobFilterType.ALL
    }

@Composable
private fun StudentJobCard(
    job: StudentJob,
    location: String,
    onClick: () -> Unit
) {
    val cardColor = if (job.isApplied) {
        Color(0xFFFF9800)
    } else {
        Color(0xFF4CAF50)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        color = cardColor
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

            Spacer(modifier = Modifier.height(8.dp))

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
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = location,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = "Saznaj više!",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (job.isApplied) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prijavljeno",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentJobsScreenPreview() {
    val navController = rememberNavController()

    val previewJobs = listOf(
        StudentJob(
            id = 1,
            name = "ADMINISTRATIVNI POSLOVI",
            startDate = "2025-01-01",
            location = "Adresa 1",
            city = "Zagreb",
            isApplied = false
        ),
        StudentJob(
            id = 2,
            name = "SAVJETNIK ZA OTPgo",
            startDate = "2025-02-10",
            location = "Adresa 2",
            city = "Zadar",
            isApplied = true
        ),
        StudentJob(
            id = 3,
            name = "ASISTENT ZA DIGITALNI MARKETING",
            startDate = "2025-03-15",
            location = "Adresa 3",
            city = "Zagreb",
            isApplied = false
        )
    )

    val previewState = StudentJobsUiState(
        isLoading = false,
        jobs = previewJobs,
        error = null,
        selectedFilter = JobFilterType.ALL
    )

    StudentJobsContent(
        paddingValues = PaddingValues(0.dp),
        state = previewState,
        onFilterChange = {},
        onAdditionalFilterClick = {},
        onJobClick = {}
    )
}