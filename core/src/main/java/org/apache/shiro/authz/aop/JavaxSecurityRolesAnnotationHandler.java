/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.authz.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import javax.annotation.security.RolesAllowed;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Checks to see if a @{@link RolesAllowed}, annotation is declared, and if so, performs
 * a role check to see if the calling <code>Subject</code> is allowed to proceed.
 *
 * @since 1.4.0
 */
public class JavaxSecurityRolesAnnotationHandler extends AuthorizingAnnotationHandler {

    /**
     * Default no-argument constructor that ensures this handler looks for
     * {@link RequiresRoles RequiresRoles} annotations.
     */
    public JavaxSecurityRolesAnnotationHandler() {
        super(RolesAllowed.class);
    }

    /**
     * Ensures that the calling <code>Subject</code> has the Annotation's specified roles, and if not, throws an
     * <code>AuthorizingException</code> indicating that access is denied.
     *
     * @param a the RolesAllowed annotation to use to check for one or more roles
     * @throws AuthorizationException
     *          if the calling <code>Subject</code> does not have the role(s) necessary to
     *          proceed.
     */
    public void assertAuthorized(Annotation a) throws AuthorizationException {

        if (!(a instanceof RolesAllowed)) {
            return;
        }

        RolesAllowed rannotation = (RolesAllowed) a;
        String[] roles = rannotation.value();

        if (roles.length == 1) {
            getSubject().checkRole(roles[0]);
            return;
        }

        // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
        boolean hasAtLeastOneRole = false;

        for (String role : roles) {
            if (getSubject().hasRole(role)) {
                hasAtLeastOneRole = true;
            }
        }

        // Cause the exception if none of the role match, note that the exception message will be a bit misleading
        if (!hasAtLeastOneRole)
        {
            getSubject().checkRole(roles[0]);
        }
    }

}
