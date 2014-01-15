<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<h:outputText value="#{relatoriosMedio.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; Lista de Alunos Matriculados</h2>

<h:form id="form">
<table align="center" class="formulario" width="50%">
  <caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th width="50%" class="obrigatorio">Unidade:</th>
		<c:if test="${!relatoriosMedio.proplan}">
			<td><b>${usuario.unidade.nome}</b></td>
		</c:if>	
		<c:if test="${relatoriosMedio.proplan}">
			<td>
				<h:selectOneMenu value="#{relatoriosMedio.unidade.id}" style="width: 70%">
					<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
			 		<f:selectItems value="#{relatoriosMedio.unidades}" /> 
				</h:selectOneMenu>
			</td>
		</c:if>	
	</tr>
	<tr>
		<th class="obrigatorio">Ano: </th>
		<td><h:inputText id="ano" title="Ano" value="#{relatoriosMedio.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/></td>
	</tr>
	<tfoot>	
		<tr>
			<td colspan="2" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosMedio.gerarRelatorioListaAlunosMatriculados}"/> 
				<h:commandButton value="Cancelar" action="#{relatoriosMedio.cancelar}" id="cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>
<br />
<div class="obrigatorio" style="width: 90%">Campos de preenchimento obrigatório.</div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>