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
		<a4j:keepAlive beanName="fiscal"/>
		<table class="formulario">
			<caption>Parâmetros da Busca</caption>
			<tbody>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{fiscal.buscaProcessoSeletivo}" id="buscaProcessoSeletivo"/>
					</td>
					<td width="20%"><label for="buscaProcessoSeletivo" onclick="$('form:buscaProcessoSeletivo').checked = !$('form:buscaProcessoSeletivo').checked;">Processo Seletivo:</label></td>
					<td>
						<h:selectOneMenu id="processoSeletivo"
							value="#{fiscal.fiscal.processoSeletivoVestibular.id}"
							onchange="submit()"
							onfocus="$('form:buscaProcessoSeletivo').checked = true;"
							valueChangeListener="#{fiscal.carregaLocalAplicacao}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{fiscal.buscaLocalAplicacao}" id="buscaLocalAplicacao"/>
					</td>
					<td><label for="buscaLocalAplicacao" onclick="$('form:buscaLocalAplicacao').checked = !$('form:buscaLocalAplicacao').checked;">Local de Aplicação:</label></td>
					<td width="70%">
						<h:selectOneMenu id="localAplicacao" immediate="true"
							value="#{fiscal.fiscal.localAplicacaoProva.id}"
							onfocus="$('form:buscaLocalAplicacao').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{fiscal.locaisAplicacao}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{fiscal.buscaMatricula}" id="buscaMatriculaFiscal"/>
					</td>
					<td><label for="buscaMatriculaFiscal" onclick="$('form:buscaMatriculaFiscal').checked = !$('form:buscaMatriculaFiscal').checked;">Matrícula do Discente:</label></td>
					<td>
						<h:inputText value="#{fiscal.fiscal.discente.matricula}" size="14" maxlength="12" onkeyup="return formatarInteiro(this);" onfocus="$('form:buscaMatriculaFiscal').checked = true;" id="matriculaFiscal"/>
					</td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{fiscal.buscaSiape}" id="buscaSiapeFiscal"/>
					</td>
					<td><label for="buscaSiapeFiscal" onclick="$('form:buscaSiapeFiscal').checked = !$('form:buscaSiapeFiscal').checked;">SIAPE do Servidor:</label></td>
					<td>
						<h:inputText value="#{fiscal.fiscal.servidor.siape}" size="14" maxlength="12" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" onfocus="$('form:buscaSiapeFiscal').checked = true;" id="siapeServidor"/>
					</td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{fiscal.buscaNomeFiscal}" id="buscaNomeFiscal"/>
					</td>
					<td><label for="buscaNomeFiscal" onclick="$('form:buscaNomeFiscal').checked = !$('form:buscaNomeFiscal').checked;">Nome do Fiscal:</label></td>
					<td>
						<h:inputText value="#{fiscal.fiscal.pessoa.nome}" size="50"	maxlength="60" onfocus="$('form:buscaNomeFiscal').checked = true;" id="nomeFiscal"/>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" align="center">
						<h:commandButton value="Buscar" action="#{fiscal.buscar}" id="buscar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{fiscal.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br />
		<c:if test="${not empty fiscal.resultadosBusca}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Dados do Fiscal
			</div>
			<table class="listagem">
			<caption>Fiscais Encontrados: ${fn:length(fiscal.resultadosBusca)}</caption>
				<thead>
					<tr>
						<td align="left">Nome</td>
						<td align="left">Curso/Unidade</td>
						<td align="left">Processo Seletivo</td>
						<td align="left">Local de Aplicação</td>
						<td align="left">Titularidade</td>
						<td width="5%"></td>
					</tr>
				</thead>
				<c:forEach items="#{fiscal.resultadosBusca}" var="item" varStatus="status">
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
						<td>
							<c:if test="${item.reserva}">Reserva</c:if> 
							<c:if test="${not item.reserva}">Titular</c:if>
						</td>
						<td align="center">
							<h:commandLink action="#{fiscal.view}" id="viewFiscal" title="Visualizar"style="border: 0;">
								<f:param name="id" value="#{item.id}" id="idFiscal"/>
								<h:graphicImage url="/img/view.gif" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>