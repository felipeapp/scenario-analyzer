<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h2> <ufrn:subSistema/> > Relatório de Alunos Especiais e Disciplinas </h2>

<h:form id="form">
<table align="center" class="formulario" width="70%">
	<caption class="listagem">Dados do Relatório</caption>
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
				<th width="20%" class="obrigatorio"><b>Programa:</b></th>
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
		<th class="obrigatorio" width="20%"><b>Ano-Período: </b></th>
		<td>
			<h:inputText id="ano" value="#{relatorioDiscente.ano}" onkeyup="return formatarInteiro(this);" size="4" maxlength="4"/> -
			<h:inputText id="periodo" value="#{relatorioDiscente.periodo}" onkeyup="return formatarInteiro(this);" size="1" maxlength="1"/>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
							<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
							action="#{relatorioDiscente.gerarRelatorioDiscentesEspeciais}"/> <h:commandButton
							value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{confirm}" /></td>
		</tr>
	</tfoot>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>