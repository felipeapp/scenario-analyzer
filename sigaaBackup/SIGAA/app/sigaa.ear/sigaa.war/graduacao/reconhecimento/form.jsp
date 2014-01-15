<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Reconhecimentos</h2>
	<br/>

	<h:form id="form">
	<a4j:keepAlive beanName="reconhecimento"></a4j:keepAlive>	
	<table class="formulario">
		<caption class="listagem">Cadastro de Reconhecimentos</caption>
		
		<tr>
			<th class="obrigatorio">Curso:</th>
			<td>
				<a4j:region>
					<h:selectOneMenu id="curso" value="#{reconhecimento.obj.matriz.curso.id }" 
						valueChangeListener="#{reconhecimento.carregarMatrizes }" disabled="#{reconhecimento.readOnly}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{cursoGrad.allCombo}" />
						<a4j:support event="onchange" reRender="matriz" />
					</h:selectOneMenu>
					<a4j:status>
						<f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
					</a4j:status>
				</a4j:region>
			</td>
		</tr>

		<tr>
			<th class="obrigatorio">Matriz Curricular:</th>
			<td>
				<h:selectOneMenu id="matriz" value="#{reconhecimento.obj.matriz.id }" disabled="#{reconhecimento.remover}" >
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{reconhecimento.possiveisMatrizes}" />
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<th class="obrigatorio">Portaria/Decreto:</th>
			<td>
				<h:inputText id="portaria_decreto" value="#{reconhecimento.obj.portariaDecreto}" size="90" disabled="#{reconhecimento.remover}" />
			</td>
		</tr>
		<tr>
			<th class="obrigatorio"> Data do Decreto:</th>
			<td>
				<t:inputCalendar id="data_decreto" value="#{reconhecimento.obj.dataDecreto}" 
					size="10" maxlength="10" disabled="#{reconhecimento.remover}"
					renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return (formataData(this,event));">
					<f:convertDateTime pattern="dd/MM/yyyy"/>
				</t:inputCalendar>	 
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Data da Publicação:</th>
			<td>
				<t:inputCalendar id="data_publicacao" value="#{reconhecimento.obj.dataPublicacao}" 
					size="10" maxlength="10" disabled="#{reconhecimento.remover}"
					renderAsPopup="true" renderPopupButtonAsImage="true" 
					onkeypress="return (formataData(this,event));">
					<f:convertDateTime pattern="dd/MM/yyyy"/>
				</t:inputCalendar> 
			</td>			
		</tr>
		<tr>
			<th>Válido Até:</th>
			<td>
				<t:inputCalendar id="validade" value="#{reconhecimento.obj.validade}" 
					size="10" disabled="#{reconhecimento.remover}"
					maxlength="10" renderAsPopup="true" renderPopupButtonAsImage="true"
					onkeypress="return (formataData(this,event));">
					<f:convertDateTime pattern="dd/MM/yyyy"/>
				</t:inputCalendar> 
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton id="Confirmar" value="#{reconhecimento.confirmButton}" action="#{reconhecimento.cadastrar}" />
					<h:commandButton id="Cancelar" value="Cancelar" immediate="true" onclick="#{confirm}" action="#{reconhecimento.cancelarCadastro}" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>