<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Entidade Financiadora</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{entidadeFinanciadora.listar}"/>
			</div>
			</h:form>
	</center>

<table class=formulario width="80%">
		<h:form>
			<caption class="listagem">Cadastro de Entidade Financiadora</caption>
			<h:inputHidden value="#{entidadeFinanciadora.confirmButton}" />
			<h:inputHidden value="#{entidadeFinanciadora.obj.id}" />
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText size="60" maxlength="80" readonly="#{entidadeFinanciadora.readOnly}" value="#{entidadeFinanciadora.obj.nome}" /></td>
			</tr>
			<tr>
				<th class="required">Sigla:</th>
				<td><h:inputText size="10" maxlength="80" readonly="#{entidadeFinanciadora.readOnly}" value="#{entidadeFinanciadora.obj.sigla}" /></td>
			</tr>
			<tr>
				<th class="required">Grupo:</th>

				<td><h:selectOneMenu 
					value="#{entidadeFinanciadora.obj.grupoEntidadeFinanciadora.id}">
					<f:selectItem itemValue="0" itemLabel=" Opções "></f:selectItem>
					<f:selectItems value="#{grupoEntidadeFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Classificação:</th>

				<td><h:selectOneMenu 
					value="#{entidadeFinanciadora.obj.classificacaoFinanciadora.id}">
					<f:selectItem itemValue="0" itemLabel=" Opções "></f:selectItem>
					<f:selectItems value="#{classificacaoFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th>Unidade Federativa:</th>

				<td><h:selectOneMenu 
					value="#{entidadeFinanciadora.obj.unidadeFederativa.id}">
					<f:selectItem itemValue="0" itemLabel=" Opções "></f:selectItem>
					<f:selectItems value="#{unidadeFederativa.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">País:</th>

				<td><h:selectOneMenu value="#{entidadeFinanciadora.obj.pais.id}" >
					<f:selectItem itemValue="0" itemLabel=" Opções "></f:selectItem>
					<f:selectItems value="#{pais.allCombo}" />
				</h:selectOneMenu></td>
			</tr>


			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{entidadeFinanciadora.confirmButton}"
						action="#{entidadeFinanciadora.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{entidadeFinanciadora.cancelar}" /></td>
				</tr>				
			</tfoot>
		</h:form>
	</table>
 <br />
 <center>
 <h:graphicImage url="/img/required.gif"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>