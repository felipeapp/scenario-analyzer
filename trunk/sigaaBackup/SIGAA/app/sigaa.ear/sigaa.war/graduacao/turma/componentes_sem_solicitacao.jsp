<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Criar Turma &gt; Turma Sem Solicitação</h2>

	<c:if test="${empty turmaGraduacaoBean.componentesSemSolicitacao}">
		<br/>
		<center><font color="red">Não há nenhum componente do seu departamento que possa ser cadastrado turmas sem solicitação.</font> </center>	
	</c:if>
	
	<c:if test="${not empty turmaGraduacaoBean.componentesSemSolicitacao}">
		<br>
		<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:Criar Turma
		</div>
		</center>
		
		<table class=listagem>
			<caption class="listagem">Lista de Componentes</caption>
			<thead>
				<tr>
					<td>Código</td>
					<td>Nome</td>
					<td>CH Total</td>
					<td>CR Total</td>
					<td>Tipo de Componente</td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="${turmaGraduacaoBean.componentesSemSolicitacao}" var="componente" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${ componente.codigo}</td>
					<td>${ componente.detalhes.nome }</td>
					<td>${ componente.detalhes.chTotal }</td>
					<td>${ componente.detalhes.crTotal }</td>
					<td>${ componente.tipoComponente.descricao }</td>
					<h:form>
						<td width=5>
							<input type="hidden" value="${componente.id}" name="id" /> 
							<h:commandButton image="/img/seta.gif" styleClass="noborder" value="Criar Turma"
							action="#{turmaGraduacaoBean.selecionarComponenteSemSolicitacao}" />
						</td>
					</h:form>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>