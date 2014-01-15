<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > LISTA DE ALUNOS VINCULADOS A UM ESTRUTURA CURRICULAR </h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	
	<tr>
		<td width="17%">
			<h:selectBooleanCheckbox value="#{relatorioDiscente.filtroCurso}" style="border: none;" id="checkCurso" />
			<label for="form:checkCurso">Curso:</label>
		</td>
		<td>
			<h:selectOneMenu value="#{relatorioDiscente.matrizCurricular.curso.id}" id="curso" onfocus="$('form:checkCurso').checked = true;"
				onchange="submit()" valueChangeListener="#{curriculo.carregarMatrizes}">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{cursoGrad.allCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<td>
			<h:selectBooleanCheckbox value="#{relatorioDiscente.filtroMatriz}" style="border: none;" id="checkMatriz" />
			<label for="form:checkMatriz">Matriz Curricular:</label>
		</td>
		<td>
			<h:selectOneMenu id="matriz" value="#{relatorioDiscente.idMatrizCurricular }" onfocus="$('form:checkMatriz').checked = true;">
			<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
			<f:selectItems value="#{curriculo.possiveisMatrizes}" />
			</h:selectOneMenu></td>
	</tr>
	<tr>
		<td>
			<h:selectBooleanCheckbox value="#{relatorioDiscente.filtroCodigo}" id="checkCodigo" />
			<label for="checkCodigo">Cód. Matriz Curricular:</label>
		</td>
		<td>
			<h:inputText value="#{relatorioDiscente.codigo}" size="7" maxlength="7" id="codigo"
			onfocus="$('form:checkCodigo').checked = true;" onkeyup="CAPS(this)" />
		</td>
	</tr>
	<tr>
		<td colspan="3" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
				action="#{relatorioDiscente.gerarRelatorioListaVinculadosEstrutura}"/> 
			 <h:commandButton value="Cancelar" id="cancelar" action="#{relatorioDiscente.cancelar}"
			  onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');" />
			 
		</td>
	</tr>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>