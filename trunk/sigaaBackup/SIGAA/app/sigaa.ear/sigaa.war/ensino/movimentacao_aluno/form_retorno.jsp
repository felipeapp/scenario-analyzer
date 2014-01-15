<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{movimentacaoAluno.create}" />
	<h2 class="title"><ufrn:subSistema /> > ${movimentacaoAluno.tituloOperacao} &gt; Confirmação</h2>

	<c:set var="discente" value="#{movimentacaoAluno.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<br>
	
	<c:if test="${acesso.algumUsuarioStricto && movimentacaoAluno.obj.trancamento}">
	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p> Esta operação retornará o aluno de um trancamento. Utilize o campo data de retorno para informar a data em que o aluno está retornando ao curso. 
		Observando que a alteração desta data resultará em uma mudança no tempo em que o discente ficou trancado (Duração do Trancamento), sendo este período contabilizado em meses. 
		Se a duração do trancamento exibida estiver correta não é necessário alterar a data de retorno.
		</p>
	</div>
	</c:if>
	
	<table class="formulario" width="600px">
		<h:form id="formulario">
			<h:outputText value="#{movimentacaoAluno.create}" />
			<caption class="listagem">Cadastro de Retorno de Afastamento</caption>
			<tr>
				<th>Último Afastamento:</th>
				<td><h:outputText value="#{movimentacaoAluno.obj.tipoMovimentacaoAluno.descricao}" /></td>
			</tr>
			
			<c:if test="${movimentacaoAluno.portalGraduacao or acesso.tecnico or acesso.coordenadorCursoTecnico or acesso.lato}">
				<tr>
					<th>Data da Ocorrência:</th>
					<td><ufrn:format type="data" valor="${movimentacaoAluno.obj.dataOcorrencia}" /></td>
				</tr>
				<tr>
					<c:if test="${not empty movimentacaoAluno.movimentacoesCombo}">
					<th>Afastamento:</th>
					<td>
						<h:selectOneMenu id="afastamento" value="#{movimentacaoAluno.afastamento.id}" onchange="submit();" valueChangeListener="#{movimentacaoAluno.anoPeriodoChange}">
							<f:selectItems value="#{movimentacaoAluno.movimentacoesCombo}"/>
						</h:selectOneMenu>
					</td>
					</c:if>
				</tr>
				<tr>
					<th class="required">Tipo de Retorno:</th>
					<td>
						<h:selectOneMenu value="#{movimentacaoAluno.obj.tipoRetorno}" id="tipoRetorno">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
							<f:selectItems value="#{movimentacaoAluno.tiposRetorno}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${acesso.algumUsuarioStricto}">
				<c:if test="${movimentacaoAluno.obj.trancamento}">
					<tr>
						<th>Inicio do Trancamento</th>
						<td>
							<ufrn:format type="mes" valor="${movimentacaoAluno.obj.inicioAfastamento}"/> / <ufrn:format type="ano" valor="${movimentacaoAluno.obj.inicioAfastamento}"/> 
						</td>
					</tr>
					<tr>
						<th>Duração do Trancamento</th>
						<td>
							<c:if test="${not empty movimentacaoAluno.obj.valorMovimentacao}">
							<h:outputText value="#{movimentacaoAluno.obj.valorMovimentacao}" id="valorMov"/> meses 
							</c:if>
						</td>
					</tr>
				</c:if>
				
				<tr>
					<th class="required">Data de Retorno</th>
					<td>
						<t:inputCalendar id="dtRetorno" value="#{movimentacaoAluno.obj.dataRetorno}" size="10" maxlength="10"
						renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="formataData(this,event); return ApenasNumeros(event)"/>
					</td>
				</tr>
			</c:if>
			<tr>
				<th valign="top">Observação:</th>
				<td><h:inputTextarea id="observacao" rows="3" cols="60" value="#{movimentacaoAluno.obj.observacao}"></h:inputTextarea> </td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="#{movimentacaoAluno.confirmButton}" action="#{movimentacaoAluno.cadastrarAfastamento}" id="btnAfastamento"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{movimentacaoAluno.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
<c:if test="${not empty movimentacaoAluno.historicoMovimentacoes}">
	<table class="subFormulario" width="100%">
		<caption>Histórico de Movimentações do Discente</caption>
		<thead>
			<td width="8%"></td>
			<td>Tipo</td>
			<td width="8%">Data</td>
			<td width="13%">Retorno</td>
		</thead>
		<c:forEach items="${movimentacaoAluno.historicoMovimentacoes}" var="mov" varStatus="status">
			<tr>
				<td>${mov.anoPeriodoReferencia }</td>
				<td>${mov.tipoMovimentacaoAluno.descricao}</td>
				<td><ufrn:format type="data" valor="${mov.dataOcorrencia}" /></td>
				<td><ufrn:format type="data" valor="${mov.dataCadastroRetorno}" /></td>
			</tr>
		</c:forEach>
	</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
