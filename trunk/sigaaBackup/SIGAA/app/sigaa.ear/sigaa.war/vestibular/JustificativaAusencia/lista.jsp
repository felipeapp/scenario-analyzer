<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Análise de Justificativa de Ausência</h2>
	
	<h:form>
		<table class="formulario" width="70%">
		<caption>Informe o Processo Seletivo</caption>
		<tbody>
			<tr>
				<th class="required">Processo Seletivo:</th>
				<td>
					<h:selectOneMenu value="#{justificativaAusencia.idProcessoSeletivo}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Buscar" action="#{justificativaAusencia.buscar}" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{justificativaAusencia.cancelar}" immediate="true" />
				</td>
			</tr>
		</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		<br/>
		<c:if test="${ not empty justificativaAusencia.resultadosBusca }">
		<div class="infoAltRem">
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Analisar Justificativa
		</div>
			<table class="listagem">
				<caption>Lista de Justificativas</caption>
				<thead>
					<tr>
						<th>Perfil</th>
						<th style="text-align: right;">Matrícula/<br/>SIAPE</th>
						<th>Nome</th>
						<th>Curso/<br/>Unidade</th>
						<th>Status</th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="#{justificativaAusencia.resultadosBusca}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<c:if test="${item.fiscal.perfilDiscente}">
							<td>Discente</td>
							<td style="text-align: right;">${item.fiscal.discente.matricula}</td>
							<td>${item.fiscal.discente.nome}</td>
							<td>${item.fiscal.discente.curso.descricaoCompleta}</td>
						</c:if>
						<c:if test="${item.fiscal.perfilServidor}">
							<td>Servidor</td>
							<td style="text-align: right;">${item.fiscal.servidor.siape}</td>
							<td>${item.fiscal.servidor.nome}</td>
							<td>${item.fiscal.servidor.unidade.nome}</td>
						</c:if>
						<td>${item.descricaoStatus}</td>
						<td>
							<h:commandLink action="#{ justificativaAusencia.atualizar }">
								<f:verbatim>
									<img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" />
								</f:verbatim>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
