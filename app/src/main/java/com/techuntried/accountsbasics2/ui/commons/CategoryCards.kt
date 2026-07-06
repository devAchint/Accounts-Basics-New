package com.techuntried.accountsbasics2.ui.commons


//@Composable
//fun CategoryItemCard(
//    modifier: Modifier = Modifier,
//    categoryModel: CategoryModel,
//    onClick: () -> Unit
//) {
//    val bgColor = try {
//        categoryModel.bgColor?.let { Color(it.toColorInt()) } ?: Color.White
//    } catch (e: Exception) {
//        Color.White
//    }
//
//    ConstraintLayout(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(200.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
//            .background(bgColor)
//            .clickable { onClick() }
//    ) {
//        val (textRef, tagRef, imageRef) = createRefs()
//
//        // Text at top/start
//        Text(
//            text = categoryModel.categoryName,
//            fontSize = 16.sp,
//            fontFamily = RubikMedium,
//            maxLines = 2,
//            color = Color.Black,
//            modifier = Modifier
//                .constrainAs(textRef) {
//                    top.linkTo(parent.top, margin = 16.dp)
//                    start.linkTo(parent.start, margin = 16.dp)
//                    end.linkTo(parent.end, margin = 16.dp)
//                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
//                }
//        )
//
//        categoryModel.tag?.let {
//            Text(
//                text = categoryModel.tag,
//                fontSize = 11.sp,
//                fontFamily = RubikRegular,
//                maxLines = 1,
//                textAlign = TextAlign.Center,
//                color = Color.White,
//                modifier = Modifier
//                    .clip(RoundedCornerShape(20.dp))
//                    .background(Color(0XFF333333))
//                    .padding(horizontal = 8.dp, vertical = 3.dp)
//                    .constrainAs(tagRef) {
//                        bottom.linkTo(parent.bottom, margin = 8.dp)
//                        start.linkTo(parent.start, margin = 8.dp)
//                    }
//            )
//        }
//
//
//        // Image at bottom/end with offset
//
//        val density = LocalDensity.current
//        val imageSizePx = remember(density) {
//            with(density) { 208.dp.roundToPx() }
//        }
//
//        AsyncImage(
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(categoryModel.imageUrl)
//                .size(imageSizePx) // 👈 density-aware downsampling
//                .scale(Scale.FILL) // matches ContentScale.Crop
//                .allowHardware(true)
//                .memoryCachePolicy(CachePolicy.ENABLED)
//                .diskCachePolicy(CachePolicy.ENABLED)
//                .build(),
//            placeholder = painterResource(R.drawable.image_placeholder),
//            error = painterResource(R.drawable.image_placeholder),
//            contentDescription = null,
//            modifier = Modifier
//                .size(208.dp)
//                .constrainAs(imageRef) {
//                    end.linkTo(parent.end, margin = (-60).dp)
//                    bottom.linkTo(parent.bottom, margin = (-60).dp)
//                }
//
//        )
//    }
//}
//
//
//@Composable
//fun CategoryItemCardWithProgress(
//    modifier: Modifier = Modifier,
//    categoryModel: CategoryWithProgressModel,
//    onClick: () -> Unit
//) {
//    val bgColor = try {
//        categoryModel.category.bgColor?.let { Color(it.toColorInt()) } ?: Color.White
//    } catch (e: Exception) {
//        Color.White
//    }
//
//    Column(
//        modifier = modifier
//            .height(220.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
//            .clickable { onClick() }
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)
//                .background(bgColor)
//                .padding(16.dp)
//        ) {
//
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//                val density = LocalDensity.current
//                val imageSizePx = remember(density) {
//                    with(density) { 208.dp.roundToPx() }
//                }
//
//
//                AsyncImage(
//                    model = ImageRequest.Builder(LocalContext.current)
//                        .data(categoryModel.category.imageUrl)
//                        .size(imageSizePx) // 👈 density-aware downsampling
//                        .scale(Scale.FILL) // matches ContentScale.Crop
//                        .allowHardware(true)
//                        .memoryCachePolicy(CachePolicy.ENABLED)
//                        .diskCachePolicy(CachePolicy.ENABLED)
//                        .build(),
//                    placeholder = painterResource(R.drawable.image_placeholder),
//                    error = painterResource(R.drawable.image_placeholder),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(64.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                        .background(Color.White)
//                        .padding(8.dp)
//
//                )
//
//
//                Text(
//                    text = categoryModel.category.categoryName,
//                    fontSize = 16.sp,
//                    fontFamily = RubikMedium,
//                    maxLines = 2,
//                    lineHeight = 1.3.em,
//                    color = Color.Black,
//                    modifier = Modifier
//                        .padding(top = 8.dp)
//                )
//
//            }
//
//
//            categoryModel.category.tag?.let {
//                Text(
//                    text = categoryModel.category.tag,
//                    fontSize = 11.sp,
//                    fontFamily = RubikRegular,
//                    maxLines = 1,
//                    textAlign = TextAlign.Center,
//                    color = Color.White,
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .clip(RoundedCornerShape(20.dp))
//                        .background(Color(0XFF333333))
//                        .padding(horizontal = 8.dp, vertical = 2.dp)
//                )
//            }
//
//        }
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(text = "Grade ${categoryModel.category.grades.first()}", fontSize = 12.sp)
//                Text(text = "• ${categoryModel.category.levels} levels", fontSize = 12.sp)
//            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                AppLinearProgress(
//                    modifier = Modifier.weight(1f),
//                    progress = (categoryModel.progress.progressPercentage / 100)
//                )
//                Spacer(8.dp)
//                Text(text = "${categoryModel.progress.progressPercentage}%")
//            }
//        }
//    }
//}