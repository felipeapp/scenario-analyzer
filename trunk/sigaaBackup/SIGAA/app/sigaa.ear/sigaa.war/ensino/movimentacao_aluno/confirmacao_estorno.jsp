<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{movimentacaoAluno.create}" />
	<h2 class="title"><ufrn:subSistema /> > ${movimentacaoAluno.tituloOperacao} &gt; Confirmação</h2>

	<br>
	<div class="descricaoOperacao">
		<p>
		Caro Usuário,
		</p><br>
		<p>
		Essa operação tem como objetivo estornar algum afastamento realizado de forma errada. O usuário seleciona o status que o
		aluno deve possuir após o estorno,
		 e por exemplo no caso do estorno de um trancamento de programa, suas matrículas são recuperadas e deixam
		 de ser canceladas.
		</p>
		<p><b>
		Essa operação não é recomendada para representar o retorno do aluno à instituição, para isso é recomendado o uso da operação 'Retorno Manual'.
		</b></p>
	</div>
	<c:set var="discenteMBean" value="#{discente}"/>
	<c:set var="discente" value="#{movimentacaoAluno.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	<c:set var="discente" value="${discenteMBean}"/>
	
	
	<table class="formulario" width="600px">
		<h:form id="formulario">
			<caption class="listagem">Confirme o Estorno</caption>
			<tr>
				<th>Discente</th>
				<td><h:outputText value="#{movimentacaoAluno.obj.discente.nome }" /></td>
			</tr>
			<tr>
				<th>Último Afastamento</th>
				<td><h:outputText value="#{movimentacaoAluno.obj.tipoMovimentacaoAluno.descricao}" /></td>
			</tr>
			<tr>
				<th>Observação</th>
				<td><h:outputText value="#{movimentacaoAluno.obj.observacao}" /></td>
			</tr>
			<tr>
				<th>Ano-Período de Referência</th>
				<td><h:outputText value="#{movimentacaoAluno.obj.anoReferencia}"/>-<h:outputText value="#{movimentacaoAluno.obj.periodoReferencia}"/></td>
			</tr>
			<tr>
				<th>Data da Ocorrência</th>
				<td><ufrn:format type="data" valor="${movimentacaoAluno.obj.dataOcorrencia}" /></td>
			</tr>
			<c:if test="${ not empty movimentacaoAluno.obj.dataCadastroRetorno}">
			<tr>
				<td colspan="2" align="center"><b>Este afastamento já foi retornado!</b></td>
			</tr>
			<tr>
				<th>Data de retorno</th>
				<td><ufrn:format type="data" valor="${movimentacaoAluno.obj.dataCadastroRetorno}" /></td>
			</tr>
			<tr>
				<th>Usuário que retornou</th>
				<td><h:outputText value="#{movimentacaoAluno.obj.usuarioRetorno.nome}" /></td>
			</tr>
			
			</c:if>
			<tr>
				<th class="required">Status que aluno deve ficar</th>
				<td>
				<%-- 
				<h:outputText value="#{movimentacaoAluno.statusAnterior}" /><ufrn:help img="/img/ajuda.gif">O discente
				voltará a ter esse status</ufrn:help>
				--%>
				<h:selectOneMenu value="#{movimentacaoAluno.statusRetorno}" id="status" style="width: 40%;">
						<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0"  />
						<f:selectItems value="#{discente.statusCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4"><h:commandButton value="#{movimentacaoAluno.confirmButton}" id="btnConfirmar"
						action="#{movimentacaoAluno.estornarAfastamento}" /> <h:commandButton value="Cancelar" onclick="#{confirm}" id="btnCancelar"
						action="#{movimentacaoAluno.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
