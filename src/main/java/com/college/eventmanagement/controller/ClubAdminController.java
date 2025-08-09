package com.college.eventmanagement.controller;

import com.college.eventmanagement.model.Club;
import com.college.eventmanagement.model.ClubMember;
import com.college.eventmanagement.model.MemberRole;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.service.ClubService;
import com.college.eventmanagement.service.ClubMemberService;
import com.college.eventmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clubs/{clubId}/admin")
public class ClubAdminController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private UserService userService;

    // Check if current user can manage the club
    private boolean canUserManageClub(Club club, User user) {
        return club.getHead().equals(user) || clubMemberService.canUserManageClub(club, user);
    }

    // Club Admin Dashboard
    @GetMapping
    public String clubAdminDashboard(@PathVariable Long clubId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Club> clubOpt = clubService.getClubById(clubId);
        if (clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Club not found");
            return "redirect:/clubs";
        }

        Club club = clubOpt.get();
        
        // Check authentication and permissions
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Please login to access club admin");
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.getUserByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/clubs";
        }

        User currentUser = userOpt.get();
        if (!canUserManageClub(club, currentUser)) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to manage this club");
            return "redirect:/clubs/" + clubId;
        }

        // Get club statistics
        List<ClubMember> members = clubMemberService.getActiveMembersByClub(club);
        long memberCount = clubMemberService.getActiveMemberCount(club);
        
        model.addAttribute("club", club);
        model.addAttribute("members", members);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("title", club.getClubName() + " - Admin Dashboard");
        
        return "clubs/admin/dashboard";
    }

    // Manage Members
    @GetMapping("/members")
    public String manageMembers(@PathVariable Long clubId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Club> clubOpt = clubService.getClubById(clubId);
        if (clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Club not found");
            return "redirect:/clubs";
        }

        Club club = clubOpt.get();
        
        // Check permissions
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.getUserByEmail(auth.getName());
        if (userOpt.isEmpty() || !canUserManageClub(club, userOpt.get())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to manage this club");
            return "redirect:/clubs/" + clubId;
        }

        List<ClubMember> members = clubMemberService.getActiveMembersByClubWithUser(clubId);
        
        model.addAttribute("club", club);
        model.addAttribute("members", members);
        model.addAttribute("memberRoles", MemberRole.values());
        model.addAttribute("title", club.getClubName() + " - Manage Members");
        
        return "clubs/admin/members";
    }

    // Update Member Role
    @PostMapping("/members/{memberId}/role")
    public String updateMemberRole(@PathVariable Long clubId, 
                                 @PathVariable Long memberId,
                                 @RequestParam MemberRole newRole,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<Club> clubOpt = clubService.getClubById(clubId);
            if (clubOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Club not found");
                return "redirect:/clubs";
            }

            Club club = clubOpt.get();
            
            // Check permissions
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login";
            }

            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            if (userOpt.isEmpty() || !canUserManageClub(club, userOpt.get())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to manage this club");
                return "redirect:/clubs/" + clubId;
            }

            // Find the member and update role
            Optional<ClubMember> memberOpt = clubMemberService.getAllMembers().stream()
                .filter(m -> m.getMemberId().equals(memberId))
                .findFirst();
                
            if (memberOpt.isPresent()) {
                ClubMember member = memberOpt.get();
                clubMemberService.updateMemberRole(club, member.getUser(), newRole);
                redirectAttributes.addFlashAttribute("success", "Member role updated successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Member not found");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating member role: " + e.getMessage());
        }

        return "redirect:/clubs/" + clubId + "/admin/members";
    }

    // Remove Member
    @PostMapping("/members/{memberId}/remove")
    public String removeMember(@PathVariable Long clubId, 
                             @PathVariable Long memberId,
                             RedirectAttributes redirectAttributes) {
        try {
            Optional<Club> clubOpt = clubService.getClubById(clubId);
            if (clubOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Club not found");
                return "redirect:/clubs";
            }

            Club club = clubOpt.get();
            
            // Check permissions
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login";
            }

            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            if (userOpt.isEmpty() || !canUserManageClub(club, userOpt.get())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to manage this club");
                return "redirect:/clubs/" + clubId;
            }

            // Find the member and remove
            Optional<ClubMember> memberOpt = clubMemberService.getAllMembers().stream()
                .filter(m -> m.getMemberId().equals(memberId))
                .findFirst();
                
            if (memberOpt.isPresent()) {
                ClubMember member = memberOpt.get();
                clubMemberService.removeMemberFromClub(club, member.getUser());
                redirectAttributes.addFlashAttribute("success", "Member removed successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Member not found");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing member: " + e.getMessage());
        }

        return "redirect:/clubs/" + clubId + "/admin/members";
    }

    // Club Settings
    @GetMapping("/settings")
    public String clubSettings(@PathVariable Long clubId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Club> clubOpt = clubService.getClubById(clubId);
        if (clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Club not found");
            return "redirect:/clubs";
        }

        Club club = clubOpt.get();
        
        // Check permissions
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.getUserByEmail(auth.getName());
        if (userOpt.isEmpty() || !canUserManageClub(club, userOpt.get())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to manage this club");
            return "redirect:/clubs/" + clubId;
        }

        model.addAttribute("club", club);
        model.addAttribute("title", club.getClubName() + " - Settings");
        
        return "clubs/admin/settings";
    }

    // Update Club Settings
    @PostMapping("/settings")
    public String updateClubSettings(@PathVariable Long clubId,
                                   @ModelAttribute Club clubForm,
                                   RedirectAttributes redirectAttributes) {
        try {
            Optional<Club> clubOpt = clubService.getClubById(clubId);
            if (clubOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Club not found");
                return "redirect:/clubs";
            }

            Club club = clubOpt.get();
            
            // Check permissions
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login";
            }

            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            if (userOpt.isEmpty() || !canUserManageClub(club, userOpt.get())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to manage this club");
                return "redirect:/clubs/" + clubId;
            }

            // Update club information
            club.setDescription(clubForm.getDescription());
            club.setContactEmail(clubForm.getContactEmail());
            club.setLogoUrl(clubForm.getLogoUrl());
            
            clubService.saveClub(club);
            redirectAttributes.addFlashAttribute("success", "Club settings updated successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating club settings: " + e.getMessage());
        }

        return "redirect:/clubs/" + clubId + "/admin/settings";
    }
}
