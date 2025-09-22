package com.assignment.movietvapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.assignment.movietvapp.R
import com.assignment.movietvapp.utils.requestFocus

@OptIn(ExperimentalComposeUiApi::class)
@Composable
 fun NoInternetScreen() {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .zIndex(1f)
                .requestFocus(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.no_intrnet),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth(),

                )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Whoops!!",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No Internet connection was found.\nCheck your connection or try again.",
                fontSize = 35.sp,
                color = Color.White,
                style = TextStyle(textAlign = TextAlign.Center)
            )

            Spacer(modifier = Modifier.height(24.dp))

        }

    }

}



