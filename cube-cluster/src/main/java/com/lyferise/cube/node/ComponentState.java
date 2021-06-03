package com.lyferise.cube.node;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ComponentState {
    CREATED,
    STARTED,
    FAILED_TO_START,
    STOPPED
}