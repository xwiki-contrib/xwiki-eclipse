/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.xwiki.eclipse.ui.adapters;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.xwiki.eclipse.model.XWikiEclipseObjectProperty;
import org.xwiki.eclipse.model.XWikiEclipseObjectSummary;
import org.xwiki.eclipse.storage.XWikiEclipseStorageException;
import org.xwiki.eclipse.ui.UIConstants;
import org.xwiki.eclipse.ui.UIPlugin;

/**
 * @version $Id$
 */
public class XWikiEclipseObjectSummaryAdapter extends WorkbenchAdapter
{
    @Override
    public String getLabel(Object object)
    {
        if (object instanceof XWikiEclipseObjectSummary) {
            XWikiEclipseObjectSummary objectSummary = (XWikiEclipseObjectSummary) object;

            int number = objectSummary.getNumber();
            /*
             * display the property name and value, the priority: author -> name or title -> the first stringClass ->
             * the first non-textarea class
             */
            String label = "[" + number + "] : ";
            List<XWikiEclipseObjectProperty> objectProperties;
            try {
                objectProperties =
                    objectSummary
                        .getDataManager()
                        .getObject(objectSummary.getWiki(), objectSummary.getSpace(), objectSummary.getPageName(),
                            objectSummary.getClassName(), objectSummary.getNumber()).getProperties();
                boolean findNameAndTitle = false, foundStringProperty = false;
                for (XWikiEclipseObjectProperty property : objectProperties) {
                    /* look for author first */
                    String propertyName = property.getName().toLowerCase();
                    if (propertyName.indexOf("author") >= 0 && property.getValue().length() > 0) {
                        label += property.getValue();
                        return label;
                    }
                }
                for (XWikiEclipseObjectProperty property : objectProperties) {
                    String propertyName = property.getName().toLowerCase();

                    if (propertyName.indexOf("name") >= 0 || propertyName.indexOf("title") >= 0) {
                        if (property.getValue().length() > 0) {
                            /* look for name and title */
                            findNameAndTitle = true;
                            label += property.getValue();
                            return label;
                        }
                    }
                }

                for (XWikiEclipseObjectProperty property : objectProperties) {
                    if (!findNameAndTitle) {
                        if (property.getType().indexOf("StringClass") >= 0 && !foundStringProperty) {
                            if (property.getValue().length() > 0) {
                                foundStringProperty = true;
                                label += property.getValue();
                                return label;
                            }
                        }
                    }
                }

                for (XWikiEclipseObjectProperty property : objectProperties) {
                    if (!findNameAndTitle && !foundStringProperty) {
                        if (!(property.getType().indexOf("TextAreaClass") >= 0) && !foundStringProperty) {
                            if (property.getValue().length() > 0) {
                                label += property.getValue();
                                return label;
                            }
                        }
                    }
                }

                return label;
            } catch (XWikiEclipseStorageException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return super.getLabel(object);
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object)
    {
        if (object instanceof XWikiEclipseObjectSummary) {
            XWikiEclipseObjectSummary objectSummary = (XWikiEclipseObjectSummary) object;
            String classname = objectSummary.getClassName();
            if (classname.equals("AnnotationCode.AnnotationClass")) {
                return UIPlugin.getImageDescriptor(UIConstants.PAGE_ANNOTATION_ICON);
            }

            if (objectSummary.getDataManager().isLocallyAvailable(objectSummary.getPageId(),
                objectSummary.getClassName(), objectSummary.getNumber())) {
                return UIPlugin.getImageDescriptor(UIConstants.OBJECT_LOCALLY_AVAILABLE_ICON);
            } else {
                return UIPlugin.getImageDescriptor(UIConstants.OBJECT_ICON);
            }

        }

        return null;
    }
}
