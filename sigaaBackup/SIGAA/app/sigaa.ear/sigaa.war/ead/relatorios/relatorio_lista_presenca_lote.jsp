<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Emitir Listas de Presença em Lote </h2>
	<h:outputText value="#{listaPresencaLoteMBean.create}" />
	
	<div class="descricaoOperacao">
		<p>
			Essa funcionalidade é utilizada para Emitir a Lista de Presença em lote das turmas existentes para os cursos a Distância.
		</p>
	 	<p>
			Assim, possibilita imprimir várias listas de uma única vez, seja por polo ou por disciplina;
		</p>
	</div>
	
	<h:form id="form">
		<table class="formulario" width="70%">
			<caption>Listas de Presença em Lote</caption>
			<tbody>
				<tr>
					<td width="1px;"><h:selectBooleanCheckbox value="#{listaPresencaLoteMBean.filtroPolo}" id="checkPolo" styleClass="noborder"/></td>
					<td width="23%"><label for="checkPolo" onclick="$('form:checkPolo').checked = !$('form:checkPolo').checked;">
							Pólo:</label></td>
					<td>
						<h:selectOneMenu id="poloSelecionado" value="#{listaPresencaLoteMBean.polo.id}" onfocus="$('form:checkPolo').checked = true;" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{listaPresencaLoteMBean.polos}" />
						</h:selectOneMenu> 
					</td>
				</tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{listaPresencaLoteMBean.filtroCodigo}" id="checkCodigo" styleClass="noborder"/></td>
					<td><label for="checkCodigo" onclick="$('form:checkCodigo').checked = !$('form:checkCodigo').checked;">
							Código do componente:</label></td>
					<td><h:inputText value="#{listaPresencaLoteMBean.disciplina.codigo}" size="10" maxlength="9" onfocus="$('form:checkCodigo').checked = true;" 
							onkeyup="CAPS(this)" id="inputCodDisciplina" /></td>
				</tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{listaPresencaLoteMBean.filtroDisciplina}" id="checkDisciplina" styleClass="noborder" /></td>
					<td><label for="checkDisciplina" onclick="$('form:checkDisciplina').checked = !$('form:checkDisciplina').checked;">
							Nome do componente:</label></td>
					<td><h:inputText value="#{listaPresencaLoteMBean.disciplina.nome}" size="60" maxlength="60" onfocus="$('form:checkDisciplina').checked = true;" 
							id="inputNomeDisciplina" /></td>
				</tr>
				
				<tr>
					<td></td>
					<td> <span class="obrigatorio">Ano-Período: </span></td>
					<td>
						<h:inputText value="#{listaPresencaLoteMBean.ano}" maxlength="4" size="4" onkeyup="return formatarInteiro(this);"/> 
						- 
						<h:inputText value="#{listaPresencaLoteMBean.periodo}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/> 
					</td>
				</tr>
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Lista" action="#{listaPresencaLoteMBean.criarListaPresencaEmLote}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{listaPresencaLoteMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br />
	<div>
		<center><img src="/shared/img/required.gif" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
	</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>