#!/bin/bash
# Prints the latest (next) offset for each DLT topic partition, used to compute
# before/after deltas = number of messages that landed in the DLQ during a window.
# Usage: dlt-offsets.sh

for TOPIC in order-events.DLT delivery-risk-events.DLT; do
    docker exec kafka kafka-run-class kafka.tools.GetOffsetShell \
        --broker-list localhost:9092 --topic "$TOPIC" --time -1 2>/dev/null
done
