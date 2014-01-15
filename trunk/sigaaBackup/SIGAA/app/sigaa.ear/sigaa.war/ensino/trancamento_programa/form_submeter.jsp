<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="trancamentoPrograma" />
<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Submeter Trancamento de Programa</h2>

	<div class="descricaoOperacao">
		<p> <strong>Atenção!</strong> </p>
		<p>O conteúdo inserido no campo <strong>Observação</strong> do formulário abaixo será exibido no histórico do aluno.</p>
	</div>
	<c:set var="discente" value="#{trancamentoPrograma.discente}"/>
	
	<table class="visualizacao" >
		<tr>
			<th width="20%"> Matrícula: </th>
			<td colspan="3"> ${discente.matricula } </td>
		</tr>
		<tr>
			<th> Discente: </th>
			<td colspan="3"> ${discente.pessoa.nome } </td>
		</tr>
		<tr>
			<th> Curso: </th>
			<td colspan="3">
				${discente.curso.descricao }
			</td>
		</tr>
		<tr>
			<th> Status: </th>
			<td width="30%"> ${discente.statusString } </td>
			<td width="8%" style="text-align: right;"> <b>Tipo:</b> </td>
			<td> ${discente.tipoString } </td>
		</tr>
	</table>
	<br/>
	
	<h:form id="form">
		<table class="formulario" width="80%">
			<caption>Dados do Trancamento</caption>
			<tbody>
				<tr>
					<th width="30%"><b>Ano-Período de Referência:</b></th>
					<td> 
						<h:outputText value="#{trancamentoPrograma.obj.ano}.#{trancamentoPrograma.obj.periodo}"/>				
					</td>
				</tr>
				<c:if test="${trancamentoPrograma.obj.discente.stricto}">
					<tr>
						<td class="obrigatorio" width="30%"><b>Início do Trancamento:</b></td>
						<td> 
							<t:inputCalendar id="inicioTrancamento" value="#{trancamentoPrograma.obj.inicioTrancamento}" renderAsPopup="true"
								size="10" maxlength="10" onkeypress="formataData(this, event)" renderPopupButtonAsImage="true" /> 				
						</td>
					</tr>
					<tr>
						<td class="obrigatorio" width="30%"><b>Número de meses:</b></td>
						<td> 
							<h:inputText id="numeroMeses" value="#{trancamentoPrograma.obj.numeroMeses}" size="2" 
							maxlength="2" converter="#{ intConverter }" />			
						</td>
					</tr>					
				</c:if>
								
				<tr>
					<th class="obrigatorio">Observação:</th>
					<td>
						<h:inputTextarea id="observacao" rows="3" value="#{trancamentoPrograma.observacao}" style="width: 95%;"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<c:set var="exibirApenasSenha" value="true" scope="request" />
						<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp" %>
					</td>
				</tr>
			</tbody>
				<tfoot>
					<tr>
						<td colspan="2">						
							<h:commandButton value="Confirmar Trancamento" id="confirm" action="#{trancamentoPrograma.submeterTrancamento}" />
							
							<h:commandButton value="<< Voltar para Solicitações" id="voltar" action="#{trancamentoPrograma.exibirSolicitacoes}" />											
							
							<h:commandButton value="<< Escolher Outro Discente" id="outroDiscente"	action="#{trancamentoPrograma.iniciarSubmeterTrancamento}" />											
							
							<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{trancamentoPrograma.cancelar}" />
						</td>
					</tr>
				</tfoot>
		</table>
		<br />
	
	</h:form>

	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br /><br />
	</center>
	
	<c:if test="${not empty movimentacaoAluno.historicoMovimentacoes}">
	<table class="subFormulario" style="width: 100%">
		<caption>Histórico de Movimentações do Discente</caption>
		<thead>
			<tr>
				<td style="text-align: center;">Ano-Período</td>
				<td>Tipo</td>
				<td>Usuário</td>
				<td width="15%" style="text-align: center;">Data</td>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${movimentacaoAluno.historicoMovimentacoes}" var="mov" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td align="center">${mov.anoPeriodoReferencia }</td>
				<td>${mov.tipoMovimentacaoAluno.descricao}</td>
				<td>${mov.usuarioCadastro.pessoa.nome}</td>
				<td align="center"><ufrn:format type="dataHora" valor="${mov.dataOcorrencia}" /></td>
			</tr>
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td colspan="4"><i>${mov.observacao}</i></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</c:if>

	<c:if test="${not empty movimentacaoAluno.historicoCancelamentos}">
	<table class="subFormulario" style="width: 100%">
		<caption>Histórico de Movimentações Canceladas</caption>
		<thead>
			<tr>
				<td width="8%"></td>
				<td>Tipo</td>
				<td	>Usuário</td>
				<td width="15%">Data</td>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${movimentacaoAluno.historicoCancelamentos}" var="mov" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td>${mov.anoPeriodoReferencia }</td>
				<td>${mov.tipoMovimentacaoAluno.descricao}</td>
				<td>${mov.usuarioCancelamento.pessoa.nome}</td>
				<td><ufrn:format type="dataHora" valor="${mov.dataEstorno}" /></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
