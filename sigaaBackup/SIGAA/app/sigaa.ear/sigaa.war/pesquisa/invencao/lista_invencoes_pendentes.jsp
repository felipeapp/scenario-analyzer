<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:form id="formAtividade">
	
			<h:messages/>
			<h2> <ufrn:subSistema /> &gt; Notifica��es de Inven��o com cadastro em andamento</h2>
			<h:outputText value="#{invencao.create}"/>
			
			<div >
				<table width="100%">
					<tr>
						<td>
						</td>
					</tr>
				</table>
			</div>
			
			<table width="100%" class="descricaoOperacao" id="aviso">
				<tr>
				<td width="40"><html:img page="/img/help.png"/> </td>
				<td valign="top" style="text-align: justify">
				<font color="red" size="2">Aten��o:</font> 
							Esta � a lista de todas as suas Notifica��es de Inven��o cadastradas no sistema.<br/>
							Para continuar o cadastro da inven��o clique no link correspondente. <br/> 
							Para notificar uma nova Inven��o clique no bot�o da barra de navega��o logo abaixo.
				</td>
				</tr>
			</table>
			
				<br/>
			
		
		<!-- Inven��es GRAVADAS PELO USUARIO LOGADO-->
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Informa��es da Inven��o
				<h:graphicImage value="/img/view2.gif"style="overflow: visible;"/>: Visualizar Comprovante <br />
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Continuar Cadastro da Inven��o 
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Excluir Notifica��o de Inven��o
			</div>
			<br/>
		
			
			<t:dataTable id="datatableInvencoesGravadas" value="#{invencao.invencoesGravadas}" var="inv" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
				<f:facet name="caption">
					<h:outputText value="Lista das Inven��es Cadastradas pelo Usu�rio" />
				</f:facet>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>C�digo</f:verbatim>
					</f:facet>
					<h:outputText value="#{inv.codigo}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Tipo</f:verbatim>
					</f:facet>
					<h:outputText value="#{inv.tipo.descricao}" escape="false"/>
					<h:outputText value=" (#{inv.descricaoCategoriaPatente})" rendered="#{inv.patente}"/>
				</t:column>

				<t:column>
					<f:facet name="header">
						<f:verbatim>Situa��o</f:verbatim>
					</f:facet>
					<h:outputText value="#{inv.statusString}" />
				</t:column>
				
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Visualizar Informa��es da Inven��o" action="#{ invencao.view }" immediate="true">
				        <f:param name="idInvencao" value="#{inv.id}"/>
			    		<h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</t:column>
				
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Visualizar Comprovante" action="#{ invencao.verComprovante }" 
						immediate="true" rendered="#{inv.submetida}">
				        <f:param name="idInvencao" value="#{inv.id}"/>
			    		<h:graphicImage url="/img/view2.gif" />
					</h:commandLink>
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Continuar Cadastro da Inven��o" action="#{ invencao.continuarCadastro }" 
						immediate="true" rendered="#{inv.status == 1}">
					        <f:param name="idInvencao" value="#{inv.id}"/>
				    		<h:graphicImage url="/img/seta.gif" />
					</h:commandLink>
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Excluir Notifica��o de Inven��o" action="#{ invencao.preRemover }" 
						immediate="true" rendered="#{inv.status == 1}">
					        <f:param name="idInvencao" value="#{inv.id}"/>
				    		<h:graphicImage url="/img/delete.gif" />
				</h:commandLink>
				
					
				</t:column>
		
			</t:dataTable>
			
		<c:if test="${(empty invencao.invencoesGravadas)}">
			<center><font color='red'>N�o h� inven��es com cadastro em andamento pelo usu�rio atual.</font></center>
		</c:if>
		
		<br/>
		<br/>

		<!-- FIM DAS inven��es GRAVADAS PELO USUARIO LOGADO-->

		
		<table class=formulario width="100%">
			<tfoot>
				<tr>
					<td><h:commandButton value="Notificar Nova Inven��o" action="#{invencao.iniciarNova}" id="btNova"/></td>
				</tr>
			</tfoot>
		</table>
		
		
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>