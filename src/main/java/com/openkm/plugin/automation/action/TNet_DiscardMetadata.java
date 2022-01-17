/**
 * Desarrollado por Guido Lara
 */

package com.openkm.plugin.automation.action;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.util.Calendar;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
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
public class TNet_DiscardMetadata extends BasePlugin implements Action {
	private static Logger log = LoggerFactory.getLogger(TNet_DiscardMetadata.class);
	private static final String TEST_PROPERTY_GROUP_NAME = "okg:test";
	private static final String TEST_PROPERTY_PARENT_NAME = "okp:test.parent";
	private static final String TEST_PROPERTY_CHILDREN_NAME = "okp:test.children";
	private static ArrayList<EnumEvents> EVENTS_AT_PRE = new ArrayList<>();

	private static ArrayList<EnumEvents> EVENTS_AT_POST =
		Stream.of(EnumEvents.EVENT_PROPERTY_GROUP_ADD, EnumEvents.EVENT_PROPERTY_GROUP_SET)
			.collect(Collectors.toCollection(ArrayList::new));

	@Autowired
	private AutomationUtils automationUtils;

	@Autowired
	private OKMDocument okmDocument;

	@Autowired
	private OKMFolder okmFolder;

	@Override
	public void executePre(Map<String, Object> env, Object... params) {
		// Nothing to do here
	}

	@Override
	public void executePost(Map<String, Object> env, Object... params) {
		log.info("executePost GFLC");
		log.info("executePost GFLC");

		try {
			NodeBase nodeBase = automationUtils.getNodeBase(env);

			if (nodeBase instanceof NodeDocument) {
				String propGrpName = automationUtils.getPropertyGroupName(env);
				Gson gson = new Gson();

				if (TEST_PROPERTY_GROUP_NAME.equals(propGrpName)) {
					Map<String, String> props = automationUtils.getPropertyGroupProperties(env);
					String childrenJson = props.get(TEST_PROPERTY_CHILDREN_NAME);
					String parentJson = props.get(TEST_PROPERTY_PARENT_NAME);

					if (parentJson != null && !parentJson.isEmpty() && childrenJson != null && !childrenJson.isEmpty()) {
						List<String> children = gson.fromJson(childrenJson, new TypeToken<List<String>>() {}.getType());
						List<String> parent = gson.fromJson(parentJson, new TypeToken<List<String>>() {}.getType());

						if (!parent.isEmpty() && !children.isEmpty()) {
							String systemToken = DbSessionManager.getInstance().getSystemToken();
							String uuid = nodeBase.getUuid();
							Calendar cal = Calendar.getInstance();
							int year = cal.get(Calendar.YEAR);
							String dstPath = "/okm:root/" + year + "/" + parent.get(0) + "/" + children.get(0);
							okmFolder.createMissingFolders(systemToken, dstPath);
							okmDocument.move(null, uuid, dstPath);
							okmDocument.lock(null, uuid);
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
		return "Metadata Catalog Test";
	}

	@Override
	public String getParamType00() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc00() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc00() {
		return "";
	}

	@Override
	public String getParamType01() {
		return Automation.PARAM_TYPE_EMPTY;
	}

	@Override
	public String getParamSrc01() {
		return Automation.PARAM_SOURCE_EMPTY;
	}

	@Override
	public String getParamDesc01() {
		return "";
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
