package com.sipios.bank.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import com.sipios.bank.model.Chat;
import com.sipios.bank.model.Role;
import com.sipios.bank.model.User;
import com.sipios.bank.repository.ChatRepository;
import com.sipios.bank.repository.RoleRepository;
import com.sipios.bank.repository.UserRepository;

import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @PostConstruct
    public void registerUsers() throws NoSuchAlgorithmException {
        List<Chat> chats = new ArrayList<>();


        Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
        Role superAdminRole = createRoleIfNotFound("ROLE_SUPER_ADMIN");
        User admin = new User();
        admin.setId(2L);
        admin.setUsername("adminSipios");
        admin.setPassword(passwordEncoder.encode("iojaze879K!"));
        admin.setRoles(Arrays.asList(adminRole));
        userRepository.save(admin);

        Role userRole = createRoleIfNotFound("ROLE_USER");
        Role userRolePremium = createRoleIfNotFound("ROLE_USER_PREMIUM");
        Role userRoleSuperPremium = createRoleIfNotFound("ROLE_USER_SUPER_PREMIUM");
        List<Long> ids = Arrays.asList(
            13L, 59L, 743L, 930L, 1390L, 1578L, 1739L, 2003L, 2198L, 2340L,
            2603L, 3029L, 3239L, 3475L, 3610L, 3823L, 4000L, 4129L, 4444L, 4511L,
            12603L, 13029L, 13239L, 13475L, 13610L, 13823L, 14000L, 14129L, 14444L, 14511L
        );

        ids.forEach(id -> {
            setUpUser("User" + id, id, chats, userRole, admin, userRolePremium);
        });

        Chat chat2 = new Chat();
        chats.add(chat2);
        chatRepository.save(chat2);
        User superPremiumUser = createUserIfNotFound(19480L, "Jeff Bezos", "test3", Arrays.asList(userRoleSuperPremium), Arrays.asList(chat2), null, 2000000000D);
        String salt = "saltPepperOliveOil";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(salt.getBytes());
        byte[] digest1 = digest.digest("4808".getBytes());
        superPremiumUser.setPinCode(HexUtils.toHexString(digest1));
        userRepository.save(superPremiumUser);

        createUserIfNotFound(19481L, "michaelm", "Sipios_Hack_The_Bank", Arrays.asList(superAdminRole), Arrays.asList(), null, null);

        admin.setChats(chats);
        userRepository.save(admin);
    }

    @Transactional
    private void setUpUser(String username, Long id, List<Chat> chats, Role role, User advisor, Role userRolePremium) {
        createUserIfNotFound(id, username, "RushClovis", Arrays.asList(role), Arrays.asList(), advisor, 2000D);

        Chat chat = new Chat();
        chats.add(chat);
        chatRepository.save(chat);
        createUserIfNotFound(id + 1L, "Jean Louis " + id, "oijzaehui12!", Arrays.asList(userRolePremium), Arrays.asList(chat), advisor, 2000D);
    }

    @Transactional
    private User createUserIfNotFound(Long id, String name, String password, List<Role> roles, List<Chat> chats, User advisor, Double money) {
        User user = userRepository.findByUsername(name);
        if (user == null) {
            user = new User();
            user.setId(id);
            user.setUsername(name);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(roles);
            user.setChats(chats);
            user.setAdvisor(advisor);
            user.setMoney(money);
            UUID uuid = UUID.randomUUID();
            user.setIban(uuid.toString());
            userRepository.save(user);
        }

        return user;
    }

    @Transactional
    private Role createRoleIfNotFound(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleRepository.save(role);
        }
        return role;
    }

}
