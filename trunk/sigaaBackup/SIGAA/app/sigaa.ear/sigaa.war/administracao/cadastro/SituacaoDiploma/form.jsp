<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Situação de Diploma</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{situacaoDiploma.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Situação de Diploma</caption>
			<h:inputHidden value="#{situacaoDiploma.confirmButton}" />
			<h:inputHidden value="#{situacaoDiploma.obj.id}" />
			<tr align="center">
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{situacaoDiploma.readOnly}" value="#{situacaoDiploma.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{situacaoDiploma.confirmButton}"
						action="#{situacaoDiploma.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{situacaoDiploma.cancelar}" /></td>
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