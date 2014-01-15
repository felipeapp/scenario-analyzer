<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<a4j:keepAlive beanName="planoCurso" />

<style>
	table.listagem tr td.periodo {
		background: #C4D2EB;
		padding: 3px;
		font-weight: bold;
	}
	
	table tr.nivel td{
		background: #404E82;
		font-weight: bold;
		padding-left: 20px;
		color: #FFF;
	}	
</style>

<f:view>

	<c:if test="${ !turmaVirtual.acessouPublicacao}">
		<h2><ufrn:subSistema /> &gt; Turmas Abertas</h2>		
		<div class="infoAltRem" style="font-variant: small-caps;">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Acessar Turma Virtual
			<h:graphicImage value="/img/avaliar.gif" style="overflow: visible;" />: Gerenciar Plano de Curso
		</div>
	</c:if>
	
	<c:if test="${ turmaVirtual.acessouPublicacao}">
		<h2><ufrn:subSistema /> &gt; Publicação de Turmas Virtuais</h2>
		<div class="descricaoOperacao">
			<p>Selecione um das Turmas Listadas abaixo para publicá-la no <a href="${ ctx }/public/curso_aberto/portal.jsf?aba=p-ensino" 
					target="_blank" title="Acessar Portal Pùblico dos Cursos Abertos">
				Portal Público dos Cursos Abertos</a>.</p>
		</div>			
			
		<div class="infoAltRem" style="font-variant: small-caps;">	
			<h:graphicImage value="/img/seta.gif"style="overflow: visible;" />: Publicar/Remover Turma Virtual para comunidade externa
		</div>
	</c:if>

<h:form>

	<table class="listagem">
	<caption>Lista de Turmas Abertas</caption>
	<thead>
		<tr>
			<th>Código</th>
			<th>Disciplina</th>
			<th>Ano/Período</th>
			<th>Turma</th>
			<th>Créditos</th>
			<th>Horário</th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	
	<c:set var="periodoAtual" value="" />
	<c:set var="nivelAtual" value="" />
	<c:forEach var="t" items="#{ portalDocente.turmasAbertas }" varStatus="loop">
		<!-- agrupando as turmas por nivel -->
		<c:if test="${nivelAtual != t.disciplina.nivelDesc}">
			<c:set var="nivelAtual" value="${t.disciplina.nivelDesc}" />
			<tr class="nivel">
				<td colspan="8">${t.disciplina.nivelDesc}</td>
			</tr>
		</c:if> 	

		<c:set var="periodo" value="${t.ano}.${t.periodo}" />
		<c:if test="${ periodo != periodoAtual }">
			<c:set var="periodoAtual" value="${periodo}" />
			<tr>
				<td class="periodo" colspan="8"> ${periodoAtual} </td>
			</tr>
		</c:if>

		<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td><h:outputText value="#{ t.disciplina.codigo }" /></td>
			<td><h:commandLink action="#{turmaVirtual.entrar}" value="#{t.disciplina.nome}">
					<f:param name="idTurma" value="#{t.id}" />
				</h:commandLink>
			</td>
			<td align="center"><h:outputText value="#{ t.anoPeriodo }" /></td>
			<td align="center"><h:outputText value="#{ t.codigo }" /></td>
			<td align="center"><h:outputText value="#{ t.disciplina.detalhes.crTotal }" /></td>
			<td><h:outputText value="#{ t.descricaoHorario }" /></td>
			<c:choose>
				<c:when test="${ turmaVirtual.acessouPublicacao }">
					<td colspan="2">
						<h:commandLink action="#{ configuracoesAva.publicarTurmaVirtual }" 
								title="Publicar/Remover Turma Virtual para comunidade externa">
							<h:graphicImage value="/img/seta.gif" /> 
							<f:param name="tid" value="#{ t.id }" />
							<f:setPropertyActionListener target="#{configuracoesAva.opcaoCursosAbertos}" value="#{true}" /> 
							<f:setPropertyActionListener target="#{configuracoesAva.portalDocente}" value="#{true}" /> 
						</h:commandLink>
					</td>			
				</c:when>
				<c:otherwise>
					<td width="center">
						<h:commandLink action="#{turmaVirtual.entrar}" title="Acessar Turma Virtual">
							<h:graphicImage value="/img/seta.gif" />
							<f:param name="idTurma" value="#{ t.id }" />
						</h:commandLink>
					</td>
					<td width="center">
						<h:commandLink action="#{planoCurso.gerenciarPlanoCurso}" title="Gerenciar Plano de Curso" rendered="#{ empty t.polo }">
							<h:graphicImage value="/img/avaliar.gif" />
							<f:param name="idTurma" value="#{ t.id }" />
						</h:commandLink>
					</td>
				</c:otherwise>
			</c:choose>
		</tr>

	</c:forEach>
	</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
