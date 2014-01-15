<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<script type="text/javascript">

	function limparCampoClassePrincipal() {
		$('form:classePrincipal').value = '';
		$('form:classePrincipal').focus();
	}

</script>

<style type="text/css">
	.numero{
		width: 5%;
	}
	
	.classePrincipal{
		width: 90%;
	}
	
	.iconeRemover{
		width: 5%;
	}
	
	
</style>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">

<h2>  <ufrn:subSistema /> > Classificação Bibliográfica</h2>

<div class="descricaoOperacao" style="width:80%;">
	
	<p>Utilize este formulário para cadastrar Classificações Bibliográficas utilizadas no sistema.</p>
	<p>O Campo MARC informa o campo no qual os dados da classificação serão informados no monento da catalogação.</p>
	
	<br/>
	<p><strong>Dicas de preenchimento:</strong></p>
	
	<p>É uma boa prática ordenar as classificações de acordo com a utilização no sistema. Sendo a mais utilizada a primeira.</p>
	<p>Recomenda-se escolher o campo 080$a para a classifiação CDU e o campo 082$a para a classificação CDD. 
	Para ficar consistente com o que é descrito no padrão MARC.</p>
	
</div>

<f:view>
	<a4j:keepAlive beanName="classificacaoBibliograficaMBean" />
	
	<br>
	<h:form id="form">
		<h:inputHidden value="#{classificacaoBibliograficaMBean.obj.id}"/>

		<table class="formulario" width="70%">
			<caption>Classificação Bibliográfica</caption>
			
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{classificacaoBibliograficaMBean.obj.descricao}" readonly="#{classificacaoBibliograficaMBean.readOnly}" maxlength="30" size="20"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Ordem:</th>
				<td>
					<h:selectOneMenu value="#{classificacaoBibliograficaMBean.ordem}" readonly="#{classificacaoBibliograficaMBean.readOnly}" style="width: 140px">
						<f:selectItem itemValue="-1" itemLabel="-SELECIONE-" />
						<f:selectItems value="#{classificacaoBibliograficaMBean.ordemCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Campo MARC:</th>
				<td>
					<h:selectOneMenu value="#{classificacaoBibliograficaMBean.campoMARC}" readonly="#{classificacaoBibliograficaMBean.readOnly}" style="width: 140px">
						<f:selectItem itemValue="-1" itemLabel="-SELECIONE-" />
						<f:selectItems value="#{classificacaoBibliograficaMBean.campoMARCCombo}" />
					</h:selectOneMenu>
					<ufrn:help>
						O Campo MARC no qual será informados os dados da classificação.
					</ufrn:help>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				
					<table class="subFormulario" style="width: 100%; text-align: center;">
						<caption> Classes Principais </caption>
					
						<tr>
							<td>
								<div>
									Digite as Classes Principais:
									<h:inputText id="classePrincipal" value="#{classificacaoBibliograficaMBean.classePrincipal}" size="20" />
																	
									<span style="top: 20px; vertical-align: bottom">
										<a4j:commandLink
												actionListener="#{classificacaoBibliograficaMBean.adicionarClassePrincipal}"
												reRender="dtTblAutoridadesDoUsuario, idAutoridadeSelecionada" oncomplete="limparCampoClassePrincipal(); marcarLinhaTabela();">
											<h:graphicImage url="/img/adicionar.gif" style="border:none" title="Adicionar Classe Principal" />
										</a4j:commandLink>
									</span>
									
									<span style="top: 20px; vertical-align: bottom">
										<ufrn:help>
											Após digitar, clique no botão <b>"+"</b> para adicioná-lo a lista de classes principais.
										</ufrn:help>
									</span>
								</div>
																									
								<a4j:outputPanel ajaxRendered="true" style="float: left; width: 100%; text-align: center; margin-top: 20px; margin-bottom: 20px;" >
									
									<t:div style="color:red;" rendered="#{classificacaoBibliograficaMBean.classesPrincipaisDataModel.rowCount == 0}">
										Sem classes principais.
									</t:div>
									
									<t:dataTable id="dtTblAutoridadesDoUsuario" var="classePrincipal" rowIndexVar="i"  
											value="#{classificacaoBibliograficaMBean.classesPrincipaisDataModel}" 
											style="width: 50%; text-align: left; margin: 0px auto;"
											styleClass="pintarLinha"
											rowClasses="linhaImpar, linhaPar" columnClasses="numero, classePrincipal, iconeRemover">
										<h:column>
											<h:outputText value="#{i + 1}."/>
										</h:column>
										<h:column>
											<h:outputText value="#{classePrincipal}" />
										</h:column>
										<h:column rendered="#{classificacaoBibliograficaMBean.classesPrincipaisDataModel.rowCount > 0}" >
											<a4j:commandLink actionListener="#{classificacaoBibliograficaMBean.removerClassePrincipal}" 
													reRender="dtTblAutoridadesDoUsuario" oncomplete="marcarLinhaTabela();">
												<h:graphicImage url="/img/delete.gif" style="border: none;" title="Remover Classe Principal" />
											</a4j:commandLink>
										</h:column>
									</t:dataTable>
								</a4j:outputPanel>
							
							</td>
						</tr>
					
					</table>
				
				</td>	
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="#{classificacaoBibliograficaMBean.confirmButton}"  
									action="#{classificacaoBibliograficaMBean.cadastrar}"
									onclick="return confirm('Confirma a criação da Classificação Bibliográfica no Sistema ? Seus dados não poderão ser alterados posteriormente.');"
									id="acao"/>
									
						<h:commandButton value="Cancelar"  action="#{classificacaoBibliograficaMBean.voltar}" immediate="true" id="cancelar" onclick="#{confirm}"  />
						
					</td>
				</tr>
			</tfoot>
		</table>
		
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
	</h:form>

</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>