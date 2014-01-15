<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>

<h2>Relat�rio de Atividades de Docente na P�s-Gradua��o</h2>
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
		<caption>N�o h� Turmas Ministradas pelo Docente no Ano Informado</caption>
	</table>
</c:if>
<c:if test="${not empty relatorioAtividadesDocente.turmasDocente}">
	<table class="tabelaRelatorio" style="width: 100%">
		<caption>Turmas Ministradas</caption>
		<thead>
			<tr>
				<th style="text-align: center;">Ano-Per�odo</th>
				<th>C�digo</th>
				<th>Componente</th>
				<th style="text-align: center;">Turma</th>
				<th style="text-align: center;">Data de In�cio</th>
				<th style="text-align: center;">Data de Finaliza��o</th>
				<th>Situa��o</th>
				<th style="text-align: right;">N� de Discentes</th>
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
		<caption>N�o h� Orienta��es Acad�micas do Docente no Ano Informado</caption>
	</table>
</c:if>
<c:if test="${!empty relatorioAtividadesDocente.orientacoesDocente}">
	<table class="tabelaRelatorio" style="width: 100%; border-bottom: 0px;">
		<caption>Orienta��es Acad�micas</caption>
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
							<th style="text-align: center;">Matr�cula</th>
							<th>Discente</th>
							<th>N�vel</th>
							<th style="text-align: center;">In�cio</th>
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
				<td>${item.tipoOrientacao == 'O' ? 'Orienta��o' : 'Co-Orienta��o'}</td>
			</tr>
		</c:forEach>
		</table>
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>