<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="alteracaoStatusMatriculaMedioMBean"/>
<f:view>
	<h2><ufrn:subSistema /> &gt; 
	Alterar Status de Matrícula por Disciplina
	</h2>

	<c:set var="discente" value="#{alteracaoStatusMatriculaMedioMBean.discente}"/>
	
		<table class="visualizacao" style="width: 90%">
		<tr>
			<td width="14%"></td>
			<th width="3%" style="text-align: right;">Matrícula:</th>
			<td style="text-align: left;">${discente.matricula }</td>
		</tr>
		<tr>
			<td></td>
			<th style="text-align: right;"> Discente: </th>
			<td style="text-align: left;"> ${discente.pessoa.nome } </td>
		</tr>
		<c:if test="${not discente.stricto}">
			<tr>
				<td></td>
				<th style="text-align: right;">Curso: </th>
				<td style="text-align: left;">			
				<c:if test="${discente.graduacao}">  ${discente.matrizCurricular.descricao} </c:if>
				<c:if test="${not discente.graduacao}">  ${discente.curso.descricao } </c:if>
				</td>
			</tr>
		</c:if>
		<c:if test="${discente.stricto}">
			<tr>
				<td></td>
				<th style="text-align: right;"> Programa: </th>
				<td style="text-align: left;"> ${discente.gestoraAcademica.nome } </td>
			</tr>
			<c:if test="${discente.regular}">
				<tr>
					<td></td>
					<th style="text-align: right;"> Curso: </th>
					<td style="text-align: left;"> ${discente.curso.nomeCursoStricto } </td>
				</tr>
			</c:if>
		</c:if>
		<tr>
			<td></td>
			<th style="text-align: right;"> Status: </th>
			<td style="text-align: left;"> ${discente.statusString } </td>
		</tr>
		<tr>
			<td></td>
			<th style="text-align: right;">Tipo:</th>
			<td style="text-align: left;"> ${discente.tipoString } </td>
		</tr>
	</table>
	<br />
	
	<h:form prependId="false">
	<table class="formulario" style="width: 80%">
		<caption> Confira os dados para efetuar a alteração do status da matrícula </caption>
		<tbody>
			<tr>
				<th width="45%"> Novo Status: </th>
				<td>
					<h:selectOneMenu value="#{ alteracaoStatusMatriculaMedioMBean.novaSituacao.id }" id="situacao">
						<f:selectItems value="#{ alteracaoStatusMatriculaMedioMBean.situacoesMatricula }"/>
					</h:selectOneMenu>
					<ufrn:help img="/img/ajuda.gif">Todas as matrículas escolhidas serão alteradas para esse status</ufrn:help>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Matrículas escolhidas</caption>
					<thead>
						<tr>
							<td>Componente Curricular</td>
							<td width="15%">Status Atual</td>
						</tr>
					</thead>
					<c:forEach items="${alteracaoStatusMatriculaMedioMBean.matriculasComponenteEscolhidas}" var="mat" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>${mat.componenteDescricao}</td>
						<td>${mat.situacaoMatricula.descricao }</td>
					</tr>
					</c:forEach>
				</table>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" action="#{alteracaoStatusMatriculaMedioMBean.efetuarAlteracaoStatusMatriculas}" id="efetuar"/>
					<h:commandButton value="Selecionar Outras Matrículas" action="#{alteracaoStatusMatriculaMedioMBean.telaSelecaoMatriculas}" id="outra"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoStatusMatriculaMedioMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>