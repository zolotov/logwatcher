severity = ['all', 'trace', 'debug', 'info', 'warning', 'error'];

$(document).ready(function() {

	function toggleEntry(entry) {
		entry.toggleClass('selectedEntry');
		var checksum = entry.attr("checksum");
		var lastOccurredDate = entry.attr("lastOccurredDate");
		var content = entry.find('.entryContainer');
		if ( content.attr("loaded") == "false" ) {
			content.attr("loaded", "true");
			entry.addClass("loadingContent");
			content.load("/service/content", {'checksum': checksum, 'date': lastOccurredDate}, function(response, code) {
				if ( code != "success" ) {
					content.html("Error while loading content");
				}
				entry.removeClass("loadingContent");

			});
		}

	}

  $('.givenQuery').click(function(target) {
    var el = $(target.target).parents('.givenQuery');
    var query = el.attr('rawQuery');
    el.html("<form action='/search'><input id='searchInput' type='text' name='q' value='"+query+"' /></form>");
    $('#searchInput').focus();
    el.unbind('click');
  });

	$('.entryHeader').click(function(target) {
		if ( $(target.target).parents(".noBubble").length <= 0 ) {
			toggleEntry($(this).parents(".entry"));
		}
	});

	$('a.removeEntry').live('click', function() {
		var entry = $(this).parents(".entry");
		var checksum = entry.attr('checksum');
		if ( confirm('Are you shure you want to remove this entry?') ) {
			entry.addClass('removing');
			$.ajax({
				type: "GET",
				url: '/entry/remove?checksum=' + checksum,
				complete: function() {
					entry.removeClass('removing');
				},
				success: function(msg) {
					entry.hide('normal');
				},
				error: function(request, status, error) {
					alert("There is an error occured while removing entry: "+status+", "+error)
				}
			})
		}

		return false;
	});

	var checksum = location.hash.substring(1);
	if ( checksum.length > 0 ) {
		$('.entry').each(function() {
			if ( $(this).attr('checksum') == checksum ) {
				toggleEntry($(this));
			}
		});
	}

	$('#applicationId').click(function() {
		showPopup($(this), $('#applicationSelector'));
		return false;
	});

	$('#date').click(function() {
		showPopup($(this), $('#dateSelector'));
		return false;
	});

	$('body').click(function(event) {
		if ( $(event.target).parents(".popupContainer").length <= 0 ) {
			hideAllPopups();
		}
	});

	function hideAllPopups() {
		$('.popupContainer').fadeOut(300);
	}

	function showPopup(activator, popup) {
		if ( popup.css('display') == 'none' ) {
			hideAllPopups();
			var position = activator.position();
			popup.css({"left": position.left, "top": position.top + activator.height() + 10});
			popup.find('img.mark').css( { left: (activator.width()/2 - 8)+"px" } );

				//;
			popup.fadeIn(300);
		}else{
			popup.fadeOut(300);
		}
	}

});
