<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Rede de Ensino</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoRedeEnsino.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Rede de Ensino</caption>
			<h:inputHidden value="#{tipoRedeEnsino.confirmButton}" />
			<h:inputHidden value="#{tipoRedeEnsino.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoRedeEnsino.readOnly}"  value="#{tipoRedeEnsino.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoRedeEnsino.confirmButton}"
						action="#{tipoRedeEnsino.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoRedeEnsino.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
 
 <br />
 <center>
 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>