<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view beforePhase="#{ projetoBase.checkChangeRole }">

<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:form id="form">
	
			<h:messages/>
			<h2><ufrn:subSistema /> > Projetos com cadastro em andamento</h2>
			
			<table width="100%" class="descricaoOperacao" id="aviso">
				<tr>
				<td width="10%" align="center"><html:img page="/img/warning.gif" width="30px" height="30px"/> </td>
				<td align="justify">
				<font color="red" size="2">Atenção:</font> 
							Esta é a lista de todos os projetos com cadastros em andamento.
							Para continuar o cadastro do projeto clique no link correspondente. 
							Para cadastrar um novo projeto base clique nos botões da barra de navegação logo abaixo.
				</td>
				</tr>
			</table>
			
			<br/>
			
		
		<!-- PROJETOS GRAVADOS PELO USUARIO LOGADO-->
		
			<div class="infoAltRem">
			    <h:graphicImage url="/img/adicionar.gif" style="overflow: visible;"/>
			    <h:commandLink value="Cadastrar Nova Proposta" action="#{projetoBase.selecionarTipoProjetoIntegrado}" id="btNovaProposta" />
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Continuar Cadastro
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Proposta
			</div>
			<br/>
		
			
			<h:dataTable id="datatableProjetosGravados" value="#{projetoBase.projetosGravados}" var="projeto" width="100%" styleClass="listagem" headerClass="linhaCinza" rowClasses="linhaPar, linhaImpar">
				<f:facet name="caption">
					<h:outputText value="Lista de propostas de projetos pendentes de conclusão" />
				</f:facet>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Título</f:verbatim>
					</f:facet>
					<h:outputText value="#{projeto.anoTitulo}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Situação</f:verbatim>
					</f:facet>
					<h:outputText value="#{projeto.situacaoProjeto.descricao}" />
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Continuar cadastro do projeto" action="#{ projetoBase.preAtualizar }" immediate="true">
					        <f:param name="id" value="#{projeto.id}"/>
				    		<h:graphicImage url="/img/seta.gif" />
					</h:commandLink>
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Remover proposta" action="#{ projetoBase.preRemover }" immediate="true">
					        <f:param name="id" value="#{projeto.id}"/>
				    		<h:graphicImage url="/img/delete.gif" />
				</h:commandLink>
					
				</t:column>
		
			</h:dataTable>
			
		<c:if test="${(empty projetoBase.projetosGravados)}">
			<center><font color='red'>Não há propostas de projetos com cadastro em andamento pelo usuário atual.</font></center>
		</c:if>
		
		<br/>
		
		<table class=formulario width="100%">
            <tfoot>
                <tr>
                    <td><h:commandButton value="Cancelar" action="#{projetoBase.cancelar}" id="btCancel" onclick="return confirm('Deseja realmente cancelar?');"/></td>
                </tr>
                </tfoot>
        </table>
		<br/>
		<!-- FIM DOS PROJETOS GRAVADOS PELO USUARIO LOGADO-->
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>