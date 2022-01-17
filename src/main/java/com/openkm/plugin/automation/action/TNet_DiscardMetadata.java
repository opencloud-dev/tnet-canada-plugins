/**
 * @author Guido F. Lara C.
 * @version 2022-01-17 / 1.0.0
 * @param okg:group - Filter Property group to considerate remove
 * @param okg:group1|okg:groupN - Properties apply the remove
 * @return(void)
 * @see  <a href = "https://docs.openkm.com/kcenter/view/okm-7.1/" /> OpenKM – KCenter </a>
 * @Description
 * - Remove properties groups 
 * @Prerequisites
 * 1.- Create openkm-config (tnet.cfg_log_info)
 *  
 */

package com.openkm.plugin.automation.action;

import com.openkm.api.OKMPropertyGroup;
import com.openkm.db.bean.Automation;
import com.openkm.db.bean.AutomationRule.EnumEvents;
import com.openkm.db.bean.NodeBase;
import com.openkm.db.bean.NodeDocument;
import com.openkm.db.service.ConfigSrv;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.plugin.BasePlugin;
import com.openkm.plugin.automation.Action;
import com.openkm.plugin.automation.AutomationUtils;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.springframework.beans.factory.annotation.Autowired;

//import com.openkm.util.FileLogger;
import com.openkm.util.TNet_Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PluginImplementation
public class TNet_DiscardMetadata extends BasePlugin implements Action {
	public static final String BASE_NAME = TNet_DiscardMetadata.class.getSimpleName();
	private static ArrayList<EnumEvents> EVENTS_AT_PRE = new ArrayList<>();

	private static ArrayList<EnumEvents> EVENTS_AT_POST =
		Stream.of(EnumEvents.EVENT_PROPERTY_GROUP_ADD, EnumEvents.EVENT_PROPERTY_GROUP_SET)
			.collect(Collectors.toCollection(ArrayList::new));
	
	private static String token;
	private static String uuid;
	private static boolean isLogInfo;
	private static String FILTER_PROPERTY_GROUP;
	private static String DISCARD_PROPERTY_GROUP;
	private static String automation_propGrpName;
	
	
	@Autowired
	  private ConfigSrv configSrv;
	@Autowired
	private AutomationUtils automationUtils;

	@Autowired
	private OKMPropertyGroup okmPropertyGroup;

	@Override
	public void executePre(Map<String, Object> env, Object... params) {
		// Nothing to do here
	}

	/**
	 * 
     * Method to remove properties group according to input parms
     * @param executePost(Map<String, Object> env, Object... params)
     * 
     */
	@Override
	public void executePost(Map<String, Object> env, Object... params) {

		try {
			token = DbSessionManager.getInstance().getSystemToken();
			NodeBase nodeBase = this.automationUtils.getNodeBase(env);
			uuid = this.automationUtils.getNodeToEvaluate(env).getUuid();
			isLogInfo = Boolean.valueOf(this.configSrv.getString(TNet_Util.TNET_CONFIG_LOG_INFO, "tnet.cfg_log_info"));
			if(isLogInfo)TNet_Util.infoLogger(BASE_NAME, token, "Init Discard Metadata ["+uuid+"]: " + nodeBase.getPath());
			
			// filters to remove properties groups
			if (nodeBase instanceof NodeDocument) {
				//Extract input params
				FILTER_PROPERTY_GROUP = this.automationUtils.getString(0, params); 
				DISCARD_PROPERTY_GROUP = this.automationUtils.getString(1, params);
				automation_propGrpName = this.automationUtils.getPropertyGroupName(env);
				if(isLogInfo)TNet_Util.infoLogger(BASE_NAME, token, "ACTUAL_PROPERTY_GOUP ["+automation_propGrpName+"] FILTER_PROPERTY_GROUP ["+FILTER_PROPERTY_GROUP+"] DISCARD_PROPERTY_GROUP ["+DISCARD_PROPERTY_GROUP+"] ");

				if (FILTER_PROPERTY_GROUP.equalsIgnoreCase(automation_propGrpName)) {
					
					String[] arrOfStr = DISCARD_PROPERTY_GROUP.split("\\|");
					for (String PROPERTY_GET : arrOfStr){
						if( this.okmPropertyGroup.hasGroup(token, uuid, PROPERTY_GET)){
							//Remove Property group
							try{ this.okmPropertyGroup.removeGroup(token, uuid, PROPERTY_GET.toLowerCase());
							if(isLogInfo)TNet_Util.infoLogger(BASE_NAME, token, "DICARD_GROUP_NAME Completed["+PROPERTY_GET+"]");
							} catch (Exception e) {
								TNet_Util.errorLogger(BASE_NAME, token, e.getMessage());
							}	
						}
					}	
				}
			}
		} catch (Exception e) {
			TNet_Util.errorLogger(BASE_NAME, token, e.getMessage());
		}
	} // End Method

	@Override
	public String getName() {
		return "TNET Discard Metadata";
	}

	@Override
	public String getParamType00() {
		return Automation.PARAM_TYPE_TEXT;
	}

	@Override
	public String getParamSrc00() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc00() {
		return "FILTER_PROPERTY_GROUP okg:group";
	}

	@Override
	public String getParamType01() {
		return Automation.PARAM_TYPE_TEXT;
	}

	@Override
	public String getParamSrc01() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc01() {
		return "DISCARD_PROPERTIES_GROUPS okg:group1|okg:groupN";
	}

	@Override
	public String getParamType02() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc02() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc02() {
		return "";
	}

	@Override
	public List<EnumEvents> getValidEventsAtPre() {
		return EVENTS_AT_PRE;
	}

	@Override
	public List<EnumEvents> getValidEventsAtPost() {
		return EVENTS_AT_POST;
	}
}
