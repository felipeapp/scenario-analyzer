<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2 class="title">
		<ufrn:subSistema /> > Recomendação de Curso
	</h2>

	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p> Esta tela permite o cadastro/atualização de uma recomendação para um curso de um programa de pós-graduação.</p>
	</div>

	<h:form id="formRecomendacaoPrograma">
		<h:inputHidden value="#{recomendacao.confirmButton}" />
		<h:inputHidden value="#{recomendacao.obj.id}" />

		<table class="formulario">
			<caption class="formulario">Dados da Recomendação</caption>
			
			<tbody>
			<tr>
				<th class="required" width="15%">Programa:</th>
				<td>
					<h:selectOneMenu id="programa" value="#{ recomendacao.programa.id }" 
						valueChangeListener="#{ recomendacao.carregaCursosStricto }" 
						onchange="submit()">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{ unidade.allProgramaPosCombo }"/>
					</h:selectOneMenu>
				
				</td>
			</tr>
			<tr>
				<th class="required">Curso:</th>
				<td>
					<h:selectOneMenu id="curso" value="#{ recomendacao.obj.curso.id }" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{ recomendacao.cursosStricto }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Conceito:</th>
				<td>
					<h:selectOneMenu id="conceito" value="#{ recomendacao.obj.conceito }" >
						<f:selectItem itemLabel="-" itemValue="0"/>
						<f:selectItem itemLabel="1" itemValue="1"/>
						<f:selectItem itemLabel="2" itemValue="2"/>
						<f:selectItem itemLabel="3" itemValue="3"/>
						<f:selectItem itemLabel="4" itemValue="4"/>
						<f:selectItem itemLabel="5" itemValue="5"/>
						<f:selectItem itemLabel="6" itemValue="6"/>
						<f:selectItem itemLabel="7" itemValue="7"/>
					</h:selectOneMenu>
					
				</td>
			</tr>
			<tr>
				<th>Portaria:</th>
				<td>
					<h:inputText value="#{ recomendacao.obj.portaria }" 
						id="portariaRecomendacao" maxlength="255" size="70"/>
				</td>
			</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{recomendacao.confirmButton}" 
							id="btnConfirmar" action="#{recomendacao.cadastrar}" /> 
						<h:commandButton value="Cancelar" id="btnCancelar" immediate="true"	
							action="#{recomendacao.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br/>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br/>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
