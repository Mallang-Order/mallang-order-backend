package com.mallang.mallnagorder.admin.service;

import com.mallang.mallnagorder.admin.domain.Admin;
import com.mallang.mallnagorder.admin.dto.AdminDetails;
import com.mallang.mallnagorder.admin.repository.AdminRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //DB에서 조회
        Admin admin = adminRepository.findByEmail(email);

        if (admin == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        } else {
            //AdminDetails에 담아 리턴하면 AuthenticationManager가 검증
            return new AdminDetails(admin);
        }


//        if (member != null) {
//            //MemberDetails에 담아 리턴하면 AuthenticationManager가 검증
//            return new CustomUserDetails(member);
//        }
//
//        return null;
    }
}