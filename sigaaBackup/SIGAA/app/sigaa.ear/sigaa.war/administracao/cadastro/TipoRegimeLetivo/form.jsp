<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Tipo de Regime Letivo</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoRegimeLetivo.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Regime Letivo</caption>
			<h:inputHidden value="#{tipoRegimeLetivo.confirmButton}" />
			<h:inputHidden value="#{tipoRegimeLetivo.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoRegimeLetivoreadOnly}" value="#{tipoRegimeLetivo.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoRegimeLetivo.confirmButton}"
						action="#{tipoRegimeLetivo.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoRegimeLetivo.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	 <center>
 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>