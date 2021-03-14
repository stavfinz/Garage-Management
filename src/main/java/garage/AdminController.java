package garage;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class AdminController {
	@RequestMapping(
			path = "/twins/admin/users/{userSpace}/{userEmail}",
			method = RequestMethod.DELETE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void deleteAllUsersInSpace () {
		// STUB IMPLEMENTATION
		System.out.println("all users deleted");
	}
	@RequestMapping(
			path = "/twins/admin/items/{userSpace}/{userEmail}",
			method = RequestMethod.DELETE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void deleteAllItemsInSpace () {
		// STUB IMPLEMENTATION
		System.out.println("all items deleted");
	}
	@RequestMapping(
			path = "/twins/admin/operations/{userSpace}/{userEmail}",
			method = RequestMethod.DELETE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void deleteAllOperationsInSpace () {
		// STUB IMPLEMENTATION.
		System.out.println("all operations deleted");
	}
	
	@RequestMapping(
			path = "/twins/admin/users/{userSpace}/{userEmail}",
			method = RequestMethod.GET,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportAllUsers () {
		// STUB IMPLEMENTATION
		UserBoundary users[]= new UserBoundary[1];
		users[0]=new UserBoundary();
		users[0].setUserId(new UserId("2021b.twins","test@test.com"));
		users[0].setRole("admin");
		users[0].setUserName("admin");
		users[0].setAvatar("admin");

		return users;
	}
	
	@RequestMapping(
			path = "/twins/admin/operations/{userSpace}/{userEmail}",
			method = RequestMethod.GET,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public OperationBoundary[] exportAllOperations () {
		// STUB IMPLEMENTATION
		OperationBoundary operations[]=new OperationBoundary[1];
		operations[0]=new OperationBoundary();
		operations[0].setType("type");
		return operations;
	}
	
}