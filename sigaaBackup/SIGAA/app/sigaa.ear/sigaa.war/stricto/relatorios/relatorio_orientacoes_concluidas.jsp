<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>

<h2>Relatório de Orientações Concluídas por Docente</h2>

<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Ano:</th>
			<td><h:outputText value="#{relatorioDocentesOrientacoes.ano}"/></td>
		</tr>
		<tr>
			<th>Programa:</th>
			<td><h:outputText value="#{relatorioDocentesOrientacoes.unidade.nome}"/></td>
		</tr>
	</table>
</div>
</br>
<c:set var="_unidadeCorrente" value="#{relatorioDocentesOrientacoes.unidade.id}"/>
<c:set var="_docenteCorrente" value="0"/>
<c:set var="_unidadeLoop" value="0"/>
<c:set var="_docenteLoop" value="0"/>
<c:set var="fechaTabela" value="false"/>
<c:set var="totalDocentes" value="0" />
<c:set var="totalTeses" value="0" />

<c:forEach items="#{relatorioDocentesOrientacoes.dadosRelatorio}" var="item">
	<c:set var="_unidadeLoop" value="${item.id_unidade}"/>
	<c:set var="_docenteLoop" value="${item.id_servidor}"/>
	<c:if test="${_unidadeCorrente != _unidadeLoop}">
		<c:if test="${fechaTabela}">
				</tbody>
			</table>
			<br/>
			<table class="tabelaRelatorio" style="width: 100%">
				<tfoot>
					<tr>
						<td colspan="3" align="right">Total de Docentes do Programa:</td>
						<td colspan="3" align="right">${totalDocentes}</td>
					</tr>
					<tr>
						<td colspan="3" align="right">Total de Trabalhos Orientados no Programa:</td>
						<td colspan="3" align="right">${totalTeses}</td>
					</tr>
				</tfoot>
			</table>
			<br/>
			<c:set var="totalDocentes" value="0" />
			<c:set var="totalTeses" value="0" />
		</c:if>
		<h4>Programa: ${item.nome_unidade}</h4>
		<c:if test="${not fechaTabela}">
			<br/>
		</c:if>
		<c:set var="_unidadeCorrente" value="${item.id_unidade}"/>
	</c:if>
	<c:if test="${_docenteCorrente != _docenteLoop}">
		<c:if test="${fechaTabela}">
				</tbody>
			</table>
			<br/>
		</c:if>
		<c:set var="fechaTabela" value = "true"/>
		<table class="tabelaRelatorioBorda" style="width: 100%">
			<caption>Docente: ${item.siape} - ${item.nome_docente}</caption>
			<thead>
				<tr>
					<th width="30%">Orientando</th>
					<th>Título</th>
					<th>Tipo</th>
					<th style="text-align: center;">Data</th>
				</tr>
			</thead>
			<tbody>
		<c:set var="_docenteCorrente" value="${item.id_servidor}"/>
		<c:set var="totalDocentes" value="${totalDocentes + 1}" />
	</c:if>
	<tr>
		<td>${item.orientando}</td>
		<td>${item.titulo}</td>
		<td>${item.tipo_orientacao}</td>
		<td style="text-align: center;"><ufrn:format type="data" valor="${item.data_publicacao}"/></td>
	</tr>
	<c:set var="totalTeses" value="${totalTeses + 1}" />
</c:forEach>
<c:if test="${fechaTabela}">
		</tbody>
	</table>
	<br/>
	<table class="tabelaRelatorio" style="width: 100%">
		<tfoot>
			<tr>
				<td colspan="3" align="right">Total de Docentes do Programa:</td>
				<td colspan="3" align="right">${totalDocentes}</td>
			</tr>
			<tr>
				<td colspan="3" align="right">Total de Trabalhos Orientados no Programa:</td>
				<td colspan="3" align="right">${totalTeses}</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	<c:set var="totalDocentes" value="0" />
	<c:set var="totalTeses" value="0" />
</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>