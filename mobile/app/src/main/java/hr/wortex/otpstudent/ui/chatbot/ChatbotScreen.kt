package hr.wortex.otpstudent.ui.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.wortex.otpstudent.di.DependencyProvider
import hr.wortex.otpstudent.domain.model.ChatMessage

val BotBubbleColor = Color(0xFF7cbe47)
val UserBubbleColor = Color(0xFFf2701b)
val InputBgColor = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    viewModel: ChatbotViewModel = viewModel(factory = DependencyProvider.chatbotViewModelFactory)
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OTP frend", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B6E2A)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }

                if (isLoading) {
                    item {
                        BotTypingIndicator()
                    }
                }
            }

            ChatInputArea(
                text = inputText,
                onTextChanged = viewModel::updateInputText,
                onSendClicked = viewModel::sendMessage,
                isEnabled = !isLoading
            )
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.isUser

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUser) UserBubbleColor else BotBubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = Color.White
            )
        }
    }
}

@Composable
fun BotTypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = BotBubbleColor,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Asistent piše...", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputArea(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Upiši poruku...") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = InputBgColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            maxLines = 3
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onSendClicked,
            enabled = isEnabled && text.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Pošalji",
                tint = if (isEnabled && text.isNotBlank()) UserBubbleColor else Color.Gray
            )
        }
    }
}
