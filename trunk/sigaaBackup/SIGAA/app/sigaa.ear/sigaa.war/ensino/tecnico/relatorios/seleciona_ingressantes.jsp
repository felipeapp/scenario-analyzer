<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatoriosTecnico.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; Lista de Alunos Ingressantes</h2>

<h:form id="form">
<table align="center" class="formulario" width="70%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Curso:</th>
		<td>
			<h:selectOneMenu id="curso" value="#{relatoriosTecnico.idCurso}">
				<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
				<f:selectItems value="#{relatoriosTecnico.cursosCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th>Ano-período de ingresso:</th>
		<td>
			<h:inputText value="#{relatoriosTecnico.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>-
			<h:inputText value="#{relatoriosTecnico.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this)"/>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosTecnico.gerarRelatorioAlunosIngressantes}"/> 
				<h:commandButton value="Cancelar" action="#{relatoriosTecnico.cancelar}" id="cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>