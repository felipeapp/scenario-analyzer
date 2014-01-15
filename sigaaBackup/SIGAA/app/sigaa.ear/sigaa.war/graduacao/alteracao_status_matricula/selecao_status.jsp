<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{alteracaoStatusMatricula.create}"/>

<h2> 	<ufrn:subSistema /> &gt; ${(alteracaoStatusMatricula.trancamento)?'Trancamento de Matrículas':'Alteração de Matrículas'}
</h2>
	<h:messages showDetail="true"></h:messages>

	<c:set var="discente" value="#{alteracaoStatusMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	<h:form>
	<table class="formulario" style="width: 80%">
		<caption> Confira os dados para efetuar a alteração do status da matrícula </caption>
		<tbody>
			<tr>
				<th width="45%"> Novo Status: </th>
				<td>
					<c:choose><c:when test="${alteracaoStatusMatricula.trancamento}">
						${alteracaoStatusMatricula.novaSituacao.descricao}
					</c:when><c:otherwise>
						<h:selectOneMenu value="#{ alteracaoStatusMatricula.novaSituacao.id }">
							<f:selectItems value="#{ alteracaoStatusMatricula.situacoes }"/>
						</h:selectOneMenu>
					</c:otherwise></c:choose>
					<ufrn:help img="/img/ajuda.gif">Todas as matrículas escolhidas serão alteradas para esse status</ufrn:help>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Matrículas escolhidas</caption>
					<thead>
						<tr>
							<td>Componente Curricular</td>
							<td width="15%">Status Atual</td>
						</tr>
					</thead>
					<c:forEach items="${alteracaoStatusMatricula.matriculasEscolhidas}" var="mat" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>${mat.componenteDescricao}</td>
						<td>${mat.situacaoMatricula.descricao }</td>
					</tr>
					</c:forEach>
				</table>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" action="#{alteracaoStatusMatricula.efetuarAlteracaoStatus}" />
					<h:commandButton value="Selecionar Outras Matrículas" action="#{alteracaoStatusMatricula.telaSelecaoMatriculas}"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoStatusMatricula.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>