package com.lambdaschool.shoppingcart.services;

public interface HelperFunctions
{
    /**
     * Checks to see if the authenticated user has access to modify the requested user's information
     *
     * @param username The user name of the user whose data is requested to be changed. This should either match the authenticated user
     *                 or the authenticate must have the role ADMIN
     * @return true if the user can make the modifications, otherwise an exception is thrown
     */
    boolean isAuthorizedToMakeChange(String username);
}
