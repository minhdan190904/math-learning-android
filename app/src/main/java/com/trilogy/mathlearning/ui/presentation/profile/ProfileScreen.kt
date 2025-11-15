package com.trilogy.mathlearning.ui.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    var darkMode by remember { mutableStateOf(false) }
    var notificationEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrateEnabled by remember { mutableStateOf(false) }
    var emailTipsEnabled by remember { mutableStateOf(true) }
    var useSystemTheme by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Settings, null)
                    }
                }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Tên người dùng",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "email@example.com",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { }) {
                            Text("Sửa")
                        }
                    }
                }
            }

            item {
                SettingsSectionTitle(title = "Tài khoản")
                SettingsNavItem(
                    icon = Icons.Outlined.Person,
                    title = "Thông tin cá nhân",
                    subtitle = "Tên, avatar, thông tin liên hệ",
                    onClick = { }
                )
                SettingsNavItem(
                    icon = Icons.Outlined.Lock,
                    title = "Bảo mật & đăng nhập",
                    subtitle = "Đổi mật khẩu, xác thực, thiết bị đã đăng nhập",
                    onClick = { }
                )
            }

            item {
                SettingsSectionTitle(title = "Thông báo")
                SettingsSwitchItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Thông báo đẩy",
                    subtitle = "Nhận thông báo khi có bài mới, nhắc luyện tập",
                    checked = notificationEnabled,
                    onCheckedChange = { notificationEnabled = it }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.Palette,
                    title = "Âm thanh thông báo",
                    subtitle = "Phát âm thanh khi có thông báo",
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Rung khi thông báo",
                    subtitle = "Rung nhẹ khi có thông báo đến",
                    checked = vibrateEnabled,
                    onCheckedChange = { vibrateEnabled = it }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.Info,
                    title = "Email gợi ý học tập",
                    subtitle = "Nhận mẹo và gợi ý luyện tập qua email",
                    checked = emailTipsEnabled,
                    onCheckedChange = { emailTipsEnabled = it }
                )
            }

            item {
                SettingsSectionTitle(title = "Hiển thị & giao diện")
                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Theo hệ thống",
                    subtitle = "Tự động dùng chế độ sáng/tối theo hệ thống",
                    checked = useSystemTheme,
                    onCheckedChange = { useSystemTheme = it }
                )
                SettingsSwitchItem(
                    icon = Icons.Outlined.Palette,
                    title = "Chế độ tối",
                    subtitle = "Giảm độ chói, phù hợp khi học buổi tối",
                    checked = darkMode,
                    enabled = !useSystemTheme,
                    onCheckedChange = { darkMode = it }
                )
                SettingsNavItem(
                    icon = Icons.Outlined.Language,
                    title = "Ngôn ngữ",
                    subtitle = "Tiếng Việt",
                    onClick = { }
                )
            }

            item {
                SettingsSectionTitle(title = "Quyền riêng tư")
                SettingsNavItem(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Quyền riêng tư",
                    subtitle = "Quản lý dữ liệu, thống kê sử dụng",
                    onClick = { }
                )
                SettingsNavItem(
                    icon = Icons.Outlined.Lock,
                    title = "Điều khoản sử dụng",
                    subtitle = "Điều khoản và quy định khi sử dụng ứng dụng",
                    onClick = { }
                )
            }

            item {
                SettingsSectionTitle(title = "Về ứng dụng")
                SettingsNavItem(
                    icon = Icons.Outlined.Info,
                    title = "Giới thiệu",
                    subtitle = "Phiên bản 1.0.0",
                    onClick = { }
                )
                SettingsNavItem(
                    icon = Icons.Outlined.Settings,
                    title = "Trung tâm trợ giúp",
                    subtitle = "Câu hỏi thường gặp, liên hệ hỗ trợ",
                    onClick = { }
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { },
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Đăng xuất",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Thoát khỏi tài khoản hiện tại",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "MathLearning • 2025",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Divider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        Divider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    }
}
