<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
.center {
	text-align: center;
	border-spacing: 3px;
}

.left {
	text-align: left;
	border-spacing: 3px;
}
-->
</style>

<f:view>
	<h2><ufrn:subSistema /> > Busca de Fiscais</h2>
	<div class="descricaoOperacao">
		Para buscar por fiscais, informe pelo menos um parâmetro válido.
    </div>
	<h:form id="form">
		<a4j:keepAlive beanName="conceitoFiscal"/>
		<table class="formulario">
			<caption>Parâmetros da Busca</caption>
			<tbody>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{conceitoFiscal.buscaProcessoSeletivo}" id="buscaProcessoSeletivo"/>
					</td>
					<td width="20%">Processo Seletivo:</td>
					<td>
						<h:selectOneMenu id="processoSeletivo"
							value="#{conceitoFiscal.idProcessoSeletivo}"
							onchange="submit()"
							onfocus="$('form:buscaProcessoSeletivo').checked = true;"
							valueChangeListener="#{conceitoFiscal.carregaLocalAplicacao}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{conceitoFiscal.buscaLocalAplicacao}" id="buscaLocalAplicacao"/>
					</td>
					<td>Local de Aplicação:</td>
					<td width="70%">
						<h:selectOneMenu id="localAplicacao" immediate="true"
							value="#{conceitoFiscal.idLocalAplicacao}"
							onfocus="$('form:buscaLocalAplicacao').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{conceitoFiscal.locaisAplicacao}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{conceitoFiscal.buscaNomeFiscal}" id="buscaNomeFiscal"/>
					</td>
					<td>Nome do Fiscal:</td>
					<td>
						<h:inputText value="#{conceitoFiscal.nomeFiscal}" size="50"	maxlength="60" onfocus="$('form:buscaNomeFiscal').checked = true;" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" align="center">
						<h:commandButton value="Buscar" action="#{conceitoFiscal.buscar}" id="buscar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{conceitoFiscal.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br />
		<c:if test="${not empty conceitoFiscal.resultadosBusca}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Fiscal
			</div>
			<table class="listagem">
			<caption>Inscrições de Fiscais Encontradas: ${fn:length(conceitoFiscal.resultadosBusca)}</caption>
				<thead>
					<tr>
						<td align="left">Nome</td>
						<td align="left">Curso/Unidade</td>
						<td align="left">Processo Seletivo</td>
						<td align="left">Local de Aplicação</td>
						<td style="text-align: center">Conceito</td>
						<td width="5%"></td>
					</tr>
				</thead>
				<c:forEach items="#{conceitoFiscal.resultadosBusca}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td align="left">
							<h:outputText value="#{item.pessoa.nome}" />
						</td>
						<td align="left">
							<h:outputText rendered="#{not empty item.discente}" value="#{item.discente.curso.descricao}" />
							<h:outputText rendered="#{not empty item.servidor}" value="#{item.servidor.unidade.nome}" />
						</td>
						<td align="left">
							<h:outputText value="#{item.processoSeletivoVestibular.nome}" />
						</td>
						<td align="left">
							<h:outputText value="#{item.localAplicacaoProva.nome}" />
						</td>
						<td align="center">
							<h:outputText value="#{item.descricaoConceito}" />
						</td>
						<td>
							<h:commandLink title="Visualizar" style="border: 0;" action="#{conceitoFiscal.selecionarFiscal}" >
								<f:param name="id" value="#{item.id}" />
								<h:graphicImage url="/img/seta.gif" alt="Selecionar Fiscal" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>