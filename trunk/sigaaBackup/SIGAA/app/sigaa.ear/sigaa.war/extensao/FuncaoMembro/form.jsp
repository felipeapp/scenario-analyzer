<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Função do Membro da Equipe </h2>

  <table class=formulario>
	<h:form> 
	<caption class="listagem"> Funcao de Membro da Equipe</caption>	
		<h:inputHidden value="#{funcaoMembroEquipe.confirmButton}"/> 
		<h:inputHidden value="#{funcaoMembroEquipe.obj.id}"/>
	
		<tr>
			<th class="required"> Descrição:</th>
			<td><h:inputText value="#{funcaoMembroEquipe.obj.descricao}" size="60" maxlength="255" readonly="#{funcaoMembroEquipe.readOnly}"/></td>
		</tr>
	
		<tr>	
			<th class="required"> Escopo:</th>	
			<td>
				<h:selectOneMenu value="#{funcaoMembroEquipe.obj.escopo}">
					<f:selectItem itemValue="1" itemLabel="SERVIDOR" />
					<f:selectItem itemValue="2" itemLabel="DISCENTE" />
					<f:selectItem itemValue="3" itemLabel="EXTERNO" />
					<f:selectItem itemValue="5" itemLabel="DOCENTE" />
					<f:selectItem itemValue="4" itemLabel="TODOS" />
				</h:selectOneMenu>
			</td>
		</tr>

		<tr>	
			<th class="obrigatorio"> Tipo Projeto:</th>	
			<td>
				<h:selectManyCheckbox value="#{funcaoMembroEquipe.obj.cadastrarEm}">
					<f:selectItem itemValue="P" itemLabel="PESQUISA" itemDisabled="#{ not funcaoMembroEquipe.pesquisa }" />
					<f:selectItem itemValue="E" itemLabel="EXTENSÃO" itemDisabled="#{ not funcaoMembroEquipe.extensao }" />
					<f:selectItem itemValue="M" itemLabel="MONITORIA" itemDisabled="#{ not funcaoMembroEquipe.monitoria }" />
					<f:selectItem itemValue="A" itemLabel="INTEGRADOS" itemDisabled="#{ not funcaoMembroEquipe.acoesIntegradas }" />
				</h:selectManyCheckbox>
			</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan=2>
					<h:commandButton value="#{funcaoMembroEquipe.confirmButton}" action="#{funcaoMembroEquipe.cadastrar}" /> 
					<h:commandButton value="Cancelar" action="#{funcaoMembroEquipe.cancelar}" />
				</td>
			</tr>
		</tfoot>
		
	</h:form>
  </table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>