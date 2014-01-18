<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h2> <ufrn:subSistema/> > Relat�rio de Alunos Reprovados</h2>
<h:form id="form">
<table align="center" class="formulario" width="80%">
	<caption class="listagem">Dados do Relat�rio</caption>
	<c:choose>	
		<c:when test="${!acesso.coordenadorCursoGrad && !acesso.secretarioGraduacao}">				
		<c:if test="${nivel == 'S' and relatorioDiscente.portalCoordenadorStricto}">	
			<tr>
				<td></td>
				<th class="rotulo" width="20%">Programa: </th>
				<td><h:outputText value="#{relatorioDiscente.programaStricto.sigla}"/> - <h:outputText value="#{relatorioDiscente.programaStricto.nome}"/></td>
			</tr>
		</c:if>		
		
		<c:if test="${nivel == 'S' and relatorioDiscente.portalPpg}">	
			<tr>
				<td></td>
				<th width="20%" class="obrigatorio">Programa:</th>
				<td><h:selectOneMenu value="#{relatorioDiscente.matrizCurricular.curso.unidade.id}" id="centro">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{unidade.allProgramaPosCombo}" />
				</h:selectOneMenu></td>
			</tr>
		</c:if>		
		</c:when>
	</c:choose>	
	<tr>
	    <th></th>
		<th class="obrigatorio" width="20%">Ano-Per�odo: </th>
		<td>
			<h:inputText id="ano" value="#{relatorioDiscente.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/> -
			<h:inputText id="periodo" value="#{relatorioDiscente.periodo}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
							<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio"
							action="#{relatorioDiscente.gerarRelatorioAlunosReprovados}"/> <h:commandButton
							value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{confirm}" /></td>
		</tr>
	</tfoot>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>