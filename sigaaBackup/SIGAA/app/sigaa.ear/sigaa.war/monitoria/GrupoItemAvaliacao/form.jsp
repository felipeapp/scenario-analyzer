<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> > Grupo de Item de Avaliação</h2>
	<br>
	<h:form id="membro">

	<h:inputHidden value="#{grupoItemAvaliacao.confirmButton}"/>
	<h:inputHidden value="#{grupoItemAvaliacao.obj.id}"/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Cadastrar Grupo de Item de Avaliação </caption>

	<tr>
		<th width="20%"> Descrição do Grupo: </th>
		<td> <h:inputText value="#{grupoItemAvaliacao.obj.denominacao}" id="nome" size="90" readonly="#{grupoItemAvaliacao.readOnly}"/>
		</td>
	</tr>
	
	<tr>
		<th width="20%"> Grupo para Avaliação de: </th>
		<td> 
			<h:selectOneMenu
				value="#{ grupoItemAvaliacao.obj.tipo }">
				<f:selectItem itemLabel="PROJETOS DE MONITORIA" itemValue="P" />
				<f:selectItem itemLabel="RELATÓRIOS PARCIAIS/FINAIS DE PROJETOS" itemValue="R" />
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
		<th width="20%"> Ativo? </th>
		<td> 
			<h:selectOneRadio value="#{ grupoItemAvaliacao.obj.ativo }">
				<f:selectItem itemLabel="Sim" itemValue="true"/>
				<f:selectItem itemLabel="Não" itemValue="false"/>
			</h:selectOneRadio>
		</td>
	</tr>

	

	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="#{grupoItemAvaliacao.confirmButton}" action="#{grupoItemAvaliacao.cadastrar}"/>
					<h:commandButton value="Cancelar" action="#{grupoItemAvaliacao.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>