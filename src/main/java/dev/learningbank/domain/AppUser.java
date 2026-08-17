package dev.learningbank.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String username;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;
    @Column(name = "customer_number", unique = true, length = 10)
    private String customerNumber;
    @Column(length = 160)
    private String email;
    @Column(length = 30)
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.CUSTOMER;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    protected AppUser() {}
    public AppUser(String username, String passwordHash, String displayName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
    }
    public AppUser(String username, String passwordHash, String displayName, String customerNumber,
                   String email, String phone, UserRole role) {
        this(username, passwordHash, displayName);
        this.customerNumber = customerNumber;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public String getCustomerNumber() { return customerNumber; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public UserRole getRole() { return role; }
    public CustomerStatus getStatus() { return status; }
    public void updateProfile(String displayName, String email, String phone, CustomerStatus status) {
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }
}
