<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Tipo de Atividade</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoAtividade.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Atividade</caption>
			<h:inputHidden value="#{tipoAtividade.confirmButton}" />
			<h:inputHidden value="#{tipoAtividade.obj.id}" />
			<tr>
				<th class="required">Descricao:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoAtividade.readOnly}"  value="#{tipoAtividade.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoAtividade.confirmButton}"
						action="#{tipoAtividade.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoAtividade.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>