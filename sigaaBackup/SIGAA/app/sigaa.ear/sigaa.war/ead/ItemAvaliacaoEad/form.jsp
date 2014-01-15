<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Itens de Avaliação</h2>
	<br>
	<h:form id="polo">
	<h:messages showDetail="true"/>
	<h:inputHidden value="#{itemAvaliacaoEad.confirmButton}"/>
	<h:inputHidden value="#{itemAvaliacaoEad.obj.id}"/>

	<table class="formulario" width="60%">
	<caption class="listagem"> Cadastrar Itens de Avaliação </caption>

	<tr>
		<th> Nome: </th>
		<td> <h:inputText value="#{itemAvaliacaoEad.obj.nome}" id="nome" size="50" readonly="#{itemAvaliacaoEad.readOnly}"/>
		</td>
	</tr>
	<tr>
		<th> Ativo: </th>
		<td> 
			<h:selectOneRadio value="#{itemAvaliacaoEad.obj.ativo}" id="ativo">
				<f:selectItem itemLabel="Sim" itemValue="true"/>
				<f:selectItem itemLabel="Não" itemValue="false"/>
			</h:selectOneRadio>
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="#{itemAvaliacaoEad.confirmButton}" action="#{itemAvaliacaoEad.cadastrar}"/>
				<h:commandButton value="Cancelar" action="#{itemAvaliacaoEad.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
