package com.example.khsapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.khsapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    placeholder: String,
    icon: ImageVector,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isError) MaterialTheme.colorScheme.error else Color.Gray
            )
        },
        isError = isError,
        enabled = enabled,
        readOnly = readOnly,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFF19B5FE),
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.LightGray,
            containerColor = Color.White,
            errorBorderColor = MaterialTheme.colorScheme.error,
            disabledBorderColor = Color.LightGray,
            disabledTextColor = Color.Black,
            disabledLeadingIconColor = Color.Gray
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPasswordField(
    value: String,
    placeholder: String,
    icon: ImageVector,
    isError: Boolean = false,
    onValueChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isError) MaterialTheme.colorScheme.error else Color.Gray
            )
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
//            val image = if (passwordVisible)
//                R.drawable.ic_visibility
//            else
//                R.drawable.ic_visibility_off

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                 // Icon(painter = painterResource(id = image), contentDescription = null)
            }
        },
        isError = isError,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color(0xFF19B5FE),
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.LightGray,
            containerColor = Color.White,
            errorBorderColor = MaterialTheme.colorScheme.error
        )
    )
}
