<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<f:view>
	<a4j:keepAlive beanName="importacaoDadosTurma" />
	<a4j:keepAlive beanName="planoCurso" />
	<h:form>
	
	<h2><ufrn:subSistema /> &gt; <h:commandLink value="Plano de Curso" action="#{planoCurso.gerenciarPlanoCurso}" /> &gt; Importação de Dados de Turmas Anteriores</h2>
	
	<div class="descricaoOperacao">
		Selecione uma das turmas abaixo para importar os seus dados para o plano de curso.
	</div>
	
	<div class="infoAltRem" style="width:80%">
		<h:graphicImage value="/img/seta.gif" />: Selecionar Turma
	</div>
	
		<table class="formulario" style="width:80%">
			<caption>Selecione uma turma</caption>
			
			<thead>
				<tr>
					<th>Componente Curricular</th>
					<th style="text-align:center;">Turma</th>
					<th style="text-align:center;">Ano-Período</th>
					<th style="width:20px;"></th>
				</tr>
			</thead>
			
			<c:forEach varStatus="s" var="t" items="#{ importacaoDadosTurma.turmasAnteriores }">
				<tr class='${s.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
					<td>
						<h:outputText value="#{ t.disciplina.codigo } - #{ t.disciplina.nome }"/>
					</td>
					<td style="text-align:center;">
						<h:outputText value="#{ t.codigo }"/>
					</td>
					<td style="text-align:center;">
						<h:outputText value="#{ t.anoPeriodo }"/>
					</td>
					<td>
						<h:commandLink action="#{ importacaoDadosTurma.opcoesImportacao }" title="Selecionar Turma">
							<h:graphicImage value="/img/seta.gif" alt="Selecionar Turma"/>
							<f:param name="id" value="#{ t.id }"/>
							<f:param name="planoCurso" value="#{ true }"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			<tfoot><tr><td colspan="4"><h:commandButton value="<< Voltar" action="#{planoCurso.gerenciarPlanoCurso}" /></td></tr></tfoot>
		</table>
	
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
