<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Lista de Email dos Alunos Concluídos</h2>

<h:form id="form">
<table align="center" class="formulario" width="80%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th style="width: 40%;" class="obrigatorio">Ano-Período de Conclusão: </th>
		<td>
			<h:inputText value="#{relatorioDiscente.ano}" id="ano" size="4" maxlength="4" 
			onkeyup="return formatarInteiro(this);"/>
			-			
			<h:inputText value="#{relatorioDiscente.periodo}" id="periodo" size="2" maxlength="1" 
			onkeyup="return formatarInteiro(this);"/>
		</td>
	</tr>
	<tr>
		<th>Curso: </th>
		<td style="font-weight: bold;">
			<h:outputText value="#{relatorioDiscente.cursoAtualCoordenacao.descricao}"/>
		</td>

	</tr>
	<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
			action="#{relatorioDiscente.relatorioEmailDosAlunosConcluidos}"/>
			<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" />
		</td>
	</tr>
	</tfoot>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>