<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h2> <ufrn:subSistema/> > Relatório de Bolsistas por Período</h2>

<h:form id="form">
<table align="center" class="formulario" width="70%">
	<caption class="listagem">Dados do Relatório</caption>
	<c:choose>	
		<c:when test="${!acesso.coordenadorCursoGrad && !acesso.secretarioGraduacao}">				
		<c:if test="${nivel == 'S' and relatorioDiscente.portalCoordenadorStricto}">	
			<tr>
				<th class="rotulo" width="20%">Programa: </th>
				<td><h:outputText value="#{relatorioDiscente.programaStricto.sigla}"/> - <h:outputText value="#{relatorioDiscente.programaStricto.nome}"/></td>
			</tr>
		</c:if>		
		
		<c:if test="${nivel == 'S' and relatorioDiscente.portalPpg}">	
			<tr>
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
		<th class="required">Ano-Período:</th>
		<td>
			<h:inputText value="#{relatorioDiscente.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" id="ano" /> - <h:inputText value="#{relatorioDiscente.periodo}" size="2" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" id="periodo" />
		</td>
	</tr>
		
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscente.gerarRelatorioPrazoMaximoBolsasAlunos}"/>
				<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>