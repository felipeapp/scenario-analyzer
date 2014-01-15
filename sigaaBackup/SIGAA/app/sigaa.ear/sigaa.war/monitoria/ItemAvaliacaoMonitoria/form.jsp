<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> > Item de Avaliação</h2>
	<br>
	<h:form>

	<h:inputHidden value="#{itemAvaliacaoMonitoria.confirmButton}"/>
	<h:inputHidden value="#{itemAvaliacaoMonitoria.obj.id}"/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Cadastrar Item de Avaliação </caption>

	<tr>
		<th width="20%"> Descrição do Item: </th>
		<td> <h:inputTextarea value="#{itemAvaliacaoMonitoria.obj.descricao}" cols="90" rows="2" readonly="#{itemAvaliacaoMonitoria.readOnly}"/>
		</td>
	</tr>

	<tr>
		<th width="20%"> Grupo: </th>
		<td>
			<h:selectOneMenu value="#{itemAvaliacaoMonitoria.obj.grupo.id}" style="width: 500px">
				<f:selectItems value="#{grupoItemAvaliacao.allComboMonitoria}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th width="20%"> Nota Máxima: </th>
		<td> 
			<h:inputText value="#{itemAvaliacaoMonitoria.obj.notaMaxima}" size="2" readonly="#{itemAvaliacaoMonitoria.readOnly}">
				<f:convertNumber pattern="#0.00"/>
			</h:inputText>
		</td>
	</tr>
	
	<tr>
		<th width="20%"> Ativo? </th>
		<td> 
			<h:selectOneRadio value="#{ itemAvaliacaoMonitoria.obj.ativo }">
				<f:selectItem itemLabel="Sim" itemValue="true"/>
				<f:selectItem itemLabel="Não" itemValue="false"/>
			</h:selectOneRadio>
		</td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="#{itemAvaliacaoMonitoria.confirmButton}" action="#{itemAvaliacaoMonitoria.cadastrar}"/>
					<h:commandButton value="Cancelar" action="#{itemAvaliacaoMonitoria.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
