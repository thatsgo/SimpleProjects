
package com.sample;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.chrono.Chronology;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;
import InvalidRequestException.InvalidRequestException;



@Controller
@RequestMapping("/user/{userId}/customer/{customerId}")
public class UtilityController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilityController.class);
    
    @Autowired(required = false)
    private ActionDataProcessor actionDataProcessor;
    
    @Autowired(required = false)
    private IDomainToCustomerDto domainToCustomerDto;
    
    @Autowired(required = false)
    private IIssuesService issueService;
    
    @Autowired(required = false)
    private ITicketTabsService ticketTabService;
    
    @Autowired(required = false)
    private SrValidationService srValidationService;
    
    @Autowired(required = false)
    private GroupMasterServiceInterface groupServiceInterface;
    
    @Autowired(required = false)
    private AssetCategoryService assetService;
    
    @Resource(name = "assetCategories")
    private Map<String, AssetCategories> assetCategories;
    
    @Resource(name = "dashboardModuleMap")
    private Map<String, IDashboardFilterData> dashboardModuleMap;
    
    @Autowired(required = false)
    private ReportService reportService;
    
    @Autowired(required = false)
    private DataProcessor dataProcessor;
    
    @Autowired(required = false)
    private DashboardService dashboardService;
    
    @Autowired(required = false)
    private ITicketService ticketService;
    
    @Autowired(required = false)
    private ProblemService problemService;
    
    @Autowired(required = false)
    private ViewIncidentServiceDatoInterface viewIncidentServiceDatoInterface;
    
    @Autowired(required = false)
    private ModulePrivilegeService modulePrivilegeService;
    
    @Resource(name = "userProfile")
    private Map<String, String> userProfile;
    
    @Resource(name = "defaultProperties")
    private Map<String, String> defaultProperties;
    
    @Autowired(required = false)
    private INamedFiltersService namedFilterService;
    
    @Autowired(required = false)
    private AuthenticationServiceIfc authenticationServiceIfc;
    
    @Resource(name = "desktopSubFiltersMap")
    private Map<String, IDesktopSubFilter> desktopSubFiltersMap;
    
    /**
     * This method returns all the filter data for the ticket listing page.
     * Implemented using strategic design pattern. based on the input it execute
     * the corresponding business logic and returns the value. It has the
     * ability to return two or more filter data. The input should be comma
     * based to get more than one value.
     * 
     * @param fields
     * @param httpResponse
     * @return Response<Map<String,List<Filter>>>
     */
    @RequestMapping(value = "/module/{moduletype}/filter/{fields}", method = RequestMethod.GET)
    public @ResponseBody Response<Map<String, List<Filter>>> getFilterData(@PathVariable String moduletype,
            @PathVariable String fields, UtilityInput input) {
        LOGGER.info("Module type for which filter fields are requested {}", moduletype);
        LOGGER.debug("Request for get Filter for the fields::{}", fields);
        /** will get the module type to get the data */
        int type = MWatchUtil.getModuleType(moduletype);
        input.setModuleType(type);
        Response<Map<String, List<Filter>>> response = new Response<Map<String, List<Filter>>>();
        Map<String, List<Filter>> dataMap = dataProcessor.getFilterData(fields, input);
        
        if(!dataMap.isEmpty()) {
            LOGGER.debug("Data found for the request field::{} and the size is::{}", fields, dataMap.size());
            response.setSuccess(dataMap);
        } else {
            LOGGER.debug("No data found for the field::{}", fields);
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.NO_RECORDS, "No records found"));
        }
        return response;
    }
    
    /**
     * This method returns all the filter data for the dcfc listing page.
     * Implemented using strategic design pattern. based on the input it execute
     * the corresponding business logic and returns the value. It has the
     * ability to return two or more filter data. The input should be comma
     * based to get more than one value.
     * 
     * @param fields
     * @param httpResponse
     * @return Response<Map<String,List<Filter>>>
     */
    @RequestMapping(value = "/module/dcfc/filter/{fields}", method = RequestMethod.GET)
    public @ResponseBody Response<Map<String, List<Filter>>> getDcfcFilterData(@RequestParam String listingType,
            @PathVariable String fields, @PathVariable String customerId) {
        StopWatch watch = new StopWatch();
        watch.start();
        LOGGER.debug("Request for get Dcfc Filter for the fields::{}", fields);
        /** will get the module type to get the data */
        DcfcListingTypeEnum listingTypeEnum = DcfcListingTypeEnum.getEnumByName(listingType);
        Response<Map<String, List<Filter>>> response = new Response<Map<String, List<Filter>>>();
        Map<String, List<Filter>> dataMap = dataProcessor.getDcfcFilterData(fields, customerId, listingTypeEnum);
        
        if(!dataMap.isEmpty()) {
            LOGGER.debug("Data found for the request field::{} and the size is::{}", fields, dataMap.size());
            response.setSuccess(dataMap);
            watch.stop();
            LOGGER.debug("Successfully retrieved for DCFC listing with named filters status:::{} within {}ms",
                    listingType, watch.getTotalTimeMillis());
        } else {
            response.setWarning(Warning
                    .getWarningMessage(MwatchStatusCode.NO_RECORDS, MWatchConstants.NO_RECORDS_FOUND));
            LOGGER.info("Data not found for the request fields {} ::for customer id{} -No records found ", fields,
                    customerId);
        }
        return response;
    }
    
    /**
     * This method returns the drop down list for all the types of dashboard
     * components
     * 
     * @param userId
     * @param ticketid
     * @param moduleid
     * @param httpResponse
     * @return Response<List<Action>>
     */
    @RequestMapping(value = "/module/dashboard/components/{componentType}/widgetutility", method = RequestMethod.GET)
    public @ResponseBody Response<List<FilterDto>> getWidgets(@PathVariable String componentType,
            DashboardBo dashboardBo) {
        LOGGER.debug("Request for get dashboard component type for the component::{}", componentType);
        Response<List<FilterDto>> response = new Response<List<FilterDto>>();
        IDashboardFilterData dashboardFilterData = dashboardModuleMap.get(componentType);
        if(dashboardFilterData == null) {
            LOGGER.debug("Invalid component type", componentType);
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.INVALID_COMPONENT_TYPE,
                    "Invalid component type"));
            return response;
        }
        List<FilterDto> data = dashboardFilterData.getWidgetUtility(dashboardBo);
        if(!data.isEmpty()) {
            LOGGER.debug("Data found for the component type::{} and the size is::{}", componentType, data.size());
            response.setSuccess(data);
        } else {
            LOGGER.debug("No data found for the component type::{}", componentType);
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.NO_RECORDS, "No records found"));
        }
        return response;
    }
    
    /**
     * This method returns the drop down list for all the types of dashboard
     * components
     * 
     * @param userId
     * @param ticketid
     * @param moduleid
     * @param httpResponse
     * @return Response<List<Action>>
     */
    @RequestMapping(value = "/module/dashboard/components/{componentType}", method = RequestMethod.GET)
    public @ResponseBody Response<List<Filter>> getDashboardData(@PathVariable String componentType) {
        LOGGER.debug("Request for get dashboard component type for the component::{}", componentType);
        Response<List<Filter>> response = new Response<List<Filter>>();
        IDashboardFilterData dashboardFilterData = dashboardModuleMap.get(componentType);
        if(dashboardFilterData == null) {
            LOGGER.debug("Invalid component type", componentType);
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.INVALID_COMPONENT_TYPE,
                    "Invalid component type"));
            return response;
        }
        List<Filter> data = dashboardFilterData.getData();
        if(!data.isEmpty()) {
            LOGGER.debug("Data found for the component type::{} and the size is::{}", componentType, data.size());
            response.setSuccess(data);
        } else {
            LOGGER.debug("No data found for the component type::{}", componentType);
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.NO_RECORDS, "No records found"));
        }
        return response;
    }
    
    /**
     * This method returns the list of components available for each component
     * type of dashboard
     * 
     * @param userId
     * @param ticketid
     * @param moduleid
     * @param httpResponse
     * @return Response<List<Action>>
     */
    @RequestMapping(value = "/module/dashboard/{dashboardId}/components/{componentType}/{componentSubType}", method = RequestMethod.GET)
    public @ResponseBody Response<Map<String, List<Filter>>> getDashboardData(@PathVariable Integer dashboardId,
            @PathVariable String componentType, @PathVariable String componentSubType, @PathVariable Integer customerId) {
        LOGGER.debug(
                "Requesting for component listing of component type::{} with subType::{} of the dashboardId::{} for given customerId::{}",
                componentType, componentSubType, dashboardId, customerId);
        Response<Map<String, List<Filter>>> response = new Response<>();
        IDashboardFilterData dashboardFilterData = dashboardModuleMap.get(componentType);
        if(dashboardFilterData == null) {
            LOGGER.debug("Invalid component type", componentType);
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.INVALID_COMPONENT_TYPE,
                    "Invalid component type"));
            return response;
        }
        /**
         * As populating subtypes of different types of components are
         * different, following strategic pattern with each class has its own
         * implementation of populating subtypes
         */
        Map<String, List<Filter>> data = dashboardFilterData.getSubType(dashboardId, componentSubType, customerId);
        if(!data.isEmpty()) {
            LOGGER.debug("Data found for the component sub type::{} and the size is::{}", componentType, data.size());
            response.setSuccess(data);
        } else {
            LOGGER.debug("No data found for the component sub type::{}", componentType);
            response.setWarning(Warning
                    .getWarningMessage(MwatchStatusCode.NO_RECORDS, MWatchConstants.NO_RECORDS_FOUND));
        }
        return response;
    }
    
    /**
     * Method to add component for the all component types
     * 
     * @param componentType
     * @param dashboardBo
     * @return
     */
    @RequestMapping(value = "/module/dashboard/{dashboardId}/addcomponents", method = RequestMethod.POST)
    public @ResponseBody Response<String> addComponent(@PathVariable Integer dashboardId,
            @RequestBody List<ComponentInput> components) {
        LOGGER.info("Requesting to add components to the dashboardId::{}", dashboardId);
        StopWatch watch = new StopWatch();
        watch.start();
        if(components == null || components.isEmpty()) {
            throw new InvalidRequestBodyException("Please provide components");
        }
        Response<String> response = new Response<String>();
        if(dashboardId == null || components == null || components.isEmpty()) {
            LOGGER.debug("No input componets sent to add it to the dashboardId::{}", dashboardId);
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.INVALID_REQUEST, "Invalid component type"));
            return response;
        }
        boolean isSucess = dashboardService.addCompsToDashboard(dashboardId, components);
        if(isSucess) {
            response.setSuccess("Successfully added components");
            watch.stop();
            LOGGER.debug("Successfully inserted components into dashboardId::{} within {}ms", dashboardId,
                    watch.getTotalTimeMillis());
            return response;
        }
        response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.INSERT_FAILED, "Error in adding components"));
        watch.stop();
        LOGGER.debug("Error while adding components to dashboardId:{} and service responded within {}ms", dashboardId,
                watch.getTotalTimeMillis());
        return response;
    }
    
    /**
     * This method returns all the action for the ticket based on the ticket
     * type and actions in the view ticket page.
     * 
     * @param userId
     * @param ticketid
     * @param moduleid
     * @param httpResponse
     * @return Response<List<Action>>
     */
    @RequestMapping(value = "/ticket/actions/{moduleCode}", method = RequestMethod.GET)
    public @ResponseBody Response<List<Action>> getActionData(@PathVariable String userId,
            @PathVariable String customerId, @RequestParam(required = false) String moduleid,
            @PathVariable String moduleCode, @RequestParam Long ticketid,
            @RequestParam(required = false) Integer portalid, @RequestParam(required = false) Integer profileid) {
        LOGGER.debug("Request for get actions for the ticket id::{}", ticketid);
        Response<List<Action>> response = new Response<List<Action>>();
        List<Action> data = actionDataProcessor.getAllActionsForTicketId(userId, ticketid, moduleid, moduleCode,
                customerId, portalid, profileid);
        if(!data.isEmpty()) {
            LOGGER.debug("Data found for the request get actions for the ticket id::{} and the size is::{}", ticketid,
                    data.size());
            response.setSuccess(data);
        } else {
            LOGGER.debug("No data found for the request get actions for the ticket id::{}", ticketid);
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No records found"));
        }
        return response;
    }
    
    /**
     * This method return all the tabs for the view ticket page it returns based
     * on the status of ticket
     * 
     * @param userId
     * @param ticketid
     * @param httpResponse
     * @return Response<List<String>>
     */
    @RequestMapping(value = "/module/{moduletype}/ticket/{ticketid}/tabs", method = RequestMethod.GET)
    public @ResponseBody Response<List<String>> getTabsData(@PathVariable String userId,
            @PathVariable String moduletype, @PathVariable Long ticketid, @PathVariable String customerId) {
        LOGGER.debug("Request for get tabs for the ticket id::{}", ticketid);
        Response<List<String>> response = new Response<List<String>>();
        List<String> data = ticketTabService.getTicketTabsForTicketId(ticketid, moduletype, customerId);
        if(!data.isEmpty()) {
            LOGGER.debug("Data found for the request get tabs for the ticket id::{} and the size is::{}", ticketid,
                    data.size());
            response.setSuccess(data);
        } else {
            LOGGER.debug("No data found for the request get tabs for the ticket id::{}", ticketid);
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.NO_RECORDS, "No records found"));
        }
        return response;
    }
    
    /**
     * Method to get the list of domain manager details based on the given
     * customers where customers is a comma separated ids
     * 
     * @param customerId
     * @param httpResponse
     * @return Response<List<DomainManagerDto>>
     */
    @RequestMapping(value = "/domainlist", method = RequestMethod.GET)
    public @ResponseBody Response<List<DomainManagerDto>> getdomainList(@PathVariable String customerId) {
        LOGGER.debug("Got the request to get the domain list for customers:: {}", customerId);
        Response<List<DomainManagerDto>> response = new Response<List<DomainManagerDto>>();
        List<DomainManagerDto> domainManagers = Collections.emptyList();
        domainManagers = domainToCustomerDto.getDomainManagerNames(customerId);
        if(!domainManagers.isEmpty()) {
            response.setSuccess(domainManagers);
        } else {
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.NO_RECORDS,
                    "No records found for given customer id"));
        }
        return response;
    }
    
    /**
     * it return the list of all issues from incident and request for the search
     * string and customer id
     * 
     * @param issue
     * @param customerId
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = "/issue/search", method = RequestMethod.GET)
    public @ResponseBody Response<List<IssuesDTO>> searchIssue(@RequestParam String issue, @PathVariable long customerId) {
        Response<List<IssuesDTO>> response = new Response<List<IssuesDTO>>();
        // get the list from the dto layer with all sorting are done in dto
        // layer
        List<IssuesDTO> dataMap = issueService.searchIssuesFromIncAndReq(customerId, issue);
        // check for the list for null or empty and return data else return no
        // records found message
        if(!dataMap.isEmpty()) {
            response.setSuccess(dataMap);
        } else {
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.NO_RECORDS,
                    "No records found for given customer id and search string"));
        }
        return response;
    }
    
    /**
     * Method to get the SR validation This method will get requestId and
     * customer Id as input And returns the response as a list of actions with
     * its ciDetails and action arguments for that actions with proper status
     * message
     * 
     * @author nasreen
     * @param customerId
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = "/request/{ticketid}/srvalidation", method = RequestMethod.GET)
    @ResponseBody
    public Response<List<SrValidationDTO>> getActionDetailsForRequest(@PathVariable Integer customerId,
            @PathVariable Long ticketid) {
        
        Response<List<SrValidationDTO>> response = new Response<List<SrValidationDTO>>();
        LOGGER.info("Requested for SrValidation actions for the input ticketId::{}", ticketid);
        
        List<SrValidationDTO> srValidationDTOs = srValidationService.getAllActionDetails(ticketid, customerId);
        if(!srValidationDTOs.isEmpty()) {
            response.setData(srValidationDTOs);
            LOGGER.info("SrValidation details are fetched successfully for the ticketId::{}", ticketid);
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No results found"));
            LOGGER.info("SrValidation details are found to be empty for the ticketId::{}", ticketid);
        }
        
        return response;
        
    }
    
    /**
     * This service to get the list of groups for the selected customer id where
     * the customerIds can be values separated by commas.
     * 
     * @param customerId
     * @param userId
     * @return Response<List<GroupsDto>>
     */
    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public @ResponseBody Response<List<GroupsDto>> getGroupList(@PathVariable String customerId,
            @PathVariable String userId) {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Get the list of groups for the user {} and customerId {}", userId, customerId);
        Response<List<GroupsDto>> response = new Response<List<GroupsDto>>();
        // send false for the isListing argument bcoz now its getting for report
        List<GroupsDto> groupList = groupServiceInterface.getGroups(customerId, false);
        if(!groupList.isEmpty()) {
            response.setSuccess(groupList);
        } else {
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.NO_RECORDS,
                    "No records found for given customer id"));
        }
        LOGGER.info("Total time taken to get the group values {} ms", System.currentTimeMillis() - startTime);
        return response;
        
    }
    
    /**
     * This service to get the list of groups for the selected customer id where
     * the customerIds can be values separated by commas.
     * 
     * @param customerId
     * @param userId
     * @return Response<List<GroupsDto>>
     */
    @RequestMapping(value = "/grouplisting", method = RequestMethod.GET)
    public @ResponseBody Response<List<GroupsDto>> getGroupListForListing(@PathVariable String customerId,
            @PathVariable String userId) {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Get the list of groups for the user {} and customerId {}", userId, customerId);
        Response<List<GroupsDto>> response = new Response<List<GroupsDto>>();
        // send true for the isListing argument bcoz now its getting for listing
        List<GroupsDto> groupList = groupServiceInterface.getGroups(customerId, true);
        if(!groupList.isEmpty()) {
            response.setSuccess(groupList);
        } else {
            response.setError(ErrorBean.getErrorMessage(MwatchStatusCode.NO_RECORDS,
                    "No records found for given customer id"));
        }
        LOGGER.info("Total time taken to get the group values {} ms", System.currentTimeMillis() - startTime);
        return response;
        
    }
    
    /**
     * @author sukanya
     * 
     *         Method to get asset categories This method gets customer id and
     *         user id as input And returns the list of asset categories which
     *         belongs to them
     * @param customerId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/assetsummary", method = RequestMethod.GET)
    @ResponseBody
    public Response<List<AssetSummaryDTO>> getAssetCategories(@PathVariable String customerId,
            @PathVariable String userId) {
        LOGGER.debug("Asset categories are to be fetched for the input user id::{}", userId);
        Response<List<AssetSummaryDTO>> response = new Response<List<AssetSummaryDTO>>();
        List<AssetSummaryDTO> assetMenu = assetService.getAssetCategories();
        if(!assetMenu.isEmpty()) {
            response.setSuccess(assetMenu);
            LOGGER.debug("Asset categories are fetched succesfully for the input user id:{}", userId);
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No record found"));
            LOGGER.debug("Asset categories are found to be empty for the given input user id:{}", userId);
        }
        return response;
    }
    
    /**
     * Method to get asset menus based on the asset category chosen This is
     * implemented based on strategic design pattern so that whenever the asset
     * category chosen by the user differs corresponding category asset menus
     * are given as the response. It also takes multiple customer id as input
     * which can be given in the request
     * 
     * @author sukanya
     * @param customerId
     * @param userId
     * @param assetCategoryType
     * @return
     */
    @RequestMapping(value = "/assetcategory/{assetCategoryType}")
    @ResponseBody
    public Response<List<AssetDTO>> getAssetMenu(@PathVariable String customerId, @PathVariable String assetCategoryType) {
        Long startTime = System.currentTimeMillis();
        LOGGER.info("Requested Asset Menus for the input asset category::{} and customer id::{}", assetCategoryType,
                customerId);
        AssetCategories categories = assetCategories.get(assetCategoryType);
        Response<List<AssetDTO>> response = new Response<List<AssetDTO>>();
        List<AssetDTO> asset = categories.getAssetMenu(customerId);
        if(!asset.isEmpty()) {
            response.setSuccess(asset);
            LOGGER.debug("Asset menus are fetched successfully for the asset category::{}", assetCategoryType);
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS,
                    "Asset Menus are not found for the requested category"));
            LOGGER.debug("Asset menus are found to be empty for the asset category type::{}", assetCategoryType);
        }
        LOGGER.info("Total time of execution for getting asset menus in ::{}", MWatchUtil.responseTime(startTime));
        return response;
        
    }
    
    /**
     * @aruna This Method returns the list of reportDetails for the loginUser
     *        loginUser may be either customer or internal user ths method takes
     *        input as userName,customerId
     * @param customerId
     * @param userName
     * @return Response<List<GroupsDto>>
     */
    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public @ResponseBody Response<List<ReportDTO>> getReportDetails(@PathVariable String userId,
            @PathVariable Integer customerId) {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Get the list of reportDetails for the user {} and customerId {}", userId, customerId);
        Response<List<ReportDTO>> response = new Response<List<ReportDTO>>();
        
        List<ReportDTO> reports = reportService.getReports(userId, customerId);
        if(!reports.isEmpty()) {
            LOGGER.debug("Data found for the request username and customerId::{} and the size is::{}", userId,
                    customerId, reports.size());
            response.setSuccess(reports);
        } else {
            LOGGER.debug("No data found for the requested userName and customerId::{}", userId, customerId);
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS,
                    "No records found for given customer id"));
        }
        LOGGER.info("Total time taken to get the group values {} ms", System.currentTimeMillis() - startTime);
        return response;
        
    }
    
    @ExceptionHandler
    public @ResponseBody Response<List<ErrorBean>> RequestBodyHandlingException(HttpMessageNotReadableException ex) {
        LOGGER.error("No request body provided :: ", ex);
        Response<List<ErrorBean>> response = new Response<List<ErrorBean>>();
        response.setFailure(ErrorBean.getErrorMessage(MwatchStatusCode.INVALID_REQUEST,
                "Required request body content is missing"));
        LOGGER.error("Error because Invalid Request boady ::{}", ex);
        return response;
    }
    
    /**
     * getMeasurementNames method is used to get the measurement names based on
     * ciid
     * 
     * @author srinivasarao
     * @param ciId
     * @param dashboardId
     * @return
     */
    @RequestMapping(value = "/module/dashboard/{dashboardId}/components/Measurement/{ciId}", method = RequestMethod.GET)
    @ResponseBody
    public Response<MeasurementDetailsDto> getMeasurementNames(@PathVariable Long ciId,
            @PathVariable Integer dashboardId) {
        LOGGER.info("Going to get the configured measurements for ciid::{}", ciId);
        StopWatch watch = new StopWatch();
        watch.start();
        MeasurementDetailsDto measurementresponse = dashboardService.getMeasurementNames(ciId, dashboardId);
        Response<MeasurementDetailsDto> response = new Response<>();
        if(measurementresponse != null) {
            response.setSuccess(measurementresponse);
            watch.stop();
            LOGGER.info("Total time taken to retrieve measurement info  details for ciId::{} is ::{}ms", ciId,
                    watch.getTotalTimeMillis());
            return response;
        }
        response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, MWatchConstants.NO_RECORDS_FOUND));
        LOGGER.info(" Measurement details for ciId {}- No records found", ciId);
        return response;
    }
    
    /**
     * Method to give the status of the ticket after performing each and every
     * action. This method will get ticket id and its type {I/R/P} as input and
     * gives its corresponding currentstatus
     * 
     * @author sukanya
     * @param userId
     * @param customerId
     * @param tickettype
     * @param ticketid
     * @return
     */
    @RequestMapping(value = "/module/{tickettype}/ticket/{ticketid}/currentstatus", method = RequestMethod.GET)
    @ResponseBody
    public Response<String> getCurrentTicketStatus(@PathVariable String userId, @PathVariable String customerId,
            @PathVariable String tickettype, @PathVariable Long ticketid) {
        Response<String> response = new Response<>();
        String status = null;
        LOGGER.debug("Ticket {} is of type {}", ticketid, tickettype);
        if(tickettype.equalsIgnoreCase(MWatchConstants.INCIDENTCODE)
                || tickettype.equalsIgnoreCase(MWatchConstants.REQUESTCODE)) {
            status = ticketService.getTicketStatus(ticketid);
        } else if(tickettype.equalsIgnoreCase(MWatchConstants.PROBLEMCODE)) {
            status = problemService.getTicketStatus(ticketid);
        }
        if(status != null) {
            response.setSuccess(status);
            LOGGER.info("Status of the ticket {} of type {} is found to be {}", ticketid, tickettype, status);
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS,
                    "Status of the ticket is not found"));
            LOGGER.info("Status of the ticket {} of type {} is  found to be empty", ticketid, tickettype);
        }
        return response;
    }
    
    /**
     * Get the Current time of Server
     * 
     * @author Pavan
     * @return
     */
    @RequestMapping(value = "/currenttime", method = RequestMethod.GET)
    @ResponseBody
    public Response<MwatchDateTime> getCurrectTime() {
        Response<MwatchDateTime> response = new Response<>();
        MwatchDateTime dateTime = new MwatchDateTime();
        LocalDateTime now = LocalDateTime.now();
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        String zone = timeZone.getDisplayName(false, timeZone.SHORT);
        LOGGER.info("time zone is {} ", zone);
        dateTime.setYear(now.getYear());
        dateTime.setMonth(now.getMonth());
        dateTime.setDayOfMonth(now.getDayOfMonth());
        dateTime.setDayOfWeek(now.getDayOfWeek());
        dateTime.setDayOfYear(now.getDayOfYear());
        dateTime.setHour(now.getHour());
        dateTime.setMinute(now.getMinute());
        dateTime.setSecond(now.getSecond());
        dateTime.setNano(now.getNano());
        dateTime.setMonthValue(now.getMonthValue());
        dateTime.setTimeZoneShortName(zone);
        response.setData(dateTime);
        Chronology chronology = now.getChronology();
        dateTime.setChronology(chronology);
        return response;
    }
    
    /**
     * Method to get user details for the search text entered. While creating
     * ticket, if one wants to change the requestor details then auto suggest
     * will be enabled for the user, so that while entering a search text he
     * will get the user names with the search string. Along with the user name
     * his details like email,phone and mobile information will be given
     * 
     * @author sukanya
     * @param userId
     * @param customerId
     * @param searchtext
     * @return
     */
    
    @RequestMapping(value = "/{searchtext}", method = RequestMethod.GET)
    @ResponseBody
    public Response<List<UserDetailsDTO>> getUserName(@PathVariable String userId, @PathVariable String customerId,
            @PathVariable String searchtext) {
        LOGGER.info("In Controller for getting user details for the search text {} for customer {}", searchtext,
                customerId);
        Response<List<UserDetailsDTO>> response = new Response<>();
        List<UserDetailsDTO> userDetails = Collections.emptyList();
        StopWatch watch = new StopWatch();
        if(!searchtext.isEmpty()) {
            watch.start();
            userDetails = ticketService.getUserDetails(searchtext, customerId);
            watch.stop();
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NOT_ACCEPTABLE, "Invalid input"));
            LOGGER.debug("Search text entered is found to be empty");
        }
        if(!userDetails.isEmpty()) {
            response.setSuccess(userDetails);
            LOGGER.info("Userdeatils for search string {} for customer {} is fetched successfully within {} ms",
                    searchtext, customerId, watch.getTotalTimeMillis());
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS,
                    "UserDetails are found to be empty"));
            LOGGER.debug("Userdeatils for search string {} for customer {} is found to be empty", searchtext,
                    customerId);
        }
        return response;
        
    }
    
    /**
     * getSearchKBInformation method is used to get the KB information.
     * kbentry,kbentryattributes,kbentrysources are the tables involved.
     * 
     * @author srinivasarao
     * @param customerId
     * @param search
     * @return
     */
    @RequestMapping(value = "/searchKB/{searchText}", method = RequestMethod.GET)
    @ResponseBody
    public Response<List<KbEntryDTO>> getSearchKBInformation(@PathVariable String customerId,
            @PathVariable String searchText, @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "kbid", required = false) String kbid,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "pagenum", required = false) Integer pageNum,
            @RequestParam(value = "pagecount", required = false) Integer pageCount) {
        StopWatch watch = new StopWatch();
        watch.start();
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("kbe.title", title);
        searchMap.put("kbe.description", description);
        searchMap.put("kbe.uid", kbid);
        searchMap.put("t.kb_entry_attributes", keywords);
        searchMap.put("kbes.ticket_id", source);
        int validatedPageNum = MWatchUtil.validatePageInfo(pageNum, MWatchConstants.PAGENUM, defaultProperties);
        int validatedPageCount = MWatchUtil.validatePageInfo(pageCount, MWatchConstants.PAGECOUNT, defaultProperties);
        Response<List<KbEntryDTO>> response = new Response<>();
        Long totalCount = viewIncidentServiceDatoInterface.getSearchKbCount(customerId, searchText, searchMap,
                validatedPageNum, validatedPageCount);
        if(totalCount == 0) {
            response.setWarning(Warning
                    .getWarningMessage(MwatchStatusCode.NO_RECORDS, MWatchConstants.NO_RECORDS_FOUND));
            LOGGER.info("Search kb info for customer id {} -No records found ", customerId);
            return response;
        }
        
        List<KbEntryDTO> kbDetails = viewIncidentServiceDatoInterface.getSearchKBDetails(customerId, searchText,
                searchMap, validatedPageNum, validatedPageCount);
        response.setPageDetails(new Pagination(validatedPageNum, validatedPageCount, totalCount.intValue()));
        response.setSuccess(kbDetails);
        watch.stop();
        LOGGER.info("Total time taken to retrieve search kb info for customer id::{} is ::{}ms", customerId,
                watch.getTotalTimeMillis());
        return response;
    }
    
    /**
     * Method to get the user privileges for the user who is logging in. This
     * will give the details of which are all the actions to be enabled for an
     * user based on the privileges given to him. This will suffice for all the
     * modules and profiles based on the role to which the logged in user is
     * assigned to
     * 
     * @author sukanya
     * @param userId
     * @param customerId
     * @param moduleDetails
     * @return
     */
    @RequestMapping(value = "/getPrivilege", method = RequestMethod.POST)
    @ResponseBody
    public Response<Map<String, Boolean>> getActionsForModule(@PathVariable String userId,
            @PathVariable String customerId, @RequestBody ModuleDetails moduleDetails) {
        
        Response<Map<String, Boolean>> response = new Response<>();
        if(!ValidateInput.isValidStringInput(moduleDetails.getPortalName(), moduleDetails.getModuleId(),
                moduleDetails.getProfileId())) {
            response.setFailure(ErrorBean.getErrorMessage(MwatchStatusCode.NOT_ACCEPTABLE, "Give a valid input"));
            LOGGER.info("As inputs for getting user privilege is not proper ,request is not processed for user {}",
                    userId);
            return response;
            
        } else {
            StopWatch watch = new StopWatch();
            watch.start();
            LOGGER.info("Getting privilege for user {} for the actions of module {} for portal {} for profile {} ",
                    userId, moduleDetails.getModuleId(), moduleDetails.getPortalName(), moduleDetails.getProfileId());
            Map<String, Boolean> actions = modulePrivilegeService.getAllActionsForModule(
                    MWatchUtil.castToInteger(moduleDetails.getModuleId()),
                    MWatchUtil.castToInteger(userProfile.get(moduleDetails.getPortalName())),
                    MWatchUtil.castToInteger(moduleDetails.getProfileId()));
            if(!actions.isEmpty()) {
                response.setSuccess(actions);
                watch.stop();
                LOGGER.info(
                        "List of action for user privilege is fetched successfully in {} ms for user {} for customer {}",
                        watch.getTotalTimeMillis(), userId, customerId);
            } else {
                response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No record found"));
                LOGGER.debug("List of action for user privilege for user {} for customer {} is found to be empty",
                        userId, customerId);
            }
        }
        return response;
        
    }
    
    /**
     * getSlaNamedFilters method is used to get the static named filters for sla
     * types resolution sla and response sla. getting the data from
     * named_filters table.
     * 
     * @author srinivasarao
     * @param customerId
     * @return
     */
    @RequestMapping(value = "/slaNamedFilters", method = RequestMethod.GET)
    public @ResponseBody Response<Map<String, List<NamedFiltersDTO>>> getSlaNamedFilters(@PathVariable String customerId) {
        StopWatch watch = new StopWatch();
        watch.start();
        LOGGER.info("Going to get sla named filters for the customerId {}", customerId);
        Map<String, List<NamedFiltersDTO>> filterDetails = namedFilterService.getSlaNamedFilterInfo();
        Response<Map<String, List<NamedFiltersDTO>>> response = new Response<>();
        response.setSuccess(filterDetails);
        watch.stop();
        LOGGER.info("Total time taken to retrieve sla named filter details for customer id::{} is ::{}ms", customerId,
                watch.getTotalTimeMillis());
        return response;
    }
    
    /**
     * Provides data to populate software listing dropdown in asset module. Gets
     * data from named_filters table
     * 
     * @param customerId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/softwares", method = RequestMethod.GET)
    @ResponseBody
    public Response<List<NamedFiltersDTO>> getSoftwareFilters(@PathVariable String customerId,
            @PathVariable String userId) {
        StopWatch watch = new StopWatch();
        watch.start();
        LOGGER.info("Software filters to be fetched for the user id {} for customer {}", userId, customerId);
        Response<List<NamedFiltersDTO>> response = new Response<List<NamedFiltersDTO>>();
        List<NamedFiltersDTO> assetMenu = assetService.getSoftwareFilters();
        if(!assetMenu.isEmpty()) {
            response.setSuccess(assetMenu);
            LOGGER.info("Software filters are fetched succesfully for the input user id:{}", userId);
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No record found"));
            LOGGER.warn("Software filters are found to be empty for the given input user id:{}", userId);
        }
        watch.stop();
        LOGGER.info("Total time taken to retrieve sla named filter details for customer id::{} is ::{}ms", customerId,
                watch.getTotalTimeMillis());
        return response;
    }
    
    /**
     * Provides first drop down for desktop listing, for e.g - byOs, byDomain.
     * Queries named_filters table.
     * 
     * @param customerId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/desktops", method = RequestMethod.GET)
    @ResponseBody
    public Response<List<NamedFiltersDTO>> getDesktopFilters(@PathVariable String customerId,
            @PathVariable String userId) {
        StopWatch watch = new StopWatch();
        watch.start();
        LOGGER.info("Desktop filters to be fetched for the user id {} for customer {}", userId, customerId);
        Response<List<NamedFiltersDTO>> response = new Response<List<NamedFiltersDTO>>();
        List<NamedFiltersDTO> assetMenu = assetService.getDesktopFilters();
        if(!assetMenu.isEmpty()) {
            response.setSuccess(assetMenu);
            LOGGER.info("Desktop filters are fetched succesfully for the input user id:{}", userId);
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No record found"));
            LOGGER.warn("Desktop filters are found to be empty for the given input user id:{}", userId);
        }
        watch.stop();
        LOGGER.info("Total time taken to retrieve sla named filter details for customer id::{} is ::{}ms", customerId,
                watch.getTotalTimeMillis());
        return response;
    }
    
    /**
     * Gets desktop sub filters from DB. For example - for By OS it would return
     * return Windows, Linux etc.
     * 
     * @param customerId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/desktopsubfilters/{desktopSubFilterSearchTerm}", method = RequestMethod.GET)
    @ResponseBody
    public Response<List<NamedFiltersDTO>> getDesktopSubFilters(@PathVariable String customerId,
            @PathVariable String desktopSubFilterSearchTerm, @RequestParam(required = false) Integer categoryId) {
        StopWatch watch = new StopWatch();
        watch.start();
        LOGGER.info(" Getting desktop sub filters for customer {}", customerId);
        IDesktopSubFilter desktopSubFilter = desktopSubFiltersMap.get(desktopSubFilterSearchTerm);
        List<NamedFiltersDTO> assetListing = desktopSubFilter.getDesktopSubFilters(customerId, categoryId);
        Response<List<NamedFiltersDTO>> response = new Response<>();
        if(!assetListing.isEmpty()) {
            response.setSuccess(assetListing);
            watch.stop();
            LOGGER.info("Total time taken to get  dependentapplications details is ::{}ms", watch.getTotalTimeMillis());
            return response;
        }
        response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No Records Found"));
        watch.stop();
        LOGGER.info("Total time taken to get  dependentapplications details is ::{}ms", watch.getTotalTimeMillis());
        return response;
    }
    
    /**
     * Method to get the ci and due date details for the particular user and
     * customer given. This is for enabling ticket creation from
     * createTicketByEndUser
     * 
     * @author sukanya
     * @param userId
     * @param customerId
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/autopopulateciinfo", method = RequestMethod.GET)
    @ResponseBody
    public Response<UserDetailsDTO> autoPopulateDetails(@PathVariable String userId, @PathVariable Integer customerId)
            throws ParseException {
        LOGGER.info(" Auto populating ci details for the customer {} for user {} ", customerId, userId);
        Response<UserDetailsDTO> response = new Response<>();
        UserDetailsDTO userDetails = viewIncidentServiceDatoInterface.getCiBasicInfo(userId, customerId);
        if(userDetails != null) {
            response.setSuccess(userDetails);
            LOGGER.info("CI details for auto population are fetched succesfully for user{} for customer {}", userId,
                    customerId);
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No Records Found"));
        }
        return response;
    }
    
    /**
     * getMemberDetails method is used to get user details from Active directory
     * and it wil display name, email address and phone number.
     * 
     * @author srinivasarao
     * @param customerId
     * @return
     * @throws IOException
     * @throws SAXException
     */
    @RequestMapping(value = "/adsearch/{type}", method = RequestMethod.GET)
    @ResponseBody
    public Response<MemberInfo> getMemberDetails(@PathVariable String customerId,
            @RequestParam(required = true) String userSearch) throws SAXException, IOException {
        StopWatch watch = new StopWatch();
        watch.start();
        LOGGER.info("Going to get member information from ad directory for customer id  {}", customerId);
        MemberInfo memberInfo = authenticationServiceIfc.getActiveDirInfo(customerId, userSearch);
        Response<MemberInfo> response = new Response<>();
        if(memberInfo != null) {
            response.setSuccess(memberInfo);
            watch.stop();
            LOGGER.info("Active directory information successfully received in {} ms", watch.getTotalTimeMillis());
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No Records Found"));
        }
        return response;
    }
    
    /**
     * @author srinivasarao
     * @param feedbackForm
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/saveFeedbackForm", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> saveFeedbackFormInfo(@RequestBody FeedbackFormInfo feedbackForm) throws ParseException {
        StopWatch watch = new StopWatch();
        watch.start();
        Boolean isSaved = namedFilterService.saveFeedbackFormDetails(feedbackForm);
        Response<String> response = new Response<>();
        if(isSaved) {
            response.setSuccess("data updated successfully");
            watch.stop();
            LOGGER.info("Total time taken to save feedback info is {} ", watch.getTotalTimeMillis());
            return response;
        }
        response.setWarning(Warning.getWarningMessage(MwatchStatusCode.UPDATE_FAILED, " save operation is failed"));
        return response;
    }
    
    /**
     * getCurrentTime method is used to get current time information based on ci
     * time zone
     * 
     * @author srinivasarao
     * @param ciId
     * @return
     */
    @RequestMapping(value = "/cicurrenttime/ciId/{ciId}", method = RequestMethod.GET)
    @ResponseBody
    public Response<CiCurrentTimeDto> getCurrentTime(@PathVariable Long ciId) {
        StopWatch watch = new StopWatch();
        watch.start();
        LOGGER.info("Going to get current time information for ciid  {}", ciId);
        CiCurrentTimeDto dateTime = assetService.getCurrentDateAndTime(ciId);
        Response<CiCurrentTimeDto> response = new Response<>();
        if(dateTime != null) {
            response.setSuccess(dateTime);
            watch.stop();
            LOGGER.info("successfully received datetime in {} ms", watch.getTotalTimeMillis());
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No Records Found"));
        }
        return response;
    }
    
    /**
     * getDisplayGroups method is used to get the ci group id based on cutomer
     * and user information
     * 
     * @author srinivasarao
     * @param customerId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/displaygroup", method = RequestMethod.GET)
    @ResponseBody
    public Response<List<AssetDTO>> getDisplayGroups(@PathVariable String customerId, @PathVariable String userId) {
        LOGGER.debug("Going to get the group id for the input user id::{}", userId);
        Response<List<AssetDTO>> response = new Response<List<AssetDTO>>();
        List<AssetDTO> assetMenu = assetService.getDisplayGroups(customerId, userId);
        if(!assetMenu.isEmpty()) {
            response.setSuccess(assetMenu);
            LOGGER.debug("Asset categories are fetched succesfully for the input user id:{}", userId);
        } else {
            response.setWarning(Warning.getWarningMessage(MwatchStatusCode.NO_RECORDS, "No record found"));
            LOGGER.debug("Asset categories are found to be empty for the given input user id:{}", userId);
        }
        return response;
    }
    
    /**
     * validatescheduletime method is used to validate scheduled time with
     * currentime + 10 mins
     * 
     * @author srinivasarao
     * @param scheduleDetails
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/validatescheduletime", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> validateScheduleInfo(@RequestBody ScheduleDetails scheduleDetails) throws ParseException {
        StopWatch watch = new StopWatch();
        watch.start();
        Response<String> response = new Response<>();
        boolean schedule = assetService.validateScheduleDetails(scheduleDetails);
        if(schedule) {
            response.setSuccess("DCFC scheduled after 10minutes from current time");
            watch.stop();
            LOGGER.info("Total time taken to validatescheduletime::{} is ::{}ms", watch.getTotalTimeMillis());
            return response;
        }
        response.setWarning(Warning.getWarningMessage(MwatchStatusCode.UPDATE_FAILED,
                "DCFC can be scheduled only after 10minutes from current time"));
        return response;
        
    }
    
    /**
     * This method handles the common exception which ensure the application at
     * least returns some error message
     * 
     * @param ex
     * @param httpResponse
     * @return Response<List<ErrorBean>>
     */
    @ExceptionHandler
    public @ResponseBody Response<List<ErrorBean>> InvalidRequest(InvalidRequestBodyException ex) {
        LOGGER.error("Handling Invalid data in request body::{} ", ex);
        Response<List<ErrorBean>> response = new Response<List<ErrorBean>>();
        response.setFailure(ErrorBean.getErrorMessage(MwatchStatusCode.INVALID_REQUEST, ex.getMessage()));
        LOGGER.error("Error because Invalid Request boady ::{}", ex);
        return response;
    }
    
    @ExceptionHandler
    public @ResponseBody Response<List<ErrorBean>> forceContentTypeException(HttpMediaTypeNotSupportedException ex) {
        LOGGER.error("Handling contetn type exceptions:: ", ex);
        Response<List<ErrorBean>> response = new Response<List<ErrorBean>>();
        response.setFailure(ErrorBean
                .getErrorMessage(MwatchStatusCode.INVALID_REQUEST, "Please provide contetn type!!"));
        LOGGER.error("Application is down because ::{}", ex);
        return response;
    }
    
    @ExceptionHandler
    public @ResponseBody Response<List<ErrorBean>> invalidRequestException(InvalidRequestException ex) {
        LOGGER.error("Handling invalid request exceptions:: ", ex);
        Response<List<ErrorBean>> response = new Response<List<ErrorBean>>();
        response.setFailure(ErrorBean.getErrorMessage(MwatchStatusCode.INVALID_REQUEST, ex.getMessage()));
        LOGGER.error("Application is down because ::{}", ex);
        return response;
    }
    
    /**
     * This method handles the common exception which ensure the application at
     * least returns some error message
     * 
     * @param ex
     * @param httpResponse
     * @return Response<List<ErrorBean>>
     */
    @ExceptionHandler
    public @ResponseBody Response<List<ErrorBean>> handleAllOtherExceptions(Exception ex) {
        LOGGER.error("Handling generic exceptions:: ", ex);
        Response<List<ErrorBean>> response = new Response<List<ErrorBean>>();
        response.setFailure(ErrorBean.getErrorMessage(MwatchStatusCode.APPLICATION_DOWN, "Something went wrong!!"));
        LOGGER.error("Application is down because ::{}", ex);
        return response;
    }
    
}
