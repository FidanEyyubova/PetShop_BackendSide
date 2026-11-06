package com.petshop.backend.service.user.impl;

import com.petshop.backend.common.Roles;
import com.petshop.backend.common.ServiceType;
import com.petshop.backend.common.UserStatus;
import com.petshop.backend.config.AppProperties;
import com.petshop.backend.dto.request.auth.*;
import com.petshop.backend.dto.request.user.ChangePasswordRequest;
import com.petshop.backend.dto.request.user.UpdateProfileRequest;
import com.petshop.backend.dto.response.auth.AuthResponse;
import com.petshop.backend.dto.response.user.UserResponse;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.entity.service.ServiceProviderProfile;
import com.petshop.backend.entity.user.PasswordResetToken;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.entity.vendor.VendorBankDetails;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.exception.UnauthorizedException;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.repository.service.ServiceProviderProfileRepository;
import com.petshop.backend.repository.user.PasswordResetTokenRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.repository.vendor.VendorBankDetailsRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.JwtTokenProvider;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.notification.EmailService;
import com.petshop.backend.service.user.AuthService;
import com.petshop.backend.service.user.RefreshTokenService;
import com.petshop.backend.service.user.UserMapper;
import com.petshop.backend.service.user.GoogleTokenVerifier;
import com.petshop.backend.util.SlugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final int RESET_CODE_MINUTES = 15;
    private static final int RESET_MAX_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ServiceProviderProfileRepository profileRepository;
    private final VendorBankDetailsRepository bankDetailsRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final AppProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(UserRepository userRepository,
                           StoreRepository storeRepository,
                           ServiceProviderProfileRepository profileRepository,
                           VendorBankDetailsRepository bankDetailsRepository,
                           PasswordResetTokenRepository resetTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider,
                           AuthenticationManager authenticationManager,
                           RefreshTokenService refreshTokenService,
                           EmailService emailService,
                           UserMapper userMapper,
                           GoogleTokenVerifier googleTokenVerifier,
                           AppProperties properties) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.profileRepository = profileRepository;
        this.bankDetailsRepository = bankDetailsRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.googleTokenVerifier = googleTokenVerifier;
        this.properties = properties;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS", "Invalid email or password"));
        return issueTokens(user, Boolean.TRUE.equals(request.getRememberMe()));
    }

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleTokenVerifier.GoogleUser google = googleTokenVerifier.verify(request.getIdToken());
        User user = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(google.email()).orElse(null);
        if (user == null) {
            user = userRepository.save(User.builder()
                    .fullName(google.name())
                    .email(google.email())
                    .avatarUrl(google.picture())
                    .role(Roles.ROLE_BUYER)
                    .status(UserStatus.ACTIVE)
                    .build());
        } else if (user.getStatus() == UserStatus.SUSPENDED || user.getStatus() == UserStatus.REJECTED) {
            throw new com.petshop.backend.exception.AccessDeniedCustomException("ACCOUNT_DISABLED", "This account is disabled");
        } else if (user.getAvatarUrl() == null && google.picture() != null) {
            user.setAvatarUrl(google.picture());
            user = userRepository.save(user);
        }
        return issueTokens(user, Boolean.TRUE.equals(request.getRememberMe()));
    }

    @Override
    @Transactional
    public AuthResponse registerBuyer(RegisterBuyerRequest request) {
        validateRegistration(request.getEmail(), request.getPassword(), request.getConfirmPassword(), request.getAgreeTerms());
        User user = userRepository.save(User.builder()
                .fullName(request.getFullName().trim())
                .email(normalizeEmail(request.getEmail()))
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(trimToNull(request.getPhone()))
                .avatarUrl(trimToNull(request.getAvatarUrl()))
                .cityAddress(trimToNull(request.getCityAddress()))
                .role(Roles.ROLE_BUYER)
                .status(UserStatus.ACTIVE)
                .build());
        return issueTokens(user, false);
    }

    @Override
    @Transactional
    public AuthResponse registerVendor(RegisterVendorRequest request) {
        validateRegistration(request.getEmail(), request.getPassword(), request.getConfirmPassword(), request.getAgreeTerms());
        User user = userRepository.save(User.builder()
                .fullName(request.getFullName().trim())
                .email(normalizeEmail(request.getEmail()))
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(trimToNull(request.getPhone()))
                .avatarUrl(trimToNull(request.getLogoUrl()))
                .role(Roles.ROLE_VENDOR)
                .status(UserStatus.PENDING_APPROVAL)
                .build());

        storeRepository.save(Store.builder()
                .ownerId(user.getId())
                .name(request.getStoreName().trim())
                .slug(SlugUtils.unique(request.getStoreName(), storeRepository::existsBySlug))
                .address(trimToNull(request.getStoreAddress()))
                .city(trimToNull(request.getCity()))
                .phone(trimToNull(request.getPhone()))
                .email(user.getEmail())
                .logoUrl(trimToNull(request.getLogoUrl()))
                .voen(trimToNull(request.getVoen()))
                .verified(false)
                .build());

        if (request.getBankIban() != null && !request.getBankIban().isBlank()) {
            bankDetailsRepository.save(VendorBankDetails.builder()
                    .vendorId(user.getId())
                    .iban(request.getBankIban().replace(" ", "").toUpperCase())
                    .bankName(trimToNull(request.getBankName()))
                    .accountHolder(user.getFullName())
                    .voen(trimToNull(request.getVoen()))
                    .build());
        }
        return issueTokens(user, false);
    }

    @Override
    @Transactional
    public AuthResponse registerServiceProvider(RegisterServiceProviderRequest request) {
        validateRegistration(request.getEmail(), request.getPassword(), request.getConfirmPassword(), request.getAgreeTerms());
        ServiceType serviceType = ServiceType.parse(request.getServiceType());
        User user = userRepository.save(User.builder()
                .fullName(request.getFullName().trim())
                .email(normalizeEmail(request.getEmail()))
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(trimToNull(request.getPhone()))
                .avatarUrl(trimToNull(request.getLogoUrl()))
                .role(Roles.ROLE_SERVICE_PROVIDER)
                .status(UserStatus.PENDING_APPROVAL)
                .build());

        profileRepository.save(ServiceProviderProfile.builder()
                .providerId(user.getId())
                .centerName(request.getCenterName().trim())
                .serviceType(serviceType.name())
                .phone(trimToNull(request.getPhone()))
                .city(trimToNull(request.getCity()))
                .address(trimToNull(request.getAddress()))
                .logoUrl(trimToNull(request.getLogoUrl()))
                .build());
        return issueTokens(user, false);
    }

    @Override
    @Transactional(noRollbackFor = UnauthorizedException.class)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshTokenService.IssuedToken rotated = refreshTokenService.rotate(request.getRefreshToken());
        User user = userRepository.findById(rotated.userId())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new UnauthorizedException("INVALID_REFRESH_TOKEN", "Account no longer exists"));
        if (user.getStatus() == UserStatus.SUSPENDED || user.getStatus() == UserStatus.REJECTED) {
            refreshTokenService.revokeAll(user.getId());
            throw new UnauthorizedException("ACCOUNT_DISABLED", "This account is disabled");
        }
        return buildAuthResponse(user, rotated);
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request, UserPrincipal currentUser) {
        if (request != null && Boolean.TRUE.equals(request.getAllDevices()) && currentUser != null) {
            refreshTokenService.revokeAll(currentUser.getId());
            return;
        }
        if (request != null) {
            refreshTokenService.revoke(request.getRefreshToken());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal currentUser) {
        return userMapper.toResponse(loadUser(currentUser));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request, UserPrincipal currentUser) {
        User user = loadUser(currentUser);
        if (request.getFullName() != null && !request.getFullName().isBlank()) user.setFullName(request.getFullName().trim());
        if (request.getPhone() != null) user.setPhone(trimToNull(request.getPhone()));
        if (request.getAvatarUrl() != null) user.setAvatarUrl(trimToNull(request.getAvatarUrl()));
        if (request.getCityAddress() != null) user.setCityAddress(trimToNull(request.getCityAddress()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, UserPrincipal currentUser) {
        User user = loadUser(currentUser);
        if (user.getPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("WRONG_PASSWORD", "Current password is incorrect");
        }
        if (request.getConfirmPassword() != null && !request.getConfirmPassword().equals(request.getNewPassword())) {
            throw new BadRequestException("PASSWORD_MISMATCH", "New password and confirmation do not match");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("SAME_PASSWORD", "New password must differ from the current password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenService.revokeAll(user.getId());
        sendPasswordChangedEmail(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(email).ifPresent(user -> {
            resetTokenRepository.invalidateAllForEmail(email);
            String code = String.format("%06d", secureRandom.nextInt(1_000_000));
            resetTokenRepository.save(PasswordResetToken.builder()
                    .email(email)
                    .code(code)
                    .expiryDate(LocalDateTime.now().plusMinutes(RESET_CODE_MINUTES))
                    .build());
            emailService.send(email, "PetShop password reset code",
                    "Hello " + user.getFullName() + ",\n\nYour password reset code is " + code
                            + ". It expires in " + RESET_CODE_MINUTES + " minutes.\n\n"
                            + "If you did not request this, ignore this email.\n\n" + properties.getFrontendUrl());
            log.debug("Password reset code generated for user {}", user.getId());
        });
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public void resetPassword(ResetPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (request.getConfirmPassword() != null && !request.getConfirmPassword().equals(request.getNewPassword())) {
            throw new BadRequestException("PASSWORD_MISMATCH", "New password and confirmation do not match");
        }
        PasswordResetToken token = resetTokenRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BadRequestException("INVALID_RESET_CODE", "Invalid or expired verification code"));
        if (LocalDateTime.now().isAfter(token.getExpiryDate())) {
            token.setUsed(true);
            resetTokenRepository.save(token);
            throw new BadRequestException("INVALID_RESET_CODE", "Invalid or expired verification code");
        }
        if (!Objects.equals(token.getCode(), request.getCode().trim())) {
            token.setAttempts(token.getAttempts() + 1);
            if (token.getAttempts() >= RESET_MAX_ATTEMPTS) token.setUsed(true);
            resetTokenRepository.save(token);
            throw new BadRequestException("INVALID_RESET_CODE", "Invalid or expired verification code");
        }
        User user = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BadRequestException("INVALID_RESET_CODE", "Invalid or expired verification code"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        token.setUsed(true);
        resetTokenRepository.save(token);
        refreshTokenService.revokeAll(user.getId());
        sendPasswordChangedEmail(user);
    }

    private void sendPasswordChangedEmail(User user) {
        emailService.send(user.getEmail(), "Your PetShop password was changed",
                "Hello " + user.getFullName() + ",\n\nYour PetShop password was changed successfully. "
                        + "You can now sign in with your new password. For your security, all other sessions were signed out.\n\n"
                        + "If you did not make this change, reset your password again right away and contact our support team.\n\n"
                        + properties.getFrontendUrl());
    }

    private AuthResponse issueTokens(User user, boolean rememberMe) {
        return buildAuthResponse(user, refreshTokenService.issue(user.getId(), rememberMe));
    }

    private AuthResponse buildAuthResponse(User user, RefreshTokenService.IssuedToken refresh) {
        UserPrincipal principal = UserPrincipal.create(user);
        UserResponse userResponse = userMapper.toResponse(user);
        return AuthResponse.builder()
                .token(tokenProvider.generateToken(principal))
                .type("Bearer")
                .expiresIn(tokenProvider.getAccessTokenSeconds())
                .refreshToken(refresh.rawToken())
                .refreshTokenExpiresAt(refresh.expiresAt())
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(userResponse.getRole())
                .avatarUrl(user.getAvatarUrl())
                .user(userResponse)
                .build();
    }

    private void validateRegistration(String email, String password, String confirmPassword, Boolean agreeTerms) {
        if (!Objects.equals(password, confirmPassword)) {
            throw new BadRequestException("PASSWORD_MISMATCH", "Password and confirmation do not match");
        }
        if (Boolean.FALSE.equals(agreeTerms)) {
            throw new BadRequestException("TERMS_NOT_ACCEPTED", "You must accept the terms to register");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizeEmail(email))) {
            throw new ConflictException("EMAIL_TAKEN", "This email address is already registered");
        }
    }

    private User loadUser(UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        return userRepository.findById(currentUser.getId())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
