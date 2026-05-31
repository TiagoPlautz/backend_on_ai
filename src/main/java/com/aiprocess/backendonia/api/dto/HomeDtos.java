package com.aiprocess.backendonia.api.dto;

import java.util.List;

public final class HomeDtos {

    private HomeDtos() {
    }

    public record HomeResponse(
            UserDtos.UserResponse me,
            DocumentDtos.DocumentMetricsResponse metrics,
            List<DocumentDtos.DocumentResponse> recentCreated,
            List<DocumentDtos.DocumentResponse> recentUpdated
    ) {
    }
}
