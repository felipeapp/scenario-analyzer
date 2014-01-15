<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Grau  de Formação</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{grauFormacao.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Grau  de Formação</caption>
			<h:inputHidden value="#{grauFormacao.confirmButton}" />
			<h:inputHidden value="#{grauFormacao.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{grauFormacao.obj.descricao}" size="60"
					 maxlength="80" readonly="#{grauFormacao.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Ordem da Titulação:</th>
				<td><h:inputText value="#{grauFormacao.obj.ordemtitulacao}"
					size="60" maxlength="255" readonly="#{grauFormacao.readOnly}" 
					onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{grauFormacao.confirmButton}"
						action="#{grauFormacao.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{grauFormacao.cancelar}" /></td>
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