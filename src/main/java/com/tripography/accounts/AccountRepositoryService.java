package com.tripography.accounts;

import com.mongodb.MongoException;
import com.tripography.dao.UniqueKeyException;
import com.tripography.web.security.PasswordUtil;
import com.tripography.web.security.PersistentLoginToken;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Implementation of AccountService that stores accounts in a MongoDB Repository.  This is a fairly
 * light wrapper to abstract MongoDB from the rest of the application.
 *
 * Its main purpose is to provide validation enforcement prior to insertion into the Database.
 *
 */
@Service("accountService")
public class AccountRepositoryService implements AccountService {

    private static final Logger logger = Logger.getLogger(AccountRepositoryService.class.getName());

    @Autowired
    private Validator validator;

    @Autowired
    private AccountRepository repository;

    @Autowired
    private MongoTemplate mongo;

    //@Autowired
    //private JobQueueService jobQueue;

    //@Autowired
    //private StorageService profileImageStorageService;

    /** Creates a new Account object
     *
     * This object is ephemeral and must be saved.
     *
     * @return
     */
    public Account newAccount() {
        return new AccountDocument();
    }

    private AccountDocument checkAccount(Account account) {
        if (account instanceof AccountDocument) {
            return (AccountDocument) account;
        }
        throw new IllegalArgumentException("Account not owned by this service object");
    }

    private void validate(Object object) {
        Set<? extends ConstraintViolation<?>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }

    public boolean isUsernameAvailable(String username) {
        // TODO don't load the full user, just check existence
        return findByUsername(username) == null;
    }

    public boolean isEmailAvailable(String email) {
        // TODO don't load the full user, just check existence
        return findByEmail(email) == null;
    }

    @SuppressWarnings("unchecked")
    public List<String> suggestAvailableUsernames(Account account) {
        return Collections.EMPTY_LIST;
    }

    public void create(Account a) {
        validate(a);

        AccountDocument account = checkAccount(a);



        // set the account status to unverified.
        // set the role to unverified account.

        // save the account to the database
        repository.save(checkAccount(account));

        // Enqueue a job to complete account creation
        // Job will send email verification message

    }

    public void update(Account account) {
        validate(account);
        try {
            repository.save(checkAccount(account));
        }
        catch (RuntimeException e) {
            throw convertException(e);
        }
    }

    public void delete(String id) {
        repository.delete(new ObjectId(id));
    }

    public void delete(Account account) {
        repository.delete(checkAccount(account));
    }

    public Account findById(String id) {
        return repository.findOne(new ObjectId(id));
    }

    public long numberOfAccounts() {
        return repository.count();
    }

    public Account findByUsername(String username) {
        return repository.findByUsername(username.toLowerCase());
    }

    public Account findByEmail(String email) {
        return repository.findByEmail(email.toLowerCase());
    }

    public Account findByPersistentTokenSeries(String series) {
        AccountDocument doc = repository.findByPersistentTokenSeries(series);
        return doc;
    }


    public void updatePassword(String id, String clearTextPassword) {
        String hashedPassword = PasswordUtil.hashPassword(id, clearTextPassword);
        // Update the password and clear all persistent login tokens
        mongo.updateFirst(new Query(where("_id").is(id)), new Update().set("pw", hashedPassword).unset("loginTokens"),
                AccountDocument.class);
    }

    /*
    public void setProfilePhoto(String id, byte[] image, String mimeType) {

        String extension = null;

        AccountProfilePhotoDocument photoDocument = new AccountProfilePhotoDocument();

        logger.info("photo doc id is " + photoDocument.getId());

        if (mimeType.equals("image/png")) {
            extension = "png";
        }
        else if (mimeType.equals("image/gif")) {
            extension = "gif";
        }
        else if (mimeType.equals("image/jpeg")) {
            extension = "jpg";
        }
        else {
            throw new IllegalArgumentException("Unsupported mimeType " + mimeType);
        }

        photoDocument.setOriginalType(extension);
        String name = photoDocument.getId() + "/original." + extension;
        profileImageStorageService.writePublicFile(name, image, mimeType);

        // Now process photo

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(image);
            BufferedImage originalImage = ImageIO.read(bis);

            BufferedImage croppedImage = ImageUtil.cropToSquare(originalImage);

            // write images
            profileImageStorageService.writePublicImage(photoDocument.getId() + "/sq_med.png",
                    ImageUtil.getScaledInstance(croppedImage, 48, 48, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true));

            profileImageStorageService.writePublicImage(photoDocument.getId() + "/sq_small.png",
                    ImageUtil.getScaledInstance(croppedImage, 24, 24, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true));

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    */

    public void addPersistentLoginTokenForUser(String id, PersistentLoginToken token) {
        //logger.info("adding loging for " + id + " token " + token);

        mongo.updateFirst(new Query(where("_id").is(id)),
                new Update().push("t", token),
                AccountDocument.class);
    }

    public void updatePersistentLoginTokenForUser(String id, PersistentLoginToken token) {
        mongo.updateFirst(new Query(where("t").elemMatch(where("s").is(token.getSeries()))),
                new Update().set("t.$.t", token.getToken())
                        .set("t.$.d", token.getDate()),
                AccountDocument.class);
    }

    public void deleteAllPersistentTokensForUser(String id) {
        mongo.updateFirst(new Query(where("_id").is(id)), new Update().unset("t"), AccountDocument.class);
    }

    private RuntimeException convertException(RuntimeException t) {
        if (t instanceof DuplicateKeyException) {
            DuplicateKeyException dke = (DuplicateKeyException) t;
            Throwable cause = dke.getCause();
            if (cause instanceof MongoException) {
                MongoException me = (MongoException)cause;
                System.out.println("code is " + me.getCode());
                if (me.getCode() == 11000) {
                    System.out.println("Found a dup key");
                    System.out.println("message is " + me.getMessage());
                    String ID = "[a-z0-9]+";
                    Pattern p = Pattern.compile("^E([0-9]+) duplicate key error index: (" + ID + "\\." + ID + ")" +
                            "\\.\\$((" + ID + ")_" + ID + ").*");

                    Matcher matcher = p.matcher(me.getMessage());

                    if (matcher.matches() && matcher.groupCount() == 4) {
                        return new UniqueKeyException(matcher.group(4), t);
                    }
                }
            }

        }
        return t;
    }
}
