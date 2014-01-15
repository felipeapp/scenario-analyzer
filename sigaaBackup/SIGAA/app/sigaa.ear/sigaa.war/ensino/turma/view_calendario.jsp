<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=ISO-8859-1"%>

<%-- Tags --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>
<%@ taglib uri="/tags/ajax" prefix="ajax"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<%@taglib uri="/tags/jawr" prefix="jwr"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>

<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js"></script>
<script type="text/javascript">
                JAWR.loader.style('/bundles/css/sigaa_base.css', 'all');
        		JAWR.loader.script('/bundles/js/sigaa_base.js');
				JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<jwr:style src="/css/agenda.css" media="all" />

<div id="conteudo">
<f:view>
<p:resources/>
<jwr:style src="/css/agenda.css" media="all" />

<h:form>
		<table class="visualizacao">
		<CAPTION>Agenda da Turma</CAPTION>
		<tr>
			<th valign="top">Horário:</th>
			<td style="text-align: left;">${agendaTurmaBean.turmaSelecionada.descricaoHorario}</td>
		</tr>
		<tr>
		<td colspan="2">
			<p:schedule id="scheduleAgenda" 
					value="#{agendaTurmaBean.agendaModelTurmaSelecionada}"
					editable="false"
					draggable="false" 
					widgetVar="minhaAgenda"
				 	minTime="7"
				 	maxTime="23"
				 	view="month" 
				 	locale="pt"
				 	aspectRatio="5" />
	 	</td>
	 	</tr>
	 	</table>
</h:form>
</f:view>
</div>