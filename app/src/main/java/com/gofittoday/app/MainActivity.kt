package com.gofittoday.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF080A10)
private val Surface = Color(0xFF111522)
private val Surface2 = Color(0xFF191F30)
private val Cyan = Color(0xFF00D9FF)
private val Purple = Color(0xFF9B5CFF)
private val Ink = Color(0xFFF4F6FF)
private val Muted = Color(0xFF929BB0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GoFitTodayApp() }
    }
}

@Composable
private fun GoFitTodayApp() {
    var tab by remember { mutableStateOf("Home") }
    var weight by remember { mutableStateOf("60") }
    var days by remember { mutableStateOf("3") }
    var goal by remember { mutableStateOf("Build strength and muscle") }
    var gym by remember { mutableStateOf("Full gym") }
    var plan by remember { mutableStateOf<List<DayPlan>>(emptyList()) }
    var completedSets by remember { mutableIntStateOf(0) }
    var sessionStarted by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg, surface = Surface, primary = Cyan, secondary = Purple,
        onBackground = Ink, onSurface = Ink
    )) {
        Scaffold(
            containerColor = Bg,
            bottomBar = {
                NavigationBar(containerColor = Surface, contentColor = Ink) {
                    listOf("Home", "Workouts", "Planner", "Muscles", "Settings").forEach { item ->
                        NavigationBarItem(
                            selected = tab == item,
                            onClick = { tab = item },
                            icon = { Text(when(item) { "Home" -> "⌂"; "Workouts" -> "▤"; "Planner" -> "✦"; "Muscles" -> "◈"; else -> "⚙" }, fontSize = 18.sp) },
                            label = { Text(item, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Cyan, selectedTextColor = Cyan, indicatorColor = Surface2, unselectedIconColor = Muted, unselectedTextColor = Muted)
                        )
                    }
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).background(Bg)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("GO FIT TODAY", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text(tab, color = Ink, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Text("● ONLINE", color = Color(0xFF69E6A6), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                when (tab) {
                    "Home" -> HomeScreen(completedSets, plan.size, onOpenPlanner = { tab = "Planner" }, onOpenWorkouts = { tab = "Workouts" })
                    "Workouts" -> WorkoutScreen(plan, completedSets, sessionStarted, onStart = { sessionStarted = true }, onSet = { completedSets++ })
                    "Planner" -> PlannerScreen(weight, { weight = it }, days, { days = it }, goal, { goal = it }, gym, { gym = it }, onGenerate = {
                        plan = makePlan(days.toIntOrNull() ?: 3, gym)
                        tab = "Workouts"
                    })
                    "Muscles" -> MuscleScreen(completedSets)
                    else -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(sets: Int, planCount: Int, onOpenPlanner: () -> Unit, onOpenWorkouts: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            CardBox {
                Text("YOUR NEXT SESSION", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(if (planCount > 0) "Your plan is ready" else "Start with a plan that fits you", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("Build consistency. Progress at your own pace.", color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                Button(onClick = onOpenPlanner, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Bg)) { Text("CREATE WORKOUT PLAN", fontWeight = FontWeight.Bold) }
            }
        }
        item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("SETS LOGGED", sets.toString(), Modifier.weight(1f))
            StatCard("PLAN DAYS", planCount.toString(), Modifier.weight(1f))
        } }
        item {
            CardBox {
                Text("TODAY'S FOCUS", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Train with good technique", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Warm up, use controlled movements, and stop if something hurts. Rest and recovery are part of training.", color = Muted, fontSize = 13.sp)
                TextButton(onClick = onOpenWorkouts) { Text("OPEN WORKOUTS", color = Cyan) }
            }
        }
    }
}

@Composable
private fun PlannerScreen(weight: String, onWeight: (String) -> Unit, days: String, onDays: (String) -> Unit, goal: String, onGoal: (String) -> Unit, gym: String, onGym: (String) -> Unit, onGenerate: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { CardBox {
            Text("PERSONAL WORKOUT BUILDER", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("A plan based on your schedule and equipment.", color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            FieldLabel("Body weight (kg) — optional")
            OutlinedTextField(weight, onWeight, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), placeholder = { Text("e.g. 60") })
            FieldLabel("Training days per week")
            OutlinedTextField(days, onDays, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            FieldLabel("Main goal")
            OptionChips(listOf("Build strength and muscle", "General fitness", "Improve endurance"), goal, onGoal)
            FieldLabel("Available equipment")
            OptionChips(listOf("Full gym", "Dumbbells only", "Bodyweight"), gym, onGym)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onGenerate, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Bg)) { Text("GENERATE MY PLAN", fontWeight = FontWeight.Bold) }
        } }
        item { Text("Training guidance is general, not medical advice. If you're still growing, prioritize technique, manageable effort, recovery and supervision over max lifts or rapid body changes.", color = Muted, fontSize = 12.sp) }
    }
}

@Composable
private fun OptionChips(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected == option, onClick = { onSelect(option) }, colors = RadioButtonDefaults.colors(selectedColor = Cyan))
                Text(option, color = Ink, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun WorkoutScreen(plan: List<DayPlan>, sets: Int, started: Boolean, onStart: () -> Unit, onSet: () -> Unit) {
    if (plan.isEmpty()) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No plan yet", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Open Planner to generate a schedule.", color = Muted)
        }
    } else LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { CardBox {
            Text("YOUR WEEKLY PLAN", color = Cyan, fontWeight = FontWeight.Bold)
            Text("$sets sets logged in this session", color = Muted)
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = if (started) Surface2 else Cyan, contentColor = if (started) Ink else Bg)) { Text(if (started) "SESSION STARTED" else "START SESSION") }
        } }
        items(plan) { day -> CardBox {
            Text(day.name.uppercase(), color = Cyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(day.focus, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            day.exercises.forEach { exercise ->
                Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(exercise, color = Ink, fontSize = 14.sp)
                        Text("2–3 controlled sets · comfortable effort", color = Muted, fontSize = 11.sp)
                    }
                    TextButton(onClick = onSet) { Text("+ SET", color = Cyan) }
                }
            }
        } }
    }
}

@Composable
private fun MuscleScreen(sets: Int) {
    val groups = listOf("Chest" to 0.9f, "Back" to 0.85f, "Shoulders" to 0.7f, "Arms" to 0.65f, "Legs" to 0.8f, "Core" to 0.45f)
    LazyColumn(contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { CardBox {
            Text("MUSCLE ACTIVITY", color = Cyan, fontWeight = FontWeight.Bold)
            Text("Activity overview · not a body-size or strength score", color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text("$sets sets logged", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        } }
        items(groups) { (name, base) ->
            CardBox {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, fontWeight = FontWeight.SemiBold)
                    Text(if (sets == 0) "No data" else "Tracked", color = Cyan, fontSize = 12.sp)
                }
                LinearProgressIndicator(progress = { if (sets == 0) 0f else base }, modifier = Modifier.fillMaxWidth().padding(top = 9.dp), color = if (name in listOf("Chest", "Back")) Cyan else Purple, trackColor = Surface2)
            }
        }
        item { Text("This is a demo activity display, not a measured symmetry assessment. Accurate muscle scores require exercise-by-exercise training history.", color = Muted, fontSize = 12.sp) }
    }
}

@Composable
private fun SettingsScreen() {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CardBox {
            Text("APP SETTINGS", color = Cyan, fontWeight = FontWeight.Bold)
            Text("GoFitToday · English", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Version 1.0.0 · Offline demo build", color = Muted, fontSize = 13.sp)
        }
        CardBox {
            Text("AI COACH", color = Cyan, fontWeight = FontWeight.Bold)
            Text("Gemini API integration is not included in this starter build. No API key is requested or stored.", color = Muted, fontSize = 13.sp)
        }
        CardBox {
            Text("PRIVACY", color = Cyan, fontWeight = FontWeight.Bold)
            Text("This starter app keeps its demo session state in memory. It does not create an account or upload your workout data.", color = Muted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun CardBox(content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp), content = content)
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Cyan, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun FieldLabel(value: String) { Text(value, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }

private data class DayPlan(val name: String, val focus: String, val exercises: List<String>)

private fun makePlan(days: Int, equipment: String): List<DayPlan> {
    val n = days.coerceIn(2, 5)
    val bodyweight = equipment == "Bodyweight"
    val dumbbells = equipment == "Dumbbells only"
    val push = if (bodyweight) listOf("Push-up progression", "Pike push-up progression", "Close-grip push-up progression") else if (dumbbells) listOf("Dumbbell floor press", "Dumbbell shoulder press", "Dumbbell triceps extension") else listOf("Chest press machine", "Seated shoulder press", "Cable triceps pressdown")
    val pull = if (bodyweight) listOf("Inverted row (if safely available)", "Backpack row", "Prone Y-T raises") else if (dumbbells) listOf("One-arm dumbbell row", "Dumbbell reverse fly", "Dumbbell curl") else listOf("Lat pulldown", "Seated cable row", "Dumbbell curl")
    val legs = if (bodyweight) listOf("Bodyweight squat", "Reverse lunge", "Glute bridge") else if (dumbbells) listOf("Goblet squat", "Dumbbell Romanian deadlift", "Split squat") else listOf("Leg press", "Seated leg curl", "Calf raise")
    val templates = listOf(
        DayPlan("Day 1", "Full body A", legs + push.take(2)),
        DayPlan("Day 2", "Full body B", pull + listOf("Dead bug")),
        DayPlan("Day 3", "Full body C", legs.take(2) + push.take(1) + pull.take(1)),
        DayPlan("Day 4", "Full body D", push.take(2) + pull.take(2)),
        DayPlan("Day 5", "Full body E", legs + listOf("Bird dog"))
    )
    return templates.take(n)
}
