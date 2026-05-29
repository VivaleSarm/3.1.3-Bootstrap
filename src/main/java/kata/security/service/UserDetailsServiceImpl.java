package kata.security.service;

import kata.security.model.User;
import kata.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;
    private final TransactionTemplate transactionTemplate;

    public UserDetailsServiceImpl(UserRepository userRepository, TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.transactionTemplate = transactionTemplate;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user for authentication: {}", email);

        return transactionTemplate.execute(status -> {
            User user = userRepository.findByEmailWithRoles(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

            user.getRoles().size();
            log.info("User loaded successfully: {}, roles: {}", user.getEmail(), user.getRoles().size());

            return user;
        });
    }
}

