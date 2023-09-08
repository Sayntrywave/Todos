package com.korotkov.todo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AcceptRequest {
    private Boolean accept;
}
