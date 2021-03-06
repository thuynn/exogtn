/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 */

package org.exoplatform.web.application;

import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;

import java.net.URLEncoder;

/**
 * Created by The eXo Platform SAS
 * Mar 29, 2007  
 */
abstract public class URLBuilder<T>
{

   private static Logger LOGGER = LoggerFactory.getLogger(URLBuilder.class);

   protected String baseURL_;

   public URLBuilder(String baseURL)
   {
      baseURL_ = baseURL;
   }

   public String getBaseURL()
   {
      return baseURL_;
   }

   public void setBaseURL(String url)
   {
      baseURL_ = url;
   }

   public String createURL(String action)
   {
      return createURL(action, (Parameter[])null);
   }

   abstract public String createURL(String action, Parameter[] params);

   public String createURL(String action, String objectId)
   {
      return createURL(action, objectId, (Parameter[])null);
   }

   abstract public String createURL(String action, String objectId, Parameter[] params);

   public String createURL(T targetComponent, String action, String targetBeanId)
   {
      return createURL(targetComponent, action, null, targetBeanId, (Parameter[])null);
   }

   public String createAjaxURL(T targetComponent, String action, String targetBeanId)
   {
      return createAjaxURL(targetComponent, action, null, targetBeanId, (Parameter[])null);
   }

   public String createAjaxURL(T targetComponent, String action, String confirm, String targetBeanId)
   {
      return createAjaxURL(targetComponent, action, confirm, targetBeanId, (Parameter[])null);
   }

   public String createAjaxURL(T targetComponent, String action, String confirm, String targetBeanId, Parameter[] params)
   {
      StringBuilder builder = new StringBuilder("javascript:");
      if (confirm != null && confirm.length() > 0)
      {
         builder.append("if(confirm('").append(confirm.replaceAll("'", "\\\\'")).append("'))");
      }
      builder.append("ajaxGet('");
      if (targetBeanId != null)
      {
         try
         {
            targetBeanId = URLEncoder.encode(targetBeanId, "utf-8");
         }
         catch (Exception e)
         {
            LOGGER.error("Fail to encode AjaxURL", e);
         }
      }
      createURL(builder, targetComponent, action, targetBeanId, params);
      builder.append("&amp;ajaxRequest=true')");
      return builder.toString();
   }

   public String createURL(T targetComponent, String action, String confirm, String targetBeanId, Parameter[] params)
   {
      StringBuilder builder = new StringBuilder();
      boolean hasConfirm = confirm != null && confirm.length() > 0;
      if (hasConfirm)
      {
         builder.append("javascript:if(confirm('").append(confirm.replaceAll("'", "\\\\'")).append("'))");
         builder.append("window.location=\'");
      }
      if (targetBeanId != null)
      {
         try
         {
            targetBeanId = URLEncoder.encode(targetBeanId, "utf-8");
         }
         catch (Exception e)
         {
            LOGGER.error("Fail to encode URL", e);
         }
      }
      createURL(builder, targetComponent, action, targetBeanId, params);
      if (hasConfirm)
         builder.append("\';");
      return builder.toString();
   }

   abstract protected void createURL(StringBuilder builder, T targetComponent, String action, String targetBeanId,
      Parameter[] params);
}
