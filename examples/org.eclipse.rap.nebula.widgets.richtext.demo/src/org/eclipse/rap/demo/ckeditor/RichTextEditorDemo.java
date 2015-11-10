/*******************************************************************************
 * Copyright (c) 2013, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.demo.ckeditor;

import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class RichTextEditorDemo extends AbstractEntryPoint {

  @Override
  protected void createContents( final Composite parent ) {
    getShell().setText( "Nebula RichTextEditor Demo" );
    parent.setLayout( new GridLayout( 1, false ) );
    // CkEditor
    final RichTextEditor richTextEditor = new RichTextEditor( parent, SWT.NONE );
    richTextEditor.setFont( new Font( parent.getDisplay(), "fantasy", 19, 0 ) );
    richTextEditor.setText( "Hello Fantasy Font" );
    richTextEditor.setLayoutData( new GridData() );
    System.out.println( richTextEditor.getText() );
    richTextEditor.setBackground( parent.getDisplay().getSystemColor( SWT.COLOR_YELLOW ) );
    richTextEditor.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    ToolBar toolbar = new ToolBar( parent, SWT.FLAT );
    ToolItem printBtn = new ToolItem( toolbar, SWT.PUSH );
    printBtn.setText( "Print" );
    printBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        System.out.println( richTextEditor.getText() );
      }
    } );
    ToolItem destrBtn = new ToolItem( toolbar, SWT.PUSH );
    destrBtn.setText( "Dispose" );
    destrBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        richTextEditor.dispose();
      }
    } );
    ToolItem fontBtn = new ToolItem( toolbar, SWT.PUSH );
    fontBtn.setText( "Font" );
    fontBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        richTextEditor.setFont( new Font( parent.getDisplay(), "serif", 9, 0 ) );
      }
    } );
    final ToolItem editableBtn = new ToolItem( toolbar, SWT.CHECK );
    editableBtn.setText( "Editable" );
    editableBtn.setSelection( true );
    editableBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        richTextEditor.setEditable( editableBtn.getSelection() );
      }
    } );
    final ToolItem visibleBtn = new ToolItem( toolbar, SWT.CHECK );
    visibleBtn.setText( "Visible" );
    visibleBtn.setSelection( true );
    visibleBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        richTextEditor.setVisible( visibleBtn.getSelection() );
      }
    } );
    ToolItem clearBtn = new ToolItem( toolbar, SWT.NONE );
    clearBtn.setText( "Clear" );
    clearBtn.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        richTextEditor.setText( "" );
      }
    } );
  }

}
