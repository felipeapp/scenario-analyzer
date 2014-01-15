<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Modalidade de Curso </h2>

	<center>
		<h:form>
		<div class="infoAltRem" style="text-align: center; width: 100%">
			<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
  			<h:commandLink value="Listar" action="#{modalidadeCursoMedio.listar}"/>
		</div>
		</h:form>
	</center>

	<table class=formulario width="70%">
		<h:form>
			<caption class="listagem">Cadastro de Modalidade de Curso de Nível Médio</caption>
			<h:inputHidden value="#{modalidadeCursoMedio.confirmButton}" />
			<h:inputHidden value="#{modalidadeCursoMedio.obj.id}" />
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText readonly="#{modalidadeCursoMedio.readOnly}" value="#{modalidadeCursoMedio.obj.descricao}" size="60"
					maxlength="80" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="#{modalidadeCursoMedio.confirmButton}" action="#{modalidadeCursoMedio.cadastrar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{modalidadeCursoMedio.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>