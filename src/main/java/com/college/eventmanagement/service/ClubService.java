package com.college.eventmanagement.service;

import com.college.eventmanagement.model.Club;
import com.college.eventmanagement.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public List<Club> getActiveClubs() {
        return clubRepository.findByIsActiveTrue();
    }

    public Optional<Club> getClubById(Long id) {
        return clubRepository.findById(id);
    }

    public Optional<Club> getClubByName(String name) {
        return clubRepository.findByClubName(name);
    }

    public Club saveClub(Club club) {
        if (club.getCreatedAt() == null) {
            club.setCreatedAt(LocalDateTime.now());
        }
        if (club.getIsActive() == null) {
            club.setIsActive(true);
        }
        return clubRepository.save(club);
    }

    public void deleteClub(Long id) {
        clubRepository.deleteById(id);
    }

    public void deactivateClub(Long id) {
        Optional<Club> clubOpt = clubRepository.findById(id);
        if (clubOpt.isPresent()) {
            Club club = clubOpt.get();
            club.setIsActive(false);
            clubRepository.save(club);
        }
    }

    public boolean existsByName(String name) {
        return clubRepository.existsByClubName(name);
    }

    public long getTotalClubCount() {
        return clubRepository.count();
    }

    public long getActiveClubCount() {
        return clubRepository.countByIsActiveTrue();
    }

    public List<Club> searchClubs(String keyword) {
        return clubRepository.findByClubNameContainingIgnoreCaseAndIsActiveTrue(keyword);
    }
}
