<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Documento Legal</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoDocumentoLegal.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="60%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Documento Legal</caption>
			<h:inputHidden value="#{tipoDocumentoLegal.confirmButton}" />
			<h:inputHidden value="#{tipoDocumentoLegal.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="80"
					readonly="#{tipoDocumentoLegal.readOnly}" value="#{tipoDocumentoLegal.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoDocumentoLegal.confirmButton}"
						action="#{tipoDocumentoLegal.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoDocumentoLegal.cancelar}" /></td>
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