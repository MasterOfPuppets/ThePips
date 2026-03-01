package com.masterofpuppets.thepips.feature.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masterofpuppets.thepips.R

@Composable
fun InfoScreen() {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            InfoSection(
                title = stringResource(id = R.string.info_title_history),
                body = stringResource(id = R.string.info_text_history)
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoSection(
                title = stringResource(id = R.string.info_title_technical),
                body = stringResource(id = R.string.info_text_technical)
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoSection(
                title = stringResource(id = R.string.info_title_composition),
                body = stringResource(id = R.string.info_text_composition)
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoSection(
                title = stringResource(id = R.string.info_title_how_it_works),
                body = stringResource(id = R.string.info_text_how_it_works)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InfoSection(title: String, body: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}