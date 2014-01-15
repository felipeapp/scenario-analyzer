<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Grupo da Entidade Financiadora</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{grupoEntidadeFinanciadora.listar}"/>
			</div>
			</h:form>
	</center>


	<table class=formulario width="70%">
		<h:form>
			<caption class="listagem">Cadastro de Grupo da Entidade Financiadora</caption>
			<h:inputHidden value="#{grupoEntidadeFinanciadora.confirmButton}" />
			<h:inputHidden value="#{grupoEntidadeFinanciadora.obj.id}" />
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText size="60" maxlength="255" readonly="#{grupoEntidadeFinanciadora.readOnly}" value="#{grupoEntidadeFinanciadora.obj.nome}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{grupoEntidadeFinanciadora.confirmButton}"
						action="#{grupoEntidadeFinanciadora.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{grupoEntidadeFinanciadora.cancelar}" /></td>
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