package com.rafalcendrowski.AccountApplication.user;

import com.rafalcendrowski.AccountApplication.payment.Payment;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String name;
    private String lastname;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();
    @OneToMany
    private List<Payment> payments;

    public enum Role {
        USER,
        ADMINISTRATOR,
        ACCOUNTANT,
        AUDITOR;


        @Override
        public String toString() {
            return "ROLE_".concat(super.toString());
        }
    }

    public User(String username, String password, String name, String lastname) {
        this.username = username.toLowerCase(Locale.ROOT);
        this.password = password;
        this.name = name;
        this.lastname = lastname;
    }

    public Map<String, Object> getUserMap() {
        List<String> rolesList = getRolesAsStrings();
        rolesList.sort(String::compareTo);
        String id = (this.id == null) ? "null" : String.valueOf(this.id);
        return Map.of("lastname", lastname, "name", name,
                "id", id, "email", username, "roles", rolesList);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        for(Role role: roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.toString()));
        }
        return grantedAuthorities;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username.toLowerCase(Locale.ROOT);
    }

    public List<String> getRolesAsStrings() {
        List<String> rolesToString = new ArrayList<>();
        for(Role role: roles) {
            rolesToString.add(role.toString());
        }
        return rolesToString;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean addRole(Role role) {
        return roles.add(role);
    }

    public boolean removeRole(Role role) {
        return roles.remove(role);
    }

    public boolean addPayment(Payment payment) {
        return payments.add(payment);
    }

    public boolean removePayment(Payment payment) {
        return  payments.remove(payment);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
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
