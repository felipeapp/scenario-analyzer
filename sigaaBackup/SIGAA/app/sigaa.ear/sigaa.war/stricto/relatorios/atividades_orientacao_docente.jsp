<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>

<h2>Relatório de Atividades de Docente na Pós-Graduação</h2>
<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Docente:</th>
			<td><h:outputText value="#{relatorioAtividadesDocente.obj.nome}"/></td>
		</tr>
		<tr>
			<th>Ano:</th>
			<td><h:outputText value="#{relatorioAtividadesDocente.ano}"/></td>
		</tr>
	</table>
</div>
</br>

<c:if test="${empty relatorioAtividadesDocente.turmasDocente}">
	<table class="tabelaRelatorio" style="width: 100%">
		<caption>Não há Turmas Ministradas pelo Docente no Ano Informado</caption>
	</table>
</c:if>
<c:if test="${not empty relatorioAtividadesDocente.turmasDocente}">
	<table class="tabelaRelatorio" style="width: 100%">
		<caption>Turmas Ministradas</caption>
		<thead>
			<tr>
				<th style="text-align: center;">Ano-Período</th>
				<th>Código</th>
				<th>Componente</th>
				<th style="text-align: center;">Turma</th>
				<th style="text-align: center;">Data de Início</th>
				<th style="text-align: center;">Data de Finalização</th>
				<th>Situação</th>
				<th style="text-align: right;">Nº de Discentes</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{relatorioAtividadesDocente.turmasDocente}" var="item">
				<tr>
					<td style="text-align: center;">${item.anoPeriodo}</td>
					<td style="text-align: center; vertical-align: top">${item.disciplina.codigo}</td>
					<td>${item.disciplina.nome}</td>
					<td style="text-align: center;">${item.codigo}</td>
					<td style="text-align: center;"><h:outputText value="#{item.dataInicio}"/></td>
					<td style="text-align: center;"><h:outputText value="#{item.dataFim}"/></td>
					<td>${item.situacaoTurma.descricao}</td>
					<td style="text-align: right;">${item.totalMatriculados}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>
<br/>
<c:if test="${empty relatorioAtividadesDocente.orientacoesDocente}">
	<table class="tabelaRelatorio" style="width: 100%">
		<caption>Não há Orientações Acadêmicas do Docente no Ano Informado</caption>
	</table>
</c:if>
<c:if test="${!empty relatorioAtividadesDocente.orientacoesDocente}">
	<table class="tabelaRelatorio" style="width: 100%; border-bottom: 0px;">
		<caption>Orientações Acadêmicas</caption>
	</table>
</c:if>
<c:if test="${not empty relatorioAtividadesDocente.orientacoesDocente}">
		<c:set var="varPrograma" value=""/>	    
		<c:forEach items="#{relatorioAtividadesDocente.orientacoesDocente}" var="item">
			<c:if test="${varPrograma != item.discente.curso.unidade.nome}">
				<c:if test='${varPrograma != ""}'>			
					</table>   		
				</c:if>			 
				<c:set var="varPrograma" value="${item.discente.curso.unidade.nome}"/>
				<br/>
				<table class="tabelaRelatorio" width="100%">
				    <caption id="programa">${varPrograma}</caption>
					<thead>
						<tr>
							<th style="text-align: center;">Matrícula</th>
							<th>Discente</th>
							<th>Nível</th>
							<th style="text-align: center;">Início</th>
							<th style="text-align: center;">Fim</th>
							<th>Tipo</th>
						</tr>
					</thead>		
			</c:if> 			
			
			<tr>
				<td  style="text-align: center; vertical-align: top;">${item.discente.matricula}</td>
				<td>${item.discente.nome}</td>
				<td>${item.discente.nivelDesc}</td>
				<td style="text-align: center;"><h:outputText value="#{item.inicio}"/></td>
				<td style="text-align: center;"><h:outputText value="#{item.dataFinalizacao}"/></td>
				<td>${item.tipoOrientacao == 'O' ? 'Orientação' : 'Co-Orientação'}</td>
			</tr>
		</c:forEach>
		</table>
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>