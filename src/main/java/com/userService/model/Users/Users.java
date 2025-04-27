package com.userService.model.Users;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "account_locked")
    private Boolean accountLocked;

    @Column(name = "password_changed")
    private Boolean passwordChanged;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    // ======= GETTERS Y SETTERS =======

    public Integer getUser_id() { return user_id; }

    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getLastname() { return lastname; }

    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public Boolean getAccountLocked() { return accountLocked; }

    public void setAccountLocked(Boolean accountLocked) { this.accountLocked = accountLocked; }

    public Boolean getPasswordChanged() { return passwordChanged; }

    public void setPasswordChanged(Boolean passwordChanged) { this.passwordChanged = passwordChanged; }

    public Roles getRole() { return role; }

    public void setRole(Roles role) { this.role = role; }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    // Métodos de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
