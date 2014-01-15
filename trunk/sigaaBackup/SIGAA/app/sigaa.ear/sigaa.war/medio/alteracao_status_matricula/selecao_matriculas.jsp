<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="alteracaoStatusMatriculaMedioMBean"/>
<f:view>
	<h2><ufrn:subSistema /> &gt; 
	Alterar Status de Matrícula por Disciplina
	</h2>

	<c:set var="discente" value="#{alteracaoStatusMatriculaMedioMBean.discente}"/>

	<h:form id="form">
	<table class="listagem">
		<caption>Selecione as matrículas que terão o status alterado: 
		</caption>
		<thead>
		<tr>
			<th width="2%"> </th>
			<th width="5%"> Ano </th>
			<th style="text-align: left;"> Componente </th>
			<th width="7%" style="text-align: center"> Turma</th>
			<th style="text-align: left;"> Status</th>
		</tr>
		</thead>
		<tbody>
			<c:set var="idSerie" value="0" />		
			<c:forEach var="matricula" items="${alteracaoStatusMatriculaMedioMBean.matriculasComponente}" varStatus="status">
				<c:if test="${idSerie != matricula.serie.id}">
					<tr><td colspan="5" style="background-color: #C8D5EC;font-weight:bold;">${matricula.serie.numero}ª - ${matricula.serie.descricao}</td></tr>
				</c:if>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> <input type="checkbox" name="matriculas" value="${status.index }" id="mat${status.index}"> </td>
					<td> ${matricula.anoPeriodo } </td>
					<td> <label for="mat${status.index}"> ${matricula.componenteDescricao} ${ matricula.turma.observacao != null ? matricula.turma.observacao : '' }</label> </td>
					<td  style="text-align: center">${matricula.turma.codigo}</td>
					<td  style="text-align: left;">${matricula.situacaoMatricula.descricao}</td>
				</tr>
				<c:set var="idSerie" value="${matricula.serie.id}" />		
				
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" align="center">
					<h:commandButton value="Alterar Matrículas" action="#{alteracaoStatusMatriculaMedioMBean.selecionarMatriculas}" id="selecionar"/>
					<h:commandButton value="<< Voltar" action="#{alteracaoStatusMatriculaMedioMBean.telaBuscaDiscentes}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoStatusMatriculaMedioMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>