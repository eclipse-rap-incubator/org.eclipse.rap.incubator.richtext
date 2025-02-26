var CKEDITOR_BASEPATH = "rwt-resources/ckeditor/";

(function(){
  'use strict';

  rap.registerTypeHandler( "eclipsesource.CKEditor", {

    factory : function( properties ) {
      return new eclipsesource.CKEditor( properties );
    },

    destructor : "destroy",

    properties : [ "text", "font" ]

  } );

  if( !window.eclipsesource ) {
    window.eclipsesource = {};
  }

  eclipsesource.CKEditor = function( properties ) {
    bindAll( this, [ "layout", "onReady", "onSend", "onRender" ] );
    this.parent = rap.getObject( properties.parent );
    this.element = document.createElement( "div" );
    this.parent.append( this.element );
    this.parent.addListener( "Resize", this.layout );
    rap.on( "render", this.onRender );
  };

  eclipsesource.CKEditor.prototype = {

    ready : false,

    onReady : function() {
      // TODO [tb] : on IE 7/8 the iframe and body has to be made transparent explicitly
      this.ready = true;
      this.layout();
      if( this._text ) {
        this.setText( this._text );
        delete this._text;
      }
      if( this._font ) {
        this.setFont( this._font );
        delete this._font;
      }
    },

    onRender : function() {
      if( this.element.parentNode ) {
        rap.off( "render", this.onRender );
        this.editor = CKEDITOR.appendTo( this.element );
        this.editor.on( "instanceReady", this.onReady );
        rap.on( "send", this.onSend );
      }
    },

    onSend : function() {
      if( this.editor.checkDirty() ) {
        rap.getRemoteObject( this ).set( "text", this.editor.getData() );
        this.editor.resetDirty();
      }
    },

    setText : function( text ) {
      if( this.ready ) {
        this.editor.setData( text );
      } else {
        this._text = text;
      }
    },

    setFont : function( font ) {
      if( this.ready ) {
        async( this, function() { // Needed by IE for some reason
          this.editor.document.getBody().setStyle( "font", font );
        } );
      } else {
        this._font = font;
      }
    },

    destroy : function() {
      if( this.element.parentNode ) {
        rap.off( "send", this.onSend );
        this.editor.destroy();
        this.element.parentNode.removeChild( this.element );
      }
    },

    layout : function() {
      if( this.ready ) {
        var area = this.parent.getClientArea();
        this.element.style.left = area[ 0 ] + "px";
        this.element.style.top = area[ 1 ] + "px";
        this.editor.resize( area[ 2 ], area[ 3 ] );
      }
    }

  };

  var bind = function( context, method ) {
    return function() {
      return method.apply( context, arguments );
    };
  };

  var bindAll = function( context, methodNames ) {
    for( var i = 0; i < methodNames.length; i++ ) {
      var method = context[ methodNames[ i ] ];
      context[ methodNames[ i ] ] = bind( context, method );
    }
  };

  var async = function( context, func ) {
    window.setTimeout( function(){
      func.apply( context );
    }, 0 );
  };

}());
