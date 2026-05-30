package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PeriodPalViewModel
import com.example.ui.theme.*
import com.example.ui.translation.Translations

@Composable
fun LearnScreen(
    viewModel: PeriodPalViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val isEnglish = profile?.appLanguage != "Bahasa Melayu"
    
    var selectedTabState by remember { mutableStateOf(0) } // 0 = Emergency, 1 = Hygiene & Ghusl, 2 = Parent Corner

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = if (isEnglish) "Learning Space" else "Ruang Belajar",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                color = CocoaBrown
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = if (isEnglish) "Quiet, reassuring guidance for girls and moms" else "Bimbingan tenang & mesra bagi remaja & ibu bapa",
            style = MaterialTheme.typography.bodyMedium.copy(color = SoftTaupe),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Segmented Control Switch with 3 Options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PaleRose.copy(alpha = 0.5f))
                .border(1.dp, WarmBeige, RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedTabState == 0) DustyRose else Color.Transparent)
                    .clickable { selectedTabState = 0 }
                    .padding(vertical = 10.dp)
                    .testTag("tab_emergency"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isEnglish) "Emergency" else "Kecemasan",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTabState == 0) Cream else CocoaBrown
                    )
                )
            }
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedTabState == 1) DustyRose else Color.Transparent)
                    .clickable { selectedTabState = 1 }
                    .padding(vertical = 10.dp)
                    .testTag("tab_hygiene"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isEnglish) "Hygiene/Ghusl" else "Mandi Wajib",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTabState == 1) Cream else CocoaBrown
                    )
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedTabState == 2) DustyRose else Color.Transparent)
                    .clickable { selectedTabState = 2 }
                    .padding(vertical = 10.dp)
                    .testTag("tab_parents"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isEnglish) "Parents Room" else "Ibu Bapa",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTabState == 2) Cream else CocoaBrown
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle Content Sections
        AnimatedContent(
            targetState = selectedTabState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "tab_switching"
        ) { targetTab ->
            when (targetTab) {
                0 -> EmergencyTabSection(isEnglish)
                1 -> MuslimHygieneTabSection(isEnglish)
                else -> ParentTabSection(isEnglish)
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun EmergencyTabSection(isEnglish: Boolean) {
    val emergencyGuides = if (isEnglish) {
        listOf(
            EmergencyGuideItem(
                "🏫 I got my period at school",
                "First, don't worry! It happens to everyone. Go to the school nurse, a trusted teacher, or the office. They always keep spare pads. If you don't have a pad right now, you can fold clean toilet paper into your underwear layered up until you get your hands on a sanitary pad. You are totally safe."
            ),
            EmergencyGuideItem(
                "🩹 How to use a product / pad",
                "1. Wash hands.\n2. Unwrap the pad from its plastic wrapper (save the wrapper to discard your used pad later!).\n3. Peel off the long backing paper strip on the back.\n4. Press the sticky side firmly onto the center of your underwear.\n5. Wrap any side flaps (wings) around the sides of your underwear to keep it secure.\n6. Wash hands again!"
            ),
            EmergencyGuideItem(
                "🧱 Is brown blood completely normal?",
                "Yes, 100%! When blood reacts with oxygen, it turns brown, dark red, or even black. This is very common, especially at the absolute beginning or the very end of your period. It simply means older blood that took a bit longer to exit. Your body is doing its job beautifully."
            ),
            EmergencyGuideItem(
                "⚡ Why do physical cramps happen?",
                "Cramps happen because your womb (uterus) gently squeezes to help shed its lining. This is normal muscular activity! To comfort yourself, place a warm heating patch on your belly, sip clean warm hot tea or water, and rest under a soft blanket. Gentle stretches also do wonders."
            ),
            EmergencyGuideItem(
                "💦 What if I accidentally leak?",
                "Accidents happen to every girl on earth! If you notice a leak on school trousers, wrap a jumper or flannel shirt around your waist—it's super stylish and hides the stain. Then, check in with the nurse for a change or call your parents to bring spare clothes. Wash stained underwear in cold water first (hot water sets blood stains!)."
            ),
            EmergencyGuideItem(
                "⏰ How often should I change pads?",
                "Aim to change your sanitary pad every 3 to 6 hours, or sooner if it starts feeling wet or cold. Keeping it clean is great for vaginal wellness and keeps you feeling super fresh, confident, and light!"
            )
        )
    } else {
        listOf(
            EmergencyGuideItem(
                "🏫 Haid bermula di sekolah",
                "Pertama sekali, jangan risau! Ia berlaku kepada setiap perempuan di dunia ini. Sila hubungi guru sains, cikgu kesihatan, atau pejabat sekolah. Mereka sentiasa bersedia membekalkan tuala wanita gantian. Sementara mendapatkan tuala wanita, anda juga boleh mengalaskan tisu tandas ke dalam seluar dalam bersih sebagai perlindungan segera. Anda sentiasa selamat."
            ),
            EmergencyGuideItem(
                "🩹 Bagaimana menggunakan tuala wanita / pad",
                "1. Basuh tangan anda.\n2. Buka pek tuala wanita dari bungkusan plastik (simpan plastik tersebut untuk melupuskan pad semalam nanti!).\n3. Kupaskan kertas pelekat panjang bahagian belakang tuala wanita.\n4. Tekan pelekat pad dengan kemas pada bahagian tengah seluar dalam anda.\n5. Lipat kedua-dua tepi pelekat (wing) ke bahagian luar tepi seluar dalam untuk memastikan ia tidak berganjak.\n6. Basuh tangan semula!"
            ),
            EmergencyGuideItem(
                "🧱 Adakah warna darah coklat itu normal?",
                "Ya, 100% normal! Apabila darah bertindak balas dengan udara luar, warnanya boleh bertukar coklat, merah gelap, atau hitam. Tompokan coklat kerap keluar pada awal hari-hari pertama atau di penghujung tempoh haid. Ini perkara biasa dan sihat."
            ),
            EmergencyGuideItem(
                "⚡ Mengapa senggugut perut boleh berlaku?",
                "Senggugut ringan atau rasa pegal terjadi kerana dinding rahim mengecut secara lembut untuk mengeluarkan lapisannya. Untuk mengurangkan senggugut, demah perut dengan minyak hangat atau air suam di dalam botol, minum air madu suam, dan baring berehat dengan selimut tebal."
            ),
            EmergencyGuideItem(
                "💦 Bagaimana jika darah bocor terkeluar?",
                "Kebocoran adalah perkara biasa! Jika terkena pakaian, ikat jaket atau kemeja di pinggang anda—ia nampak bergaya dan dapat menutup kotoran. Kemudian pergi ke bilik persalinan atau bilik guru kesihatan. Rendam baju kotor dengan air sejuk dahulu sebelum dicuci (air hangat mengunci kotoran darah!)."
            ),
            EmergencyGuideItem(
                "⏰ Berapa kerap tuala wanita perlu ditukar?",
                "Disyorkan menukar tuala wanita anda setiap 3 hingga 6 jam sekali, atau bila-bila masa anda rasa tidak selesa atau basah. Mengekalkan kebersihan intim dapat memberikan kesegaran sepanjang hari."
            )
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = if (isEnglish) "Emergency Preparation Guidance 🌸" else "Panduan Menghadap Kecemasan 🌸",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CocoaBrown),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        emergencyGuides.forEach { guide ->
            ExpandableGuideCard(title = guide.title, content = guide.content)
        }
    }
}

@Composable
fun MuslimHygieneTabSection(isEnglish: Boolean) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Welcoming & Gentle Reassurance Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PaleRose),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌸", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (isEnglish) "Hello, Beautiful! 🌿" else "Hai, Sayang! 🌿",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CocoaBrown
                        )
                    )
                    Text(
                        text = if (isEnglish) {
                            "Growing up is a beautiful journey. In our faith, finishing your period is a special, positive milestone of cleansing that prepares you to resume your prayers with a fresh, shining spirit. Let's explore this together with care and kindness."
                        } else {
                            "Remaja matang adalah kurniaan indah. Dalam kepercayaan kita, suci daripada haid adalah fasa pembersihan yang sangat murni untuk kita bersiap memulakan ibadat semula dengan jiwa yang wangi dan bersih. Mari belajar bersama-sama secara santai."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe, lineHeight = 18.sp)
                    )
                }
            }
        }

        // Section 1: Signs That a Period Has Ended
        Text(
            text = if (isEnglish) "Signs Your Period Has Ended ✨" else "Tanda-Tanda Haid Anda Selesai ✨",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = CocoaBrown
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isEnglish) {
                        "A period typically lasts 5 to 7 days, but everyone is unique! There are two natural, beautiful signs that show your period has fully finished:"
                    } else {
                        "Setiap individu berbeza! Haid kebiasaannya tamat sepenuhnya apabila anda mendapati salah satu daripada dua tanda kesucian utama ini:"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(color = CocoaBrown, lineHeight = 20.sp),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Sign 1: Dryness (Jafaf)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(WarmBeige)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("☁️", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEnglish) "1. Complete Dryness (Al-Jafaf)" else "1. Kering Sepenuhnya (Al-Jafaf)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CocoaBrown
                            )
                        )
                        Text(
                            text = if (isEnglish) {
                                "When you wipe the intimate area with a clean white cotton pad or tissue and it comes out completely clean—with no trace of blood, pink spotting, brown stains, or yellow liquid."
                            } else {
                                "Kering di mana tiada lagi sebarang warna spotting merah jambu, tompok coklat, merah darah mahupun kuning yang kelihatan pada tisu atau kapas bersih selepas dikesat."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe, lineHeight = 16.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sign 2: Clear Discharge (Qassah al-Bayda)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(WarmBeige)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💧", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEnglish) "2. Pure White Discharge (Qassah al-Bayda)" else "2. Cecair Putih Bersih (Al-Qassah Al-Bayda)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CocoaBrown
                            )
                        )
                        Text(
                            text = if (isEnglish) {
                                "A clean white or transparent watery secretion that flows naturally at the end of menses. It signals that your body has fully cleared and finished shedding its cycle."
                            } else {
                                "Keluarnya cecair putih pekat jernih atau lutsinar dari rahim yang menyusul di hujung kitaran tanda pendarahan telah berhenti sepenuhnya."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = SoftTaupe, lineHeight = 16.sp)
                        )
                    }
                }
            }
        }

        // Section 2: Interactive Checker Widget
        Text(
            text = if (isEnglish) "Simple Discharge Checker 🔍" else "Penilai Kebersihan Celik 🔍",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = CocoaBrown
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = PaleRose.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, WarmBeige),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isEnglish) "What does your discharge or tissue wipe look like today?" else "Apakah rupa warna tompokan anda hari ini?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CocoaBrown
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                var checkerSelection by remember { mutableStateOf<Int?>(null) }

                val checkOptions = if (isEnglish) {
                    listOf(
                        "Light pink, brown, or red spots of blood",
                        "A trace of yellow or cloudy fluid",
                        "Completely clear, watery, or white discharge"
                    )
                } else {
                    listOf(
                        "Masih ada tompokan merah jambu, coklat, atau darah nipis",
                        "Kesan cecair kuning samar atau keruh",
                        "Cecair putih jernih, berair, atau kering sepenuhnya"
                    )
                }

                checkOptions.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (checkerSelection == index) SoftCream else Color.Transparent)
                            .border(
                                1.dp,
                                if (checkerSelection == index) DustyRose else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { checkerSelection = index }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (checkerSelection == index),
                            onClick = { checkerSelection = index },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = DustyRose,
                                unselectedColor = SoftTaupe
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium.copy(color = CocoaBrown)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = checkerSelection != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftCream)
                            .padding(12.dp)
                    ) {
                        val message = when (checkerSelection) {
                            0 -> if (isEnglish) {
                                "🌱 You are still in your final spotting stage! This means your cycle is finishing its work. Rest well and keep wearing a light pad or pantyliner. Mandi Hadas isn't needed quite yet."
                            } else {
                                "🌱 Anda masih berada dalam fasa tompokan sisa! Ini bermaksud kitaran sedang dikosongkan. Pakai pantyliner nipis dahulu. Mandi wajib belum dituntut di waktu ini."
                            }
                            1 -> if (isEnglish) {
                                "🚿 This is highly normal. Yellowish fluid can represent the final transition. Wait a little longer and check again. Once it turns completely dry or sparkling white/clear, you are ready!"
                            } else {
                                "🚿 Ini perkara biasa. Cecair kekuningan merupakan peralihan akhir rahim sebelum suci penuh. Sila tunggu sedikit waktu lagi dan seka semula. Apabila ia kering mutlak, mandi wajib sedia dilakukan!"
                            }
                            else -> if (isEnglish) {
                                "🎉 Bravo! Your period is fully ended. This beautiful clear discharge represents completion. You are now perfectly ready to perform Mandi Hadas Besar to wash away the impurity and welcome your normal hygiene routine."
                            } else {
                                "🎉 Alhamdulilah! Kitaran haid anda dikesan telah bersih penuh. Cecair jernih / kering mutlak bermakna anda sudah suci. Lakukan Mandi Hadas Besar untuk bersedia mula bersolat semula."
                            }
                        }
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = CocoaBrown,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }

        // Section 3: Step-by-Step Mandi Hadas Besar (Ghusl) Guide
        Text(
            text = if (isEnglish) "Easy Step-by-Step Mandi Hadas Guide 🧼" else "Panduan Ringkas Cara Mandi Wajib 🧼",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = CocoaBrown
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        var activeStepIndex by remember { mutableStateOf(0) }

        val mandiSteps = if (isEnglish) {
            listOf(
                mandiStepItem(
                    "1. Niat (Intention)",
                    "Say it softly in your heart to remove the major impurity. In Malay:\n'Sengaja aku mandi wajib kerana Allah Ta’ala.'\n(Quietly making this intention is the core of your bath!)",
                    "💭"
                ),
                mandiStepItem(
                    "2. Say Bismillah",
                    "Begin with 'Bismillahirrahmanirrahim' to bring calmness and blessing into your action.",
                    "🌱"
                ),
                mandiStepItem(
                    "3. Wash Your Hands",
                    "Wash both your hands three times. Keeping them completely clean helps as you begin.",
                    "🤲"
                ),
                mandiStepItem(
                    "4. Wash Private Parts",
                    "Gently wash and clean the intimate area using your left hand to remove any remaining traces of blood or impurity.",
                    "🧼"
                ),
                mandiStepItem(
                    "5. Perform Wuduk",
                    "Perform your normal wuduk (ablution) just like you do before you start praying. You can wash your feet now or at the end.",
                    "💦"
                ),
                mandiStepItem(
                    "6. Pour Water Over Head",
                    "Pour water over your head three times. Gently massage your hair roots and scalp to ensure water contacts every single part of your hair.",
                    "🚿"
                ),
                mandiStepItem(
                    "7. Spread Water Over Entire Body",
                    "Ratakan air: Run clean flowing water all over your body. Modern practice suggests doing the right side first, then the left side, letting it flow thoroughly down. Make sure water touches folds, underarms, and navel.",
                    "🕊️"
                ),
                mandiStepItem(
                    "8. Fresh and Ready!",
                    "You are fully purified! Put on fresh clothes and spray some calm scent. You can happily resume your daily activities and prayers.",
                    "💖"
                )
            )
        } else {
            listOf(
                mandiStepItem(
                    "1. Niat Mengangkat Hadas",
                    "Ucap niat ini di dalam hati anda secara ikhlas:\n'Sahaja aku mandi wajib kerana Allah Ta’ala.'\n(Niat di dalam hati merupakan rukun utama mandi wajib!)",
                    "💭"
                ),
                mandiStepItem(
                    "2. Membaca Bismillah",
                    "Mulakan dengan bacaan 'Bismillahirrahmanirrahim' untuk mengundang ketenangan ke dalam mandi wajib anda.",
                    "🌱"
                ),
                mandiStepItem(
                    "3. Membasuh Tangan",
                    "Cuci tangan kiri dan kanan sehingga bersih sebanyak tiga kali untuk menjaga kebersihan.",
                    "🤲"
                ),
                mandiStepItem(
                    "4. Membersih Kemaluan",
                    "Gunakan tangan kiri untuk membersihkan sisa kotoran darah haid pada bahagian sulit anda sehingga tiada rasa licin.",
                    "🧼"
                ),
                mandiStepItem(
                    "5. Mengambil Wuduk",
                    "Lakukan wuduk dengan sempurna seperti biasa. Mengambil wuduk sebelum menjirus tubuh adalah sunat yang dituntut.",
                    "💦"
                ),
                mandiStepItem(
                    "6. Menjirus Air ke Kepala",
                    "Jirus air pada kepala sebanyak tiga kali sambil meleraikan rambut celah-celah akar supaya air mutlak mengalir rata.",
                    "🚿"
                ),
                mandiStepItem(
                    "7. Meratakan Air Setubuh",
                    "Mengalirkan air suci keseluruh badan dari atas hingga bawah. Mulakan bahagian kanan dahulu kemudian bahagian kiri, celah lipatan ketiak, pusat, serta sela jemari kaki.",
                    "🕊️"
                ),
                mandiStepItem(
                    "8. Selesai & Segar!",
                    "Syabas! Mandi anda sempurna. Pakai pakaian bersih, rapi, dan anda kini bebas bersolat serta melakukan aktiviti kegemaran.",
                    "💖"
                )
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Step Indicator bubbles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    mandiSteps.forEachIndexed { idx, _ ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (idx == activeStepIndex) DustyRose
                                    else if (idx < activeStepIndex) WarmBeige
                                    else WarmBeige.copy(alpha = 0.25f)
                                )
                                .clickable { activeStepIndex = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            if (idx < activeStepIndex) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            } else {
                                Text(
                                    text = (idx + 1).toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (idx == activeStepIndex) Color.White else CocoaBrown
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current Step Content Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PaleRose.copy(alpha = 0.2f))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mandiSteps[activeStepIndex].emoji,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = mandiSteps[activeStepIndex].title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CocoaBrown
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = mandiSteps[activeStepIndex].detail,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = CocoaBrown,
                                lineHeight = 20.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation of Steps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { if (activeStepIndex > 0) activeStepIndex-- },
                        enabled = activeStepIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarmBeige,
                            contentColor = CocoaBrown,
                            disabledContainerColor = WarmBeige.copy(alpha = 0.3f),
                            disabledContentColor = SoftTaupe
                        )
                    ) {
                        Text(if (isEnglish) "Back" else "Kembali")
                    }

                    Button(
                        onClick = { if (activeStepIndex < mandiSteps.size - 1) activeStepIndex++ else activeStepIndex = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DustyRose,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            if (activeStepIndex == mandiSteps.size - 1) {
                                if (isEnglish) "Restart" else "Mula Semula"
                            } else {
                                if (isEnglish) "Next Step" else "Langkah Seterusnya"
                            }
                        )
                    }
                }
            }
        }

        // Section 4: Menstrual Hygiene & Care Tips
        Text(
            text = if (isEnglish) "Care & Freshness Tips 🌿" else "Tip Kesegaran & Kebersihan Diri 🌿",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = CocoaBrown
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = SoftCream),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val hygieneTips = if (isEnglish) {
                    listOf(
                        Triple("🩲", "Change Knickers", "Put on dry, fresh, breathable cotton underwear. Avoid synthetic fibers right after your cycle to keep the skin safe."),
                        Triple("💨", "Stay Dry & Fresh", "Dampness can cause irritation. Wipe gently from front to back with a clean towel after bathing or using the washroom."),
                        Triple("🧼", "Avoid Harsh Soap", "Your intimate area is extremely self-cleansing! Clean water or a mild, unperfumed soap is wonderful. Avoid heavily scented sprays."),
                        Triple("💤", "Rest & Hydrate", "Shedding a cycle takes physical energy. Replenish with plenty of warm water, fresh herbal drinks, and a good nap!")
                    )
                } else {
                    listOf(
                        Triple("🩲", "Tukar Seluar Dalam", "Pakailah seluar dalam kapas yang longgar dan menyerap peluh seboleh mungkin untuk kebaikan kulit."),
                        Triple("💨", "Kekal Kering", "Kelembapan boleh menyebabkan kegatalan. Seka lembut dari depan ke belakang dengan kain lap lembut yang bersih."),
                        Triple("🧼", "Elak Sabun Pewangi", "Bahagian intim wanita mempunyai ejen pembersih semula jadi. Cukup gunakan air biasa atau pencuci intim tanpa pewangi kuat."),
                        Triple("💤", "Rehat & Air Suam", "Membina dinding baru memerlukan tenaga yang tinggi. Sentiasa hidrat dengan air suam, jus segar, atau tidur secukupnya!")
                    )
                }

                hygieneTips.forEach { (emoji, title, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PaleRose.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CocoaBrown
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SoftTaupe,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 5: Common FAQ Accordion Questions
        Text(
            text = if (isEnglish) "Faith & Care FAQ 🌸" else "Soalan Lazim (FAQ) 🌸",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = CocoaBrown
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        val faqs = if (isEnglish) {
            listOf(
                EmergencyGuideItem(
                    "❓ I performed Mandi Hadas, but saw a brown/yellow spot later. What do I do?",
                    "Don't worry! In contemporary logic and jurisprudence, if a complete dryness (Jafaf) was achieved first and you bathed, short isolated brown or yellowish spots occurring outside of your main period dates do not count back as a period. Wash the spot, do wuduk, and you are good to go! However, if it flows as rich blood, count it as a resumption of period."
                ),
                EmergencyGuideItem(
                    "❓ What is the maximum number of days for a period?",
                    "In Islamic reference, the maximum duration for a standard monthly cycle is 15 days. If bleeding persists beyond 15 days, it is gently classified as non-menstrual bleeding ('Istihadah') where you perform normal minor hygiene wash before each prayer, and do not need to pause prayers."
                ),
                EmergencyGuideItem(
                    "❓ I am totally new to this. Will it hurt?",
                    "Never! Mandi Hadas is just a beautiful, warm, comforting bath that you can enjoy just like any regular shower. It is a highly meditative and respectful ritual to start fresh. There is absolutely no pressure or pain."
                ),
                EmergencyGuideItem(
                    "❓ Can I braid or leave my hair tied during mandi wajib?",
                    "Yes, you can! You don't have to undo simple braids, but check that water can flow down and touch your scalp and the roots of your hair successfully."
                )
            )
        } else {
            listOf(
                EmergencyGuideItem(
                    "❓ Selepas Mandi Wajib, keluar sisa kuning/coklat. Bagaimana?",
                    "Jangan bimbang! Mengikut pendapat muktamat, jika anda sudah capai fasa kesucian (kering penuh) dan telah mandi wajib, sisa kekuningan atau keruh setelah suci tidak dianggap haid. Anda hanya perlu membasuh sisa tersebut, mengambil wuduk baru, dan solat seperti biasa."
                ),
                EmergencyGuideItem(
                    "❓ Berapakah tempoh maksimum hari haid?",
                    "Dalam hukum Syarak, tempoh paling lama darah dikira haid ialah 15 hari 15 malam. Jika pendarahan masih berterusan selepas hari ke-15, ia dipanggil darah penyakit atau darah istihadah. Anda boleh bersolat setelah mencuci kawasan sulit pada setiap kali masuk waktu solat."
                ),
                EmergencyGuideItem(
                    "❓ Adakah mandi wajib ini membebankan atau menyakitkan?",
                    "Sama sekali tidak! Mandi wajib adalah ritual mandi biasa yang menenangkan, membina kesegaran mental, dan lambang disiplin diri yang baik. Ia penuh berkah dan kesegaran."
                ),
                EmergencyGuideItem(
                    "❓ Adakah ikatan tocang rambut perlu dilepaskan semasa mandi wajib?",
                    "Tocang rambut biasa tidak wajib diurai sekiranya air dapat diratakan menyentuh kulit kepala dan akar rambut anda sepenuhnya."
                )
            )
        }

        faqs.forEach { faq ->
            ExpandableGuideCard(title = faq.title, content = faq.content)
        }
    }
}

@Composable
fun ParentTabSection(isEnglish: Boolean) {
    val parentGuides = if (isEnglish) {
        listOf(
            EmergencyGuideItem(
                "💕 How to support your daughter",
                "Be open, positive, and gentle. Celebrate this step rather than making it feel like a chore or disease. Listen with empathy, buy her a cute pouch for school, and remind her that her body is beautiful and completely healthy."
            ),
            EmergencyGuideItem(
                "🎒 First period pouch preparation",
                "Prepare a school kit inside a small cute zipper pouch. Include 2–3 sanitary pads, a pair of spare clean knickers, wet wipes for easy cleanup, a small plastic zip bag (for soiled knickers if leak occurs), and a sweet comforting note from you."
            ),
            EmergencyGuideItem(
                "🧘 Emotional support tips",
                "Fluctuating hormones are a real thing and cause sudden crying, mood swings, or exhaustion. Give her space, make warm tea, allow extra rest time, and reassure her. Avoid mocking, teasing, or calling her emotional state 'hormonal' in front of others."
            ),
            EmergencyGuideItem(
                "🩺 When to consult a medical doctor",
                "Consult a pediatrician/gynae if:\n- Her period lasts longer than 7 consecutive days.\n- Her cramps are so severe she cannot go to school even with comforting medicine.\n- She hasn't started by age 15.\n- The cycle occurs more frequently than every 21 days."
            ),
            EmergencyGuideItem(
                "🏫 School emergency tips",
                "Make sure your daughter knows where the school nurse's office is. Remind her she can discreetly ask any female teacher or staff since they are all prepared to protect her."
            )
        )
    } else {
        listOf(
            EmergencyGuideItem(
                "💕 Bagaimana untuk membantu anak gadis anda",
                "Amalkan komunikasi terbuka dan dorongan positif. Raikan peralihan ini bagi menaikkan jati dirinya. Rancang membelikannya beg persalinan haid yang comel sebagai tanda kasih-sayang."
            ),
            EmergencyGuideItem(
                "🎒 Kit kecemasan beg sekolah",
                "Sediakan pouch zip bersaiz kecil untuk diletakkan dalam beg sekolahnya. Lengkapkan dengan 2 keping pad tuala wanita lembut, seluar dalam bersih ganti, tisu basah kecil, beg plastik lutsinar bersih, serta nota penyemangat kecil daripada anda."
            ),
            EmergencyGuideItem(
                "🧘 Penjagaan & bimbingan emosi",
                "Perubahan hormon adalah nyata dan boleh menyebabkan tangisan mendadak, perubahan angin harian, atau keletihan melampau. Berikan dia ruang bertenang, elakkan mengusik atau menyakat emosinya secara ngeri."
            ),
            EmergencyGuideItem(
                "🩺 Bilakah perlu dinilai oleh doktor?",
                "Sila bincang dengan pediatrik jika haidnya melebihi 7 hari berturut-turut, senggugutnya sangat keterlaluan mengganggu rutin sekolah harian, atau dia belum memulakan kitaran pertamanya pada usia 15 tahun."
            ),
            EmergencyGuideItem(
                "🏫 Tip kecemasan guru sekolah",
                "Ingatkan anak gadis anda bahawa semua cikgu sekolah wanita dan pegawai kesihatan sentiasa sedia untuk melindunginya dengan bekalan kecemasan."
            )
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = if (isEnglish) "Parental Preparation Resource Room 🏡" else "Bilik Sumber Persediaan Ibu Bapa 🏡",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CocoaBrown),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        parentGuides.forEach { guide ->
            ExpandableGuideCard(title = guide.title, content = guide.content)
        }
    }
}

@Composable
fun ExpandableGuideCard(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = SoftCream),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, WarmBeige.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CocoaBrown
                    ),
                    modifier = Modifier.weight(0.9f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = SoftTaupe
                )
            }
            
            // Expand with elegant transition
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = WarmBeige.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = CocoaBrown,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }
    }
}

data class mandiStepItem(val title: String, val detail: String, val emoji: String)
data class EmergencyGuideItem(val title: String, val content: String)
