package in.kshatriya.app

import android.os.Bundle
import android.os.SystemClock
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.delay
import java.util.Locale
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import android.util.Base64
import java.security.SecureRandom

private val Olive = Color(0xFF283629)
private val Deep = Color(0xFF171C16)
private val Gold = Color(0xFFD5B56B)
private val Card = Color(0xFF303A30)
private val Muted = Color(0xFFB8C0B4)

class MainActivity : FragmentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        setContent { KshatriyaApp(onSpeak = { speak(it) }, onBiometric = { callback -> authenticateBiometric(callback) }) }
    }
    private fun authenticateBiometric(callback: (Boolean) -> Unit) {
        val manager = BiometricManager.from(this)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        if (manager.canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            callback(false); return
        }
        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { callback(true) }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) { callback(false) }
            override fun onAuthenticationFailed() { }
        })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock KSHATRIYA")
            .setSubtitle("Use biometrics or your device screen lock")
            .setAllowedAuthenticators(authenticators)
            .build()
        prompt.authenticate(info)
    }
    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts?.language = Locale.UK }
    private fun speak(text: String) { tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ustaad") }
    override fun onDestroy() { tts?.stop(); tts?.shutdown(); super.onDestroy() }
}

data class Exercise(
    val name: String, val category: String, val muscle: String, val seconds: Int,
    val reps: String, val cue: String, val breathing: String, val mistake: String,
    val easier: String, val level: String = "All"
)

private val exerciseLibrary = listOf(
    Exercise("March in Place", "Cardio & Endurance", "Full body", 45, "45 sec", "Stand tall and march with controlled arm swings.", "Breathe steadily; do not hold your breath.", "Leaning back or stamping heavily.", "March more slowly and reduce knee lift."),
    Exercise("Low-impact Step Jacks", "Cardio & Endurance", "Full body", 40, "40 sec", "Step one foot out at a time while raising arms.", "Exhale as arms rise; inhale as they lower.", "Bouncing or rushing the steps.", "Keep arms below shoulder height."),
    Exercise("Jumping Jacks", "Cardio & Endurance", "Full body", 30, "30 sec", "Jump feet apart as arms rise, then return smoothly.", "Keep breathing rhythmic.", "Landing stiff-legged or knees collapsing inward.", "Use step jacks instead."),
    Exercise("High-knee March", "Cardio & Endurance", "Hip flexors", 40, "40 sec", "Lift alternating knees to a comfortable height.", "Exhale gently with each lift.", "Leaning backward to lift the knee.", "Reduce the knee height."),
    Exercise("Butt Kicks", "Cardio & Endurance", "Hamstrings", 30, "30 sec", "Jog gently, bringing heels toward the glutes.", "Breathe continuously.", "Overstriding or landing heavily.", "Walk and alternate heel curls."),
    Exercise("Mountain Climbers", "Cardio & Endurance", "Core / shoulders", 30, "30 sec", "From a strong plank, drive knees forward alternately.", "Exhale on each knee drive.", "Hips bouncing or shoulders behind wrists.", "Elevate hands on a stable bench."),
    Exercise("Fast Feet", "Cardio & Endurance", "Calves", 25, "25 sec", "Take quick, short steps on the spot.", "Use short, steady breaths.", "Stiff knees and heavy foot strikes.", "Slow to a brisk march."),
    Exercise("Shadow Boxing", "Cardio & Endurance", "Shoulders / core", 45, "45 sec", "Punch forward in the air with relaxed shoulders.", "Exhale lightly with each punch.", "Locking elbows or twisting knees.", "Use slow alternating punches."),
    Exercise("Bodyweight Squat", "Strength", "Quadriceps / glutes", 40, "10–15 reps", "Sit hips back and lower only as far as comfortable; stand tall.", "Inhale down; exhale up.", "Knees collapsing inward or heels lifting.", "Use a chair-assisted squat."),
    Exercise("Chair Sit-to-Stand", "Strength", "Quadriceps / glutes", 40, "8–12 reps", "Stand from a stable chair, then sit with control.", "Exhale while standing.", "Dropping onto the chair.", "Use a higher seat and hand support."),
    Exercise("Reverse Lunge", "Strength", "Legs / glutes", 35, "8 each side", "Step back and lower gently; keep front knee aligned.", "Inhale down; exhale up.", "Front knee falling inward or losing balance.", "Hold a support and shorten the step."),
    Exercise("Wall Push-up", "Strength", "Chest / triceps", 40, "10–15 reps", "Hands on wall; bend elbows and press away.", "Inhale toward wall; exhale away.", "Sagging hips or flared elbows.", "Stand closer to the wall."),
    Exercise("Knee Push-up", "Strength", "Chest / triceps", 35, "6–12 reps", "Keep a straight line from knees to head as you lower.", "Inhale down; exhale up.", "Hips folding or elbows flaring wide.", "Use wall push-ups."),
    Exercise("Push-up", "Strength", "Chest / triceps", 30, "5–12 reps", "Keep body aligned and lower with control.", "Inhale down; exhale up.", "Lower back sagging or partial rushed reps.", "Use knee or wall push-ups."),
    Exercise("Glute Bridge", "Strength", "Glutes / hamstrings", 40, "12–15 reps", "Lie on back, knees bent; raise hips without arching back.", "Exhale as hips rise.", "Pushing through the lower back.", "Lift hips only a short distance."),
    Exercise("Calf Raise", "Strength", "Calves", 35, "15–20 reps", "Rise onto the balls of both feet, then lower slowly.", "Breathe naturally.", "Bouncing or rolling ankles outward.", "Hold a wall for balance."),
    Exercise("Bird Dog", "Core", "Core / back", 40, "8 each side", "On hands and knees, extend opposite arm and leg.", "Exhale as you extend.", "Rotating hips or arching the back.", "Move one limb at a time."),
    Exercise("Dead Bug", "Core", "Deep core", 40, "8 each side", "On back, lower opposite arm and leg while keeping back controlled.", "Exhale as limbs extend.", "Lower back lifting away from the floor.", "Move only the legs or arms."),
    Exercise("Forearm Plank", "Core", "Abdominals", 30, "20–40 sec", "Keep elbows under shoulders and body in a straight line.", "Breathe slowly; do not hold breath.", "Hips sagging or rising too high.", "Perform plank from knees."),
    Exercise("Side Plank", "Core", "Obliques", 25, "15–30 sec each", "Support on forearm and keep hips lifted.", "Breathe steadily.", "Shoulder collapsing or hips rotating forward.", "Bend knees and keep them on floor."),
    Exercise("Standing Knee-to-Elbow", "Core", "Obliques", 35, "10 each side", "Bring opposite elbow and knee together with control.", "Exhale as they meet.", "Pulling the neck or rushing.", "Use a smaller range of motion."),
    Exercise("Heel Taps", "Core", "Abdominals", 35, "12 each side", "Lie on back with knees bent; reach toward each heel.", "Exhale on each reach.", "Pulling the head forward.", "Keep head resting on the floor."),
    Exercise("Glute Bridge March", "Core", "Glutes / core", 30, "8 each side", "Hold bridge and lift one foot a little at a time.", "Keep breathing steadily.", "Allowing hips to tilt or drop.", "Do a regular glute bridge."),
    Exercise("Cat-Cow", "Flexibility & Mobility", "Spine", 40, "8 slow cycles", "On hands and knees, gently round and extend the spine.", "Inhale to open; exhale to round.", "Forcing the neck or lower back.", "Use a smaller comfortable movement."),
    Exercise("Standing Hamstring Stretch", "Flexibility & Mobility", "Hamstrings", 30, "20 sec each side", "Place one heel forward and hinge gently at hips.", "Breathe slowly.", "Rounding back or bouncing.", "Keep knee slightly bent."),
    Exercise("Hip Flexor Stretch", "Flexibility & Mobility", "Hip flexors", 30, "20 sec each side", "Take a short split stance and gently shift hips forward.", "Breathe slowly; relax shoulders.", "Arching lower back.", "Reduce the forward shift."),
    Exercise("Calf Wall Stretch", "Flexibility & Mobility", "Calves", 30, "20 sec each side", "Hands on wall, step one foot back and press heel down.", "Breathe steadily.", "Bending the rear knee too much.", "Stand closer to the wall."),
    Exercise("Shoulder Circles", "Flexibility & Mobility", "Shoulders", 30, "10 each direction", "Circle shoulders slowly without forcing range.", "Breathe naturally.", "Shrugging forcefully.", "Make smaller circles."),
    Exercise("Thoracic Rotation", "Flexibility & Mobility", "Upper back", 35, "8 each side", "On all fours, rotate one arm upward while following with eyes.", "Exhale as you rotate.", "Twisting through lower back.", "Keep hand on chest and rotate gently."),
    Exercise("Ankle Mobility Rocks", "Flexibility & Mobility", "Ankles", 30, "10 each side", "With foot flat, gently move knee forward over toes.", "Breathe normally.", "Heel lifting or forcing the joint.", "Reduce forward movement."),
    Exercise("Child's Pose", "Recovery & Conditioning", "Back / hips", 40, "30–40 sec", "Sit hips toward heels and reach arms forward comfortably.", "Slow, relaxed breathing.", "Forcing hips down despite discomfort.", "Place a cushion under hips."),
    Exercise("Supine Knee Rolls", "Recovery & Conditioning", "Lower back", 35, "8 each side", "On back with knees bent, roll knees gently side to side.", "Breathe slowly.", "Forcing knees to the floor.", "Use a smaller range."),
    Exercise("Box Breathing", "Recovery & Conditioning", "Breathing", 60, "4 calm cycles", "Sit comfortably and breathe gently in a steady rhythm.", "Never strain or hold breath uncomfortably.", "Taking excessively deep breaths.", "Use natural slow breathing."),
    Exercise("Easy Walk", "Recovery & Conditioning", "Full body", 120, "2 minutes", "Walk at an easy pace to bring effort down.", "Breathe comfortably.", "Stopping abruptly after hard effort.", "Walk more slowly."),
    Exercise("Standing Quad Stretch", "Flexibility & Mobility", "Quadriceps", 30, "20 sec each side", "Hold ankle if comfortable and keep knees close.", "Breathe slowly.", "Arching back or pulling hard.", "Use a wall for balance and a smaller stretch."),
    Exercise("Wall Sit", "Strength", "Quadriceps", 30, "15–30 sec", "Slide down wall to a comfortable partial squat.", "Breathe continuously.", "Going too low or knees collapsing inward.", "Use a higher position."),
    Exercise("Step-up", "Strength", "Legs / glutes", 40, "8 each side", "Step onto a stable low step and descend with control.", "Exhale stepping up.", "Knee collapsing inward or pushing off excessively.", "Use a lower step and hand support."),
    Exercise("Superman Hold", "Core", "Back extensors", 20, "10–20 sec", "Lie face down and lift arms and legs slightly.", "Breathe normally.", "Overarching the lower back or lifting too high.", "Lift one arm or leg at a time."),
    Exercise("Standing Side Bend", "Flexibility & Mobility", "Side body", 30, "8 each side", "Reach one arm overhead and lean gently to the opposite side.", "Exhale into the stretch.", "Rotating torso or forcing the bend.", "Keep hand on hip and reduce range."),
    Exercise("Marching Glute Bridge", "Strength", "Glutes / core", 30, "6 each side", "In bridge position, alternate small foot lifts while hips stay level.", "Breathe steadily.", "Pelvis dropping or twisting.", "Return to standard bridge."),
    Exercise("Incline Push-up", "Strength", "Chest / triceps", 35, "8–12 reps", "Hands on a secure raised surface; lower chest toward it.", "Inhale down; exhale up.", "Using an unstable surface.", "Use a wall push-up."),
    Exercise("Squat to Calf Raise", "Strength", "Legs / calves", 35, "10–12 reps", "Stand from a squat and rise onto toes smoothly.", "Exhale as you rise.", "Rushing or knees collapsing inward.", "Use a shallow squat and omit calf raise."),
    Exercise("Lateral Step", "Cardio & Endurance", "Legs", 40, "40 sec", "Step side to side with soft knees.", "Breathe rhythmically.", "Feet crossing or abrupt steps.", "Take smaller steps."),
    Exercise("Skater Step", "Cardio & Endurance", "Glutes / legs", 30, "30 sec", "Step diagonally side to side, reaching opposite hand across.", "Breathe steadily.", "Overreaching or losing balance.", "Keep steps short and upright."),
    Exercise("Arm Circles", "Flexibility & Mobility", "Shoulders", 30, "15 each way", "Raise arms and make controlled small circles.", "Breathe naturally.", "Shrugging or forcing shoulder range.", "Keep arms lower."),
    Exercise("Marching Bridge", "Core", "Glutes / core", 25, "6 each side", "Maintain a stable bridge and alternate small knee lifts.", "Exhale on each lift.", "Hips rocking side to side.", "Hold a regular bridge."),
    Exercise("Wall Angel", "Flexibility & Mobility", "Shoulders / upper back", 40, "8–10 reps", "Stand against wall and slide arms up and down within comfort.", "Breathe steadily.", "Forcing wrists or back against wall.", "Use a smaller arm range."),
    Exercise("Heel-to-Toe Walk", "Recovery & Conditioning", "Balance", 40, "10 steps", "Walk slowly placing heel close to opposite toes.", "Breathe naturally.", "Rushing or looking down continuously.", "Walk beside a wall for support."),
    Exercise("Seated Knee Extension", "Strength", "Quadriceps", 35, "10 each side", "Sit tall and straighten one knee, then lower slowly.", "Exhale as knee straightens.", "Swinging leg or locking knee forcefully.", "Use a smaller range."),
    Exercise("Standing Hip Abduction", "Strength", "Hip abductors", 35, "10 each side", "Stand tall and move one leg sideways without leaning.", "Breathe normally.", "Tilting torso or turning toes outward.", "Hold support and move a shorter distance.")
)

@Composable
fun KshatriyaApp(onSpeak: (String) -> Unit, onBiometric: (((Boolean) -> Unit) -> Unit)) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { encryptedPreferences(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var tab by remember { mutableStateOf("Home") }
    var profileComplete by remember { mutableStateOf(prefs.getBoolean("profile_complete", false)) }
    var securityStage by remember {
        mutableStateOf(when {
            !prefs.getBoolean("developer_activated", false) -> "developer"
            prefs.getString("personal_pin_hash", null) == null -> "setup_pin"
            else -> "lock"
        })
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) prefs.edit().putLong("background_at", SystemClock.elapsedRealtime()).apply()
            if (event == Lifecycle.Event.ON_START) {
                val backgroundAt = prefs.getLong("background_at", 0L)
                if (backgroundAt > 0L && SystemClock.elapsedRealtime() - backgroundAt >= 120_000L && prefs.getBoolean("developer_activated", false)) {
                    securityStage = "lock"
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    var completed by remember { mutableIntStateOf(prefs.getInt("completed", 0)) }
    var secondsLeft by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var started by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    var elapsed by remember { mutableIntStateOf(0) }
    var history by remember { mutableStateOf(prefs.getString("history", "No sessions completed yet") ?: "No sessions completed yet") }
    if (securityStage == "developer") {
        DeveloperActivationScreen(onActivate = { pin ->
            val configured = BuildConfig.DEVELOPER_PIN_VERIFIER
            if (configured.isBlank()) "Activation is not configured in this build. Contact the developer."
            else if (verifyConfiguredDeveloperPin(pin, configured)) {
                prefs.edit().putBoolean("developer_activated", true).apply()
                securityStage = "setup_pin"
                ""
            } else "Incorrect Developer PIN. Try again or contact the developer."
        })
        return
    }
    if (securityStage == "setup_pin") {
        PersonalPinScreen(title = "CREATE PERSONAL PIN", button = "SET PERSONAL PIN", onSubmit = { pin ->
            if (pin.length != 6 || !pin.all(Char::isDigit)) "Enter a six-digit PIN."
            else {
                val salt = newPinSalt()
                prefs.edit().putString("personal_pin_salt", salt)
                    .putString("personal_pin_hash", hashPersonalPin(pin, salt))
                    .putBoolean("pin_unlocked", true).apply()
                securityStage = "welcome"
                ""
            }
        })
        return
    }
    if (securityStage == "lock") {
        PersonalPinScreen(title = "KSHATRIYA LOCKED", button = "UNLOCK", biometricEnabled = prefs.getBoolean("biometric_enabled", false), onBiometric = {
            onBiometric { success -> if (success) securityStage = "welcome" }
        }, onSubmit = { pin ->
            val salt = prefs.getString("personal_pin_salt", "") ?: ""
            val expected = prefs.getString("personal_pin_hash", "") ?: ""
            if (verifyPersonalPin(pin, salt, expected)) { securityStage = "welcome"; "" }
            else "Incorrect PIN. Please try again."
        })
        return
    }
    if (securityStage == "welcome") {
        WelcomeSecurityScreen(onContinue = { securityStage = if (profileComplete) "ready" else "ready" })
        return
    }
    if (!profileComplete) {
        RegistrationScreen(onSave = { values ->
            values.forEach { (key, value) -> prefs.edit().putString(key, value).apply() }
            prefs.edit().putBoolean("profile_complete", true).apply()
            profileComplete = true
            onSpeak("Jai Hind! Registration complete. Welcome to KSHATRIYA.")
        })
        return
    }
    val level = prefs.getString("level", "Beginner") ?: "Beginner"
    val pathway = prefs.getString("pathway", "Generalised Fitness") ?: "Generalised Fitness"
    val durationChoice = prefs.getString("session_duration", "30 minutes") ?: "30 minutes"
    val targetMinutes = durationChoice.substringBefore(" ").toIntOrNull() ?: 30
    val pathwayCategories = if (pathway == "Weight Management") listOf("Cardio & Endurance", "Strength", "Core", "Recovery & Conditioning") else listOf("Cardio & Endurance", "Strength", "Core", "Flexibility & Mobility", "Recovery & Conditioning")
    val exercises = remember(level, pathway, targetMinutes) {
        val eligible = exerciseLibrary.filter { it.category in pathwayCategories }
        val perExercise = when(level) { "Advanced" -> 45; "Intermediate" -> 35; else -> 30 }
        val count = (targetMinutes * 60 / (perExercise + 15)).coerceIn(4, 18)
        val selected = pathwayCategories.flatMap { category -> eligible.filter { it.category == category }.take(2) }
        (selected + eligible).distinctBy { it.name }.take(count).map { it.copy(seconds = perExercise) }
    }
    var libraryCategory by remember { mutableStateOf("All") }
    var showLibrary by remember { mutableStateOf(false) }
    LaunchedEffect(running, secondsLeft) {
        if (running && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
            elapsed += 1
            if (secondsLeft == 0) {
                running = false
                onSpeak("Exercise complete. Move to the next exercise when ready.")
            }
        }
    }
    MaterialTheme(colorScheme = darkColorScheme(primary = Gold, background = Deep, surface = Card)) {
        Scaffold(containerColor = Deep, bottomBar = {
            NavigationBar(containerColor = Deep, contentColor = Gold) {
                listOf("Home", "Workout", "Progress", "Profile").forEach { label ->
                    NavigationBarItem(selected = tab == label, onClick = { tab = label }, icon = { Text(when(label) { "Home" -> "⌂"; "Workout" -> "▶"; "Progress" -> "▥"; else -> "●" }, fontSize = 19.sp) }, label = { Text(label) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold, selectedTextColor = Gold, indicatorColor = Card, unselectedTextColor = Muted))
                }
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().background(Deep).padding(padding).padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("KSHATRIYA", color = Gold, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Text("BUILD STRENGTH. STAY MISSION READY.", color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
                    }
                    Text("● OFFLINE", color = Color(0xFF9BC58A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(22.dp))
                when (tab) {
                    "Home" -> {
                        Text("Jai Hind, Warrior", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        Text("Your fitness journey starts here.", color = Muted)
                        Spacer(Modifier.height(18.dp))
                        Panel("MY FITNESS SCORE", "— / 100", "Complete assessed workouts to build your score.")
                        Spacer(Modifier.height(12.dp))
                        Panel("TODAY'S WORKOUT", "${exercises.size} exercises", "Personalised session • ${durationChoice} • ${level}")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { tab = "Workout" }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Deep)) { Text("START WORKOUT", fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.height(18.dp))
                        Text("TRAINING MODULES", color = Gold, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Panel("FITQUEST", "Weight Management  •  Generalised Fitness", "Personalised training pathways")
                        Spacer(Modifier.height(8.dp))
                        Panel("SERVICE FITNESS TESTS", "CPT / BPET / PPT", "Standards will be added only from verified official charts.")
                    }
                    "Workout" -> {
                        Text("WORKOUT CENTRE", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = !showLibrary, onClick = { showLibrary = false }, label = { Text("MY SESSION") })
                            FilterChip(selected = showLibrary, onClick = { showLibrary = true }, label = { Text("EXERCISE LIBRARY") })
                        }
                        if (showLibrary) {
                            Text("50-EXERCISE LIBRARY", color = Gold, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            ChoiceField("Category", listOf("All", "Cardio & Endurance", "Strength", "Core", "Flexibility & Mobility", "Recovery & Conditioning"), libraryCategory) { libraryCategory = it }
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(exerciseLibrary.filter { libraryCategory == "All" || it.category == libraryCategory }) { ex ->
                                    Column(Modifier.fillMaxWidth().background(Card, RoundedCornerShape(12.dp)).padding(12.dp)) {
                                        Text(ex.name, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("${ex.category} • ${ex.muscle} • ${ex.reps}", color = Gold, fontSize = 11.sp)
                                        Text(ex.cue, color = Muted, fontSize = 12.sp)
                                        Text("Breathing: ${ex.breathing}", color = Muted, fontSize = 11.sp)
                                        Text("Avoid: ${ex.mistake}", color = Muted, fontSize = 11.sp)
                                        Text("Easier option: ${ex.easier}", color = Muted, fontSize = 11.sp)
                                    }
                                }
                            }
                        } else {
                            Panel("PERSONALISED SESSION", "${exercises.size} exercises • $durationChoice", "$pathway • $level • Fully offline")
                            Spacer(Modifier.height(10.dp))
                            Panel("EXERCISE ${index + 1} OF ${exercises.size}", exercises[index].name, "${exercises[index].category} • ${exercises[index].muscle}\n${exercises[index].cue}")
                            Spacer(Modifier.height(12.dp))
                            Text(if (started) "%02d:%02d".format(secondsLeft / 60, secondsLeft % 60) else "%02d:%02d".format(exercises[index].seconds / 60, exercises[index].seconds % 60), color = Gold, fontSize = 52.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(if (running) "IN PROGRESS" else if (started) "PAUSED / READY" else "READY TO BEGIN", color = Muted, modifier = Modifier.align(Alignment.CenterHorizontally))
                            Spacer(Modifier.height(8.dp))
                            Text("Target: ${exercises[index].reps}", color = Gold, fontWeight = FontWeight.Bold)
                            Text("Breathing: ${exercises[index].breathing}", color = Muted, fontSize = 12.sp)
                            Text("Common mistake: ${exercises[index].mistake}", color = Muted, fontSize = 12.sp)
                            Text("Easier option: ${exercises[index].easier}", color = Muted, fontSize = 12.sp)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                if (!started) { started = true; secondsLeft = exercises[index].seconds; running = true; onSpeak("Ustaad. ${exercises[index].name}. ${exercises[index].cue} ${exercises[index].breathing}") }
                                else { running = !running; if (running) onSpeak("Resume. ${exercises[index].cue}") }
                            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Deep)) { Text(if (!started) "START WORKOUT" else if (running) "PAUSE" else "RESUME", fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = {
                                    running = false
                                    if (index < exercises.lastIndex) {
                                        index++; secondsLeft = exercises[index].seconds; started = true
                                        onSpeak("Skipped. Next exercise: ${exercises[index].name}. ${exercises[index].cue}")
                                    } else {
                                        completed++; prefs.edit().putInt("completed", completed).apply()
                                        history = "Session completed with final exercise skipped • ${elapsed / 60} min ${elapsed % 60} sec"
                                        prefs.edit().putString("history", history).apply()
                                        index = 0; started = false; secondsLeft = 0; elapsed = 0
                                        onSpeak("Session ended. Your progress has been saved.")
                                    }
                                }, modifier = Modifier.weight(1f)) { Text("SKIP") }
                                OutlinedButton(onClick = { running = false; if (index < exercises.lastIndex) { index++; secondsLeft = exercises[index].seconds; started = true; onSpeak("Next exercise. ${exercises[index].name}. ${exercises[index].cue}") } else { completed++; prefs.edit().putInt("completed", completed).apply(); history = "Session completed • ${elapsed / 60} min ${elapsed % 60} sec • ${exercises.size} exercises"; prefs.edit().putString("history", history).apply(); index = 0; started = false; secondsLeft = 0; elapsed = 0; onSpeak("Workout complete. Well done. Your session has been saved.") } }, modifier = Modifier.weight(1f)) { Text(if (index == exercises.lastIndex) "FINISH" else "NEXT") }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("SESSION PLAN", color = Gold, fontWeight = FontWeight.Bold)
                            LazyColumn { items(exercises) { ex -> Text("${if (ex == exercises[index]) "▶" else "○"}  ${ex.name} — ${ex.reps}", color = if (ex == exercises[index]) Gold else Muted, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) } }
                        }
                    }
                    "Progress" -> {
                        Text("MY PROGRESS", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(14.dp))
                        Panel("COMPLETED SESSIONS", completed.toString(), "Saved on this device")
                        Spacer(Modifier.height(10.dp))
                        Panel("LATEST SESSION", history, "Workout history")
                        Spacer(Modifier.height(10.dp))
                        Text("Fitness scores and rank promotions will be activated after the agreed assessment formula and verified test standards are implemented.", color = Muted)
                    }
                    else -> {
                        Text("PROFILE & SETTINGS", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Panel("PERSONAL PROFILE", prefs.getString("full_name", "Warrior") ?: "Warrior", "${prefs.getString("service", "Service not selected")} • ${prefs.getString("pathway", "Pathway not selected")} • ${prefs.getString("level", "Level not selected")}")
                        Spacer(Modifier.height(10.dp))
                        Panel("BODY METRICS", "BMI ${bmiText(prefs.getString("height_cm", "") ?: "", prefs.getString("weight_kg", "") ?: "")}", "Age ${prefs.getString("age", "—")} • ${prefs.getString("gender", "—")} • Height ${prefs.getString("height_cm", "—")} cm • Weight ${prefs.getString("weight_kg", "—")} kg")
                        Spacer(Modifier.height(10.dp))
                        Panel("PRIVACY", "Local device storage", "This milestone does not transmit data to a server.")
                        Spacer(Modifier.height(10.dp))
                        Panel("USTAAD", "Voice guidance", "Uses the Android device text-to-speech engine.")
                        Spacer(Modifier.height(10.dp))
                        var biometricEnabled by remember { mutableStateOf(prefs.getBoolean("biometric_enabled", false)) }
                        OutlinedButton(onClick = {
                            onBiometric { success ->
                                if (success) {
                                    biometricEnabled = !biometricEnabled
                                    prefs.edit().putBoolean("biometric_enabled", biometricEnabled).apply()
                                }
                            }
                        }, modifier = Modifier.fillMaxWidth()) { Text(if (biometricEnabled) "DISABLE BIOMETRIC UNLOCK" else "ENABLE BIOMETRIC UNLOCK") }
                        Text("Automatic lock activates after two minutes in the background. Personal PIN remains available as a fallback.", color = Muted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeveloperActivationScreen(onActivate: (String) -> String) {
    var pin by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    SecurityFrame(title = "DEVELOPER ACTIVATION", subtitle = "KSHATRIYA is currently in testing. Enter the common six-digit Developer PIN to activate this installation.") {
        OutlinedTextField(value = pin, onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) pin = it }, label = { Text("Six-digit Developer PIN") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), modifier = Modifier.fillMaxWidth(), colors = securityFieldColors())
        if (message.isNotBlank()) Text(message, color = Color(0xFFFF8A80), fontSize = 12.sp)
        Button(onClick = { message = if (pin.length != 6) "Enter all six digits." else onActivate(pin); if (message.isBlank()) pin = "" }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Deep)) { Text("ACTIVATE", fontWeight = FontWeight.Bold) }
        Text("Developer: Maj Sushant Mandal • 14 AR • 2LS", color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun PersonalPinScreen(title: String, button: String, biometricEnabled: Boolean = false, onBiometric: () -> Unit = {}, onSubmit: (String) -> String) {
    var pin by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    SecurityFrame(title = title, subtitle = if (title == "CREATE PERSONAL PIN") "Create your own six-digit PIN. This is separate from the Developer PIN." else "Enter your personal six-digit PIN to continue.") {
        OutlinedTextField(value = pin, onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) pin = it }, label = { Text("Personal six-digit PIN") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), modifier = Modifier.fillMaxWidth(), colors = securityFieldColors())
        if (message.isNotBlank()) Text(message, color = Color(0xFFFF8A80), fontSize = 12.sp)
        Button(onClick = { message = if (pin.length != 6) "Enter all six digits." else onSubmit(pin); if (message.isBlank()) pin = "" }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Deep)) { Text(button, fontWeight = FontWeight.Bold) }
        if (title == "KSHATRIYA LOCKED" && biometricEnabled) {
            OutlinedButton(onClick = onBiometric, modifier = Modifier.fillMaxWidth()) { Text("UNLOCK WITH BIOMETRICS / DEVICE LOCK") }
        }
    }
}

@Composable
private fun WelcomeSecurityScreen(onContinue: () -> Unit) {
    SecurityFrame(title = "JAI HIND!", subtitle = "I am Ustaad, your personal fitness instructor. Welcome to KSHATRIYA. Build strength. Improve fitness. Stay mission ready.") {
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Deep)) { Text("CONTINUE", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun SecurityFrame(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(primary = Gold, background = Deep, surface = Card)) {
        Column(Modifier.fillMaxSize().background(Deep).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("KSHATRIYA", color = Gold, fontSize = 29.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(28.dp))
            Text(title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(subtitle, color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(24.dp))
            Column(verticalArrangement = Arrangement.spacedBy(14.dp), content = content)
        }
    }
}

@Composable
private fun securityFieldColors() = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Gold, unfocusedBorderColor = Muted, focusedLabelColor = Gold, unfocusedLabelColor = Muted)

private fun encryptedPreferences(context: android.content.Context): android.content.SharedPreferences {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    val secure = EncryptedSharedPreferences.create(
        context,
        "kshatriya_secure",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    // One-time migration from the earlier prototype's plain preferences.
    val legacy = context.getSharedPreferences("kshatriya_local", android.content.Context.MODE_PRIVATE)
    if (!secure.getBoolean("secure_migration_complete", false)) {
        val editor = secure.edit()
        legacy.all.forEach { (key, value) ->
            when (value) {
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
            }
        }
        editor.putBoolean("secure_migration_complete", true).apply()
    }
    return secure
}

private fun newPinSalt(): String {
    val bytes = ByteArray(16); SecureRandom().nextBytes(bytes)
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

private fun hashPersonalPin(pin: String, salt: String): String {
    val spec = PBEKeySpec(pin.toCharArray(), Base64.decode(salt, Base64.NO_WRAP), 120_000, 256)
    return Base64.encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded, Base64.NO_WRAP)
}

private fun verifyPersonalPin(pin: String, salt: String, expected: String): Boolean = try {
    MessageDigest.isEqual(Base64.decode(hashPersonalPin(pin, salt), Base64.NO_WRAP), Base64.decode(expected, Base64.NO_WRAP))
} catch (_: Exception) { false }

private fun verifyConfiguredDeveloperPin(pin: String, verifier: String): Boolean = try {
    val parts = verifier.split(":")
    if (parts.size != 2) false else verifyPersonalPin(pin, parts[0], parts[1])
} catch (_: Exception) { false }

@Composable
private fun Panel(title: String, value: String, detail: String) {
    Column(Modifier.fillMaxWidth().background(Card, RoundedCornerShape(16.dp)).padding(16.dp)) {
        Text(title, color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(Modifier.height(5.dp))
        Text(value, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(detail, color = Muted, fontSize = 12.sp)
    }
}


@Composable
private fun RegistrationScreen(onSave: (Map<String, String>) -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("Assam Rifles") }
    var pathway by remember { mutableStateOf("Generalised Fitness") }
    var level by remember { mutableStateOf("Beginner") }
    var days by remember { mutableStateOf("5 days per week") }
    var duration by remember { mutableStateOf("30 minutes") }
    var error by remember { mutableStateOf("") }
    MaterialTheme(colorScheme = darkColorScheme(primary = Gold, background = Deep, surface = Card)) {
        Column(Modifier.fillMaxSize().background(Deep).padding(horizontal = 20.dp, vertical = 18.dp).imePadding()) {
            Text("KSHATRIYA", color = Gold, fontSize = 27.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("PERSONAL REGISTRATION", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Set up your fitness profile. You can update these preferences later.", color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                item { FormField("Full name", name, { name = it }) }
                item { FormField("Age (years)", age, { age = it }, true) }
                item { ChoiceField("Gender", listOf("Male", "Female"), gender) { gender = it } }
                item { FormField("Height (cm)", height, { height = it }, true) }
                item { FormField("Current weight (kg)", weight, { weight = it }, true) }
                item { FormField("Target weight (kg) – optional", target, { target = it }, true) }
                item { ChoiceField("Service", listOf("Indian Army", "Assam Rifles"), service) { service = it } }
                item { ChoiceField("FITQUEST pathway", listOf("Weight Management", "Generalised Fitness"), pathway) { pathway = it } }
                item { ChoiceField("Training level", listOf("Beginner", "Intermediate", "Advanced"), level) { level = it } }
                item { ChoiceField("Weekly availability", listOf("3 days per week", "4 days per week", "5 days per week", "6 days per week"), days) { days = it } }
                item { ChoiceField("Preferred session duration", listOf("15 minutes", "20 minutes", "30 minutes", "45 minutes"), duration) { duration = it } }
                item {
                    val bmi = bmiText(height, weight)
                    Panel("BMI PREVIEW", bmi, "Calculated automatically from height and current weight. This is a screening indicator, not a medical diagnosis.")
                }
            }
            if (error.isNotBlank()) Text(error, color = Color(0xFFFF8A80), fontSize = 12.sp)
            Button(onClick = {
                val a = age.toIntOrNull(); val h = height.toFloatOrNull(); val w = weight.toFloatOrNull()
                error = when {
                    name.isBlank() -> "Enter your full name."
                    a == null || a !in 18..100 -> "Enter a valid age (18–100)."
                    h == null || h !in 100f..230f -> "Enter a valid height in centimetres."
                    w == null || w !in 30f..250f -> "Enter a valid current weight in kilograms."
                    target.isNotBlank() && (target.toFloatOrNull() == null || target.toFloat() !in 30f..250f) -> "Enter a valid target weight or leave it blank."
                    else -> ""
                }
                if (error.isBlank()) onSave(mapOf("full_name" to name.trim(), "age" to age, "gender" to gender, "height_cm" to height, "weight_kg" to weight, "target_weight_kg" to target, "service" to service, "pathway" to pathway, "level" to level, "weekly_days" to days, "session_duration" to duration))
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Deep)) { Text("SAVE PROFILE & CONTINUE", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun FormField(label: String, value: String, onValue: (String) -> Unit, numeric: Boolean = false) {
    OutlinedTextField(value = value, onValueChange = onValue, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Gold, unfocusedBorderColor = Muted, focusedLabelColor = Gold, unfocusedLabelColor = Muted))
}

@Composable
private fun ChoiceField(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Card, RoundedCornerShape(12.dp)).padding(12.dp)) {
        Text(label, color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        options.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { option ->
                    val active = option == selected
                    OutlinedButton(onClick = { onSelect(option) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(containerColor = if (active) Gold else Color.Transparent, contentColor = if (active) Deep else Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, if (active) Gold else Muted)) { Text(option, fontSize = 11.sp) }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

private fun bmiText(heightCm: String, weightKg: String): String {
    val h = heightCm.toFloatOrNull()?.div(100f) ?: return "—"
    val w = weightKg.toFloatOrNull() ?: return "—"
    if (h <= 0f) return "—"
    return "%.1f".format(w / (h * h))
}
