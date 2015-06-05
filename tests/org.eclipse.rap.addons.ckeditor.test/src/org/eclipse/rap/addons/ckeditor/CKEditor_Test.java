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
package org.eclipse.rap.addons.ckeditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class CKEditor_Test {

  private Display display;
  private Shell shell;
  private CKEditor editor;
  private Connection connection;
  private RemoteObject remoteObject;

  @Rule
  public TestContext context = new TestContext();

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    remoteObject = mock( RemoteObject.class );
    connection = mock( Connection.class );
    when( connection.createRemoteObject( anyString() ) ).thenReturn( remoteObject );
    context.replaceConnection( connection );
    editor = new CKEditor( shell, SWT.BORDER );
  }

  @Test
  public void testSetLayout() {
    try {
      editor.setLayout( new FillLayout() );
      fail();
    } catch( UnsupportedOperationException ex ) {
      // expected
    }
  }

  @Test
  public void testContructor_CreatesRemoteObjectWithCorrectType() {
    verify( connection ).createRemoteObject( eq( "eclipsesource.CKEditor" ) );
  }

  @Test
  public void testContructor_SetsParent() {
    verify( remoteObject ).set( "parent", WidgetUtil.getId( editor ) );
  }

  @Test
  public void testContructor_LoadsJavaScriptFiles() {
    JavaScriptLoader loader = mockJavaScriptLoader();
    ResourceManager resourceManager = RWT.getResourceManager();

    new CKEditor( shell, SWT.BORDER );

    verify( loader ).require( resourceManager.getLocation( "ckeditor/ckeditor.js" ) );
    verify( loader ).require( resourceManager.getLocation( "ckeditor/config.js" ) );
    verify( loader ).require( resourceManager.getLocation( "ckeditor/handler.js" ) );
  }

  @Test
  public void testSetText_GetText() {
    String text = "foo<span>bar</span>";

    editor.setText( text );

    assertEquals( text, editor.getText() );
  }

  @Test
  public void testSetText_RendersToClient() {
    String text = "foo<span>bar</span>";

    editor.setText( text );

    verify( remoteObject ).set( "text", text );
  }

  @Test
  public void testSetTextNull() {
    try {
      editor.setText( null );
      fail();
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }

  @Test
  public void testSetTextFromClient() {
    String text = "foo<span>bar</span>";

    remoteSet( remoteObject, "text", JsonValue.valueOf( text ) );

    assertEquals( text, editor.getText() );
  }

  @Test
  public void testSetFont_RendersToClient() {
    editor.setFont( new Font( display, "fantasy", 13, 0 ) );

    verify( remoteObject ).set( "font", "13px fantasy" );
  }

  @Test
  public void testDispose_RendersDestroyToClient() {
    editor.dispose();

    verify( remoteObject ).destroy();
  }

  /////////
  // Helper

  private JavaScriptLoader mockJavaScriptLoader() {
    WebClient client = mock( WebClient.class );
    context.replaceClient( client );
    JavaScriptLoader loader = mock( JavaScriptLoader.class );
    when( client.getService( JavaScriptLoader.class ) ).thenReturn( loader );
    return loader;
  }

  private static void remoteSet( RemoteObject remoteObjectMock, String proprety, JsonValue value ) {
    getHandler( remoteObjectMock ).handleSet( new JsonObject().add( proprety, value ) );
  }

  private static OperationHandler getHandler( RemoteObject remoteObjectMock ) {
    ArgumentCaptor<OperationHandler> captor = ArgumentCaptor.forClass( OperationHandler.class );
    verify( remoteObjectMock ).setHandler( captor.capture() );
    return captor.getValue();
  }

}
