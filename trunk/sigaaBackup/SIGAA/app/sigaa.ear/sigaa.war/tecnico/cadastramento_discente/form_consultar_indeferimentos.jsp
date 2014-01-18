<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="cadastramentoDiscenteTecnico"></a4j:keepAlive>
	<h2><ufrn:subSistema /> &gt; Consultar Indeferimentos</h2>
	<div class="descricaoOperacao">
		<p>Utilize o formulário abaixo para visualizar e imprimir o motivo do indeferimento dos candidatos a discentes do IMD.</p>
	</div>
	
	<h:form id="form">
	
		<table class="formulario" width="80%">
			<caption>Parâmetros da Consulta</caption>
			<tr>
				<th class="obrigatorio" width="25%">Processo Seletivo:</th>
				<td>
					<h:selectOneMenu id="selectPsVestibular" value="#{cadastramentoDiscenteTecnico.processoSeletivo.id}" valueChangeListener="#{cadastramentoDiscenteTecnico.carregarConvocacoes}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems id="itensPsVestibular" value="#{convocacaoProcessoSeletivoTecnico.processosCombo}" />
						<a4j:support event="onchange" reRender="convocacao" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th width="25%">Convocação:</th>
				<td>
					<h:selectOneMenu id="convocacao" value="#{cadastramentoDiscenteTecnico.idConvocacao}">
						<f:selectItem itemValue="0" itemLabel="-- TODAS --" />
						<f:selectItems value="#{cadastramentoDiscenteTecnico.convocacoesCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th width="25%">Polo / Grupo:</th>
				<td>
					<h:selectOneMenu id="selectOpcao" value="#{cadastramentoDiscenteTecnico.opcao}">
						<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
						<f:selectItems value="#{convocacaoProcessoSeletivoTecnico.polosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th width="25%">Grupo de Reserva de Vagas: </th>
				<td>
					<h:selectOneMenu id="selectGrupo" value="#{cadastramentoDiscenteTecnico.idGrupoReservaVaga}">
						<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
						<f:selectItems value="#{cadastramentoDiscenteTecnico.gruposCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th width="25%">Nome:</th>
				<td>
					<h:inputText id="nome" value="#{cadastramentoDiscenteTecnico.nome}" style="display:inline;" size="50" />
					<rich:suggestionbox for="nome" height="100" width="500" minChars="3" id="suggestionNome" suggestionAction="#{cadastramentoDiscenteTecnico.autocompleteNomeDiscente}" var="_c" fetchValue="#{_c[0]}">
						<h:column>
							<h:outputText value="#{_c[0]}"/>
						</h:column>
						<h:column>
							<h:outputText value="#{_c[1]}"/>
						</h:column>
					</rich:suggestionbox> 
				</td>
			</tr>
			<tr>
				<th width="25%">CPF:</th>
				<td>
					<h:inputText id="cpf" value="#{cadastramentoDiscenteTecnico.cpf}" size="16" maxlength="14" 
						onkeypress="return formataCPF(this, event, null);">
						<f:converter converterId="convertCpf"/>
						<f:param name="type" value="cpf" />
					</h:inputText>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btnBuscar" value="Buscar Discentes" action="#{cadastramentoDiscenteTecnico.buscarIndeferimentos}"/>
						<h:commandButton value="Cancelar" action="#{ cadastramentoDiscenteTecnico.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
			<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		<c:if test="${ not empty cadastramentoDiscenteTecnico.convocacoes}" >
		
		<br/><br/>
		
		<div class="infoAltRem" style="width:80%">
			<h:graphicImage value="/img/seta.gif" /> : Selecionar Candidato
		</div>
		
			<table class="listagem" id="resultadoBusca">
				<caption>Candidatos encontrados (${fn:length(cadastramentoDiscenteTecnico.convocacoes)})</caption>
				<thead>
					<tr>
						<th>Nome</th>
						<th style="text-align: center;">CPF</th>
						<th>Polo / Grupo</th>
						<th>Grupo</th>
						<th>Motivo</th>
						<th>Ação</th>
					</tr>
				</thead>
				
				<a4j:repeat value="#{cadastramentoDiscenteTecnico.convocacoes}" var="conv" rowKeyVar="loop">
					<tr class="<h:outputText value="linhaImpar" rendered="#{ loop % 2 == 0 }" />">
						<td><strong><h:outputText value="#{conv.discente.pessoa.nome}"/></strong></td>
						<td style="text-align: center;"><h:outputText value="#{conv.discente.pessoa.cpf_cnpjString}" /></td>
						<td><h:outputText value="#{conv.inscricaoProcessoSeletivo.opcao.descricao}"/></td>
						<td><h:outputText value="#{conv.inscricaoProcessoSeletivo.grupo.denominacao}"/></td>
						<td><h:outputText value="#{conv.cancelamento.motivo.descricao}"/></td>
						<td style="text-align:center;">
							<h:commandLink action="#{ cadastramentoDiscenteTecnico.consultarIndeferimento }" >
								<f:param name="idCandidato" value="#{ conv.id }" />
								<h:graphicImage id="imgSelecionar" title="Selecionar Candidato" value="/img/seta.gif" />
							</h:commandLink>
						</td>
					</tr>
				</a4j:repeat>
			</table>
		</c:if>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>