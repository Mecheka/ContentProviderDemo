package com.mecheka.contentproviderdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mecheka.contentproviderdemo.ui.theme.ContentProviderDemoTheme
import com.mecheka.core.UserDemoState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContentProviderDemoTheme {
                val context = LocalContext.current
                val userState = remember {
                    UserDemoState(context)
                }
                // A surface container using the 'background' color from the theme
                UserContentCompose(userState)
            }
        }
    }
}

@Composable
fun UserContentCompose(userState: UserDemoState) {
    var text by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())) {
        Text(
            text = stringResource(id = R.string.heading),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 70.dp)
        )
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(text = stringResource(id = R.string.hintText)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        )
        Button(
            onClick = { scope.launch(Dispatchers.IO) { userState.addUser(text) } },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp, bottom = 20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.insertButtontext),
                style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
            )
        }
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) { userState.loadUser() }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp, bottom = 20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.loadButtonText),
                style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
            )
        }

        userState.userList.forEach {
            Text(it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ContentProviderDemoTheme {
        val context = LocalContext.current
        UserContentCompose(UserDemoState(context))
    }
}