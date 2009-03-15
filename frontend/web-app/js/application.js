var Ajax;
if (Ajax && (Ajax != null)) {
	Ajax.Responders.register({
	  onCreate: function() {
        if($('spinner') && Ajax.activeRequestCount>0)
          Effect.Appear('spinner',{duration:0.5,queue:'end'});
	  },
	  onComplete: function() {
        if($('spinner') && Ajax.activeRequestCount==0)
          Effect.Fade('spinner',{duration:0.5,queue:'end'});
	  }
	});
}

function toggle(el) {
	if ( (/selected/).test(el.className) ) {
    el.className = el.className.replace(/selected/, '');
  }else{
    el.className += ' selected';
  }
}

function copyToClipboard(el) {
  var contentNode = el.parentNode.parentNode.nextSibling;
  var text = contentNode.innerHTML.createTextRange();
  window.clipboardData.setData("Text", text);
  alert(text);
}