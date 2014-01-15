<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>
	<h2>Lista de Fiscais Não Alocados</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Processo Seletivo:</th>
		<td>${relatoriosVestibular.obj.nome}</td>
	</tr>
</table>
</div>
<br/>
<c:set var="_categoriaGrupo" />
<c:set var="fechaTabela" value ="false" />

<c:forEach items="${lista}" var="item" >
	<c:choose>
		<c:when test="${item.perfilDiscente}">
			<c:set var="_categoriaAtual" value="DISCENTE" />
			<c:set var="cursoUnidade" value="Curso" />
			<c:set var="matricula" value="Matrícula" />
		</c:when>
		<c:when test="${item.perfilServidor}">
			<c:set var="_categoriaAtual" value="SERVIDOR" />
			<c:set var="cursoUnidade" value="Unidade" />
			<c:set var="matricula" value="SIAPE" />
		</c:when>
		<c:otherwise>
			<c:set var="_categoriaAtual" value="INDETERMINADO" />
			<c:set var="cursoUnidade" value="" />
		</c:otherwise>
	</c:choose>
	<c:if test="${_categoriaGrupo != _categoriaAtual}">
	<c:set var="_categoriaGrupo" value="${_categoriaAtual}"/>
		<c:if test="${fechaTabela}">
			</tbody>
			</table>
			<br/>
			<c:set var="fechaTabela" value ="false" />
		</c:if>	
		<c:set var="fechaTabela" value ="true" />
		<table class="tabelaRelatorioBorda" width="100%">
			<caption>Perfil do Fiscal: ${_categoriaGrupo}</caption>
			<thead>
				<tr>
					<th width="5%" style="text-align: right;">${matricula}</th>
					<th width="30%">Nome</th>
					<th width="30%">${cursoUnidade}</th>
					<th width="5%">(Re)Cadastro</th>
					<th width="30%">Local de Preferência</th>
				</tr>
			</thead>
			<tbody>
	</c:if>
	<tr>
		<td style="text-align: right;">${item.servidor.siape}${item.discente.matricula}</td>
		<td>${item.pessoa.nome}</td>
		<td>${item.servidor.unidade}${item.discente.curso}</td>
		<td>
			<c:choose>
				<c:when test="${item.recadastro}">RECADASTRO</c:when>
				<c:otherwise>CADASTRO</c:otherwise>
			</c:choose>
		</td>
		<td>
			${item.inscricaoFiscal.localAplicacaoProvas[0].nome}
		</td>
	</tr>
</c:forEach>
<c:if test="${fechaTabela}">
	</tbody>
	</table>
	<c:set var="fechaTabela" value ="false" />
</c:if>	
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
