<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>

	<h2 class="title"><ufrn:subSistema /> &gt; ${movimentacaoAluno.tituloOperacao} &gt; Informe os Dados</h2>

	<c:set var="discente" value="#{movimentacaoAluno.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:form id="form">
	<table class="formulario" width="50%">
			<caption class="listagem">Confirme os dados para ${movimentacaoAluno.tituloOperacao}</caption>
			<tr>
				<th width="30%" class="rotulo">Ano-Período:</th>
				<td>${movimentacaoAluno.obj.anoPeriodoReferencia}</td>
			</tr>
			<c:if test="${movimentacaoAluno.obj.discente.stricto and movimentacaoAluno.trancamento}">
				<tr>
					<th class="rotulo">Início do Trancamento:</th>
					<td><ufrn:format type="data" valor="${movimentacaoAluno.obj.inicioAfastamento}"></ufrn:format> </td>
				</tr>
				<tr>
					<th class="rotulo">Número de meses:</th>
					<td>${movimentacaoAluno.obj.valorMovimentacao}</td>
				</tr>
			</c:if>
			<c:if test="${movimentacaoAluno.conclusao}">
				<tr>
					<th class="rotulo">${movimentacaoAluno.obj.apostilamento ? 'Data de Apostilamento:' : 'Data de Colação:' }</th>
					<td><ufrn:format type="data" valor="${movimentacaoAluno.obj.dataColacaoGrau}"></ufrn:format> </td>
				</tr>
				<tr>
					<th class="rotulo">Apostilamento:</th>
					<td><ufrn:format type="bool_sn" valor="${movimentacaoAluno.obj.apostilamento}"></ufrn:format></td>
				</tr>
			</c:if>
			<c:if test="${not empty movimentacaoAluno.obj.observacao}">
				<tr>
					<th class="rotulo" valign="top">Observação:</th>
					<td>${movimentacaoAluno.obj.observacao}</td>
				</tr>
			</c:if>
			<c:if test="${not empty movimentacaoAluno.obj.matriculasAlteradas}">
			<tr>
				<td colspan="2">
				<table class="subFormulario" style="width: 100%">
					<caption>Matrículas a serem ${movimentacaoAluno.stricto ? 'TRANCADAS' : 'CANCELADAS' }</caption>
					<c:forEach items="${movimentacaoAluno.obj.matriculasAlteradas}" var="matricula" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td> ${matricula.anoPeriodo } </td>
							<td>  ${matricula.componenteDescricao}  </td>
							<td align="center">${matricula.turma.codigo}</td>
							<td align="center">${matricula.situacaoMatricula.descricao}</td>
						</tr>
					</c:forEach>
				</table>
				</td>
			</tr>
			</c:if>
			<tr>
				<td colspan="2">
					<c:set var="exibirApenasSenha" value="true" scope="request"/>
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Confirmar" action="#{movimentacaoAluno.cadastrarAfastamento}"/>
					<h:commandButton value="<< Voltar" action="#{movimentacaoAluno.telaDadosMovimentacao}"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{movimentacaoAluno.cancelar}" />
				</td>
			</tr>
			</tfoot>
	</table>
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
