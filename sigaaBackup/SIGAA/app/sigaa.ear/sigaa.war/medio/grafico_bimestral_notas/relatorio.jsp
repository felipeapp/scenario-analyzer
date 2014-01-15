<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf" %>

<style>
	.titulo {
		font-weight: bold;
		border: 1px solid;
	}	
</style>

<f:view>
	<h2> Relatório Gráfico Bimestral Notas</h2>
	<div style="font-weight:bold;">
		${graficoBimestralNotas.obj.serie.nomeCurso } - 
		${graficoBimestralNotas.obj.serie.numeroSerieOrdinal} -
		Turma ${graficoBimestralNotas.obj.nome}
	</div>
	
	<div align="center">
		<table style="width: 80%;" >
			<tbody>
				<c:set var="idRegraAtual" value=""/>
				<c:set var="contador" value="${0}"/>
				<c:set var="mediaBimestre" value="${0}"/>
				<c:set var="maiores" value="${0}"/>
				<c:set var="menores" value="${0}"/>
				<c:set var="iguais" value="${0}"/>
				<jsp:useBean id="dados" class="br.ufrn.sigaa.ensino.medio.jsf.GraficoBimestralNotas" scope="page" />
				
				<c:forEach var="media" items="#{graficoBimestralNotas.mediaDiscenteBimestre}" varStatus="status">	
					<c:if test="${status.first}">
						<c:set var="idRegraAtual" value="${media.regra.id}"/>
					</c:if>
					<!-- Caso mude de a regra, imprimir a tabela  -->
					<c:if test="${idRegraAtual != media.regra.id}">
						<%@include file="dados_grafico.jsp"%>  
						
						<c:set var="idRegraAtual" value="${media.regra.id}"/>
						<c:set var="contador" value="${0}"/>
						<c:set var="mediaBimestre" value="${0}"/>
						<c:set var="maiores" value="${0}"/>
						<c:set var="menores" value="${0}"/>
						<c:set var="iguais" value="${0}"/>
					</c:if>
					
					<c:set var="contador" value="${contador+1}"/>
					<c:set var="mediaBimestre" value="${mediaBimestre+media.media}"/>
					<!-- Escolhe qual grupo o discente pertence -->
					<c:choose>
						<c:when test="${media.media >= graficoBimestralNotas.param.mediaMinimaPassarPorMedia }">
							<c:set var="maiores" value="${maiores+1}"/>	
						</c:when>
						<c:when test="${media.media <= graficoBimestralNotas.param.mediaMinimaAprovacao}">
							<c:set var="menores" value="${menores+1}"/>	
						</c:when>
						<c:when test="${media.media > graficoBimestralNotas.param.mediaMinimaAprovacao 
										&&	media.media < graficoBimestralNotas.param.mediaMinimaPassarPorMedia }">
							<c:set var="iguais" value="${iguais+1}"/>	
						</c:when>
					</c:choose>
				</c:forEach>
					<%@include file="dados_grafico.jsp"%>
				<tr><td style="height:20px;"></td></tr>
			</tbody>
		</table>
	</div>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>