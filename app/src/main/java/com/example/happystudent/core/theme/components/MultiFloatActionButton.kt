package com.example.happystudent.core.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class MultiFabState {
    Expanded,
    Collapsed
}

@Composable
fun rememberMultiFabState() = remember {
    mutableStateOf(MultiFabState.Collapsed)
}

data class FabItem(
    val id: Int,
    val label: String
)

@Composable
fun MultiFloatingActionButton(
    state: MultiFabState,
    onStateChange: (MultiFabState) -> Unit,
    rotateDegree: Float = 0f,
    mainIconRes: Int,
    fabItems: List<FabItem>,
    onItemClicked: (FabItem) -> Unit
) {

    val transition = updateTransition(targetState = state, label = "transition")

    val rotate by transition.animateFloat(label = "rotate") {
        if (it == MultiFabState.Expanded) rotateDegree else 0f
    }

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.End
    ) {

        AnimatedVisibility(
            visible = state == MultiFabState.Expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut()
        ) {

            LazyColumn(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {

                items(items = fabItems, key = { it.id }) { item ->
                    MiniFabItem(
                        fabItem = item,
                        onItemClicked = onItemClicked
                    )
                }

                item {} // for Spacing
            }

        }

        FloatingActionButton(
            onClick = {
                onStateChange(
                    if (state == MultiFabState.Expanded) {
                        MultiFabState.Collapsed
                    } else {
                        MultiFabState.Expanded
                    }
                )
            },

            ) {
            Icon(
                painter = painterResource(id = mainIconRes),
                contentDescription = null,
                modifier = Modifier.rotate(rotate)
            )
        }
    }



}

@Composable
fun MiniFabItem(
    fabItem: FabItem,
    onItemClicked: (FabItem) -> Unit
) {

    Text(
        fabItem.label,
        fontSize = 15.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .clickable { onItemClicked(fabItem) }

    )

}
