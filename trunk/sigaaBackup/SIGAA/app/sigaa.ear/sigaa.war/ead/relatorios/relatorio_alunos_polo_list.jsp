<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.header td {padding: 0px ; background-color: #DCDCDC;   font-weight: bold;}
	tr.componente td {padding: 0px 0px 0px; border-bottom: 1px dashed #888;}
	tr.espaco td {padding: 15px; height: 15px;}
</style>
<f:view>
	<h2>Relatório de Alunos por Polo</h2>
	<h:outputText value="#{relatorioAlunosPolo.create}" />

	<div>
		<table>
			<tr>
				<td><b>Polo: </b>  
				<c:if test="${ not empty relatorioAlunosPolo.polo }">${ relatorioAlunosPolo.polo.descricao }</c:if>
				<c:if test="${ empty relatorioAlunosPolo.polo || relatorioAlunosPolo.polo.id == 0 }">Todos</c:if>
				</td>
			</tr>
			<tr>
				<td><b>Curso: </b> 
				<c:if test="${ not empty relatorioAlunosPolo.curso }">${ relatorioAlunosPolo.curso.nome }</c:if>
				<c:if test="${ empty relatorioAlunosPolo.curso || relatorioAlunosPolo.curso.id == 0 }">Todos</c:if>
				</td>
			</tr>
			<tr>
				<td><b>Ano de Ingresso: </b>  ${(relatorioAlunosPolo.ano > 0 ? relatorioAlunosPolo.ano : 'Todos') }</td>
			</tr>
			<c:if test="${ relatorioAlunosPolo.periodo > 0 }">
			<tr>
				<td><b>Período de Ingresso: </b>${ relatorioAlunosPolo.periodo }</td>
			</tr>
			</c:if>
			<tr>
			<td><b>Apenas Matrículados: </b>
			<c:if test="${ relatorioAlunosPolo.matriculados }">Sim</c:if>
			<c:if test="${ !relatorioAlunosPolo.matriculados }">Não</c:if>
			</td>
		</tr>
		</table>
	</div>
	
	<br />
	
	<c:set var="_polo" />
	<c:set var="_curso" />
	<table class="tabelaRelatorioBorda" cellspacing="1" width="100%" style="font-size: 11px;" align="center">
	  <c:forEach items="#{relatorioAlunosPolo.discentes}" var="d" varStatus="indice">
		<c:set var="poloAtual" value="${d.polo.descricao}"/>
		<c:set var="cursoAtual" value="${d.curso.descricao}"/>
			
			<c:if test="${_polo != poloAtual}">
				<tr style="border-right : hidden;border-left : hidden;border-bottom: hidden"class="espaco">
					<td colspan="4"></td>
				</tr>
				<tr style="border-right : hidden;border-left : hidden;">
					<td colspan="4"><b>Pólo:</b> ${poloAtual}</td>
				</tr>
			
			<c:set var="_polo" value="${poloAtual}"/>
			</c:if>
				<c:if test="${_curso != cursoAtual}">
					<c:if test="${not indice.first}">
						<tr class="espaco">
							<td colspan="4"></td>
						</tr>
					</c:if>
				
				<tr class="header">
					<td colspan="4">${d.curso.descricao}</td>
				</tr>
				<tr class="header">
					<td align="right">Matrícula</td>
					<td>Discente</td>
					<td align="center">CPF</td>
					<td align="center">Data de Nascimento</td>
				</tr>
			
			<c:set var="_curso" value="${cursoAtual}"/>
			</c:if>

				<tr class="componente">
					<td align="right">${d.matricula }</td>	
					<td>${d.pessoa.nome}</td>
					<td align="center"><ufrn:format valor="${d.pessoa.cpf_cnpj}" type="cpf_cnpj" /></td>
					<td align="center"><ufrn:format valor="${d.pessoa.dataNascimento}" type="data" /></td>
				</tr>				
		</c:forEach>
	</table>
	
	<br />
	
	<table cellspacing="1" width="100%" style="font-size: 11px;" align="center">
		<tr align="center">
			<td><b>Total de Discentes Encontrados:</b> ${ fn:length(relatorioAlunosPolo.discentes) }<br/></td>
		</tr>
	</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>