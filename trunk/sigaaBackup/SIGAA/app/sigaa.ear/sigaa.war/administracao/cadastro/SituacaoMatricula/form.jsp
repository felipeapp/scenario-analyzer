<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cadastrar Situação de Matrícula</h2>


	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{situacaoMatricula.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Situação de Matrícula</caption>
			<h:inputHidden value="#{situacaoMatricula.confirmButton}" />
			<h:inputHidden value="#{situacaoMatricula.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255" value="#{situacaoMatricula.obj.descricao}" 
					readonly="#{situacaoMatricula.readOnly}"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{situacaoMatricula.confirmButton}"
						action="#{situacaoMatricula.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{situacaoMatricula.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>