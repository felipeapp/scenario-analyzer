<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{alteracaoStatusMatricula.create}"/>
	<h2><ufrn:subSistema /> &gt; 
	${(alteracaoStatusMatricula.trancamento)?'Trancamento de Matrículas':'Alteração de Matrículas'}
	</h2>

	<c:set var="discente" value="#{alteracaoStatusMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	<h:form id="form">
	<table class="listagem">
		<caption>Selecione as matrículas
		<c:if test="${alteracaoStatusMatricula.novaSituacao.id > 0}">
		que terão o status alterado para: ${alteracaoStatusMatricula.novaSituacao.descricao}
		</c:if>
		</caption>
		<thead>
		<tr>
			<th width="2%"> </th>
			<th width="5%">  </th>
			<th style="text-align: left;"> Componente </th>
			<th style="text-align: right;"> Turma</th>
			<th style="text-align: left;"> Status</th>
		</tr>
		</thead>
		<tbody>
			<c:forEach var="matricula" items="${alteracaoStatusMatricula.matriculas}" varStatus="status">
			<c:if test="${not matricula.componente.subUnidade}">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> <input type="checkbox" name="matriculas" value="${status.index }" id="mat${status.index}"> </td>
					<td> ${matricula.anoPeriodo } </td>
					<td> <label for="mat${status.index}"> ${matricula.componenteDescricao} ${ matricula.turma.observacao != null ? matricula.turma.observacao : '' }</label> </td>
					<td  style="text-align: right;">${matricula.turma.codigo}</td>
					<td  style="text-align: left;">${matricula.situacaoMatricula.descricao}</td>
				</tr>
			</c:if>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" align="center">
					<h:commandButton value="Alterar Matrículas" action="#{alteracaoStatusMatricula.selecionarMatriculas}" id="selecionar"/>
					<h:commandButton  value="<< Voltar" action="#{alteracaoStatusMatricula.iniciar}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoStatusMatricula.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>