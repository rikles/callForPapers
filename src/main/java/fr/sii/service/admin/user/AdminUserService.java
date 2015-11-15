package fr.sii.service.admin.user;

import com.google.appengine.api.users.UserServiceFactory;
import fr.sii.domain.exception.NotFoundException;
import fr.sii.entity.AdminUser;
import fr.sii.repository.AdminUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminUserService {

    @Autowired
    private AdminUserRepo adminUserRepo;

    public void setAdminUserRepo(AdminUserRepo adminUserRepo) {
        this.adminUserRepo = adminUserRepo;
    }

    public List<AdminUser> findAll()
    {
        return adminUserRepo.findAll();
    }

    public AdminUser getCurrentUser() throws NotFoundException {
        com.google.appengine.api.users.UserService userService = UserServiceFactory.getUserService();
        com.google.appengine.api.users.User user = userService.getCurrentUser();
        if(user == null) throw new NotFoundException("User not found");

        String id = user.getUserId();
        String idParsed = id.substring(0, id.length() - 2);
        int userId = Integer.parseInt(idParsed);
        AdminUser admin = adminUserRepo.findOne(userId);
        if (admin == null) {
            admin = save(user);
        }

        return admin;
    }

    public void deleteAll()
    {
        adminUserRepo.deleteAll();
    }

    public AdminUser save(com.google.appengine.api.users.User user) {
        String id = user.getUserId();
        String idParsed = id.substring(0, id.length() - 2);
        int userId = Integer.parseInt(idParsed);

        AdminUser adminUser = new AdminUser();
        adminUser.setId(userId);
        adminUser.setName(user.getNickname());
        adminUser.setEmail(user.getEmail());

        AdminUser saved = adminUserRepo.save(adminUser);
        adminUserRepo.flush();
        return saved;
    }
}