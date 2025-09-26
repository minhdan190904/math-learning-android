package com.trilogy.mathlearning.ui.presentation.camera

sealed class ImageOrigin { object CAMERA : ImageOrigin(); object FILE : ImageOrigin() }