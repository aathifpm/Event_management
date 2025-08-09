package com.college.eventmanagement.service;

import com.college.eventmanagement.model.Club;
import com.college.eventmanagement.model.ClubMember;
import com.college.eventmanagement.model.MemberRole;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClubMemberService {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    public List<ClubMember> getAllMembers() {
        return clubMemberRepository.findAll();
    }

    public List<ClubMember> getMembersByClub(Club club) {
        return clubMemberRepository.findByClub(club);
    }

    public List<ClubMember> getActiveMembersByClub(Club club) {
        return clubMemberRepository.findByClubAndIsActiveTrue(club);
    }

    public List<ClubMember> getMembersByUser(User user) {
        return clubMemberRepository.findByUser(user);
    }

    public List<ClubMember> getActiveMembersByUser(User user) {
        return clubMemberRepository.findByUserAndIsActiveTrue(user);
    }

    public Optional<ClubMember> getMemberByClubAndUser(Club club, User user) {
        return clubMemberRepository.findByClubAndUser(club, user);
    }

    public boolean isUserMemberOfClub(Club club, User user) {
        return clubMemberRepository.existsByClubAndUser(club, user);
    }

    public ClubMember addMemberToClub(Club club, User user, MemberRole role) {
        // Check if user is already a member
        if (isUserMemberOfClub(club, user)) {
            throw new RuntimeException("User is already a member of this club");
        }

        ClubMember clubMember = new ClubMember();
        clubMember.setClub(club);
        clubMember.setUser(user);
        clubMember.setMemberRole(role);
        clubMember.setJoinDate(LocalDateTime.now());
        clubMember.setIsActive(true);

        return clubMemberRepository.save(clubMember);
    }

    public ClubMember joinClub(Club club, User user) {
        return addMemberToClub(club, user, MemberRole.MEMBER);
    }

    public void removeMemberFromClub(Club club, User user) {
        Optional<ClubMember> memberOpt = clubMemberRepository.findByClubAndUser(club, user);
        if (memberOpt.isPresent()) {
            ClubMember member = memberOpt.get();
            member.setIsActive(false);
            clubMemberRepository.save(member);
        }
    }

    public void updateMemberRole(Club club, User user, MemberRole newRole) {
        Optional<ClubMember> memberOpt = clubMemberRepository.findByClubAndUser(club, user);
        if (memberOpt.isPresent()) {
            ClubMember member = memberOpt.get();
            member.setMemberRole(newRole);
            clubMemberRepository.save(member);
        }
    }

    public long getActiveMemberCount(Club club) {
        return clubMemberRepository.countActiveByClubId(club.getClubId());
    }

    public List<ClubMember> getMembersByRole(Club club, MemberRole role) {
        return clubMemberRepository.findByClubIdAndMemberRoleAndIsActiveTrue(club.getClubId(), role);
    }

    public List<ClubMember> getClubHeads(Club club) {
        return getMembersByRole(club, MemberRole.VICE_HEAD);
    }

    public List<ClubMember> getClubAdmins(Club club) {
        return getMembersByRole(club, MemberRole.MODERATOR);
    }

    public List<ClubMember> getRegularMembers(Club club) {
        return getMembersByRole(club, MemberRole.MEMBER);
    }

    public boolean isUserHeadOfClub(Club club, User user) {
        List<ClubMember> heads = getClubHeads(club);
        return heads.stream().anyMatch(member -> member.getUser().equals(user));
    }

    public boolean isUserAdminOfClub(Club club, User user) {
        List<ClubMember> admins = getClubAdmins(club);
        return admins.stream().anyMatch(member -> member.getUser().equals(user));
    }

    public boolean canUserManageClub(Club club, User user) {
        return isUserHeadOfClub(club, user) || isUserAdminOfClub(club, user);
    }

    public ClubMember saveMember(ClubMember clubMember) {
        if (clubMember.getJoinDate() == null) {
            clubMember.setJoinDate(LocalDateTime.now());
        }
        if (clubMember.getIsActive() == null) {
            clubMember.setIsActive(true);
        }
        return clubMemberRepository.save(clubMember);
    }

    public void deleteMember(Long memberId) {
        clubMemberRepository.deleteById(memberId);
    }

    public List<ClubMember> getActiveMembersByClubWithUser(Long clubId) {
        return clubMemberRepository.findActiveByClubIdWithUser(clubId);
    }
}
