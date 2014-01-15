<%@page import="br.ufrn.academico.dominio.StatusDiscente"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cadastramento de Discentes Convocados</h2>
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Para cadastrar um discente selecione o Processo Seletivo, a Convocação do mesmo e informe ou o CPF ou o nome do candidato.</p> 
	</div>
	<br/>
	<h:form id="formulario">
		<table class="formulario" width="80%">
			<caption>Informe os Parâmetros da Busca</caption>
			<tbody>
				<tr>
					<th width="5%"></th>
					<th class="required" width="15%">Processo Seletivo:</th>
					<td>
						<h:selectOneMenu id="processoSeletivo" immediate="true" onchange="submit()" 
						 	valueChangeListener="#{ cadastramentoDiscenteConvocadoMBean.processoSeletivoListener}"
							value="#{cadastramentoDiscenteConvocadoMBean.convocacaoProcessoSeletivo.processoSeletivo.id}">
							<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th></th>
					<th class="required">Convocação:</th>
					<td>
						<h:selectOneMenu id="convocacaoProcessoSeletivo" immediate="true"
							value="#{cadastramentoDiscenteConvocadoMBean.convocacaoProcessoSeletivo.id}">
							<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{cadastramentoDiscenteConvocadoMBean.convocacaoProcessoSeletivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{ cadastramentoDiscenteConvocadoMBean.buscaCPF }" id="buscaCPF"/> 
					</th>
					<th>
						<label for="buscaCPF" onclick="$('formulario:buscaCPF').checked = !$('formulario:buscaCPF').checked;">
							CPF:
						</label>
					</th>
					<td>
						<h:inputText value="#{cadastramentoDiscenteConvocadoMBean.obj.inscricaoVestibular.pessoa.cpf_cnpj}" 
							size="16" maxlength="14" id="txtCPF" onkeyup="return formataCPF(this, event, null)" 
							onkeypress="return formataCPF(this, event, null)" onfocus="getEl('formulario:buscaCPF').dom.checked = true;">
								<f:converter converterId="convertCpf"/>
								<f:param name="type" value="cpf"/>
						</h:inputText>
					</td>
				</tr>
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{ cadastramentoDiscenteConvocadoMBean.buscaNome }" id="buscaNome"/> 
					</th>
					<th>
						<label for="buscaNome" onclick="$('formulario:buscaNome').checked = !$('formulario:buscaNome').checked;">
							Nome:
						</label>
					</th>
					<td><h:inputText value="#{ cadastramentoDiscenteConvocadoMBean.obj.inscricaoVestibular.pessoa.nome }" id="nome" 
							size="80" maxlength="80" onfocus="getEl('formulario:buscaNome').dom.checked = true;"/>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{ cadastramentoDiscenteConvocadoMBean.buscar }" id="buscar"/>
						<h:commandButton value="Cancelar" action="#{ cadastramentoDiscenteConvocadoMBean.cancelar }" id="cancelar" onclick="#{ confirm }"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<c:if test="${ not empty cadastramentoDiscenteConvocadoMBean.resultadosBusca }">
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Cadastrar Discente
				<h:graphicImage value="/img/imprimir.gif" style="overflow: visible;" />: Imprimir Comprovantes
			</div>
			<table class="listagem">
				<caption>Convocações Encontradas ${ fn:length(cadastramentoDiscenteConvocadoMBean.resultadosBusca) }</caption>
				<thead>
					<tr>
						<th width="10%">CPF</th>
						<th>Nome</th>
						<th>Curso</th>
						<th>Status</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:set var="_PENDENTE_CADASTRO" value="<%= StatusDiscente.PENDENTE_CADASTRO %>"/>
				<c:forEach items="#{cadastramentoDiscenteConvocadoMBean.resultadosBusca}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>
							<ufrn:format type="cpf" valor="${ item.inscricaoVestibular.pessoa.cpf_cnpj }" />
						</td>
						<td>${ item.inscricaoVestibular.pessoa.nome }</td>
						<td>${ item.matrizCurricular.descricao }</td>
						<td>${ item.discente.statusString }</td>
						<td width="2%">
							<c:if test="${ item.discente.status == _PENDENTE_CADASTRO }">
								<h:commandLink title="Cadastrar Discente" action="#{cadastramentoDiscenteConvocadoMBean.selecionarCadastro}" id="cadastrar">
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage url="/img/seta.gif" />
								</h:commandLink>
							</c:if>
						</td>
						<td width="2%">
							<c:if test="${ item.discente.status != _PENDENTE_CADASTRO}">
								<h:commandLink title="Imprimir Comprovantes" action="#{cadastramentoDiscenteConvocadoMBean.selecionarDocumentacao}" id="comprovante">
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage url="/img/imprimir.gif" />
								</h:commandLink>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>