<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>

<h2>Lista de ${registroDiplomaColetivo.obj.livroRegistroDiploma.tipoRegistroDescricao}s Registrados</h2> 
<div id="parametrosRelatorio">
	<table>
		<tr>
			<th width="25%"> Curso: </th>
			<td>
				<h:outputText value="#{registroDiplomaColetivo.obj.curso.descricao}"/>
			</td>
		</tr>
		<tr>
			<th width="25%"> Livro: </th>
			<td>
				<h:outputText value="#{registroDiplomaColetivo.obj.livroRegistroDiploma.titulo}"/>
			</td>
		</tr>
		<tr>
			<th> Processo: </th>
			<td>
				<h:outputText value="#{registroDiplomaColetivo.obj.processo}"/>
			</td>
		</tr>
		<tr>
			<th>
				<c:choose>
					<c:when test="${acesso.graduacao}">Data da Colação:</c:when>
					<c:otherwise>Data de Conclusão:</c:otherwise> 
				</c:choose>
			</th>
			<td>
				<fmt:formatDate value="${registroDiplomaColetivo.obj.dataColacao}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>
		<tr>
			<th> Data do Registro: </th>
			<td >
				<fmt:formatDate value="${registroDiplomaColetivo.obj.dataRegistro}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>
		<tr>
			<th> Data de Expedição: </th>
			<td >
				<fmt:formatDate value="${registroDiplomaColetivo.obj.dataExpedicao}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>
	</table>
</div>
<br>
<table class="listagem"	style="font-size: 10px">
	<thead>	
		<tr>
			<th style="text-align: center;">Matricula</th>
			<th>Nome</th>
			<th style="text-align: right;">Folha</th>
			<th style="text-align: right;">Nº do<br>Registro</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="#{registroDiplomaColetivo.obj.registrosDiplomas}" var="item" varStatus="status">
			<c:if test="${item.ativo}">
			<tr valign="top">
				<td style="text-align: center;">${item.discente.matricula}</td>
				<td>${item.discente.nome}</td>
				<td style="text-align: right;">${item.folha.numeroFolha}</td>
				<td style="text-align: right;">${item.numeroRegistro}</td>
			</tr>
			</c:if>
		</c:forEach>
	</tbody>
</table>

<br /><br /><br /><br />
<div align="center">
<c:if test="${acesso.graduacao}">
	<p class="assinatura">____________________________________________________<br>Diretor(a) da DRED</p><br><br>
	<p class="assinatura">____________________________________________________<br>Diretor(a) do ${ configSistema['siglaUnidadeGestoraGraduacao'] }</p><br><br>
</c:if>
<c:if test="${acesso.lato || acesso.stricto}">
	<p class="assinatura">____________________________________________________<br>Diretor(a) da ${ configSistema['siglaUnidadeGestoraPosGraduacao'] }</p><br><br>
</c:if>

</div>
<br/>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>