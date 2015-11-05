/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;


public class RichTextEditor extends Composite {

  private static final String RESOURCES_PATH = "resources/";
  private static final String REGISTER_PATH = "ckeditor/";

  private static final String[] RESOURCE_FILES = {
    "ckeditor.js",
    "config.js",
    "handler.js",
    "contents.css",
    "lang/en.js",
    "skins/kama/editor.css",
    "skins/kama/icons.png",
    "skins/kama/images/sprites.png",
    "skins/kama/images/sprites_ie6.png"
  };
  private static final String REMOTE_TYPE = "rwt.widgets.RichTextEditor";

  private String text = "";
  private final RemoteObject remoteObject;

  private final OperationHandler operationHandler = new AbstractOperationHandler() {
    @Override
    public void handleSet( JsonObject properties ) {
      JsonValue textValue = properties.get( "text" );
      if( textValue != null ) {
        text = textValue.asString();
      }
    }
  };

  public RichTextEditor( Composite parent, int style ) {
    super( parent, style );
    registerResources();
    loadJavaScript();
    Connection connection = RWT.getUISession().getConnection();
    remoteObject = connection.createRemoteObject( REMOTE_TYPE );
    remoteObject.setHandler( operationHandler );
    remoteObject.set( "parent", WidgetUtil.getId( this ) );
  }

  private static void registerResources() {
    ResourceManager resourceManager = RWT.getResourceManager();
    boolean isRegistered = resourceManager.isRegistered( REGISTER_PATH + RESOURCE_FILES[ 0 ] );
    if( !isRegistered ) {
      try {
        for( String fileName : RESOURCE_FILES ) {
          register( resourceManager, fileName );
        }
      } catch( IOException ioe ) {
        throw new IllegalArgumentException( "Failed to load resources", ioe );
      }
    }
  }

  private static void loadJavaScript() {
    JavaScriptLoader jsLoader = RWT.getClient().getService( JavaScriptLoader.class );
    ResourceManager resourceManager = RWT.getResourceManager();
    jsLoader.require( resourceManager.getLocation( REGISTER_PATH + "handler.js" ) );
    jsLoader.require( resourceManager.getLocation( REGISTER_PATH + "ckeditor.js" ) );
    jsLoader.require( resourceManager.getLocation( REGISTER_PATH + "config.js" ) );
  }

  private static void register( ResourceManager resourceManager, String fileName ) throws IOException {
    ClassLoader classLoader = RichTextEditor.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( RESOURCES_PATH + fileName );
    try {
      resourceManager.register( REGISTER_PATH + fileName, inputStream );
    } finally {
      inputStream.close();
    }
  }

  ////////////////////
  // overwrite methods

  @Override
  public void setLayout( Layout layout ) {
    throw new UnsupportedOperationException( "Cannot change internal layout of RichTextEditor" );
  }

  @Override
  public void setFont( Font font ) {
    super.setFont( font );
    remoteObject.set( "font", getCssFont() );
  }

  @Override
  public void dispose() {
    remoteObject.destroy();
    super.dispose();
  }

  //////
  // API

  /**
   * Set text to the editing area. Can contain HTML tags for styling.
   *
   * @param text The text to set to the editing area.
   */
  public void setText( String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
    remoteObject.set( "text", text );
  }

  /**
   * Get the text from the editing area. Contains HTML tags for formatting.
   *
   * @return The text that is currently set in the editing area.
   */
  public String getText() {
    checkWidget();
    return text;
  }

  private String getCssFont() {
    StringBuilder result = new StringBuilder();
    if( getFont() != null ) {
      FontData data = getFont().getFontData()[ 0 ];
      result.append( data.getHeight() );
      result.append( "px " );
      result.append( data.getName() );
    }
    return result.toString();
  }

}