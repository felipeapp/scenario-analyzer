<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema /> > Prorrogação de Prazo Máximo de Conclusão</h2>

	<br>
	<h:form id="formulario">
		<table class="formulario" width="600px">
			<h:outputText value="#{prorrogacao.create}" />
			<caption class="listagem">
			Informe o Número de ${(prorrogacao.graduacao) ? 'Semestres' : 'Meses'}
			 a ser Prorrogado
			</caption>
			<tr>
				<th class="rotulo">Discente:</th>
				<td><h:outputText value="#{prorrogacao.obj.discente.nome }" /></td>
			</tr>
			<c:if test="${ prorrogacao.graduacao || prorrogacao.obj.discente.stricto }">
				<tr>
					<th class="rotulo">Atual Prazo Máximo: </th>
					<c:if test="${!prorrogacao.graduacao}">
						<td>${prorrogacao.obj.discente.anoMesPrazoConclusao}</td>
					</c:if>
					<c:if test="${prorrogacao.graduacao}">
						<td><ufrn:format type="anosemestre" valor="${prorrogacao.obj.discente.prazoConclusao}" /></td>
					</c:if>
				</tr>
			</c:if>
			<tr>
				<th class="obrigatorio">Tipo de Prorrogação:</th>
				<td>
				<h:selectOneRadio id="tipo" value="#{prorrogacao.obj.tipoMovimentacaoAluno.id}" >
					<f:selectItems value="#{prorrogacao.tipos}"/>
				</h:selectOneRadio>
				</td>
			</tr>
			<c:if test="${ prorrogacao.obj.discente.stricto || prorrogacao.obj.discente.tecnico }">
				<tr>
					<th class="obrigatorio">Número de ${ prorrogacao.obj.discente.tecnico ? 'Semestres' : 'Meses' }:</th>
					<td>
					<h:inputText id="numeroMeses" value="#{prorrogacao.obj.valorMovimentacao}" size="3" maxlength="2" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"></h:inputText> 
							${ prorrogacao.tecnico ? 'Semestres' : 'Meses' }
					</td>
				</tr>
			</c:if>
			<c:if test="${ prorrogacao.graduacao }">
				<tr>
					<th>Número de Semestres:</th>
					<td>
					<h:selectOneMenu id="semestres" value="#{prorrogacao.obj.valorMovimentacao}" >
						<f:selectItems value="#{prorrogacao.semestres}" />
					</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tr>
				<th valign="top">Observação:</th>
				<td><h:inputTextarea id="observacao" rows="3" cols="70" value="#{prorrogacao.obj.observacao}"></h:inputTextarea> </td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="Confirmar Prorrogação" id="cadastrar" action="#{prorrogacao.prorrogarPrazo}" /> 
						<h:commandButton value="Cancelar" immediate="true" onclick="#{confirm}" action="#{prorrogacao.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
