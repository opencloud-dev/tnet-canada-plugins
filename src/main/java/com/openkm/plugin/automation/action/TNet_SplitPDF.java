/**
 * @author Guido F. Lara C.
 * @version 2022-01-17 / 1.0.0
 * @param okg:group - Filter Property group to considerate remove
 * @param okg:group1|okg:groupN - Properties apply the remove
 * @return(void)
 * @see  <a href = "https://docs.openkm.com/kcenter/view/okm-7.1/" /> OpenKM – KCenter </a>
 */

package com.openkm.plugin.automation.action;

import com.openkm.api.OKMPropertyGroup;
import com.openkm.db.bean.Automation;
import com.openkm.db.bean.AutomationRule.EnumEvents;
import com.openkm.db.bean.NodeBase;
import com.openkm.db.bean.NodeDocument;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.plugin.BasePlugin;
import com.openkm.plugin.automation.Action;
import com.openkm.plugin.automation.AutomationUtils;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PluginImplementation
public class TNet_SplitPDF extends BasePlugin implements Action {
	private static Logger log = LoggerFactory.getLogger(TNet_SplitPDF.class);
	private static ArrayList<EnumEvents> EVENTS_AT_PRE = new ArrayList<>();

	private static ArrayList<EnumEvents> EVENTS_AT_POST =
		Stream.of(EnumEvents.EVENT_PROPERTY_GROUP_ADD, EnumEvents.EVENT_PROPERTY_GROUP_SET)
			.collect(Collectors.toCollection(ArrayList::new));

	@Autowired
	private AutomationUtils automationUtils;

	@Autowired
	private OKMPropertyGroup okmPropertyGroup;

	@Override
	public void executePre(Map<String, Object> env, Object... params) {
		// Nothing to do here
	}

	@Override
	public void executePost(Map<String, Object> env, Object... params) {
		log.info("Init Discard Metadata");

		try {
			NodeBase nodeBase = this.automationUtils.getNodeBase(env);
			String token = DbSessionManager.getInstance().getSystemToken();
			String uuid = this.automationUtils.getNodeToEvaluate(env).getUuid();
			
			//Extract input params
			String FILTER_PROPERTY_GROUP = this.automationUtils.getString(0, params); 
			String DISCARD_PROPERTY_GROUP = this.automationUtils.getString(1, params);
			String automation_propGrpName = this.automationUtils.getPropertyGroupName(env);
			
			log.error("OC: FILTER_PROPERTY_GROUP {"+FILTER_PROPERTY_GROUP+"} DISCARD_PROPERTY_GROUP {"+DISCARD_PROPERTY_GROUP+"} automationUtils.getPropertyGroupName(env) {"+automation_propGrpName+"}");
			
			if (nodeBase instanceof NodeDocument) {
				
				if (FILTER_PROPERTY_GROUP.equals(automation_propGrpName)) {
					
					String[] arrOfStr = DISCARD_PROPERTY_GROUP.split("\\|");
					for (String PROPERTY_GET : arrOfStr){
						if( this.okmPropertyGroup.hasGroup(token, uuid, PROPERTY_GET)){
							//Remove Property group
							try{ this.okmPropertyGroup.removeGroup(token, uuid, PROPERTY_GET);
							log.error("OC: DICARD_GROUP_NAME {"+PROPERTY_GET+"}");
							} catch (Exception e) {
								log.error("OC: ERROR DICARD_GROUP_NAME {"+PROPERTY_GET+"}");
							}	
						}
					}	
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

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
		return "FILTER PROPERTY_GROUP okg:group";
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
		return "DISCARD PROPERTY_GROUP okg:group1|okg:groupN";
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
