<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
function mudarTodos(status) {
	var re= new RegExp('selectCompareceu', 'g')
	var elements = document.getElementsByTagName('select');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].id.match(re)) {
			elements[i].selectedIndex = status.selectedIndex;
		}
	}
}
</script>
<f:view>
<a4j:keepAlive beanName="cadastramentoDiscente"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Gerenciar Cadastramento de Discentes</h2>
<div class="descricaoOperacao">
	<p> Esta operação permite confirmar os alunos que compareceram ao cadastramento após uma convocação do vestibular,
	bem como identificar aqueles que não compareceram, excluindo suas matrículas do sistema e abrindo vagas para
	uma posterior reconvocação dos próximos classificados no vestibular. </p>
	<ul>
		<li>Para indicar que um aluno <strong>não compareceu</strong> ao cadastramento <strong>desmarque a caixinha</strong> correspondente ao aluno. </li>
	</ul>
	<p> Também nesta operação é possível cancelar as convocações dos alunos já cadastrados mas que tiveram seus vínculos cancelados no sistema pelo não comparecimento à matrícula. </p>
</div>

<h:form id="form">

<table class="formulario" width="80%">
	<caption>Parâmetros do Cadastramento</caption>
	<tr>
		<th class="obrigatorio" width="25%">Processo Seletivo Vestibular:</th>
		<td>
			<h:selectOneMenu id="selectPsVestibular" value="#{cadastramentoDiscente.processoSeletivo.id}" valueChangeListener="#{cadastramentoDiscente.changeProcessoSeletivo}">
				<f:selectItem itemValue="" itemLabel="-- SELECIONE --" />
				<f:selectItems id="itensPsVestibular" value="#{convocacaoVestibular.processoSeletivoVestibularCombo}" />
				<a4j:support event="onchange" reRender="selectMatriz"/>
				<a4j:status>
					<f:facet name="start" >
						<h:graphicImage value="/img/indicator.gif" />
					</f:facet>
				</a4j:status>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Matriz Curricular:</th>
		<td>
			<h:selectOneMenu id="selectMatriz" value="#{cadastramentoDiscente.matriz.id}" style="width: 80%;">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems id="listaMatrizes" value="#{cadastramentoDiscente.matrizesCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnBuscar" value="Buscar Discentes" action="#{cadastramentoDiscente.buscar}"/>
				<h:commandButton value="Cancelar" action="#{ cadastramentoDiscente.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
<c:if test="${ not empty cadastramentoDiscente.convocacoes}" >
<br/><br/>
	<c:set var="habilitaProximoPasso" value="false"/>
	<table class="listagem" id="resultadoBusca">
		<caption>Discentes encontrados (${fn:length(cadastramentoDiscente.convocacoes)})</caption>
		<thead>
			<tr>
				<th>Compareceu</th>
				<th style="text-align: center;">Ingresso</th>
				<th style="text-align: center;">Matrícula</th>
				<th>Nome</th>
				<th>Convocação</th>
				<th>Status</th>
			</tr>
		</thead>
		<tr>
			<td colspan="6" class="subFormulario">
				Definir o status de todos discente para: 
				<h:selectOneMenu id="selectStatusTodos" value="#{cadastramentoDiscente.idStatusTodosDiscentes}"
					onchange="mudarTodos(this)" >
					<f:selectItems value="#{cadastramentoDiscente.opcoesCadastramentoCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<c:forEach items="#{cadastramentoDiscente.convocacoes}" var="conv" varStatus="loop">
			<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td>
					<h:selectOneMenu id="selectCompareceu" value="#{conv.discente.opcaoCadastramento}"  
						rendered="#{(conv.discente.discente.pendenteCadastro)}" 
						title="#{conv.discente.pessoa.nome}">
						<f:selectItems value="#{cadastramentoDiscente.opcoesCadastramentoCombo}"/>
					</h:selectOneMenu>
					<c:set var="habilitaProximoPasso" value="${habilitaProximoPasso || conv.discente.discente.pendenteCadastro }" />
				</td>
				<td style="text-align: center;"><h:outputText value="#{conv.discente.anoPeriodoIngresso}" /></td>
				<td style="text-align: center;"><h:outputText value="#{conv.discente.matricula}" /></td>
				<td><h:outputText value="#{conv.discente.pessoa.nome}"/></td>
				<td><h:outputText value="#{conv.convocacaoProcessoSeletivo.descricao}"/></td>
				<td><h:outputText value="#{conv.discente.statusString}"/></td>
			</tr>
		</c:forEach>
		<c:if test="${habilitaProximoPasso}">	
			<tfoot>
			<tr>
				<td colspan="6" style="text-align: center;">
				<h:commandButton value="Processar Cadastramento >>" action="#{ cadastramentoDiscente.processar }" id="btnProcessar"/>
				</td>
			</tr>
			</tfoot>
		</c:if>
	</table>
</c:if>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>