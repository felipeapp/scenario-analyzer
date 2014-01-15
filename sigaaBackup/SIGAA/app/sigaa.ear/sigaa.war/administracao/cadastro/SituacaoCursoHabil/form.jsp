<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Situa��o de Curso H�bil</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{situacaoCursoHabil.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Situa��o de Curso H�bil</caption>
			<h:inputHidden value="#{situacaoCursoHabil.confirmButton}" />
			<h:inputHidden value="#{situacaoCursoHabil.obj.id}" />
			<tr>
				<th class="required">Descri��o:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{situacaoCursoHabil.readOnly}" value="#{situacaoCursoHabil.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{situacaoCursoHabil.confirmButton}"
						action="#{situacaoCursoHabil.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{situacaoCursoHabil.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
 <br />
 <center>
 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
 </center>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>