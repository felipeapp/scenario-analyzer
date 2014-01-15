<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Logradouro</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoLogradouro.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="60%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Logradouro</caption>
			<h:inputHidden value="#{tipoLogradouro.confirmButton}" />
			<h:inputHidden value="#{tipoLogradouro.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoLogradouro.readOnly}" value="#{tipoLogradouro.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoLogradouro.confirmButton}"
						action="#{tipoLogradouro.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoLogradouro.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>

	<br />
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>