<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<% CheckRoleUtil.checkRole(request, response, new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD }); %>

<f:view>
	<h2><ufrn:subSistema /> > Avaliação Periódica</h2>
	<br>
	<h:form id="form">
	<h:messages showDetail="true"/>
	<h:inputHidden value="#{avaliacaoPeriodica.confirmButton}"/>
	<h:inputHidden value="#{avaliacaoPeriodica.obj.id}"/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Cadastrar Avaliações Periódicas </caption>

	<tr>
		<th> Data Início: </th>
		<td> <t:inputCalendar
				value="#{ avaliacaoPeriodica.obj.dataInicio}"
				renderAsPopup="true" renderPopupButtonAsImage="true" size="12"
				maxlength="10" onkeypress="return(formataData(this, event))">
				<f:convertDateTime pattern="dd/MM/yyyy" />
			</t:inputCalendar>
		</td>
	</tr>
	<tr>
		<th> Data Fim: </th>
		<td> <t:inputCalendar
				value="#{ avaliacaoPeriodica.obj.dataFim}"
				renderAsPopup="true" renderPopupButtonAsImage="true" size="12"
				maxlength="10" onkeypress="return(formataData(this, event))">
				<f:convertDateTime pattern="dd/MM/yyyy" />
			</t:inputCalendar>
		</td>
	</tr>
	<tr>
		<th> Pólo: </th>
		<td> 
			<h:selectOneMenu value="#{ avaliacaoPeriodica.obj.poloCurso.polo.id }">
				<f:selectItems value="#{ avaliacaoPeriodica.allPolos }"/>	
			</h:selectOneMenu> <span class="required">&nbsp;</span>
		</td>
	</tr>
	<tr>
		<th>Curso:</th>
		<td><h:selectOneMenu value="#{ avaliacaoPeriodica.obj.poloCurso.curso.id }" id="curso">
			<f:selectItem itemValue="0" itemLabel="Escolha um curso" />
			<f:selectItems value="#{tutorOrientador.cursos}" />
		</h:selectOneMenu> <span class="required">&nbsp;</span></td>
	</tr>
	<tr>
		<th> Unidade: </th>
		<td> 
			<h:selectOneMenu value="#{ avaliacaoPeriodica.obj.unidade }">
				<f:selectItems value="#{ avaliacaoPeriodica.unidades }"/>	
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="#{avaliacaoPeriodica.confirmButton}" action="#{avaliacaoPeriodica.cadastrar}"/>
				<h:commandButton value="Cancelar" action="#{avaliacaoPeriodica.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
