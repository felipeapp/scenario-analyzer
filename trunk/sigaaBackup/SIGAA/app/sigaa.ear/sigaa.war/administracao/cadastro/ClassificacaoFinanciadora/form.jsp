<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Classificação da Entidade Financiadora</h2>


	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{classificacaoFinanciadora.listar}"/>
			</div>
			</h:form>
	</center>

<table class=formulario width="70%">
		<h:form>
			<caption class="listagem">Cadastro de Classificação da Entidade Financiadora</caption>
			<h:inputHidden value="#{classificacaoFinanciadora.confirmButton}" />
			<h:inputHidden value="#{classificacaoFinanciadora.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{classificacaoFinanciadora.obj.descricao}" 
					size="60" maxlength="80" readonly="#{classificacaoFinanciadora.readOnly}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{classificacaoFinanciadora.confirmButton}"
						action="#{classificacaoFinanciadora.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{classificacaoFinanciadora.cancelar}" /></td>
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