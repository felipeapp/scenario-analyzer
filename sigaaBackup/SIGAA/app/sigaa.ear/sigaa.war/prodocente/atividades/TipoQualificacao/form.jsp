<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Qualificação</h2>
	
		<h:form>
		 <div class="infoAltRem" style="width: 100%">
	     <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
  		<h:commandLink action="#{tipoQualificacao.listar}"
			value="Listar Tipo de Qualificação Cadastrados"/>
		</div>
	</h:form>
	
	<h:messages showDetail="true" />
	<h:form id="form">
	<table class=formulario width="70%">
			<caption class="listagem">Cadastro de Tipo de Qualificação</caption>
			<h:inputHidden value="#{tipoQualificacao.confirmButton}" />
			<h:inputHidden value="#{tipoQualificacao.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{tipoQualificacao.obj.descricao}" size="60"
					maxlength="255" readonly="#{tipoQualificacao.readOnly}" id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoQualificacao.confirmButton}"
						action="#{tipoQualificacao.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{tipoQualificacao.cancelar}" onclick="#{confirm}"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
