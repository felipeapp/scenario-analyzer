<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> > Item de Avaliação</h2>
	<br>
	<h:form>

	<h:inputHidden value="#{itemAvaliacaoExtensao.confirmButton}"/>
	<h:inputHidden value="#{itemAvaliacaoExtensao.obj.id}"/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Cadastrar Item de Avaliação </caption>

	<tr>
		<th width="20%"> Descrição do Item: <h:graphicImage  url="/img/required.gif" /> </th>
		<td>			
			<h:inputTextarea value="#{itemAvaliacaoExtensao.obj.descricao}" cols="90" rows="2" readonly="#{itemAvaliacaoExtensao.readOnly}"/>
		</td>
	</tr>

	<tr>
		<th width="20%" class="required"> Grupo: </th>
		<td>
			<h:selectOneMenu value="#{itemAvaliacaoExtensao.obj.grupo.id}" style="width: 600px">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{grupoItemAvaliacaoExtensao.allComboExtensao}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th width="20%" class="required"> Peso: </th>
		<td> 			
			<h:inputText value="#{itemAvaliacaoExtensao.obj.peso}" size="5">
				<f:convertNumber/>
			</h:inputText>
		</td>
	</tr>

	<tr>
		<th width="20%" class="required"> Nota Máxima: </th>
		<td> 			
			<h:inputText value="#{itemAvaliacaoExtensao.obj.notaMaxima}" size="5">
				<f:convertNumber/>
			</h:inputText>
		</td>
	</tr>
	
	<c:if test="${itemAvaliacaoExtensao.confirmButton != 'Cadastrar'}">
		<tr>
			<th width="20%"> Ativo? </th>
			<td> 
				<h:selectOneRadio value="#{ itemAvaliacaoExtensao.obj.ativo }">
					<f:selectItem itemLabel="Sim" itemValue="true"/>
					<f:selectItem itemLabel="Não" itemValue="false"/>
				</h:selectOneRadio>
			</td>
		</tr>
	</c:if>

	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="#{itemAvaliacaoExtensao.confirmButton}" action="#{itemAvaliacaoExtensao.cadastrar}"/>
					<h:commandButton value="Cancelar" action="#{itemAvaliacaoExtensao.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>

	</table>
	
	<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center>
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
