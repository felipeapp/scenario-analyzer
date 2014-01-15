<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Veiculação de Ensino à Distância</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoVeiculacaoEad.listar}"/>
			</div>
			</h:form>
	</center>


	<table class=formulario width="60%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Veiculação de Ensino à Distância</caption>
			<h:inputHidden value="#{tipoVeiculacaoEad.confirmButton}" />
			<h:inputHidden value="#{tipoVeiculacaoEad.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoVeiculacaoEad.readOnly}" value="#{tipoVeiculacaoEad.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoVeiculacaoEad.confirmButton}"
						action="#{tipoVeiculacaoEad.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoVeiculacaoEad.cancelar}" /></td>
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