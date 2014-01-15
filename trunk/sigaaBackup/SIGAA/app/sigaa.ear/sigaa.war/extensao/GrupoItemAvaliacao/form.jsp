<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> > Grupo de Item de Avaliação</h2>
	<br>
	<h:form id="membro">

	<h:inputHidden value="#{grupoItemAvaliacaoExtensao.confirmButton}"/>
	<h:inputHidden value="#{grupoItemAvaliacaoExtensao.obj.id}"/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Cadastrar Grupo de Item de Avaliação </caption>

	<tr>
		<td></td> 
	</tr>

	<tr>
	
		<th width="23%" class="required"> Descrição do Grupo: </th>
		<td> <h:inputText value="#{grupoItemAvaliacaoExtensao.obj.denominacao}" id="nome" size="90" readonly="#{grupoItemAvaliacaoExtensao.readOnly}"/>
		</td>
	</tr>
	
	<tr>
		<th width="23%" class="required"> Grupo para Avaliação de: </th>
		<td> 
			<h:selectOneMenu
				value="#{ grupoItemAvaliacaoExtensao.obj.tipo }">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItem itemLabel="PROJETOS DE EXTENSÃO" itemValue="E" />				
			</h:selectOneMenu>
		</td>
	</tr>
	
	<c:if test="${grupoItemAvaliacaoExtensao.confirmButton != 'Cadastrar'}">
		<tr>
			<th width="20%"> Ativo? </th>
			<td> 
				<h:selectOneRadio value="#{ grupoItemAvaliacaoExtensao.obj.ativo }">
					<f:selectItem itemLabel="Sim" itemValue="true"/>
					<f:selectItem itemLabel="Não" itemValue="false"/>
				</h:selectOneRadio>
			</td>
		</tr>
	</c:if>
	

	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="#{grupoItemAvaliacaoExtensao.confirmButton}" action="#{grupoItemAvaliacaoExtensao.cadastrar}"/>
					<h:commandButton value="Cancelar" action="#{grupoItemAvaliacaoExtensao.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
	</table>
	
	<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center>
	
		
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>