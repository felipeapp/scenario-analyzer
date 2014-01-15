<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{movimentacaoAluno.create}" />
	<h2 class="title"><ufrn:subSistema /> > ${movimentacaoAluno.tituloOperacao} &gt; Informe os Dados</h2>

	<c:set var="discente" value="#{movimentacaoAluno.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:form id="form">
	<table class="formulario" width="90%">
			<caption class="listagem">Dados</caption>
			<tr>
				<th class="rotulo">Título do Trabalho:</th>
				<td>${ movimentacaoAluno.homologacao.banca.dadosDefesa.titulo }</td>
			</tr>
			<tr>
				<th valign="top" class="rotulo">Resumo: </th>
				<td>${ movimentacaoAluno.homologacao.banca.dadosDefesa.resumo }</td>
			</tr>
			<tr>
				<th valign="top" class="rotulo">Palavras-Chave: </th>
				<td>${ movimentacaoAluno.homologacao.banca.dadosDefesa.palavrasChave }</td>
			</tr>
			<tr>
				<th valign="top" class="rotulo">Banca: </th>
				<td> 
				<c:forEach var="m" items="${ movimentacaoAluno.homologacao.banca.membrosBanca }">
					${ m.descricao }<br/>
				</c:forEach>
				</td>
			</tr>
			<tr>
				<th class="rotulo">Data da Defesa:</th>
				<td><fmt:formatDate value="${ movimentacaoAluno.homologacao.banca.data }" pattern="dd/MM/yyyy"/></td>
			</tr>
			<tr>
				<th nowrap="nowrap" class="rotulo">Processo de Homologação: </th>
				<td>${ movimentacaoAluno.homologacao.numProcesso }/${ movimentacaoAluno.homologacao.anoProcesso }</td>
			</tr>
			<tr>
				<th width="20%" class="required">Ano-Período de Referência:</th>
				<td><h:inputText id="ano" value="#{movimentacaoAluno.obj.anoReferencia}" size="4" maxlength="4" /> -
				<h:inputText id="periodo" value="#{movimentacaoAluno.obj.periodoReferencia}" size="1" maxlength="1" /></td>
			</tr>
			<tr>
				<th valign="top">Observação:</th>
				<td><h:inputTextarea id="observacao" rows="3" value="#{movimentacaoAluno.obj.observacao}" style="width: 93%;"></h:inputTextarea> </td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
					<h:commandButton value="<< Escolher Outro Discente" id="voltar" action="#{movimentacaoAluno.buscarDiscente}" />
					<h:commandButton value="#{movimentacaoAluno.confirmButton}"	id="confirm" action="#{movimentacaoAluno.cadastrarAfastamento}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{movimentacaoAluno.cancelar}" />					
					</td>
				</tr>
			</tfoot>
	</table>
	<br>

		<c:set var="exibirApenasSenha" value="true" scope="request"/>
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
		
	</h:form>

	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
	<c:if test="${not empty movimentacaoAluno.historicoMovimentacoes}">
	<table class="subFormulario" style="width: 100%">
		<caption>Histórico de Movimentações do Discente</caption>
		<thead>
			<td width="8%"></td>
			<td>Tipo</td>
			<td	>Usuário</td>
			<td width="15%">Data</td>
		</thead>
		<c:forEach items="${movimentacaoAluno.historicoMovimentacoes}" var="mov" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td>${mov.anoPeriodoReferencia }</td>
				<td>${mov.tipoMovimentacaoAluno.descricao}</td>
				<td>${mov.usuarioCadastro.pessoa.nome}</td>
				<td><ufrn:format type="dataHora" valor="${mov.dataOcorrencia}" /></td>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td colspan="4"><i>${mov.observacao}</i></td>
			</tr>
		</c:forEach>
	</table>
	</c:if>
	<br><br>

	<c:if test="${not empty movimentacaoAluno.historicoCancelamentos}">
	<table class="subFormulario" style="width: 100%">
		<caption>Histórico de Movimentações Canceladas</caption>
		<thead>
			<td width="8%"></td>
			<td>Tipo</td>
			<td	>Usuário</td>
			<td width="15%">Data</td>
		</thead>
		<c:forEach items="${movimentacaoAluno.historicoCancelamentos}" var="mov" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td>${mov.anoPeriodoReferencia }</td>
				<td>${mov.tipoMovimentacaoAluno.descricao}</td>
				<td>${mov.usuarioCancelamento.pessoa.nome}</td>
				<td><ufrn:format type="dataHora" valor="${mov.dataEstorno}" /></td>
			</tr>
		</c:forEach>
	</table>
	</c:if>

</f:view>
<script type="text/javascript">
<!--
function mudarLabelData(chk) {
	var checked = chk.value;
	if (checked  == 'true') {
		$('labelData').innerHTML = 'Data de Apostilamento';
	} else if (checked  = 'false') {
		$('labelData').innerHTML = 'Data de Colação';
	}
}
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
