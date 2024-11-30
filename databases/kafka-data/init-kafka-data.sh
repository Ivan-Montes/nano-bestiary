#!/bin/bash
set -e 
set -x

KAFKA_PORT="${1}"
KAFKA_BROKER="kafka:${KAFKA_PORT}"

check_kafka() {

    kcat -L -b "$KAFKA_BROKER" -t __consumer_offsets -J > /dev/null 2>&1
    return $?
    
}

checking_kafka_loop() {

    local max_attempts=5
    local attempt=0
    local wait_time=15
    
    echo "Waiting for Kafka is ready..."
    
    until check_kafka; do
    
       attempt=$((attempt + 1))
        if [ ${attempt} -ge ${max_attempts} ]; then
            echo "Kafka is not ready after ${max_attempts} tries. Exit."
            return 1
        fi
        echo "Kafka is not ready. Waiting ${wait_time} secs..."
        sleep ${wait_time}
        
    done
    
    echo "Kafka is ready."
    
}

create_topic(){
 
 	local topic=$1
 	kafka-topics.sh --create --if-not-exists --bootstrap-server "$KAFKA_BROKER" --replication-factor 1 --partitions 1 --topic "$topic"
 
 }

create_all_topics() { 
    
    local topics=("area.created" "area.updated" "area.deleted" "creature.created" "creature.updated" "creature.deleted")

	for topic in "${topics[@]}"; do
	    create_topic "$topic"
	done
	
}

send_event() {

    local topic=$1
    local json=$2
    echo "$json" | kcat -P -b "$KAFKA_BROKER" -t "$topic" -H "__TypeId__=dev.ime.domain.event.Event"
    
}

send_all_events() {

    local timestamp
    timestamp=$(date +%s.0)

    echo "Sending events..."
    
    send_event "area.created" '{"eventId": "1cc70000-0000-0000-0000-000000000001","eventCategory": "Area","eventType": "area.created","eventTimestamp": '"$timestamp"',"eventData": {"areaId": "11000000-0000-0000-0000-000000000001","areaName": "Ancient Greece"}}'
	send_event "area.created" '{"eventId": "1cc70000-0000-0000-0000-000000000002","eventCategory": "Area","eventType": "area.created","eventTimestamp": '"$timestamp"',"eventData": {"areaId": "11000000-0000-0000-0000-000000000002","areaName": "Many cultures"}}'	
	send_event "area.created" '{"eventId": "1cc70000-0000-0000-0000-000000000003","eventCategory": "Area","eventType": "area.created","eventTimestamp": '"$timestamp"',"eventData": {"areaId": "11000000-0000-0000-0000-000000000003","areaName": "Egypt"}}'	
	send_event "area.created" '{"eventId": "1cc70000-0000-0000-0000-000000000004","eventCategory": "Area","eventType": "area.created","eventTimestamp": '"$timestamp"',"eventData": {"areaId": "11000000-0000-0000-0000-000000000004","areaName": "Norway"}}'	
	send_event "area.created" '{"eventId": "1cc70000-0000-0000-0000-000000000005","eventCategory": "Area","eventType": "area.created","eventTimestamp": '"$timestamp"',"eventData": {"areaId": "11000000-0000-0000-0000-000000000005","areaName": "Mexico"}}'	
	send_event "area.created" '{"eventId": "1cc70000-0000-0000-0000-000000000006","eventCategory": "Area","eventType": "area.created","eventTimestamp": '"$timestamp"',"eventData": {"areaId": "11000000-0000-0000-0000-000000000006","areaName": "Nepal"}}'


    send_event "creature.created" '{"eventId": "2cc70000-0000-0000-0000-000000000001","eventCategory": "Creature","eventType": "creature.created","eventTimestamp": '"$timestamp"',"eventData": {"creatureId": "22000000-0000-0000-0000-000000000001", "creatureName": "Griffin", "creatureDescription": "This mythical creature has the body, tail, and back legs of a lion. The head and wings are of an eagle", "areaId": "11000000-0000-0000-0000-000000000001"}}'
    send_event "creature.created" '{"eventId": "2cc70000-0000-0000-0000-000000000002","eventCategory": "Creature","eventType": "creature.created","eventTimestamp": '"$timestamp"',"eventData": {"creatureId": "22000000-0000-0000-0000-000000000002", "creatureName": "Dragon", "creatureDescription": "A large, serpent-like creature that often possesses magical or spiritual qualities", "areaId": "11000000-0000-0000-0000-000000000002"}}'
    send_event "creature.created" '{"eventId": "2cc70000-0000-0000-0000-000000000003","eventCategory": "Creature","eventType": "creature.created","eventTimestamp": '"$timestamp"',"eventData": {"creatureId": "22000000-0000-0000-0000-000000000003", "creatureName": "Phoenix", "creatureDescription": "A long-lived bird that is cyclically regenerated or reborn", "areaId": "11000000-0000-0000-0000-000000000003"}}'
    send_event "creature.created" '{"eventId": "2cc70000-0000-0000-0000-000000000004","eventCategory": "Creature","eventType": "creature.created","eventTimestamp": '"$timestamp"',"eventData": {"creatureId": "22000000-0000-0000-0000-000000000004", "creatureName": "Kraken", "creatureDescription": "A giant sea monster, resembling an octopus or squid", "areaId": "11000000-0000-0000-0000-000000000004"}}'
    send_event "creature.created" '{"eventId": "2cc70000-0000-0000-0000-000000000005","eventCategory": "Creature","eventType": "creature.created","eventTimestamp": '"$timestamp"',"eventData": {"creatureId": "22000000-0000-0000-0000-000000000005", "creatureName": "Chupacabra", "creatureDescription": "A creature known for its reported attacks on livestock", "areaId": "11000000-0000-0000-0000-000000000005"}}'
    send_event "creature.created" '{"eventId": "2cc70000-0000-0000-0000-000000000006","eventCategory": "Creature","eventType": "creature.created","eventTimestamp": '"$timestamp"',"eventData": {"creatureId": "22000000-0000-0000-0000-000000000006", "creatureName": "Minotaur", "creatureDescription": "A creature with the head of a bull and the body of a man", "areaId": "11000000-0000-0000-0000-000000000001"}}'
    send_event "creature.created" '{"eventId": "2cc70000-0000-0000-0000-000000000007","eventCategory": "Creature","eventType": "creature.created","eventTimestamp": '"$timestamp"',"eventData": {"creatureId": "22000000-0000-0000-0000-000000000007", "creatureName": "Centaur", "creatureDescription": "A creature with the upper body of a human and the lower body and legs of a horse", "areaId": "11000000-0000-0000-0000-000000000001"}}'
    send_event "creature.created" '{"eventId": "2cc70000-0000-0000-0000-000000000008","eventCategory": "Creature","eventType": "creature.created","eventTimestamp": '"$timestamp"',"eventData": {"creatureId": "22000000-0000-0000-0000-000000000008", "creatureName": "Yeti", "creatureDescription": "A large ape-like creature", "areaId": "11000000-0000-0000-0000-000000000006"}}'
 

}

checking_kafka_loop
send_all_events

echo "File init-kafka-sh processed"
