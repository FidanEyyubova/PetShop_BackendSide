package com.petshop.backend.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeleteAccountRequest {

    @Schema(description = "Current password, to confirm the deletion. Required when the account has a password "
            + "(accounts created with Google sign-in have none).")
    private String password;

    @Pattern(regexp = "(?i)NOT_USING|SWITCHING|PRIVACY|BAD_EXPERIENCE|OTHER",
            message = "Reason must be NOT_USING, SWITCHING, PRIVACY, BAD_EXPERIENCE or OTHER")
    @Schema(description = "Why the account is being deleted; shown to admins", example = "NOT_USING")
    private String reason;

    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    @Schema(description = "Optional explanation in the user's own words; shown to admins")
    private String comment;
}
