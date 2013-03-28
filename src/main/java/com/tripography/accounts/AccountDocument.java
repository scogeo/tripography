package com.tripography.accounts;

import com.tripography.mongodb.DatedDocument;
import com.tripography.web.security.PasswordUtil;
import com.tripography.web.security.PersistentLoginToken;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gscott
 */
@Document(collection = "accounts")
public class AccountDocument extends DatedDocument implements Account {

    @Field("u")
    @NotEmpty
    @Username
    private String _username;

    // lower-cased version of username for queries.
    @Field("U") @Indexed(unique = true)
    @NotEmpty @Username
    @SuppressWarnings("unused")
    private String _usernameLowerCase;

    @Field("e")
    @NotEmpty @Email
    private String _email;

    @Field("E") @Indexed (unique = true)
    @NotEmpty @Email
    @SuppressWarnings("unused")
    private String _emailLowerCase;

    @Field("p")
    @NotEmpty @Size(min = 64, max = 64) // SHA-256 size is 64
    private String _password;

    @Field("n")
    @NotEmpty
    private String _fullName;

    @Field("s") // roleS
    private List<String> _roles;

    //@Field("d") // profile Detail
    //private AccountProfileDocument _profile;

    // A String that is used to verify email
    @Field("ev")
    private String _emailVerifier;


    // Field to receive the login tokens from the DB.  Note that we do not have accessors to set this
    // property.  It is managed directly from the Account service to comply with the security requirements
    // for managing tokens
    @Field("t") @Indexed(name = "t.s", unique = true, sparse = true)
    private List<PersistentLoginToken> _loginTokens;

    public AccountDocument() {
        super();
    }

    @PersistenceConstructor
    public AccountDocument(ObjectId id) {
        super(id);
    }

    public String getUsername() {
        return _username;
    }

    public String getFullname() {
        return _fullName;
    }

    public void setFullname(String fullname) {
        _fullName = fullname;
    }

    public String getEmail() {
        return _email;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setEmail(String email) {
        _email = email;
        _emailLowerCase = _email.toLowerCase();
    }

    public void setUsername(String username) {
        _username = username;
        _usernameLowerCase = username.toLowerCase();
    }

    public String getHashedPassword() {
        return _password;
    }

    public void setClearTextPassword(String password) {
        _password = PasswordUtil.hashPassword(id.toString(), password);
    }

    public State getState() {
        return State.ACTIVE;
    }

    public String getEmailVerififer() {
        return _emailVerifier;
    }

    public void requestEmailVerification() {
        //_emailVerifier = SecureRandom.getInstance();
    }

    public List<String> getRoles() {
        if (_roles == null) {
            return Collections.emptyList();
        }
        else {
            return Collections.unmodifiableList(_roles);
        }
    }

    public void setRoles(List<String> roles) {
        // keep a copy of the list.
        _roles = new ArrayList<String>(roles);
    }

    public List<PersistentLoginToken> getLoginTokens() {
        if (_loginTokens == null) {
            return Collections.emptyList();
        }
        else {
            return Collections.unmodifiableList(_loginTokens);
        }
    }

}
