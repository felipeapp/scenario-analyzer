<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	
	<h2 class="title"><ufrn:subSistema /> &gt; ${movimentacaoAluno.tituloOperacao} &gt; Informe os Dados</h2>

	<div class="descricaoOperacao">
		<p> <strong>Atenção!</strong> </p>
		<p>O conteúdo inserido no campo <strong>Observação</strong> do formulário abaixo será exibido no histórico do aluno.</p>
	</div>

	<c:set var="discente" value="#{movimentacaoAluno.obj.discente}" />
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:form id="form">
	<table class="formulario" width="100%">
		<caption>Informe os dados para ${movimentacaoAluno.tituloOperacao}</caption>
			<c:if test="${fn:length(movimentacaoAluno.tiposAfastamentos) > 1 }">
				<tr>
					<th width="25%" class="required">Tipo:</th>
					<td><h:selectOneMenu value="#{movimentacaoAluno.obj.tipoMovimentacaoAluno.id}" id="tipo">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{movimentacaoAluno.tiposAfastamentos}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="required" style="width: 20%">Ano-Período de Referência:</th>
				<td><h:inputText id="ano" value="#{movimentacaoAluno.obj.anoReferencia}" size="4" maxlength="4" 
							onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" /> -
					<h:inputText id="periodo" value="#{movimentacaoAluno.obj.periodoReferencia}" size="1" maxlength="1" 
							onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
				</td>
			</tr>
			<c:if test="${movimentacaoAluno.trancamento}">
				<c:if test="${acesso.administradorDAE}">
					<tr>
						<th><h:selectBooleanCheckbox id="limiteTrancamentos" value="#{movimentacaoAluno.obj.limiteTrancamentos}" /></th>
						<td>Respeitar o limite máximo permitido de trancamentos</td>
					</tr>			
				</c:if>
				<c:if test="${movimentacaoAluno.obj.discente.stricto}">
					<tr>
						<th class="required">Início do Trancamento:</th>
						<td><t:inputCalendar id="inicioTrancamento" value="#{movimentacaoAluno.obj.inicioAfastamento}" renderAsPopup="true"
									size="10" maxlength="10" onkeypress="formataData(this, event)" renderPopupButtonAsImage="true" />							
						</td>
					</tr>
					<tr>
						<th class="required">Número de meses:</th>
						<td><h:inputText id="numeroMeses" value="#{movimentacaoAluno.obj.valorMovimentacao}" size="2" 
									maxlength="2" converter="#{ intConverter }" onkeyup="formatarInteiro(this);" />						 	
						</td>
					</tr>
				</c:if>
			</c:if>
			<c:if test="${movimentacaoAluno.conclusao}">
				<tr>
					<th id="labelData" class="required">
						${movimentacaoAluno.obj.apostilamento ? 'Data de Apostilamento:' : 'Data de Colação:' }
					</th>
					<td><t:inputCalendar id="dataColacaoGrau" value="#{movimentacaoAluno.obj.dataColacaoGrau}" 
							renderAsPopup="true" size="10" maxlength="10" popupTodayString="Hoje" 
							renderPopupButtonAsImage="true" onkeypress="formataData(this,event)" popupDateFormat="dd/MM/yyyy"> 
						<f:converter converterId="convertData"/> 
						</t:inputCalendar>
					</td>
				</tr>
				<tr>
					<th>Apostilamento:</th>
					<td>
					<c:if test="${movimentacaoAluno.obj.discente.curriculo.matriz.permiteColacaoGrau}">
						<h:selectOneRadio id="apostilamento" value="#{movimentacaoAluno.obj.apostilamento}" onclick="mudarLabelData(this)">
							<f:selectItem itemValue="true" itemLabel="Sim" />
							<f:selectItem itemValue="false" itemLabel="Não" />
						</h:selectOneRadio>
					</c:if>
					<c:if test="${not movimentacaoAluno.obj.discente.curriculo.matriz.permiteColacaoGrau}">	SIM	</c:if>
					</td>
				</tr>
			</c:if>
			<tr>
				<th valign="top">Observação:</th>
				<td><h:inputTextarea id="observacao" rows="3" value="#{movimentacaoAluno.obj.observacao}" style="width: 95%;">
					</h:inputTextarea></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="<< Escolher Outro Discente" id="voltar" action="#{relatoriosStricto.gerarRelatorioAlunosNaoMatriculadosOnLine}" 
							rendered="#{ movimentacaoAluno.discenteNaoMatriculadoOnline }"/>											
						<h:commandButton value="<< Escolher Outro Discente" id="voltar2" action="#{movimentacaoAluno.buscarDiscente}" 
							rendered="#{ !movimentacaoAluno.discenteNaoMatriculadoOnline }"/>											
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{movimentacaoAluno.cancelar}" />
						<h:commandButton value="Próximo Passo >>" id="proximo" action="#{movimentacaoAluno.submeterDadosAfastamento}" />
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
				<td width="8%"></td>
				<td>Tipo</td>
				<td>Usuário</td>
				<td width="15%">Data</td>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${movimentacaoAluno.historicoMovimentacoes}" var="mov" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td>${mov.anoPeriodoReferencia }</td>
				<td>${mov.tipoMovimentacaoAluno.descricao}</td>
				<td>${mov.usuarioCadastro.pessoa.nome}</td>
				<td><ufrn:format type="dataHora" valor="${mov.dataOcorrencia}" /></td>
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
<script type="text/javascript">
<!--
	function mudarLabelData(chk) {
		var checked = chk.value;
		if (checked  == 'true') {
			$('labelData').innerHTML = 'Data de Apostilamento:';
		} else if (checked  == 'false') {
			$('labelData').innerHTML = 'Data de Colação:';
		}
	}
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
