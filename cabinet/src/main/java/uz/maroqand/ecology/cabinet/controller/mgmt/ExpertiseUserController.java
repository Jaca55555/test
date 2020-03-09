package uz.maroqand.ecology.cabinet.controller.mgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtTemplates;
import uz.maroqand.ecology.cabinet.constant.mgmt.MgmtUrls;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.constant.sys.TableHistoryType;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.sys.TableHistory;
import uz.maroqand.ecology.core.entity.user.EvidinceStatus;
import uz.maroqand.ecology.core.entity.user.UserEvidence;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.TableHistoryService;
import uz.maroqand.ecology.core.service.user.*;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.util.Common;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 27.03.2019.
 * (uz) Bitta tashkilotga tegishli foydalanuvchilar
 */
@Controller
public class ExpertiseUserController {

    private final UserService userService;
    private final PositionService positionService;
    private final DepartmentService departmentService;
    private final RoleService userRoleService;
    private final TableHistoryService tableHistoryService;
    private final OrganizationService organizationService;
    private final ObjectMapper objectMapper;
    private final UserEvidenceService userEvidenceService;
    private final FileService fileService;

    @Autowired
    public ExpertiseUserController(UserService userService, PositionService positionService, DepartmentService departmentService, TableHistoryService tableHistoryService, RoleService userRoleService, OrganizationService organizationService, ObjectMapper objectMapper, UserEvidenceService userEvidenceService, FileService fileService) {
        this.userService = userService;
        this.positionService = positionService;
        this.departmentService = departmentService;
        this.tableHistoryService = tableHistoryService;
        this.userRoleService = userRoleService;
        this.organizationService = organizationService;
        this.objectMapper = objectMapper;
        this.userEvidenceService = userEvidenceService;
        this.fileService = fileService;
    }

    @RequestMapping(MgmtUrls.UsersList)
    public String getUserListPage(Model model) {
        User user = userService.getCurrentUserFromContext();

        model.addAttribute("departmentList", departmentService.getByOrganizationId(user.getOrganizationId()));
        model.addAttribute("positionList", positionService.getByOrganizationId(user.getOrganizationId()));
        model.addAttribute("roleList", userRoleService.getRoleList());
        model.addAttribute("add_url",MgmtUrls.UsersNew);
        return MgmtTemplates.UserList;
    }

    @RequestMapping(value = MgmtUrls.UsersListAjax, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> getUserAjaxList(
            @RequestParam(name = "userId", defaultValue = "", required = false) Integer userId,
            @RequestParam(name = "firstname", defaultValue = "", required = false) String firstname,
            @RequestParam(name = "lastname", defaultValue = "", required = false) String lastname,
            @RequestParam(name = "middlename", defaultValue = "", required = false) String middlename,
            @RequestParam(name = "username", defaultValue = "", required = false) String username,
            @RequestParam(name = "departmentId", defaultValue = "", required = false) Integer departmentId,
            @RequestParam(name = "positionId", defaultValue = "", required = false) Integer positionId,
            Pageable pageable
    ) {
        User currentUser = userService.getCurrentUserFromContext();
        firstname = StringUtils.trimToNull(firstname);
        lastname = StringUtils.trimToNull(lastname);
        middlename = StringUtils.trimToNull(middlename);
        username = StringUtils.trimToNull(username);

        Page<User> usersPage = userService.findFiltered(userId,firstname,lastname,middlename,username, currentUser.getOrganizationId(),departmentId,positionId,pageable);
        HashMap<String, Object> result = new HashMap<>();
        result.put("recordsTotal", usersPage.getTotalElements()); //Total elements
        result.put("recordsFiltered", usersPage.getTotalElements()); //Filtered elements

        List<User> users = usersPage.getContent();
        List<Object[]> convenientForJSONArray = new ArrayList<>(users.size());
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        for (User user : usersPage) {
            convenientForJSONArray.add(new Object[]{
                    user.getId(),
                    user.getFullName(),
                    user.getUsername(),
                    user.getOrganizationId()!=null? organizationService.getById(user.getOrganizationId()).getNameTranslation(locale):"",
                    user.getDepartmentId()!=null? departmentService.getById(user.getDepartmentId()).getName():"",
                    user.getPositionId()!=null? positionService.getById(user.getPositionId()).getName():"",
                    user.getPhone(),
                    user.getEnabled(),
                    user.getLastEvent()!=null? Common.uzbekistanDateAndTimeFormat.format(user.getLastEvent()):""
            });
        }

        result.put("data", convenientForJSONArray);
        return result;
    }

    @RequestMapping(MgmtUrls.UsersNew)
    public String getUsersNewPage(Model model) {
        User currentUser = userService.getCurrentUserFromContext();

        model.addAttribute("user", new User());
        model.addAttribute("departmentId",null);
        model.addAttribute("departmentList",departmentService.getByOrganizationId(currentUser.getOrganizationId()));
        model.addAttribute("positionList", positionService.getByOrganizationId(currentUser.getOrganizationId()));
        model.addAttribute("roleList", userRoleService.getRoleList());
        model.addAttribute("action_url", MgmtUrls.UsersCreate);
        model.addAttribute("back_url", MgmtUrls.UsersList);
        return MgmtTemplates.UserNew;
    }

    @RequestMapping(MgmtUrls.UsersEdit)
    public String getUsersEditPage(
            @RequestParam(name = "id") Integer userId,
            Model model
    ) {
        User currentUser = userService.getCurrentUserFromContext();
        User user = userService.findById(userId, currentUser.getOrganizationId());
        if (user == null) {
            return "redirect:" + MgmtUrls.UsersList;
        }
        model.addAttribute("departmentList",departmentService.getByOrganizationId(currentUser.getOrganizationId()));

        model.addAttribute("user",user);
        model.addAttribute("departmentId",user.getDepartmentId()!=null?user.getDepartmentId():null);
        model.addAttribute("positionList",positionService.getByOrganizationId(currentUser.getOrganizationId()));
        model.addAttribute("roleList", userRoleService.getRoleList());
        model.addAttribute("action_url",MgmtUrls.UsersUpdate);
        model.addAttribute("back_url",MgmtUrls.UsersList);
        return MgmtTemplates.UserNew;
    }

    @RequestMapping(value = MgmtUrls.UsersUsernameCheck, produces = "application/json",method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> getUsernameCheck(
            @RequestParam(name = "username", defaultValue = "", required = false) String username,
            @RequestParam(name = "id", required = false) Integer userId
    ) {
        User user1 =null;
        if (userId!=null){
            user1 = userService.findById(userId);
        }
        HashMap<String, Object> result = new HashMap<>();
        Integer nameStatus = 2;
        if(username.length()>4){
            User user = userService.findByUsername(username);
            if(user==null || (user1!=null && user1.getId().equals(user.getId()))){
                nameStatus = 0;
            }else {
                nameStatus = 1;
            }
        }

        result.put("nameStatus", nameStatus);
        result.put("username", username);
        return result;
    }

    //fileUpload
    @RequestMapping(value = MgmtUrls.UsersEvidenceFileUpload, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> uploadFile(
            @RequestParam(name = "id",required = false) Integer id,
            @RequestParam(name = "file_name") String fileNname,
            @RequestParam(name = "file") MultipartFile multipartFile
    ) {
        User user = userService.getCurrentUserFromContext();
        UserEvidence userEvidence = new UserEvidence() ;

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (id!=null){
            userEvidence = userEvidenceService.getById(id);
            if (userEvidence == null) {
                responseMap.put("message", "Object not found.");
                return responseMap;
            }
        }else{
            userEvidence = userEvidenceService.save(userEvidence);
        }

        File file = fileService.uploadFile(multipartFile, user.getId(),"userEvidence="+userEvidence.getId(),fileNname);
        if (file != null) {
            Set<File> fileSet = userEvidence.getDocumentFiles();
            if (fileSet==null) fileSet = new HashSet<>();
            fileSet.add(file);
            userEvidence.setDocumentFiles(null);
            userEvidence.setDocumentFiles(fileSet);
            userEvidence = userEvidenceService.save(userEvidence);

            responseMap.put("name", file.getName());
            responseMap.put("link", MgmtUrls.UsersEvidenceFileDownload+ "?file_id=" + file.getId());
            responseMap.put("fileId", file.getId());
            responseMap.put("id", userEvidence.getId());
            responseMap.put("status", 1);
        }
        return responseMap;
    }

    //fileDownload
    @RequestMapping(MgmtUrls.UsersEvidenceFileDownload)
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "file_id") Integer fileId
    ){
        File file = fileService.findById(fileId);

        if (file == null) {
            return null;
        } else {
            return fileService.getFileAsResourceForDownloading(file);
        }
    }

    //fileDelete
    @RequestMapping(value = MgmtUrls.UsersEvidenceFileDelete, method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public HashMap<String, Object> deleteFile(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "fileId") Integer fileId
    ) {
        User user = userService.getCurrentUserFromContext();
        UserEvidence userEvidence = userEvidenceService.getById(id);

        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", 0);

        if (userEvidence == null) {
            responseMap.put("message", "Object not found.");
            return responseMap;
        }
        File file = fileService.findByIdAndUploadUserId(fileId, user.getId());

        if (file != null) {
            Set<File> fileSet = userEvidence.getDocumentFiles();
            if(fileSet.contains(file)) {
                fileSet.remove(file);
                userEvidenceService.save(userEvidence);

                file.setDeleted(true);
                file.setDateDeleted(new Date());
                file.setDeletedById(user.getId());
                fileService.save(file);

                responseMap.put("status", 1);
            }
        }
        return responseMap;
    }

    @RequestMapping(value = MgmtUrls.UsersCreate, method = RequestMethod.POST)
    public String createUserMethod(
            @RequestParam(name = "userPassword") String userPassword,
            @RequestParam(name = "userPasswordConfirmation") String userPasswordConfirmation,
            @RequestParam(name = "departmentId") Integer departmentId,
            @RequestParam(name = "userEvidenceId") Integer userEvidenceId,
            @RequestParam(name = "reason") String reason,
            @RequestParam(name = "roleId") Integer roleId,
            @RequestParam(name = "enabled") Integer enebled,
            User userCreate
    ) {
        User currentUser = userService.getCurrentUserFromContext();
        if(userCreate.getUsername()==null){
            return "redirect:" + MgmtUrls.UsersList;
        }
        if(!userPassword.equals(userPasswordConfirmation)){
            return "redirect:" + MgmtUrls.UsersList;
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        User user1 = new User();
        User userCheck = userService.findByUsername(userCreate.getUsername());
        if(userCheck==null ){
            user1.setFirstname(userCreate.getFirstname());
            user1.setLastname(userCreate.getLastname());
            user1.setMiddlename(userCreate.getMiddlename());
            user1.setOrganizationId(currentUser.getOrganizationId());
            user1.setDepartmentId(departmentId);
            user1.setPositionId(userCreate.getPositionId());
            user1.setRole(userRoleService.getById(roleId));
            user1.setEnabled(enebled==1?Boolean.TRUE:Boolean.FALSE);
            user1.setPhone(userCreate.getPhone());
            user1.setEmail(userCreate.getEmail());
            user1.setUsername(userCreate.getUsername());
            user1.setPassword(encoder.encode(userPassword));
            user1 = userService.createUser(user1);

            String after="";
            try {
                after = objectMapper.writeValueAsString(user1);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            TableHistory tableHistory = tableHistoryService.create(
                    TableHistoryType.add,
                    TableHistoryEntity.User,
                    user1.getId(),
                    null,
                    after,
                    "",
                    currentUser.getId(),
                    currentUser.getUserAdditionalId()
            );
            UserEvidence userEvidence = null;
            if (userEvidenceId!=null) userEvidence = userEvidenceService.getById(userEvidenceId);
            if (userEvidence==null) userEvidence = new UserEvidence();

            userEvidence.setUserId(user1.getId());
            userEvidence.setTableHistoryId(tableHistory.getId());
            userEvidence.setTableHistoryId(tableHistory.getId());
            userEvidence.setReason(reason);
            userEvidence.setRegisteredDate(new Date());
            userEvidence.setEvidinceStatus(EvidinceStatus.Create);
            userEvidenceService.save(userEvidence);
        }
        return "redirect:" + MgmtUrls.UsersList;
    }

    @RequestMapping(value = MgmtUrls.UsersUpdate, method = RequestMethod.POST)
    public String userUpdateMethod(
            @RequestParam(name = "id") Integer userId,
            @RequestParam(name = "userEvidenceId") Integer userEvidenceId,
            @RequestParam(name = "reason") String reason,
            @RequestParam(name = "departmentId") Integer departmentId,
            @RequestParam(name = "roleId") Integer roleId,
            @RequestParam(name = "enabled") Integer enebled,
            User userUpdate
    ) {
        User currentUser = userService.getCurrentUserFromContext();
        if(userUpdate.getUsername()==null){
            return "redirect:" + MgmtUrls.UsersList;
        }
        User updateUser  = userService.findById(userId);
        String oldUser = "";
        try {
            oldUser = objectMapper.writeValueAsString(updateUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (updateUser==null){
            return "redirect:" + MgmtUrls.UsersList;
        }
        User userCheck = userService.findByUsername(userUpdate.getUsername());
        if(userCheck==null || userCheck.getId().equals(userId)){
            updateUser.setLastname(userUpdate.getLastname());
            updateUser.setFirstname(userUpdate.getFirstname());
            updateUser.setMiddlename(userUpdate.getMiddlename());
            updateUser.setOrganizationId(currentUser.getOrganizationId());
            updateUser.setDepartmentId(departmentId);
            updateUser.setPositionId(userUpdate.getPositionId());
            updateUser.setRole(userRoleService.getById(roleId));
            updateUser.setEnabled(enebled==1?Boolean.TRUE:Boolean.FALSE);
            updateUser.setPhone(userUpdate.getPhone());
            updateUser.setEmail(userUpdate.getEmail());
            updateUser.setUsername(userUpdate.getUsername());
            userService.updateUser(updateUser);

            String after="";
            try {
                after = objectMapper.writeValueAsString(updateUser);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            TableHistory tableHistory = tableHistoryService.create(
                    TableHistoryType.edit,
                    TableHistoryEntity.User,
                    updateUser.getId(),
                    oldUser,
                    after,
                    "",
                    currentUser.getId(),
                    currentUser.getUserAdditionalId()
            );
            UserEvidence userEvidence = null;
            if (userEvidenceId!=null) userEvidence = userEvidenceService.getById(userEvidenceId);
            if (userEvidence==null) userEvidence = new UserEvidence();

            userEvidence.setUserId(updateUser.getId());
            userEvidence.setTableHistoryId(tableHistory.getId());
            userEvidence.setTableHistoryId(tableHistory.getId());
            userEvidence.setReason(reason);
            userEvidence.setRegisteredDate(new Date());
            userEvidence.setEvidinceStatus(EvidinceStatus.Active);
            userEvidenceService.save(userEvidence);
        }

        return "redirect:" + MgmtUrls.UsersList;
    }

    @RequestMapping(value = MgmtUrls.UsersEditEnebled)
    @ResponseBody
    public String userDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "status") boolean status
    ){
        User user = userService.getCurrentUserFromContext();
        Integer result=1;
        User editedUser = userService.findById(id);
        String oldUser = "";
        try {
            oldUser = objectMapper.writeValueAsString(editedUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (editedUser == null){
            result=-1;
            return result.toString();
        }
        editedUser.setEnabled(status);
        userService.updateUser(editedUser);
        String after ="";
        try {
            after = objectMapper.writeValueAsString(editedUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        tableHistoryService.create(
                TableHistoryType.edit,
                TableHistoryEntity.User,
                editedUser.getId(),
                oldUser,
                after,
                "",
                user.getId(),
                user.getUserAdditionalId()
        );
        return result.toString();
    }

    @RequestMapping(MgmtUrls.UsersView)
    public String getUsersViewPage(
            @RequestParam(name = "id") Integer id,
            Model model
    ){
        User currentUser = userService.getCurrentUserFromContext();
        User user = userService.findById(id, currentUser.getOrganizationId());
        if (user==null){
            return "redirect:" + MgmtUrls.UsersList;
        }
        Type type = new TypeToken<List<User>>(){}.getType();
        List<HashMap<String,Object>> beforeAndAfterList = tableHistoryService.forAudit(type,TableHistoryEntity.User,id);
        List<UserEvidence> userEvidenceList = userEvidenceService.getListByUserId(user.getId());
        model.addAttribute("user",user);
        model.addAttribute("beforeAndAfterList",beforeAndAfterList);
        model.addAttribute("userEvidenceList",userEvidenceList);
        return MgmtTemplates.UserView;
    }

    @RequestMapping(value = MgmtUrls.UsersPswEdit)
    public String userPswEdit(
            @RequestParam(name = "id")Integer id,
            Model model
    ){
        User currentUser = userService.getCurrentUserFromContext();
        User user = userService.findById(id, currentUser.getOrganizationId());
        if (user == null){
            return "redirect:"+MgmtUrls.UsersList;
        }

        model.addAttribute("user",user);
        model.addAttribute("action_url",MgmtUrls.UsersPswUpdate);
        return MgmtTemplates.UserPswEdit;
    }

    @RequestMapping(value = MgmtUrls.UsersPswUpdate,method = RequestMethod.POST)
    public String updateUserPsw(
            @RequestParam(name = "userPassword")String userPassword,
            @RequestParam(name = "userPasswordConfirmation")String userPasswordConfirmation,
            User user
    ){
        if (!userPassword.equals(userPasswordConfirmation)){
          return "redirect:"+ MgmtUrls.UsersPswEdit + "?error=true";
        }
        User oldUser = userService.findById(user.getId());

        String oldUserString = "";
        try {
            oldUserString = objectMapper.writeValueAsString(oldUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        oldUser.setPassword(new BCryptPasswordEncoder().encode(userPassword));
        userService.updateUser(oldUser);
        String after ="";
        try {
            after = objectMapper.writeValueAsString(oldUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        tableHistoryService.create(
              TableHistoryType.edit,
              TableHistoryEntity.User,
              oldUser.getId(),
              oldUserString,
              after,
              "Users password updated successfully!!!",
              oldUser.getId(),
              oldUser.getUserAdditionalId()
        );
        return "redirect:" + MgmtUrls.UsersList;
    }

    /*@RequestMapping(value = MgmtUrls.UserDelete)
    @ResponseBody
    public String userDelete(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "message") String message
    ){
        User user = userService.getCurrentUserFromContext();
        Integer status=1;
        User deletedUser = userService.findById(id);
        String oldUser = gson.toJson(deletedUser);
        if (deletedUser == null){
            status=-1;
            return status.toString();
        }
        deletedUser.setDeleted(true);
        deletedUser.setMessage(message);
        deletedUser.setEnabled(Boolean.FALSE);
        userService.updateUser(deletedUser);
        tableHistoryService.create(
                TableHistoryType.delete,
                TableHistoryEntity.User,
                deletedUser.getId(),
                oldUser,
                gson.toJson(deletedUser),
                "",
                user.getId(),
                user.getUserAdditionalId());
        return status.toString();
    }*/

}