package com.techuntried.accountsbasics2.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.techuntried.accountsbasics2.ui.theme.ShimmerColor

@Composable
fun ShimmerHomeSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Box(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ShimmerColor)
        )


        repeat(3) {
            SkeletonTypeB()
        }
    }
}

@Composable
private fun SkeletonTypeB(
    modifier: Modifier = Modifier,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ShimmerColor)

            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(width = 180.dp, height = 200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ShimmerColor)

                )
            }
        }
    }

}