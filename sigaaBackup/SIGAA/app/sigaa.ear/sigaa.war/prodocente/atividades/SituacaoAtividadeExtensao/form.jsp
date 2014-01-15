<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema/> > Situacao de Atividade de Extens�o</h2>
	<h:messages showDetail="true" />
	<h:form id="form">
		<table class=formulario width="50%">
			
		  <caption class="listagem">Cadastro de Situa��o de Atividade de Extens�o</caption>
		  <h:inputHidden value="#{situacaoAtividadeExtensao.confirmButton}" />
		  <h:inputHidden value="#{situacaoAtividadeExtensao.obj.id}" />
			
			<tr>
				<th  class="required">Descri��o:</th>
				<td><h:inputText
					value="#{situacaoAtividadeExtensao.obj.descricao}" size="60"
					maxlength="255" readonly="#{situacaoAtividadeExtensao.readOnly}"
					id="descricao" /></td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan=2>
					   <h:commandButton value="Cadastrar"
						action="#{situacaoAtividadeExtensao.cadastrar}" /> 
					   <h:commandButton value="Cancelar" 
					    action="#{situacaoAtividadeExtensao.cancelar}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>