/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.designer.core.config;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.openhab.designer.core.CoreActivator;
import org.openhab.designer.core.DesignerCoreConstants;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationFolderProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationFolderProvider.class);
	
	private static IFolder folder; 
	
	static public synchronized IFolder getRootConfigurationFolder() {
		if(folder==null) {
			IProject defaultProject = ResourcesPlugin.getWorkspace().getRoot().getProject("config");
			try {
				if(!defaultProject.exists()) defaultProject.create(null);
				defaultProject.open(null);
				folder = defaultProject.getFolder("config");
				folder.createLink(getFileFromPreferences().toURI(), IResource.BACKGROUND_REFRESH|IResource.REPLACE, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return folder;
	}
	
	private static File getFileFromPreferences() {
		IPreferencesService service = Platform.getPreferencesService();
		Preferences node = service.getRootNode().node(DefaultScope.SCOPE).node(CoreActivator.PLUGIN_ID);
		if(node!=null) {
			String folderPath = node.get(DesignerCoreConstants.CONFIG_FOLDER_PREFERENCE, null);
			if(folderPath!=null) {
				File file = new File(folderPath);
				if(file!=null && file.isDirectory()) {
					return file;
				} else {
					logger.warn("'{}' is no valid directory.", folderPath);
				}
			}
		}
		return null;
	}
}