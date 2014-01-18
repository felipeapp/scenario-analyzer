<script type="text/javascript">
$(document).ready(function(){
	
	$('.filter').parent().addClass("no-content-filter");
	$('.filter').parent().after('<tr></tr>').next().addClass("no-content-filter");
	
	$('.filter').parent().children().each(function(i, elem) {
		var linha = $('.filter').parent().next().append('<' + elem.tagName + '></' + elem.tagName + '>');
		if ($(elem).hasClass('filter')) {			
			linha.children(elem.tagName).each(function(j, item) {
				if ($(elem).hasClass('filter') && i == j) {
					var filtro = $(item).append('<input type="text" class="filterbox"/>').children('input').css('width', $(elem).css('width'));
					filtro.keyup(function() {
						$('.tablefilter').find('tr:not(.no-content-filter)').each(function(k, line) {
							$(line).children('td,th').each(function(l, coluna) {
								if (l == j) {
									var content = $.trim($(filtro).attr('value')).toUpperCase();
									if (content != '') {
										if ($(coluna).text().toUpperCase().indexOf(content) == 0) {
											$(line).show();
										} else {
											$(line).hide();	
										}
									} else {
										$(line).show();
									}
								}
							});
						});
					});
				}
			});
		}
	});
 
});
</script>