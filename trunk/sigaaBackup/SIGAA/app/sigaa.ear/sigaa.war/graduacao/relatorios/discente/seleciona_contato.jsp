<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"/>
<h2><ufrn:subSistema /> > Relatório de Contatos dos Alunos</h2>
<h:form id="form">

<table align="center" class="formulario" width="70%">
	<caption class="listagem">Dados do Relatório</caption>
	<c:choose>	
		<c:when test="${!acesso.coordenadorCursoGrad && !acesso.secretarioGraduacao}">				
		<c:if test="${nivel == 'S' and relatorioDiscente.portalCoordenadorStricto}">	
			<tr>
				<td></td>
				<th>Programa: </th>
				<td><b><h:outputText value="#{relatorioDiscente.programaStricto.sigla}"/> - <h:outputText value="#{relatorioDiscente.programaStricto.nome}"/></b></td>
			</tr>
		</c:if>		
		
		<c:if test="${nivel == 'S' and relatorioDiscente.portalPpg}">	
			<tr>
				<td><h:selectBooleanCheckbox styleClass="noborder" value="#{relatorioDiscente.filtroUnidade}" id="checkPrograma"/></td>
				<td width="10%" nowrap="nowrap"><label for="form:checkPrograma">Programa:</label></td>
				<td><h:selectOneMenu value="#{relatorioDiscente.matrizCurricular.curso.unidade.id}" id="centro"
						onchange=" $('form:checkPrograma').checked = true;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{unidade.allProgramaPosCombo}" />
				</h:selectOneMenu></td>
			</tr>
		</c:if>		
		</c:when>
	</c:choose>
	<tr>
	    <td></td>
		<th width="10%" nowrap="nowrap">Status do Aluno:</th>
		<td>
			<h:selectOneMenu id="status" value="#{relatorioDiscente.status}">
				<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
				<f:selectItems value="#{statusDiscente.allStrictoCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>		
	<tfoot>
	<tr>
		<td colspan="3" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscente.gerarRelatorioContatosAlunos}"/>
			<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatorioDiscente.cancelar}" id="cancelar" />
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>