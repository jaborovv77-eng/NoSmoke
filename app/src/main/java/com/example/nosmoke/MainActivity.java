package com.example.nosmoke;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {

    LinearLayout content;
    TextView timer;
    TextView streak;
    TextView money;

    long quitAt = 0;
    int cravings = 0;

    Handler handler = new Handler(Looper.getMainLooper());

    int green = Color.rgb(22, 138, 69);
    int dark = Color.rgb(25, 35, 28);
    int bg = Color.rgb(245, 247, 245);

    android.content.SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("nosmoke", MODE_PRIVATE);

        quitAt = prefs.getLong("quitAt", 0);
        cravings = prefs.getInt("cravings", 0);

        createInterface();
        updateTimer();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTimer();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    TextView createText(String text, float size) {

        TextView view = new TextView(this);

        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(dark);
        view.setPadding(20, 12, 20, 12);

        return view;
    }

    Button createButton(String text) {

        Button button = new Button(this);

        button.setText(text);
        button.setTextSize(16);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setBackgroundColor(green);

        return button;
    }

    void createInterface() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(bg);

        TextView title = createText("🚭 NoSmoke", 28);
        title.setTextColor(green);
        title.setPadding(24, 28, 24, 10);

        root.addView(title);

        ScrollView scroll = new ScrollView(this);

        content = new LinearLayout(this);

        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(18, 8, 18, 30);

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        TextView description = createText(
                "Твоя цель — не идеальность, а следующий час без сигареты.",
                17
        );

        content.addView(description);

        streak = createText("", 20);
        streak.setPadding(20, 24, 20, 8);

        content.addView(streak);

        timer = createText("", 36);
        timer.setTextColor(green);
        timer.setPadding(20, 8, 20, 18);

        content.addView(timer);

        Button start = createButton(
                quitAt == 0
                        ? "🚭 Начать день без сигарет"
                        : "Я не курил — продолжаем"
        );

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (quitAt == 0) {

                    quitAt = System.currentTimeMillis();

                    save();
                }

                Toast.makeText(
                        MainActivity.this,
                        "Отлично! Следующая цель — 10 минут.",
                        Toast.LENGTH_SHORT
                ).show();

                updateTimer();
            }
        });

        content.addView(start);

        Button craving = createButton(
                "🔥 МЕНЯ ТЯНЕТ КУРИТЬ"
        );

        craving.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                cravingMode();
            }
        });

        content.addView(craving);

        money = createText("", 17);

        content.addView(money);

        TextView tips = createText(
                "\nКогда тянет курить:\n\n" +
                "1. Подожди 10 минут.\n" +
                "2. Выпей стакан воды.\n" +
                "3. Сделай 10 медленных вдохов.\n" +
                "4. Пройдись 5 минут.\n" +
                "5. Не покупай «только одну» сигарету.",
                17
        );

        content.addView(tips);

        Button reset = createButton(
                "Начать заново"
        );

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(MainActivity.this)

                        .setTitle("Начать заново?")

                        .setMessage(
                                "Сбросить текущий счётчик? " +
                                "Это не поражение — просто новый старт."
                        )

                        .setNegativeButton(
                                "Отмена",
                                null
                        )

                        .setPositiveButton(
                                "Сбросить",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which
                                    ) {

                                        quitAt =
                                                System.currentTimeMillis();

                                        cravings = 0;

                                        save();

                                        updateTimer();
                                    }
                                }
                        )

                        .show();
            }
        });

        content.addView(reset);

        TextView note = createText(
                "\nВажно: приложение помогает переживать тягу, " +
                "но не может гарантировать, что желание исчезнет " +
                "навсегда. При сильной зависимости можно обсудить " +
                "с врачом доказанные методы отказа от курения.",
                14
        );

        content.addView(note);

        setContentView(root);
    }

    void cravingMode() {

        cravings++;

        save();

        final String[] steps = {

                "Отложи сигарету на 10 минут.",

                "Выпей стакан воды.",

                "Дыши медленно: вдох 4 секунды — выдох 6 секунд.",

                "Встань и пройдись 5 минут.",

                "Скажи себе: «Я не обязан действовать на эту тягу»."

        };

        final int[] step = {0};

        final AlertDialog dialog =
                new AlertDialog.Builder(this)

                        .setTitle("🔥 Переживаем тягу")

                        .setMessage(steps[0])

                        .setPositiveButton(
                                "Следующий шаг",
                                null
                        )

                        .setNegativeButton(
                                "Закрыть",
                                null
                        )

                        .create();

        dialog.setOnShowListener(
                new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface d) {

                        dialog.getButton(
                                AlertDialog.BUTTON_POSITIVE
                        ).setOnClickListener(
                                new View.OnClickListener() {

                                    @
