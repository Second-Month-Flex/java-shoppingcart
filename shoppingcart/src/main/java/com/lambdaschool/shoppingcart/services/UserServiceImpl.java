package com.lambdaschool.shoppingcart.services;

import com.lambdaschool.shoppingcart.exceptions.ResourceFoundException;
import com.lambdaschool.shoppingcart.exceptions.ResourceNotFoundException;
import com.lambdaschool.shoppingcart.models.Role;
import com.lambdaschool.shoppingcart.models.User;
import com.lambdaschool.shoppingcart.models.UserRoles;
import com.lambdaschool.shoppingcart.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service(value = "userService")
public class UserServiceImpl
        implements UserService
{
    /**
     * Connects this service to the users repository
     */
    @Autowired
    private UserRepository userrepos;

    @Autowired
    private CartService cartService;


    @Autowired
    RoleService roleService;

    @Autowired
    private HelperFunctions helper;

    @Autowired
    private UserAuditing userAuditing;


    @Override
    public List<User> findAll()
    {
        List<User> list = new ArrayList<>();
        /*
         * findAll returns an iterator set.
         * iterate over the iterator set and add each element to an array list.
         */
        userrepos.findAll()
                .iterator()
                .forEachRemaining(list::add);
        return list;
    }

    @Override
    public User findUserById(long id)
    {
        return userrepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
    }

    @Transactional
    @Override
    public void delete(long id)
    {
        userrepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
        userrepos.deleteById(id);
    }

    @Transactional
    @Override
    public User save(User user)
    {
        User newUser = new User();

        newUser.setUsername(user.getUsername().toLowerCase());
        newUser.setComments(user.getComments());
//        newUser.setPrimaryemail(user.getPrimaryemail()
//                .toLowerCase());

        newUser.getRoles()
                .clear();
        for (UserRoles ur : user.getRoles())
        {
            Role addRole = roleService.findRoleById(ur.getRole()
                    .getRoleid());

            newUser.getRoles()
                    .add(new UserRoles(newUser, addRole));
        }



        if (user.getCarts()
                .size() > 0)
        {
            throw new ResourceFoundException("Carts are not added through users");
        }
        return userrepos.save(newUser);
    }

    @Override
    public User findByName(String name) {
        User u = userrepos.findByUsername(name.toLowerCase());
        if (u == null)
        {
            throw new ResourceNotFoundException("User name " + name + " not found!");
        }
        return u;
    }

    @Transactional
    @Override
    public User update(User user, long id)
    {
        User currentUser = findUserById(id);

        if (helper.isAuthorizedToMakeChange(currentUser.getUsername()))
        {
            if (user.getUsername() != null)
            {
                currentUser.setUsername(user.getUsername()
                        .toLowerCase());
            }

            if (user.getPassword() != null)
            {
                currentUser.setPasswordNoEncrypt(user.getPassword());
            }

            if (user.getRoles()
                    .size() > 0)
            {
                // delete the roles for the old user we are replacing
                for (UserRoles ur : currentUser.getRoles())
                {
                    deleteUserRole(ur.getUser()
                                    .getUserid(),
                            ur.getRole()
                                    .getRoleid());
                }

                // add the new roles for the user we are replacing
                for (UserRoles ur : user.getRoles())
                {
                    addUserRole(currentUser.getUserid(),
                            ur.getRole()
                                    .getRoleid());
                }
            }

            return userrepos.save(currentUser);
        } else
        {
            {
                // note we should never get to this line but is needed for the compiler
                // to recognize that this exception can be thrown
                throw new ResourceNotFoundException("This user is not authorized to make change");
            }
        }
    }
    @Transactional
    @Override
    public void deleteUserRole(
            long userid,
            long roleid)
    {
        userrepos.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + userid + " not found!"));
        roleService.findRoleById(roleid);

        if (userrepos.checkUserRolesCombo(userid,
                roleid)
                .getCount() > 0)
        {
            userrepos.deleteUserRoles(userid,
                    roleid);
        } else
        {
            throw new ResourceNotFoundException("Role and User Combination Does Not Exists");
        }
    }

    @Transactional
    @Override
    public void addUserRole(
            long userid,
            long roleid)
    {
        userrepos.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + userid + " not found!"));
        roleService.findRoleById(roleid);

        if (userrepos.checkUserRolesCombo(userid,
                roleid)
                .getCount() <= 0)
        {
            userrepos.insertUserRoles(userAuditing.getCurrentAuditor()
                            .get(),
                    userid,
                    roleid);
        } else
        {
            throw new ResourceFoundException("Role and User Combination Already Exists");
        }
    }

}
