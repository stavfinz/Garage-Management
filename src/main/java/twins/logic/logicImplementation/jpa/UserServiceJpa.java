package twins.logic.logicImplementation.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import twins.data.UserEntity;
import twins.data.UserIdPK;
import twins.data.UserRole;
import twins.data.dao.UsersDao;
import twins.logic.UpdatedUsersService;
import twins.logic.Exceptions.UserAccessDeniedException;
import twins.logic.Exceptions.UserAlreadyExistsException;
import twins.logic.Exceptions.UserNotFoundException;
import twins.logic.logicImplementation.EntityConverter;
import twins.logic.logicImplementation.Validator;
import twins.users.UserBoundary;

@Service
public class UserServiceJpa implements UpdatedUsersService {

	private UsersDao usersDao;
	private EntityConverter entityConverter;
	private Validator validator;
	private String springApplicatioName;

	public UserServiceJpa() {
	}

	@Value("${spring.application.name:defaultName}")
	public void setSpringApplicatioName(String springApplicatioName) {
		this.springApplicatioName = springApplicatioName;
	}

	@Autowired
	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	@Autowired
	public void setEntityConverter(EntityConverter entityConverter) {
		this.entityConverter = entityConverter;
	}

	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Override
	@Transactional(readOnly = false) // The default value
	public UserBoundary createUser(UserBoundary user) {

		validator.isValidUser(user);
		
		try {
			if (this.login(springApplicatioName, user.getUserId().getEmail()) != null)
				throw new UserAlreadyExistsException("This user is already exists");
		} catch (UserNotFoundException e) {
			// user does not exists in the database, nothing to do
		}

		UserEntity entity = this.entityConverter.toEntity(user);
		entity.getUserId().setSpace(springApplicatioName);
		
		UserBoundary userB = this.entityConverter.toBoundary(entity);
		if (!validator.isValidUser(userB))
			return null;
		
		this.usersDao.save(entity);
		return userB;
	}

	@Override
	@Transactional(readOnly = true)
	public UserBoundary login(String userSpace, String userEmail) {
		// Users unique addressID combined from this String --> userSpace and userEmail
		// (TOGETHER)

		Optional<UserEntity> optionalUser = this.usersDao.findById(new UserIdPK(userSpace, userEmail));
		if (optionalUser.isPresent()) {
			UserEntity entity = optionalUser.get();
			UserBoundary boundary = entityConverter.toBoundary(entity);
			return boundary;
		} else {
			throw new UserNotFoundException(
					"Could not find user by userSpace/userEmail : " + userSpace + "/" + userEmail);// NullPointerException
		}
	}

	@Override
	@Transactional(readOnly = false) // The default value
	public UserBoundary updateUser(String userSpace, String userEmail, UserBoundary update) {
		// get existing user from database
		// Users unique addressID combined from this String --> userSpace and userEmail
		// (TOGETHER)
		Optional<UserEntity> existingOptional = this.usersDao.findById(new UserIdPK(userSpace, userEmail));
		if (existingOptional.isPresent()) {
			UserEntity existing = existingOptional.get();
			// update collection and return update

			validator.isValidUser(update);
			existing.setUsername(update.getUsername());
			existing.setAvatar(update.getAvatar());
			existing.setRole(UserRole.valueOf(update.getRole()));

			// userSpace and userEmail are never changed!!!!! (id)

			// update database
			existing = this.usersDao.save(existing);

			UserBoundary rv = this.entityConverter.toBoundary(existing);
			return rv;
		} else {
			throw new UserNotFoundException(
					"could not find user by userSpace/userEmail: " + userSpace + "/" + userEmail);// NullPointerException
		}
	}

	@Override
	@Transactional(readOnly = true)
	@Deprecated
	public List<UserBoundary> getAllUsers(String adminSpace, String adminEmail) {
		throw new RuntimeException("Deprecated method");
//		// Users unique addressID combined from this String --> userSpace and userEmail
//		// (TOGETHER)
//		Optional<UserEntity> optionalUser = this.usersDao.findById(new UserIdPK(adminSpace, adminEmail));
//		if (optionalUser.isPresent()) {
//			UserEntity entity = optionalUser.get();
//			if (entity.getRole() == UserRole.ADMIN) {
//				Iterable<UserEntity> allEntities = this.usersDao.findAll();
//				return StreamSupport.stream(allEntities.spliterator(), false).map(this.entityConverter::toBoundary)
//						.collect(Collectors.toList());
//			} else {
//				throw new UserAccessDeniedException("User is not ADMIN,therefore access denied! ");// Not a Manager
//			}
//		} else {
//			throw new UserNotFoundException(
//					"Could not find user by userSpace/userEmail : " + adminSpace + "/" + adminEmail);// NullPointerException
//		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(String adminSpace, String adminEmail, int size, int page) {
		// Users unique addressID combined from this String --> userSpace and userEmail
		// (TOGETHER)
		Optional<UserEntity> optionalUser = this.usersDao.findById(new UserIdPK(adminSpace, adminEmail));
		if (optionalUser.isPresent()) {
			UserEntity entity = optionalUser.get();
			if (entity.getRole() == UserRole.ADMIN) {
				
					return this.usersDao
							.findAll(PageRequest.of(page, size, Direction.DESC, "username", "userId"))
							.getContent()
							.stream()
							.map(this.entityConverter::toBoundary)
							.collect(Collectors.toList());
				
			}else {
				throw new UserAccessDeniedException("User is not ADMIN,therefore access denied! ");// Not a Manager
			}
		}else {
			throw new UserNotFoundException(
					"Could not find user by userSpace/userEmail : " + adminSpace + "/" + adminEmail);// NullPointerException
		}
	}

	@Override
	@Transactional(readOnly = false) // The default value
	public void deleteAllUsers(String adminSpace, String adminEmail) {
		// Users unique addressID combined from this String --> userSpace and userEmail
		// (TOGETHER)
		Optional<UserEntity> optionalUser = this.usersDao.findById(new UserIdPK(adminSpace, adminEmail));
		if (optionalUser.isPresent()) {
			UserEntity entity = optionalUser.get();
			if (entity.getRole() == UserRole.ADMIN) {
				this.usersDao.deleteAll();
			} else {
				throw new UserAccessDeniedException("User is not ADMIN,therefore access denied! ");// Not a Manager
			}
		} else {
			throw new UserNotFoundException(
					"Could not find user by userSpace/userEmail : " + adminSpace + "/" + adminEmail);// NullPointerException
		}
	}

	@Override
	public List<UserBoundary> getAllUsersByRole(UserRole role, int size, int page) {
		return this.usersDao.findAllByRole(
							role,
							PageRequest.of(page, size, Direction.DESC, "username", "userId"))
					.stream()
					.map(this.entityConverter::toBoundary)
					.collect(Collectors.toList());
	}



}
