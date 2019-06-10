package uz.maroqand.ecology.core.component;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
public class UserDetailsImpl implements UserDetails {

    private User user;
    private Set<GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.user = user;
        if (authorities == null) authorities = new HashSet<>();

        user.getRoles().forEach(userRole -> {
            System.out.println("roleName="+userRole.getRole().name());
            authorities.add(new SimpleGrantedAuthority(userRole.getRole().name()));
        });

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
        return user.getEnabled();
    }

}
