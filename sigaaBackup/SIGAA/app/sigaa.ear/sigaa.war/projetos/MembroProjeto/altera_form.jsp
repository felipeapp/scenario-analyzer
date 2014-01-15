<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Membros da Equipe</h2>
	
	<h:form id="equipe">
		
		<input type="hidden" name="lista_origem" id="lista_origem" value="${listaOrigem}"/>

		<table class="formulario" width="100%">
			<caption class="listagem">Dados do Membro da Equipe</caption>

			<tr>
				<th width="20%"><b>Projeto:</b></th>
				<td><h:outputText value="#{membroProjeto.membroEquipe.projeto.titulo}" rendered="#{not empty membroProjeto.membroEquipe}" /></td>
			</tr>

			<tr>
				<th><b>Período do Projeto:</b></th>
				<td><h:outputText value="#{membroProjeto.membroEquipe.projeto.dataInicio}" rendered="#{not empty membroProjeto.membroEquipe}" /> 
				 a <h:outputText value="#{membroProjeto.membroEquipe.projeto.dataFim}" rendered="#{not empty membroProjeto.membroEquipe}" /></td>
			</tr>

			<tr>
				<th><b>Nome:</b></th>
				<td><h:outputText value="#{membroProjeto.membroEquipe.pessoa.nome}" rendered="#{not empty membroProjeto.membroEquipe}" /></td>
			</tr>

			<tr>
				<th><b>Categoria:</b></th>
				<td><h:outputText value="#{membroProjeto.membroEquipe.categoriaMembro.descricao}"/></td>
			</tr>

			<tr>
				<th class="required">Remuneração / Bolsa:</th>
				<td>
					<table>
						<tr>
							<td>
								<h:selectOneRadio value="#{membroProjeto.membroEquipe.remunerado}" id="membroRemunerado" disabled="#{membroProjeto.readOnly}">
									<f:selectItem itemValue="true" itemLabel="Sim"/>
									<f:selectItem itemValue="false" itemLabel="Não"/>
								</h:selectOneRadio>
							</td>
							<td>
								<ufrn:help img="/img/ajuda.gif">Marque SIM caso o membro da equipe possua bolsa ou outro tipo de remuneração.</ufrn:help>
							</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<th width="15%"  class="required">Função:</th>
				<td>
					<h:selectOneMenu id="funcaoMembroEquipe"
						value="#{membroProjeto.membroEquipe.funcaoMembro.id}"
						disabled="#{membroProjeto.readOnly}">

						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{funcaoMembroEquipe.allServidoresCombo}"/>
						<f:selectItems value="#{funcaoMembroEquipe.allDiscentesCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">CH Semanal:</th>
				<td>
					<h:inputText id="chSemanalDedicada" value="#{membroProjeto.membroEquipe.chDedicada}" maxlength="3" rendered="#{ membroProjeto.chPassivoAlteracao }"
					size="5" onkeyup="formatarInteiro(this)" readonly="#{membroProjeto.readOnly}" disabled="#{membroProjeto.readOnly}"/>
					<h:outputText value="#{membroProjeto.membroEquipe.chDedicada}" rendered="#{ not membroProjeto.chPassivoAlteracao }" /> horas
				</td>
			</tr>
			<tr>
				<th class="required">Data Início:</th>
				<td><t:inputCalendar size="10" value="#{membroProjeto.membroEquipe.dataInicio}" popupDateFormat="dd/MM/yyyy"
				renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))"  
				maxlength="10" id="inicio" disabled="#{membroProjeto.readOnly}"/></td>
			</tr>

			<tr>
				<th class="required">Data Fim:</th>
				<td><t:inputCalendar size="10" value="#{membroProjeto.membroEquipe.dataFim}" popupDateFormat="dd/MM/yyyy"
				renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))"  
				maxlength="10" id="fim" /></td>
    		</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btn_alterar" value="#{membroProjeto.confirmButton}" action="#{membroProjeto.alterarMembroEquipe}"/>
						<h:commandButton id="btn_cancelar" value="Cancelar" action="#{membroProjeto.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			</table>

		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>