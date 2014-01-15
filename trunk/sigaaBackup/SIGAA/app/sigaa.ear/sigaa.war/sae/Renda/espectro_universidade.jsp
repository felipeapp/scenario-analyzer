<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	
	<h2>Relatório de Espectro de Renda com Dados da Matrícula de Alunos de Graduação</h2>
	
	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Nível de Ensino:</th>
				<td> Graduação </td>
			</tr>
		</table>
	</div>
	<br>	
	<c:set var="colunas" value="1"/>
	<c:set var="totalAlunos" value="0" />
	
	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
			<c:if test="${not empty rendaEspectro.classeEconomica}">
				<tr>
					<th></th>
					<c:forEach items="#{rendaEspectro.classeEconomica}" var="classe">
						<th style="text-align: right;">${ classe.descricao }</th>
						<c:set var="colunas" value="${colunas + 1}"/>
					</c:forEach>
				</tr>
			</c:if>
		</thead>
		<tbody>
			<c:if test="${not empty rendaEspectro.classeEconomica}">
				<tr>
					<td><strong>Número de Alunos</strong></td>
					<c:forEach items="#{rendaEspectro.classeEconomica}" var="classe">
						<td align="right">${ classe.valorBruto }</td>
						<c:set var="totalAlunos" value="${ totalAlunos + classe.valorBruto }" />
					</c:forEach>
				</tr>
				<tr>	
					<td><strong>Percentual</strong></td>					
					<c:forEach items="#{rendaEspectro.classeEconomica}" var="classe">
						<td align="right">(${classe.porcentagemFormatada}%)</td>
					</c:forEach>
				</tr>
				<tr>
					<th colspan="${colunas}">Total: ${totalAlunos} Alunos</th>
				</tr>				
			</c:if>
		</tbody>
	</table>
	<br><br>
	<p><b>LEGENDAS:</b></p>
	<br><b>Classe Alta:</b> Discentes com renda familiar superior a 30 vezes o salário mínimo.
	<br><b>Classe Média Alta:</b> Discentes com renda familiar inferior a 30 vezes o salário mínimo e superior a 15 vezes o salário mínimo.
	<br><b>Classe Média Baixa:</b> Discentes com renda familiar inferior a 15 vezes o salário mínimo e superior a 5 vezes o salário mínimo.
	<br><b>Classe Baixa:</b> Discentes com renda familiar inferior a 5 vezes o salário mínimo.
	<br><br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>