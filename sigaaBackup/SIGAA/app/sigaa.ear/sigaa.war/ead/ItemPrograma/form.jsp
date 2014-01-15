<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
	<h2><ufrn:subSistema /> > Itens de um programa</h2>
	<br>
	<h:messages />
	<table class="formulario" width="100%">
		<caption class="formulario">Cadastrar Itens de um programa
		</caption>
		<tr>
			<th class="required">Cursos:</th>
			<td>
				<a4j:region>
					<h:selectOneMenu id="tipo"
						valueChangeListener="#{itemProgramaMBean.changeCursoDistancia}">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{itemProgramaMBean.allCursosDistancia}" />
						<a4j:support event="onchange" reRender="disciplinas" />
					</h:selectOneMenu>
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/indicator.gif" />
						</f:facet>
					</a4j:status>
				</a4j:region>
			</td>
		</tr>
		<tr>
			<th class="required">Disciplinas:</th>
			<td>
				<a4j:region>
					<h:selectOneMenu id="disciplinas"  value="#{itemProgramaMBean.idComponenteCurricular}"
						valueChangeListener="#{itemProgramaMBean.changeDisciplinaPorCurso}">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems
							value="#{itemProgramaMBean.allDisciplinasPorCurso}" />
						<a4j:support event="onchange" reRender="itensProgram" />
					</h:selectOneMenu>
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/indicator.gif" />
						</f:facet>
					</a4j:status>
				</a4j:region>
			</td>
		</tr>
		<tr>
			<th class="required">Número da Aula:</th>
			<td><h:inputText size="4" maxlength="3" id="aula"
				value="#{itemProgramaMBean.itemPrograma.aula}" 
				onkeyup="return formatarInteiro(this);" 
				converter="#{ intConverter }"/></td>
		</tr>
		<tr>
			<th class="required">Conteúdo:</th>
			<td><h:inputTextarea rows="5" cols="54" id="conteudo"
				value="#{itemProgramaMBean.itemPrograma.conteudo}"></h:inputTextarea>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="3"><h:commandButton value="Cadastrar"
					action="#{itemProgramaMBean.cadastrar}" /> <h:commandButton
					value="Cancelar" action="#{itemProgramaMBean.cancelar}"
					onclick="#{confirm}" /></td>
			</tr>
		</tfoot>
	</table>
	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br/>
	<div class="infoAltRem">
		<img src="${ctx}/img/delete.gif"/>: Remover
	</div>	
	<t:dataTable value="#{itemProgramaMBean.dataModel}" var="etapa"
		id="itensProgram" styleClass="listagem" style="width:100%"
		rowClasses="linhaPar,linhaImpar">
		<f:facet name="caption">
			<f:verbatim>Itens já cadastrados nesse programa</f:verbatim>
		</f:facet>
		<t:column style="width: 10%; text-align: left;">
			<f:facet name="header">
				<f:verbatim>Aula</f:verbatim>
			</f:facet>
			<h:outputText value="#{etapa.aula}" />
		</t:column>
		<t:column style="width: 85%; text-align: left;">
			<f:facet name="header">
				<f:verbatim>Conteúdo</f:verbatim>
			</f:facet>
			<h:outputText value="#{etapa.conteudo}" />
		</t:column>
		<t:column style="width: 5%;">
			<f:facet name="header">
				<f:verbatim>Remover</f:verbatim>
			</f:facet>
				<h:commandLink action="#{ itemProgramaMBean.removerItemPrograma }" title="Remover" onclick="return(confirm('Deseja realmente excluir este item?'));" styleClass="confirm-remover">
					<h:graphicImage value="/img/delete.gif" />
				</h:commandLink>
		</t:column>
	</t:dataTable>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
