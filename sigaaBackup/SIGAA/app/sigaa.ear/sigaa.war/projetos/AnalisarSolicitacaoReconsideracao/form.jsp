<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>	
	<h2><ufrn:subSistema /> > Analisar Solicitação de Reconsideração</h2>
	
	<h:form>	

	<h:outputText  value="#{solicitacaoReconsideracao.create}"/>
	<h:outputText  value="#{avaliacaoAtividade.create}"/>
	
	<h:inputHidden value="#{solicitacaoReconsideracao.obj.id}"/>
	
	<table class="formulario" width="100%">	
	<caption class="listagem"> Analisando Solicitação de Reconsideração </caption>


	<tr>
		<th><b>Ano:</b> </th>
		<td><h:outputText value="#{solicitacaoReconsideracao.obj.projeto.ano}"/></td>
	</tr>

	<tr>
		<th><b>Título:</b></th>
		<td><h:outputText value="#{solicitacaoReconsideracao.obj.projeto.titulo}"/></td>
	</tr>

	<tr>
		<th><b>Dimensão Acadêmica:</b> </th>
		<td><h:outputText value="#{solicitacaoReconsideracao.obj.projeto.dimensaoAcademica}"/></td>
	</tr>


	<tr>
		<th><b>Unidade Proponente:</b></th>
		<td><h:outputText value="#{solicitacaoReconsideracao.obj.projeto.unidade.nome}"/></td>
	</tr>


	<tr>
		<td colspan="2" align="center"><h:commandLink value="Visualizar Dados Completos" action="#{ projetoBase.view }" immediate="true">
			<f:param name="id" value="#{solicitacaoReconsideracao.obj.projeto.id}" />
			<h:graphicImage url="/img/view.gif" />
		</h:commandLink></td>
	</tr>




	<tr>
		<td colspan="2" class="subFormulario">Solicitação</td>
	</tr>	



	<tr>
		<th width="15%"><b>Submissão:</b></th>
		<td><fmt:formatDate value="${solicitacaoReconsideracao.obj.dataSolicitacao}" pattern="dd/MM/yyyy HH:mm:ss" /> </td>
	</tr>


	<tr>
		<th><b>Justificativa:</b></th>
		<td>
			<c:if test="${not empty solicitacaoReconsideracao.obj.justificativa}">
				<ufrn:format type="texto" name="solicitacaoReconsideracao" property="obj.justificativa" />
			</c:if>		
		</td>
	</tr>

	<tr>
		<td colspan="2" class="subFormulario">Parecer</td>
	</tr>
	
	<tr>
		<th>Situação:</th>
		<td><h:selectOneRadio id="sit"
			value="#{solicitacaoReconsideracao.obj.aprovado}">
			<f:selectItem itemValue="true" itemLabel="Aprovar solicitação" />
			<f:selectItem itemValue="false" itemLabel="NÃO aprovar solicitação" />
		</h:selectOneRadio></td>
	</tr>


	<tr>
		<th>Parecer:</th>
		<td><h:inputTextarea style="width: 98%" rows="4"
			id="justificativaAvaliacao"
			value="#{solicitacaoReconsideracao.obj.parecer}" /></td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="2">			
				<h:commandButton value="Confirmar" action="#{solicitacaoReconsideracao.analisarSolicitacaoProjeto}" rendered="#{acesso.comissaoIntegrada}"/>
				<h:commandButton value="Cancelar" action="#{solicitacaoReconsideracao.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>

	</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>