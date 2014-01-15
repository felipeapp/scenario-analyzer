<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Estado Civil</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{estadoCivil.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="70%">
		<h:form>
			<caption class="listagem">Cadastro de Estado Civil</caption>
			<h:inputHidden value="#{estadoCivil.confirmButton}" />
			<h:inputHidden value="#{estadoCivil.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText readonly="#{estadoCivil.readOnly}"  value="#{estadoCivil.obj.descricao}" size="60"
					maxlength="80" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{estadoCivil.confirmButton}"
						action="#{estadoCivil.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{estadoCivil.cancelar}" /></td>
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