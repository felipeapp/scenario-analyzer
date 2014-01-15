<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:outputText value="#{modalidadeEducacao.create }"></h:outputText>
<c:if test="${!modalidadeEducacao.subSistemaGraduacao}">
</c:if>
	<h2><ufrn:subSistema /> > Forma de Participação do Aluno</h2>

<c:if test="${!modalidadeEducacao.subSistemaGraduacao}">
	<center>
			<h:messages/>
			<h:form prependId="false">
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{modalidadeEducacao.listar}" id="listar"/>
			</div>
			</h:form>
	</center>
</c:if>

	<table class=formulario width="60%">
		<h:form prependId="false">
			<caption class="listagem">Cadastro de Modalidade de Educação</caption>
			<h:inputHidden value="#{modalidadeEducacao.confirmButton}" />
			<h:inputHidden value="#{modalidadeEducacao.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td>
					<h:inputText size="50" maxlength="75" value="#{modalidadeEducacao.obj.descricao}" id="descricao"
						disabled="#{modalidadeEducacao.readOnly}"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{modalidadeEducacao.confirmButton}" id="cadastrar"
						action="#{modalidadeEducacao.cadastrar}" /> <h:commandButton id="cancelar"
						value="Cancelar" onclick="#{confirm}" action="#{modalidadeEducacao.cancelar}" /></td>
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