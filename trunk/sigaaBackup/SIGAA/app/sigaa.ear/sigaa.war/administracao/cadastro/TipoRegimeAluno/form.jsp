<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Tipo de Regime do Aluno</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoRegimeAluno.listar}"/>
			</div>
			</h:form>
	</center>


	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Regime do Aluno</caption>
			<h:inputHidden value="#{tipoRegimeAluno.confirmButton}" />
			<h:inputHidden value="#{tipoRegimeAluno.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{tipoRegimeAluno.obj.descricao}" size="60" maxlength="80"
					readonly="#{tipoRegimeAluno.readOnly}" /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{tipoRegimeAluno.confirmButton}"
						action="#{tipoRegimeAluno.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoRegimeAluno.cancelar}" /></td>
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