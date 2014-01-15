<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<h2 class="title">Solicitação de Abertura de Turma > Resumo</h2>
	<h:outputText value="#{sugestaoSolicitacaoTurma.create}"/>
	<a4j:keepAlive beanName="horarioTurmaBean"></a4j:keepAlive>
	<div class="descricaoOperacao">
		Confira os dados da solicitação e clique em Cadastrar Solicitação de Turma caso
		os dados estejam corretos.
		</div>

	<table class="formulario" width="100%">
		<caption>Resumo da Solicitação</caption>

		<tr><td>
			<table width="100%">
				<caption>Dados Gerais</caption>

				<tr>
					<th width="20%" class="rotulo"> Componente Curricular: </th>
					<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.nome } </td>
				</tr>
				<tr>
					<th class="rotulo"> Código: </th>
					<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.codigo } </td>
				</tr>
				<tr>
					<th class="rotulo"> Tipo: </th>
					<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.tipoComponente.descricao } </td>
				</tr>
				<c:if test="${sugestaoSolicitacaoTurma.obj.componenteCurricular.bloco}">
				<tr>
					<th  class="rotulo"> Subunidades: </th>
					<td>
						<c:forEach items="${sugestaoSolicitacaoTurma.obj.componenteCurricular.subUnidades}" var="subunidade">
							${subunidade} - ${subunidade.chTotal}h  <br/>						
						</c:forEach> 
					</td>
				</tr>
				</c:if>
				<tr>
					<th  class="rotulo"> Carga Horária: </th>
					<td> ${sugestaoSolicitacaoTurma.obj.componenteCurricular.detalhes.chTotal } horas </td>
				</tr>
				<tr>
					<th  class="rotulo">Horário: </th>
					<td> ${sugestaoSolicitacaoTurma.obj.horario}</td>
				</tr>
				<tr>
					<th  class="rotulo">Ano-Período: </th>
					<td> ${sugestaoSolicitacaoTurma.obj.ano}-${sugestaoSolicitacaoTurma.obj.periodo}</td>
				</tr>
				
			</table>
		</td></tr>
		<tr><td>
			<table width="100%">
				<caption>Curso para Sugestão de Turma</caption>

				<tr>
					<th width="20%" class="rotulo"> Curso: </th>
					<td> ${sugestaoSolicitacaoTurma.obj.curso.nome } </td>
				</tr>
				<tr>
					<th class="rotulo">Observação: </th>
					<td> ${sugestaoSolicitacaoTurma.obj.observacaoSugestao} </td>
				</tr>	

			</table>
		</td></tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:form>
							<h:commandButton value="#{sugestaoSolicitacaoTurma.confirmButton}" action="#{sugestaoSolicitacaoTurma.cadastrar}" id="btCadastrarSugestao" rendered="#{sugestaoSolicitacaoTurma.chefeDepartamento}"/>
							<h:commandButton value="<< Voltar" action="#{sugestaoSolicitacaoTurma.listar}" id="btVoltarRemover" rendered="#{sugestaoSolicitacaoTurma.operacaoAtivaRemover}" />
							<h:commandButton value="<< Voltar" action="#{horarioTurmaBean.telaHorarios}" id="btVoltar" rendered="#{not sugestaoSolicitacaoTurma.operacaoAtivaRemover}" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{sugestaoSolicitacaoTurma.cancelar}" id="btCancelar"/>
					</h:form>
				</td>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>