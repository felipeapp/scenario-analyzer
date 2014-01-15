<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
  
<h2><ufrn:subSistema /> > Mapa de Horários</h2>

<style>
.teste td{
	border: 1px solid #DDD;
}

div.schedule-detailed-evolution .entry {
	border-width: 0px;
}
#loading {
	position: absolute;
	top: 5px;
	right: 5px;
	}		
</style>

<link rel='stylesheet' type='text/css' href='http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/themes/start/jquery-ui.css' />
<link rel='stylesheet' type='text/css' href='/sigaa/css/jquery.weekcalendar.css' />
<link rel='stylesheet' type='text/css' href='/sigaa/css/fullcalendar.css' />

<%-- <script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js'></script>  --%>
<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js'></script>
<script type='text/javascript' src='/sigaa/javascript/jquery-weekcalendar/jquery.weekcalendar.js'></script>
<script type='text/javascript' src='/sigaa/javascript/fullcalendar/fullcalendar.js'></script>
<script type="text/javascript" src="/shared/javascript/paineis/painel_generico.js"></script>

<f:view>
<h:form id="form">

	<a4j:loadScript src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"/>
	
	<div class="descricaoOperacao">
		Selecione uma data para inserir uma solicitação de reserva, ou clique numa reserva já cadastrada para ver os detalhes. 
	</div>

	<div id="calendarioMes" style="width: 85%"></div>
	<div id="calendarioSemana" style="width: 85%"></div>
	<rich:jQuery selector="#calendarioMes" query="fullCalendar(parametros)" timing="onload"/>


</h:form>
</f:view>

<script type='text/javascript'>
var $j = jQuery.noConflict();

function exibirPainel(data) {
	PainelDetalhar.show('/sigaa/infra_fisica/reserva/form.jsf','Detalhes da Reserva', 300, 300);
};

var parametros = {
	timeFormat: "H:i",
	dayClick: function(dayDate) {
		exibirPainel(dayDate);
	}
};
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>