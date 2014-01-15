<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
		<h2><ufrn:subSistema /> > Natureza do Curso</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{naturezaCurso.listar}"/>
			</div>
			</h:form>
	</center>


	<table class=formulario width="60%">
		<h:form>
			<caption class="listagem">Cadastro de Natureza do Curso</caption>
			<h:inputHidden value="#{naturezaCurso.confirmButton}" />
			<h:inputHidden value="#{naturezaCurso.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="50" maxlength="180" readonly="#{naturezaCurso.readOnly}" value="#{naturezaCurso.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{naturezaCurso.confirmButton}"
						action="#{naturezaCurso.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{naturezaCurso.cancelar}" /></td>
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