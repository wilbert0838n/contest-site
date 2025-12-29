#!/bin/bash
# A simple loop that waits for code, runs it, and signals completion.

WORK_DIR="/dev/shm"

echo "READY" # Signal to Java that container is ready

while read -r line; do
    # 1. We expect the first line to be "START_CODE"
    if [ "$line" == "START_CODE" ]; then

        # 2. Read lines until we see "END_CODE" and save to file
        cat > "$WORK_DIR/solution.cpp" <<EOF
$(sed '/^END_CODE$/q')
EOF
        # (The sed command above reads until END_CODE)

        # 3. Compile (quietly)
        g++ -O0 "$WORK_DIR/solution.cpp" -o "$WORK_DIR/solution" 2> "$WORK_DIR/error.txt"
        COMPILE_STATUS=$?

        if [ $COMPILE_STATUS -ne 0 ]; then
            echo "---COMPILATION_ERROR---"
            cat "$WORK_DIR/error.txt"
        else
            echo "---OUTPUT_START---"
            # Run with timeout (e.g., 2s)
            timeout 2s "$WORK_DIR/solution" < /dev/null
            echo "" # Newline ensuring separation
            echo "---OUTPUT_END---"
        fi

        # 4. Print the Delimiter so Java knows to stop reading
        echo "###DONE###"
    fi
done