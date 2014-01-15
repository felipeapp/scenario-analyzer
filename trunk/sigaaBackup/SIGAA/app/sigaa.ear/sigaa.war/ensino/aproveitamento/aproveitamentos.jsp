
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{aproveitamento.create}" />
	<h2 class="title"><ufrn:subSistema /> > Aproveitamento de Estudos &gt; Aproveitamentos Cadastrados</h2>
	<c:set value="#{aproveitamento.obj.discente}" var="discente" />
	<%@ include file="/graduacao/info_discente.jsp"%>
	<center>
		<div class="infoAltRem">
		Selecione os Aproveitamentos que deseja cancelar.

		<h:messages/>

		</div>
	</center>
	<table class="formulario">
		<h:form id="form">
		<thead>
			<tr>
				<td colspan="3">Esse discente possui ${fn:length(aproveitamento.aproveitamentosAluno) } Componente(s) Aproveitado(s)</td>
			</tr>
		</thead>
		<tbody>
			<tr><td>
			<h:dataTable width="700" value="#{aproveitamento.aproveitamentosAluno}" id="datatable" var="mat" rowClasses="linhaPar, linhaImpar">
				<h:column>
					 <h:selectBooleanCheckbox value="#{mat.selected}" id="selecionar"/>
				</h:column>
				<h:column>
				 	<h:outputText value="#{mat.anoPeriodo}" />
				</h:column>

				<h:column>
					<h:outputText value="#{mat.componenteDescricao}" />
				</h:column>
				<h:column>
					<h:outputText value="#{mat.situacaoMatricula.descricao}" />
				</h:column>
			</h:dataTable>
			</td></tr>
		</tbody>
		<tfoot>
			<tr>
			<td>
				<h:commandButton action="#{aproveitamento.buscarDiscente}" value="Escolher outro Discente" id="btnCancelar"/>
				<h:commandButton action="#{aproveitamento.selecionarAproveitamento}" value="Remover aproveitamento" id="btnRemover"/>
			</td>
			</tr>
		</tfoot>
		</h:form>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
