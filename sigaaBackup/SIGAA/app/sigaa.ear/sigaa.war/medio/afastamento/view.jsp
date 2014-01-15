<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="afastamentoDiscenteMedioMBean"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> &gt; <h:outputText value="#{(afastamentoDiscenteMedioMBean.conclusao?'Conclusão de Programa':'Cadastrar Afastamento')}"/></h2>

	<c:set var="discente" value="#{afastamentoDiscenteMedioMBean.obj.discente}" />
	<%@include file="/medio/discente/info_discente.jsp"%>

	<h:form id="form">
	<table class="formulario" width="50%">
			<caption class="listagem">Confirme os dados para <h:outputText value="#{(afastamentoDiscenteMedioMBean.conclusao ?'a Conclusão de Programa ':'o Afastamento ') }" /> do Discente</caption>
			<c:if test="${afastamentoDiscenteMedioMBean.conclusao}">
				<tr>
					<th width="30%" style="font-weight: bold;">Data de Colação:</th>
					<td><ufrn:format type="data" valor="${afastamentoDiscenteMedioMBean.obj.dataColacaoGrau}"></ufrn:format></td>	
				</tr>
			</c:if>
			<c:if test="${not afastamentoDiscenteMedioMBean.conclusao}">
				<tr>
					<th width="30%" style="font-weight: bold;">Tipo:</th>
					<td>${afastamentoDiscenteMedioMBean.obj.tipoMovimentacaoAluno.descricao}</td>
				</tr>			
			</c:if>
			
			<tr>
				<th width="30%" style="font-weight: bold;">Ano:</th>
				<td>${afastamentoDiscenteMedioMBean.obj.anoReferencia}</td>
			</tr>
			<c:if test="${!empty afastamentoDiscenteMedioMBean.obj.observacao}">
				<tr>
					<th valign="top" style="font-weight: bold;">Observação</th>
					<td>${afastamentoDiscenteMedioMBean.obj.observacao}</td>
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
					<h:commandButton value="Confirmar" action="#{afastamentoDiscenteMedioMBean.cadastrar}"/>
					<h:commandButton value="<< Voltar" action="#{afastamentoDiscenteMedioMBean.telaDadosAfastamento}"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{afastamentoDiscenteMedioMBean.cancelar}" />
				</td>
			</tr>
			</tfoot>
	</table>
	</h:form>

	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
	<c:if test="${not empty afastamentoDiscenteMedioMBean.historicoMovimentacoes}">
	<table class="subFormulario" style="width: 100%">
		<caption>Histórico de Movimentações do Discente</caption>
		<thead>
			<td width="8%"></td>
			<td>Tipo</td>
			<td	>Usuário</td>
			<td width="15%">Data</td>
		</thead>
		<c:forEach items="${afastamentoDiscenteMedioMBean.historicoMovimentacoes}" var="mov" varStatus="status">
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
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
