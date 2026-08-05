"""Render loadtest/results/lag_scenario_a.csv as a lag-vs-time chart.

Highlights the ramp stage's rate transitions (5 -> 10 -> 20 -> 50 -> 100 req/s)
so the weather-agent throughput ceiling is visible as an inflection point.
Usage: python plot_lag.py <input_csv> <output_png>
"""
import csv
import sys
from datetime import datetime, timedelta

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
from matplotlib import font_manager

for candidate in ("Malgun Gothic", "AppleGothic", "NanumGothic"):
    if any(f.name == candidate for f in font_manager.fontManager.ttflist):
        plt.rcParams["font.family"] = candidate
        break
plt.rcParams["axes.unicode_minus"] = False

IN_CSV = sys.argv[1] if len(sys.argv) > 1 else "loadtest/results/lag_scenario_a.csv"
OUT_PNG = sys.argv[2] if len(sys.argv) > 2 else "loadtest/results/lag_timeline.png"

TOPIC_LABELS = {
    "order-events": "weather-agent (order-events)",
    "delivery-risk-events": "notification-service (delivery-risk-events)",
}
TOPIC_COLORS = {
    "order-events": "#d1495b",
    "delivery-risk-events": "#2e6f95",
}

series = {"order-events": [], "delivery-risk-events": []}
with open(IN_CSV, newline="", encoding="utf-8") as f:
    for row in csv.DictReader(f):
        ts = datetime.strptime(row["timestamp"], "%Y-%m-%dT%H:%M:%S")
        topic = row["topic"]
        if topic in series:
            series[topic].append((ts, int(row["lag"])))

fig, ax = plt.subplots(figsize=(11, 6))

for topic, pts in series.items():
    pts.sort()
    xs = [t for t, _ in pts]
    ys = [lag for _, lag in pts]
    ax.plot(xs, ys, marker="o", markersize=2.5, linewidth=1.3,
            label=TOPIC_LABELS[topic], color=TOPIC_COLORS[topic])

ax.set_xlabel("시각")
ax.set_ylabel("Consumer lag (건)")
ax.set_title("Kafka consumer lag 시계열 — smoke -> ramp(5~100 req/s) -> soak")
ax.xaxis.set_major_formatter(mdates.DateFormatter("%H:%M"))
ax.grid(True, alpha=0.3)
ax.legend(loc="upper left")

# Ramp stage rate transitions: k6's ramping-arrival-rate stages in loadtest/k6/ramp.js
# run 1 minute each (5 -> 10 -> 20 -> 50 -> 100 req/s), starting when the "ramp" label
# first appears in the CSV.
ramp_t0 = min(t for pts in series.values() for t, _ in pts
              if t >= datetime(2026, 8, 5, 3, 2, 0))
stage_bounds = [
    (ramp_t0, "5 req/s"),
    (ramp_t0 + timedelta(minutes=1), "10 req/s"),
    (ramp_t0 + timedelta(minutes=2), "20 req/s"),
    (ramp_t0 + timedelta(minutes=3), "50 req/s"),
    (ramp_t0 + timedelta(minutes=4), "100 req/s"),
    (ramp_t0 + timedelta(minutes=5), "부하 종료"),
]

ymax = max(lag for pts in series.values() for _, lag in pts)
for boundary_ts, label in stage_bounds:
    ax.axvline(boundary_ts, color="gray", linestyle="--", linewidth=0.8, alpha=0.6)
    ax.annotate(label, xy=(boundary_ts, ymax * 1.02), xytext=(2, 0),
                textcoords="offset points", rotation=90, va="bottom", ha="left",
                fontsize=8, color="dimgray")

ax.set_ylim(top=ymax * 1.2)
fig.autofmt_xdate()
fig.tight_layout()
fig.savefig(OUT_PNG, dpi=150)
print(f"wrote {OUT_PNG}")
