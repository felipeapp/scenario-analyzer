<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:form>
	
	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{autorizacaoReconsideracao.create}"/>
	<h:outputText  value="#{util.create}"/>	
	
	
				<c:set var="EM_ABERTO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO) %>" scope="application"/>
				<c:set var="AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS" value="<%= String.valueOf(TipoSituacaoProjeto.MON_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS) %>" scope="application"/>
				<c:set var="RECOMENDADO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_RECOMENDADO) %>" scope="application"/>
				<c:set var="NAO_RECOMENDADO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_NAO_RECOMENDADO) %>" scope="application"/>				
				<c:set var="AVALIADO_COM_DISCREPANCIA" value="<%= String.valueOf(TipoSituacaoProjeto.MON_AVALIADO_COM_DISCREPANCIA_DE_NOTAS) %>" scope="application"/>
				<c:set var="EM_ANDAMENTO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_EM_EXECUCAO) %>" scope="application"/>
	

	<h2><ufrn:subSistema /> > Meus Projetos de Apoio à Melhoria da Qualidade do Ensino</h2>

	<div class="descricaoOperacao">
		<table width="80%">
			<tr>
			<td valign="top"><html:img page="/img/warning.gif"/> </td>
			<td style="text-align: justify">
			Lista com todos os Projetos de Apoio à Melhoria da Qualidade do Ensino onde o(a) usuário(a) atual é coordenador(a) ou orientador(a).
			Os projetos só poderão ser alterados ou excluídos enquanto estiverem com a situação 'CADASTRO EM ANDAMENTO'.
			</td>
			</tr>
		</table>
	</div>

	<br>

		<!-- Projetos GRAVADOS PELO USUARIO LOGADO-->
		<c:if test="${not empty projetoMonitoria.projetosGravados}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Continuar Cadastro do Projeto
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Projeto
			    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto			    
			</div>
			<br/>
		
			
			<t:dataTable id="projetosGravados" value="#{projetoMonitoria.projetosGravadosApoioMelhoriaQualidadeEnsino}" var="proj"
			 	align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
			 	
				<f:facet name="caption">
					<h:outputText value="Lista dos Projetos de Apoio à Melhoria da Qualidade do Ensino Pendentes de Envio" />
				</f:facet>


				<t:column>
					<f:facet name="header">
						<f:verbatim>Ano</f:verbatim>
					</f:facet>
					<h:outputText value="#{proj.ano}" />
				</t:column>

				<t:column>
					<f:facet name="header">
						<f:verbatim>Título do Projeto</f:verbatim>
					</f:facet>
					<h:outputText value="#{proj.titulo}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Situação</f:verbatim>
					</f:facet>
					<h:outputText value="#{proj.situacaoProjeto.descricao}" />
				</t:column>
		
				<t:column width="2%">			
					<h:commandLink title="Continuar Cadastro" action="#{projetoMonitoria.atualizar}" style="border: 0;" >
				    	<f:param name="id" value="#{proj.id}"/>				    	
						<h:graphicImage url="/img/seta.gif"/>
					</h:commandLink>
				</t:column>
				
				<t:column width="2%">	
					<h:commandLink title="Remover" action="#{projetoMonitoria.preRemover}" style="border: 0;">
				    	<f:param name="id" value="#{proj.id}"/>				    	
						<h:graphicImage url="/img/delete.gif" />
					</h:commandLink>
				</t:column>

				<t:column width="2%">
					<h:commandLink  title="Visualizar Projeto" action="#{projetoMonitoria.view}" style="border: 0;">
				    	<f:param name="id" value="#{proj.id}"/>				    	
						<h:graphicImage url="/img/view.gif"/>
					</h:commandLink>
				</t:column>
		
			</t:dataTable>
		<br/>
		<br/>
		</c:if>
		<!-- FIM DOS PRJETOS GRAVADOS PELO USUARIO LOGADO-->


	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;" />: Alterar Projeto
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Projeto
    	<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto
	</div>


	
	<table class="listagem">
		<caption class="listagem">Lista de Projetos de Apoio à Melhoria da Qualidade do Ensino que participo </caption>
		<thead>
			<tr>
				<th>Título do Projeto</th>
				<th>Situação</th>
				<th></th>
				<th></th>
				<th></th>
			</tr>
		</thead>
		
		
	<c:set var="meusProjetos"  value="#{projetoMonitoria.meusProjetosApoioMelhoriaQualidadeEnsino}"/>		
	<c:if test="${not empty meusProjetos}">
		<c:forEach items="#{meusProjetos}" var="projeto" varStatus="status">
            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

				<td>${projeto.anoTitulo} </td>
				<td width="30%"> ${projeto.situacaoProjeto.descricao} </td>

				<td>
						<c:if test="${projeto.situacaoProjeto.id == EM_ABERTO}">
							<h:commandLink title="Alterar" action="#{projetoMonitoria.atualizar}" style="border: 0;" >
						    	<f:param name="id" value="#{projeto.id}"/>				    	
								<h:graphicImage url="/img/alterar.gif"/>
							</h:commandLink>
						</c:if>
				</td>
				<td>							
						<c:if test="${projeto.situacaoProjeto.id == EM_ABERTO}">
							<h:commandLink title="Remover" action="#{projetoMonitoria.preRemover}" style="border: 0;">
						    	<f:param name="id" value="#{projeto.id}"/>				    	
								<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
						</c:if>
				</td>
				<td>							
					<h:commandLink  title="Ver" action="#{projetoMonitoria.view}" style="border: 0;">
					   	<f:param name="id" value="#{projeto.id}"/>				    	
						<h:graphicImage url="/img/view.gif"/>
					</h:commandLink>
				</td>
			</tr>
		 </c:forEach>
		</c:if>

		<c:if test="${empty meusProjetos}">
			<tr>
				<td colspan="5"><center><font color="red">Não há projetos de ensino ativos com participação do usuário atual.</font> </center></td>
			</tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="5" align="center">
						<h:commandButton value="Cadastrar Nova Proposta..." action="#{projetoMonitoria.novoProjetoPAMQEG}"/>
				</td>
			</tr>
		</tfoot>

	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>