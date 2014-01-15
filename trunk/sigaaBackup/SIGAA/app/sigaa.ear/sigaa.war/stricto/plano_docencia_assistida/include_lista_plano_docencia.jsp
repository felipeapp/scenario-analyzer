<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@taglib uri="/tags/rich" prefix="rich"%>

<c:set var="planos" value="#{planoDocenciaAssistidaMBean.planosSemIndicacao}"/>
<c:if test="${not empty planos}">
	<table class="listagem" style="width: 100%">
		<caption class="listagem">Planos de Docência Assistida Cadastrados (${fn:length(planos)})</caption>
		<thead>
			<tr>
				<th style="width:1px;"></th>
				<th>Discente</th>
				<th>Programa</th>
				<th>Componente Curricular</th>
				<th>Nível</th>
				<th style="text-align: center;">Ano/Período</th>
				<th>Status</th>
				<th colspan="8"></th>
			</tr>
		</thead>
		<c:forEach items="#{planos}" var="plano" varStatus="loop">	
			<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td>
					<c:if test="${plano.possuiIndicacao }">
						<h:graphicImage value="/img/star.png" title="Possui indicação de Bolsa REUNI"/>
					</c:if>
				</td>
				<td>${plano.discente.matriculaNome}</td>
				<td>${plano.discente.gestoraAcademica.nome}</td>
				<td>${(empty(plano.componenteCurricular) ? '** NÃO INFORMADO **' : plano.componenteCurricular.codigoNome)}</td>
				<td>${plano.discente.nivelDesc}</td>
				<td style="text-align: center;">${plano.ano}.${plano.periodo}</td>
				<td>
					<c:choose>
						<c:when test="${plano.reprovado || not plano.ativo}">
							<span style="color:red; font-weight: bold;">${plano.descricaoStatus}</span>
						</c:when>
						<c:when test="${plano.aprovado}">
							<span style="color:green; font-weight: bold;">${plano.descricaoStatus}</span>
						</c:when>
						<c:when test="${plano.concluido}">
							<span style="color:#4169E1; font-weight: bold;">${plano.descricaoStatus}</span>
						</c:when>
						<c:otherwise>
							<span style="color:black; font-weight: normal;">${plano.descricaoStatus}</span>
						</c:otherwise>						
					</c:choose>		
					<c:if test="${acesso.ppg && plano.modificadoPorPPG}">
					  (PPG)
					</c:if>
				</td>		
				<td width="1px">
					<h:commandLink title="Analisar Plano" action="#{planoDocenciaAssistidaMBean.analisarPlano}"
						rendered="#{plano != null && plano.ativo && (acesso.ppg || 
						(planoDocenciaAssistidaMBean.portalCoordenadorStricto && !plano.concluido && plano.permiteAnalisarPlano))}">
						<h:graphicImage value="/img/seta.gif"/>
						<f:param name="idPlano" value="#{plano.id}"/>
						<f:param name="tipo" value="1"/>
					</h:commandLink>																
				</td>						
				<td width="1px">					
					<h:commandLink title="Analisar Relatório Semestral" action="#{planoDocenciaAssistidaMBean.analisarPlano}"
						rendered="#{plano != null && plano.ativo && (acesso.ppg || 
						(planoDocenciaAssistidaMBean.portalCoordenadorStricto && !plano.concluido && plano.permiteAnalisarRelatorio))}">
						<h:graphicImage value="/img/table_go.png"/>
						<f:param name="idPlano" value="#{plano.id}"/>
						<f:param name="tipo" value="2"/>
					</h:commandLink>					
				</td>					
				<%@include file="include_acoes.jsp"%>
			</tr>					
		</c:forEach>
	</table>
</c:if>	
