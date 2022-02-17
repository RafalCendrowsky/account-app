package com.rafalcendrowski.AccountApplication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.*;

@Service
class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username.toLowerCase(Locale.ROOT));
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        } else {
            return user;
        }
    }
}

@Repository
interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}


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

    public User(String username, String password, String name, String lastname) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.roles.add(Role.of("ROLE_USER"));
    }

    public Map<String, Object> getUserMap() {
        List<String> rolesList = getRolesAsStrings();
        rolesList.sort(String::compareTo);
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

    public List<String> getRolesAsStrings() {
        List<String> rolesToString = new ArrayList<>();
        for(Role role: roles) {
            rolesToString.add(role.getRole());
        }
        return rolesToString;
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

@Embeddable
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
class Role {
    @Pattern(regexp = "ROLE_(ADMINISTRATOR|USER|ACCOUNTANT)")
    private String role;
}