package ma.xproce.club_gestion.service;

import ma.xproce.club_gestion.dao.entities.Club;

import java.util.List;

public interface IAdherentService {
    List<Club> getClubsByAdherent(Long adherentId);
}
