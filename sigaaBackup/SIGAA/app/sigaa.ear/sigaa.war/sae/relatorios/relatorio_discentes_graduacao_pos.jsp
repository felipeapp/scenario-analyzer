<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Relatório de discentes que possuem vínculo simultâneo de Graduação e Pós-Graduação</h2>
	<c:set var="nivelLato" value="${0}"/>
	<c:set var="nivelMestrado" value="${0}"/>
	<c:set var="nivelDoutorado" value="${0}"/>
	<table class="tabelaRelatorioBorda" style="font-size: 0.8em;width: 100%"> 
		<thead>
			<tr> 
				<th></th>
				<th colspan="2" align="center">
					Graduação
				</th>
				<th colspan="3" align="center">
					Pós-Graduação
				</th>
			</tr>
			<tr>
				<th>Nome</th>
				<th>Matrícula</th>
				<th>Curso</th>
				<th>Matrícula</th>
				<th>Curso</th>
				<th>Nível</th>
			</tr>
		</thead>
		<tbody>	
			<c:forEach var="item" items="#{relatoriosSaeMBean.discentesGraducaoPos}" varStatus="status">
				<tr style="${status.index % 2 == 0 ? 'background-color:#FFFFFF;':'background-color:#DDDDDD;'};">
					<td><h:outputText value="#{item.nome}"/></td>
					<td><h:outputText value="#{item.matricula}"/></td>
					<td><h:outputText value="#{item.nome_curso_gra}"/></td>
					<td><h:outputText value="#{item.matricula_pos}"/></td>
					<td><h:outputText value="#{item.nome_curso_pos}"/></td>
					<td><h:outputText value="#{item.nivel_pos}"/></td>
					
					<c:choose>
						<c:when test="${item.nivel_pos == 'LATO'}">
							<c:set var="nivelLato" value="${nivelLato + 1}"/>
						</c:when>
						<c:when test="${item.nivel_pos == 'MESTRADO'}">
							<c:set var="nivelMestrado" value="${nivelMestrado + 1}"/>
						</c:when>
						<c:when test="${item.nivel_pos == 'DOUTORADO'}">
							<c:set var="nivelDoutorado" value="${nivelDoutorado + 1}"/>
						</c:when>
					</c:choose>
				</tr>
			
			</c:forEach>
		</tbody>
		<tr style="font-weight:bold;"> 
			<td colspan="5">
				Total de Discentes de Graduação e Lato:
			</td>
			<td align="right">
				${nivelLato}
			</td>
		</tr>
		
		<tr style="font-weight:bold;"> 
			<td colspan="5">
				Total de Discentes de Graduação e Mestrado:
			</td>
			<td align="right">
				${nivelMestrado}
			</td>
		</tr>
		
		<tr style="font-weight:bold;"> 
			<td colspan="5">
				Total de Discentes do de Graduação e Doutorado:
			</td>
			<td align="right">
				${nivelDoutorado}
			</td>
		</tr>
		
		<tr style="font-weight:bold;"> 
			<td colspan="5">
				Total de Discentes:
			</td>
			<td align="right">
				${ fn:length(relatoriosSaeMBean.discentesGraducaoPos) }
			</td>
		</tr>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>