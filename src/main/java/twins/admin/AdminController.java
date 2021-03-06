package twins.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import twins.logic.ItemsService;
import twins.logic.OperationsService;
import twins.logic.UsersService;
import twins.operations.OperationBoundary;
import twins.users.UserBoundary;


@RestController
public class AdminController {
	
	private UsersService userService;
	private OperationsService operationService;
	private ItemsService itemService;
	
	
	@Autowired
	public AdminController(UsersService userService, OperationsService operationService, ItemsService itemService) {
		this.userService = userService;
		this.operationService = operationService;
		this.itemService = itemService;
	}
	@RequestMapping(
			path = "/twins/admin/users/{userSpace}/{userEmail}",
			method = RequestMethod.DELETE)
	public void deleteAllUsersInSpace(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email) {
		this.userService.deleteAllUsers(space, email);
	}

	@RequestMapping(
			path = "/twins/admin/items/{userSpace}/{userEmail}",
			method = RequestMethod.DELETE)
	public void deleteAllItemsInSpace(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email) {
		this.itemService.deleteAllItems(space, email);
	}

	@RequestMapping(
			path = "/twins/admin/operations/{userSpace}/{userEmail}",
			method = RequestMethod.DELETE)
	public void deleteAllOperationsInSpace(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email) {
		this.operationService.deleteAllOperations(space, email);
	}

	@RequestMapping(
			path = "/twins/admin/users/{userSpace}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportAllUsers(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email,
			@RequestParam(name="size", required = false, defaultValue = "20") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page){
		return this.userService.getAllUsers(space, email, size, page).toArray(new UserBoundary[0]);
	}
	

	// invoke url, either with no optional parameters : /twins/admin/operations/{userSpace}/{userEmail}
	//             		  or with optional parameters : /twins/admin/operations/{userSpace}/{userEmail}?size=20&page=2
	@RequestMapping(
			path = "/twins/admin/operations/{userSpace}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public OperationBoundary[] exportAllOperations(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email,
			@RequestParam(name="size", required = false, defaultValue = "20") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		
		List<OperationBoundary> allOperations = 
				this.operationService
				.getAllOperations(space, email, size, page);
		
		return allOperations
				.toArray(new OperationBoundary[0]);
	}
}
