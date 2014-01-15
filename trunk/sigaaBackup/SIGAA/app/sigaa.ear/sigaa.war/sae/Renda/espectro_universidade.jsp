<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	
	<h2>Relat�rio de Espectro de Renda com Dados da Matr�cula de Alunos de Gradua��o</h2>
	
	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>N�vel de Ensino:</th>
				<td> Gradua��o </td>
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
					<td><strong>N�mero de Alunos</strong></td>
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
	<br><b>Classe Alta:</b> Discentes com renda familiar superior a 30 vezes o sal�rio m�nimo.
	<br><b>Classe M�dia Alta:</b> Discentes com renda familiar inferior a 30 vezes o sal�rio m�nimo e superior a 15 vezes o sal�rio m�nimo.
	<br><b>Classe M�dia Baixa:</b> Discentes com renda familiar inferior a 15 vezes o sal�rio m�nimo e superior a 5 vezes o sal�rio m�nimo.
	<br><b>Classe Baixa:</b> Discentes com renda familiar inferior a 5 vezes o sal�rio m�nimo.
	<br><br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>