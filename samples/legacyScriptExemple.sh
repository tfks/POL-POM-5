#!/bin/bash
[ "$PLAYONLINUX" = "" ] && exit 0
source "$PLAYONLINUX/lib/sources"

TITLE="Legacy script"

echo "Test"
POL_SetupWindow_Init
POL_SetupWindow_message "Test"
POL_SetupWindow_message "Test 2"
POL_SetupWindow_message "Test 3"

POL_SetupWindow_Close
exit 