package com.example.nammahomestay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammahomestay.ui.theme.NammaHomeStayTheme
import com.google.firebase.firestore.FirebaseFirestore

private val Coconut = Color(0xFFFFF8EC)
private val Leaf = Color(0xFF2F6B4F)
private val DeepLeaf = Color(0xFF1F4C39)
private val Turmeric = Color(0xFFF0B429)
private val Clay = Color(0xFFA85D3A)
private val Ink = Color(0xFF24342D)
private val MutedInk = Color(0xFF66736C)
private const val DemoHostId = "demo-host"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NammaHomeStayTheme {
                NammaHomeStayApp()
            }
        }
    }
}

@Composable
private fun NammaHomeStayApp() {
    var loggedIn by remember { mutableStateOf(false) }
    var screen by remember { mutableStateOf("dashboard") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Coconut
    ) {
        if (!loggedIn) {
            LoginScreen(onLogin = {
                loggedIn = true
                screen = "dashboard"
            })
        } else {
            when (screen) {
                "menu" -> DailyMenuScreen { screen = "dashboard" }
                "profile" -> HomeProfileScreen { screen = "dashboard" }
                "inquiry" -> InquiryScreen { screen = "dashboard" }
                "guide" -> LocalGuideScreen { screen = "dashboard" }
                "checklist" -> ChecklistScreen { screen = "dashboard" }
                else -> DashboardScreen(
                    onMenu = { screen = "menu" },
                    onProfile = { screen = "profile" },
                    onInquiry = { screen = "inquiry" },
                    onGuide = { screen = "guide" },
                    onChecklist = { screen = "checklist" },
                    onLogout = { loggedIn = false }
                )
            }
        }
    }
}

@Composable
private fun LoginScreen(onLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(Modifier.height(18.dp)) }
        item { LogoMark() }
        item {
            Text("Namma-HomeStay", color = Ink, fontSize = 31.sp, fontWeight = FontWeight.Bold)
        }
        item {
            Text("Simple host portal for coastal family stays", color = MutedInk, fontSize = 16.sp)
        }
        item { SampleImageCard("farm") }
        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email or phone") },
                singleLine = true
            )
        }
        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                singleLine = true
            )
        }
        item {
            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Leaf),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Login")
            }
        }
        item {
            Text("New host? Create account", color = Leaf, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DashboardScreen(
    onMenu: () -> Unit,
    onProfile: () -> Unit,
    onInquiry: () -> Unit,
    onGuide: () -> Unit,
    onChecklist: () -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Namaskara, Host", color = Ink, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Manage today's food, room details, and traveler messages.", color = MutedInk)
        }
        item { SampleImageCard("farm") }
        item { DashboardCard("Today's Menu", "Dinner: Akki Rotti and Bamboo Shoot Curry", Turmeric, onMenu) }
        item { DashboardCard("My Home Profile", "Room photos, farm view, daily rate", Leaf, onProfile) }
        item { DashboardCard("Inquiry Box", "See traveler messages and call button", Color.White, onInquiry) }
        item { DashboardCard("Local Guide", "Add waterfalls, viewpoints, beaches", Color.White, onGuide) }
        item { DashboardCard("Verification Checklist", "Clean room, toilet, water, family safe", Color.White, onChecklist) }
        item {
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun DashboardCard(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = if (color == Leaf) Color.White else Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = if (color == Leaf) Color.White else MutedInk)
        }
    }
}

@Composable
private fun DailyMenuScreen(onBack: () -> Unit) {
    var breakfast by remember { mutableStateOf("Neer Dosa") }
    var lunch by remember { mutableStateOf("Rice, sambar, vegetable fry") }
    var dinner by remember { mutableStateOf("Akki Rotti") }
    var special by remember { mutableStateOf("Bamboo Shoot Curry") }
    var rate by remember { mutableStateOf("1200") }

    FormScreen("Today's Menu", onBack) {
        SampleImageCard("food")
        OutlinedTextField(breakfast, { breakfast = it }, Modifier.fillMaxWidth(), label = { Text("Breakfast") })
        OutlinedTextField(lunch, { lunch = it }, Modifier.fillMaxWidth(), label = { Text("Lunch") })
        OutlinedTextField(dinner, { dinner = it }, Modifier.fillMaxWidth(), label = { Text("Dinner") })
        OutlinedTextField(special, { special = it }, Modifier.fillMaxWidth(), label = { Text("Special item") })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickButton("Akki Rotti") { special = "Akki Rotti" }
            QuickButton("Fish Curry") { special = "Fish Curry" }
        }
        OutlinedTextField(rate, { rate = it }, Modifier.fillMaxWidth(), label = { Text("Daily rate") })
        PrimaryButton("Save Today's Menu") {
            val data = mapOf(
                "breakfast" to breakfast,
                "lunch" to lunch,
                "dinner" to dinner,
                "specialItem" to special,
                "dailyRate" to rate,
                "updatedAt" to System.currentTimeMillis()
            )
            FirebaseFirestore.getInstance()
                .collection("hosts")
                .document(DemoHostId)
                .collection("todayMenu")
                .document("current")
                .set(data)
            FirebaseFirestore.getInstance()
                .collection("hosts")
                .document(DemoHostId)
                .set(mapOf("dailyRate" to rate, "lastUpdated" to System.currentTimeMillis()), com.google.firebase.firestore.SetOptions.merge())
            onBack()
        }
    }
}

@Composable
private fun HomeProfileScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("Namma Coastal Farm Stay") }
    var village by remember { mutableStateOf("Kumta") }
    var rooms by remember { mutableStateOf("2") }
    var about by remember { mutableStateOf("Family homestay with local food and farm walk.") }

    FormScreen("My Home Profile", onBack) {
        SampleImageCard("room")
        PrimaryButton("Add Room Photo") { }
        OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Homestay name") })
        OutlinedTextField(village, { village = it }, Modifier.fillMaxWidth(), label = { Text("Village") })
        OutlinedTextField(rooms, { rooms = it }, Modifier.fillMaxWidth(), label = { Text("Rooms available") })
        OutlinedTextField(about, { about = it }, Modifier.fillMaxWidth(), label = { Text("About your home") })
        PrimaryButton("Save Home Profile") {
            val data = mapOf(
                "homestayName" to name,
                "village" to village,
                "rooms" to rooms,
                "description" to about,
                "updatedAt" to System.currentTimeMillis()
            )
            FirebaseFirestore.getInstance()
                .collection("hosts")
                .document(DemoHostId)
                .set(data, com.google.firebase.firestore.SetOptions.merge())
            onBack()
        }
    }
}

@Composable
private fun InquiryScreen(onBack: () -> Unit) {
    FormScreen("Inquiry Box", onBack) {
        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Traveler: Ananya", color = Ink, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Is the room available this Saturday? We would like local dinner also.", color = MutedInk)
                Text("Phone: 9876543210", color = Ink)
            }
        }
        PrimaryButton("Call Traveler") { }
    }
}

@Composable
private fun LocalGuideScreen(onBack: () -> Unit) {
    var spot by remember { mutableStateOf("Hidden waterfall") }
    var distance by remember { mutableStateOf("2 km") }
    var desc by remember { mutableStateOf("Best after monsoon, quiet morning walk.") }

    FormScreen("Local Guide", onBack) {
        SampleImageCard("farm")
        OutlinedTextField(spot, { spot = it }, Modifier.fillMaxWidth(), label = { Text("Secret spot name") })
        OutlinedTextField(distance, { distance = it }, Modifier.fillMaxWidth(), label = { Text("Distance") })
        OutlinedTextField(desc, { desc = it }, Modifier.fillMaxWidth(), label = { Text("Description") })
        PrimaryButton("Save Local Spot") {
            val data = mapOf(
                "spotName" to spot,
                "distance" to distance,
                "description" to desc,
                "createdAt" to System.currentTimeMillis()
            )
            FirebaseFirestore.getInstance()
                .collection("hosts")
                .document(DemoHostId)
                .collection("guideSpots")
                .add(data)
            onBack()
        }
    }
}

@Composable
private fun ChecklistScreen(onBack: () -> Unit) {
    val labels = listOf("Room is clean", "Toilet is clean", "Drinking water available", "Family-safe stay", "Local food available", "Emergency contact available")
    var checked by remember { mutableStateOf(setOf(0, 1, 2)) }

    FormScreen("Verification Checklist", onBack) {
        Text("${checked.size}/${labels.size} completed", color = Leaf, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        labels.forEachIndexed { index, label ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checked.contains(index),
                    onCheckedChange = {
                        checked = if (checked.contains(index)) checked - index else checked + index
                    }
                )
                Text(label, color = Ink)
            }
        }
        PrimaryButton("Save Checklist") {
            val data = labels.mapIndexed { index, label ->
                label.replace(" ", "").replace("-", "").replace("safe", "Safe") to checked.contains(index)
            }.toMap() + mapOf("updatedAt" to System.currentTimeMillis())
            FirebaseFirestore.getInstance()
                .collection("hosts")
                .document(DemoHostId)
                .collection("verification")
                .document("checklist")
                .set(data)
            onBack()
        }
    }
}

@Composable
private fun FormScreen(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(title, color = Ink, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Text("Namma-HomeStay", color = MutedInk)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
        }
        item {
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Dashboard")
            }
        }
    }
}

@Composable
private fun RowScope.QuickButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = Turmeric),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, color = Ink)
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Leaf),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}

@Composable
private fun LogoMark() {
    Canvas(
        modifier = Modifier
            .size(108.dp)
            .background(Coconut)
    ) {
        drawHouse(size.width, size.height)
    }
}

@Composable
private fun SampleImageCard(kind: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.photo_room),
            contentDescription = "$kind photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

private fun DrawScope.drawHouse(w: Float, h: Float) {
    val roof = Path().apply {
        moveTo(w * 0.18f, h * 0.56f)
        lineTo(w * 0.5f, h * 0.22f)
        lineTo(w * 0.82f, h * 0.56f)
        close()
    }
    drawPath(roof, DeepLeaf)
    drawRect(Leaf, topLeft = Offset(w * 0.24f, h * 0.52f), size = Size(w * 0.52f, h * 0.34f))
    drawRect(Clay, topLeft = Offset(w * 0.43f, h * 0.66f), size = Size(w * 0.14f, h * 0.2f))
    drawRect(Color.White, topLeft = Offset(w * 0.29f, h * 0.61f), size = Size(w * 0.12f, h * 0.1f))
    drawRect(Color.White, topLeft = Offset(w * 0.59f, h * 0.61f), size = Size(w * 0.12f, h * 0.1f))
    drawCircle(Turmeric, radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.43f))
}

private fun DrawScope.drawFarmScene() {
    drawRect(Color(0xFFBFE1F2), size = size)
    drawCircle(Turmeric, radius = 24.dp.toPx(), center = Offset(size.width * 0.82f, size.height * 0.22f))
    drawOval(Leaf, topLeft = Offset(-40f, size.height * 0.48f), size = Size(size.width * 1.3f, size.height * 0.75f))
    drawTriangle(DeepLeaf, Offset(size.width * 0.16f, size.height * 0.75f), 55f)
    drawTriangle(DeepLeaf, Offset(size.width * 0.31f, size.height * 0.72f), 70f)
    drawTriangle(DeepLeaf, Offset(size.width * 0.68f, size.height * 0.76f), 62f)
    drawRect(Color(0xFFFFF8EC), topLeft = Offset(size.width * 0.42f, size.height * 0.58f), size = Size(size.width * 0.18f, size.height * 0.22f))
    val roof = Path().apply {
        moveTo(size.width * 0.38f, size.height * 0.6f)
        lineTo(size.width * 0.51f, size.height * 0.42f)
        lineTo(size.width * 0.64f, size.height * 0.6f)
        close()
    }
    drawPath(roof, Clay)
}

private fun DrawScope.drawFoodScene() {
    drawRect(Coconut, size = size)
    drawRoundRect(Leaf, topLeft = Offset(size.width * 0.1f, size.height * 0.18f), size = Size(size.width * 0.8f, size.height * 0.62f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f))
    drawOval(Turmeric, topLeft = Offset(size.width * 0.22f, size.height * 0.36f), size = Size(size.width * 0.22f, size.height * 0.2f))
    drawOval(Color.White, topLeft = Offset(size.width * 0.34f, size.height * 0.48f), size = Size(size.width * 0.22f, size.height * 0.18f))
    drawCircle(Clay, radius = size.height * 0.16f, center = Offset(size.width * 0.66f, size.height * 0.45f))
    drawRect(Color(0xFFFFE4C2), topLeft = Offset(size.width * 0.61f, size.height * 0.38f), size = Size(size.width * 0.12f, size.height * 0.14f))
}

private fun DrawScope.drawRoomScene() {
    drawRect(Color(0xFFF6E6D1), size = size)
    drawRect(Color(0xFFB9855A), topLeft = Offset(0f, size.height * 0.68f), size = Size(size.width, size.height * 0.32f))
    drawRect(Color.White, topLeft = Offset(size.width * 0.1f, size.height * 0.38f), size = Size(size.width * 0.42f, size.height * 0.24f))
    drawRect(Leaf, topLeft = Offset(size.width * 0.1f, size.height * 0.32f), size = Size(size.width * 0.42f, size.height * 0.08f))
    drawRect(Coconut, topLeft = Offset(size.width * 0.62f, size.height * 0.2f), size = Size(size.width * 0.23f, size.height * 0.4f))
    drawRect(Leaf, topLeft = Offset(size.width * 0.65f, size.height * 0.28f), size = Size(size.width * 0.16f, size.height * 0.25f))
}

private fun DrawScope.drawTriangle(color: Color, baseCenter: Offset, height: Float) {
    val path = Path().apply {
        moveTo(baseCenter.x - height * 0.45f, baseCenter.y)
        lineTo(baseCenter.x, baseCenter.y - height)
        lineTo(baseCenter.x + height * 0.45f, baseCenter.y)
        close()
    }
    drawPath(path, color)
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    NammaHomeStayTheme {
        NammaHomeStayApp()
    }
}
