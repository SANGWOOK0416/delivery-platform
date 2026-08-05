#!/bin/bash
# Polls Kafka consumer group lag at a fixed interval and appends rows to a CSV.
# Usage: poll-lag.sh <output_csv> <stage_label> [interval_seconds]

OUT="$1"
STAGE="$2"
INTERVAL="${3:-2}"

if [ -z "$OUT" ] || [ -z "$STAGE" ]; then
    echo "usage: poll-lag.sh <output_csv> <stage_label> [interval_seconds]" >&2
    exit 1
fi

if [ ! -f "$OUT" ]; then
    echo "timestamp,stage,group,topic,partition,current_offset,log_end_offset,lag" > "$OUT"
fi

while true; do
    TS=$(date -u +%Y-%m-%dT%H:%M:%S)
    for GROUP in weather-group notification-group; do
        docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group "$GROUP" 2>/dev/null \
            | awk -v ts="$TS" -v stage="$STAGE" -v grp="$GROUP" \
                '$1!="GROUP" && $3 ~ /^[0-9]+$/ && NF>=6 {print ts","stage","grp","$2","$3","$4","$5","$6}'
    done
    sleep "$INTERVAL"
done >> "$OUT"
