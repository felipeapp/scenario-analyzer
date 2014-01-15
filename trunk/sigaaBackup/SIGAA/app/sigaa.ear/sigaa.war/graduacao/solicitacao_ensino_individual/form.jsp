<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript">
function marcaJSFChkBox(elem) {
	$(elem).checked = true;
}
</script>
<f:view>
<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Solicitação de Turma de ${solicitacaoEnsinoIndividual.tipoSolicitacao}</h2>

	<table width="100%" class="subFormulario">
		<!-- REGRAS PARA SOLICITAÇÃO DE TURMAS DE ENSINO INDIVIDUALIZADO -->
		<c:if test="${not solicitacaoEnsinoIndividual.ferias}">
			<div id="ajuda" class="descricaoOperacao">
				 ${sf:mensagem('MensagensGraduacao.DESCRICAO_OPERACAO_SOLICITAR_ENSINO_INDIVIDUALIZADO')}
			</div>
		</c:if>
		<!-- REGRAS PARA SOLICITAÇÃO DE TURMAS DE FÉRIAS -->
		<c:if test="${solicitacaoEnsinoIndividual.ferias}">
			<div id="ajuda" class="descricaoOperacao">
				${sf:mensagem('MensagensGraduacao.DESCRICAO_OPERACAO_SOLICITAR_TURMA_FERIAS')}	
			</div>
		</c:if>
	</table>
	<br>
	<h:form id="formBusca">
		<h:inputHidden value="#{solicitacaoEnsinoIndividual.ferias}"/>
		<table class="formulario" width="75%">
			<caption>Buscar Componente Curricular</caption>
			<tr>
				<td><input type="radio" id="checkCodigo" name="paramBusca" value="codigo" class="noborder" alt="Código" title="Código"></td>
				<td><label for="checkNome">Código:</label></td>
				<td><h:inputText value="#{solicitacaoEnsinoIndividual.obj.componente.codigo}" size="10" onchange="$('checkCodigo').checked = true;" maxlength="7" onkeyup="CAPS(this);$('checkCodigo').checked = true;"  alt="Código" title="Código" id="codigo"/>
				</td>
			</tr>
			<tr>
				<td><input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder" alt="Nome" title="Nome"></td>
				<td><label for="checkNome">Nome:</label></td>
				<td><h:inputText value="#{solicitacaoEnsinoIndividual.obj.componente.detalhes.nome}" size="60" maxlength="80" onchange="$('checkNome').checked = true;" onkeyup="CAPS(this);$('checkNome').checked = true;" alt="Nome" title="Nome" id="nome"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{solicitacaoEnsinoIndividual.buscarComponente}" value="Buscar" id="buscarComponente"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoEnsinoIndividual.cancelar}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br>
	<c:if test="${not empty  solicitacaoEnsinoIndividual.resultadosBusca}">

	<div class="descricaoOperacao">
		<p>Caro Aluno,</p><br>

			<p>Selecione abaixo a disciplina que deseja cursar como
				disciplina de ${solicitacaoEnsinoIndividual.tipoSolicitacao}.
				Observe que sua solicitação passará pela análise do coordenador do
				curso e pelo chefe do departamento. Você pode acompanhar a situação
				das suas solicitações através do menu Ensino &gt; Solicitações de
				${solicitacaoEnsinoIndividual.tipoSolicitacao} &gt; Visualizar
				Solicitações Enviadas.</p>

			<c:if test="${false}">
			<p>Utilize o campo <img src="/sigaa/img/icones/alarmclock_preferences.png" height="20" width="20"/> <b>Sugestão de Horário</b> para
			SUGERIR ao coordenador o horário que você deseja que a turma seja criada. 
			Observe que é apenas uma sugestão, a decisão final é do coordenador do curso.
			Utilize o formato 12M24 para sugerir o horário.</p>
			</c:if>
		<c:if test="${not solicitacaoEnsinoIndividual.ferias}">
		<p><strong>Atenção!</strong> Se a solicitação for aceita você será matriculado automaticamente na turma.</p>
		</c:if>
	</div>

	<div class="infoAltRem" style="width:100%;"> 
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Turma
	</div>
	<table class="listagem">
		<caption class="listagem">Componentes Curriculares Encontrados</caption>

		<thead>
			<tr>
				<td width="10%">Código</td>
				<td>Nome</td>
				<td style="text-align:center;">Ativo</td>
				<td width="2%"></td>
			</tr>
		</thead>

		<tbody>
		<h:form id="selecionarTurma">
			<c:forEach items="#{solicitacaoEnsinoIndividual.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>${item.componente.codigo}</td>
					<td>${item.componente.nome} - ${item.componente.chTotal}h</td>
					<td style="${ item.componente.ativo ? '' : 'color:red;'}" align="center"><ufrn:format type="simnao" valor="${item.componente.ativo}"/></td>
					<td>
						<h:commandButton action="#{solicitacaoEnsinoIndividual.selecionarComponenteSolicitacao}" 
							disabled="#{!item.componente.ativo}" id="submeterSolicitacao" image="/img/seta.gif" title="Selecionar Turma"> 
							 <f:setPropertyActionListener target="#{solicitacaoEnsinoIndividual.obj.componente.id}" value="#{ item.componente.id }" />
						</h:commandButton>
					</td>
				</tr>
				<c:if test="${not empty item.equivalente}">
					<tr class="linhaImpar">
						<td colspan="6"><i>Cumpre ${item.equivalente.codigoNome }</i></td>
					</tr>
				</c:if>
			</c:forEach>
		</h:form>
		</tbody>
	</table>
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
