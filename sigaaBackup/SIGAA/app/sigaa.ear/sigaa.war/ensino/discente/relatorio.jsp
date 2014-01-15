<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>	
	table.listagem tr.destaque td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
<h2>Consulta Geral de Discentes</h2>

<strong>Critérios de busca utilizados:</strong><br/><br/>

<c:forEach items="${ buscaAvancadaDiscenteMBean.mapRestricoes }" var="mr">
	<b>${mr.key} ${ mr.value != '' ? ':' : ' ' }</b> ${ mr.value } <br/>
</c:forEach>

<br/>

<c:set value="#{buscaAvancadaDiscenteMBean.discentesEncontrados}" var="resultado" />
		<h3 class="tituloTabela">Discentes Encontrados (${ fn:length(resultado) })</h3>
		<table id="lista-turmas" class="listagem">
	
			<c:if test="${empty resultado}">
				<tr><td>Nenhum discente foi encontrado com os critérios utilizados</td></tr>
			</c:if>
			<c:if test="${not empty resultado}">
			<thead>
			<tr style="font-style: italic;">
					<th style="text-align: center;">Matrícula</th>
					<th>Nome</th>
					<th>Curso</th>
					<th>Status</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${resultado}" var="d" varStatus="s">
				<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td align="center">${d.matricula}</td>
					<td>${d.nome}</td>
					<td>${d.curso.descricao}</td>
					<td>${d.statusString}</td>
				</tr>
			</c:forEach>
			</tbody>
			</c:if>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
