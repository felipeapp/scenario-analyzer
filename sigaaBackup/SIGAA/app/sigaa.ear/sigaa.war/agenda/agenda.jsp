<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>
<script>
	YAHOO.util.Event = null;
</script>

<f:view>
	<p:resources />
	<jwr:style src="/css/agenda.css" media="all" />
	<a4j:keepAlive beanName="agendaBean" />
	
	<h2>Agenda</h2>
	<h:form>
		<div class="infoAltRem">
		<h:commandLink value="Cadastrar novo evento" action="#{eventoBean.iniciar}" id="linkNovoEvento">
			<f:setPropertyActionListener target="#{eventoBean.agenda}" value="#{agendaBean.obj}"/>
		</h:commandLink>
		</div>
	
		<br />
		<p:schedule value="#{agendaBean.model}" firstHour="06" minTime="05" view="agendaWeek" aspectRatio="2"
		  leftHeaderTemplate="today"
	      rightHeaderTemplate="prev,next"
	      centerHeaderTemplate="month, agendaWeek, agendaDay"/>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>