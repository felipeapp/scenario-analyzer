<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>

<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "100%", height : "250", language : "pt", 
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "center"
});
</script>

<% CheckRoleUtil.checkRole(request, response, new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD }); %>

<f:view>
	<h2><ufrn:subSistema /> > Metodologia de Avaliação</h2>
	<br>
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Dados da Tutoria</caption>
			<a4j:outputPanel rendered="#{metodologiaAvaliacaoEad.mostrarNumeroAulas}">
			<tr>
				<th class="obrigatorio">Número de Aulas:</th>
				<td> 
					<h:inputText id="numeroAulas" value="#{metodologiaAvaliacaoEad.obj.numeroAulas}" size="10" readonly="#{metodologiaAvaliacaoEad.readOnly}" /> 		
					(Número Aulas 1a. Unidade, Número Aulas 2a. Unidade)
				</td>
			</tr>		
			</a4j:outputPanel>
			<tr>
				<td colspan="2">
					<table width="100%" class="subFormulario">
					<caption>Cabeçalho da Ficha de Avaliação</caption>
					<tr>
					<td> 
						<h:inputTextarea value="#{ metodologiaAvaliacaoEad.obj.cabecalhoFicha }"/>
					</td>
					</tr>
					</table>
				</td>
			</tr>		
		<tr>
			<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Itens da Ficha de Avaliação<span class="obrigatorio" /></caption>
						<tr>
							<td colspan="2">
							<t:dataTable var="item" value="#{ metodologiaAvaliacaoEad.obj.itens }" width="100%" rowClasses="linhaPar,linhaImpar" rowIndexVar="row" rowStyleClass="center, center">
								<t:column>
									<f:facet name="header"><f:verbatim>&nbsp;</f:verbatim></f:facet>
									<h:outputText value="#{ row + 1 }."/>
								</t:column>
								<t:column>
									<f:facet name="header"><f:verbatim>Item</f:verbatim></f:facet>
									<h:inputText value="#{ item.nome }" maxlength="70" size="73"/>
								</t:column>
								<t:column>
									<f:facet name="header"><f:verbatim><center>Ativo</center></f:verbatim></f:facet>
									<center>
									<h:selectOneRadio value="#{ item.ativo }">
										<f:selectItem itemLabel="Sim" itemValue="true"/>
										<f:selectItem itemLabel="Não" itemValue="false"/>
									</h:selectOneRadio>
									</center>
								</t:column>
								
								<t:column>
									<f:facet name="header">
										<f:verbatim></f:verbatim>
									</f:facet>
									<h:commandLink title="Remover Item" actionListener="#{metodologiaAvaliacaoEad.removerItem}">
										<f:param name="id" value="#{item.id}"/>
										<f:param name="nome" value="#{item.nome}"/>
										<h:graphicImage url="/img/delete.gif"/>
									</h:commandLink>
								</t:column>
							</t:dataTable>
				   		</td>
					</tr>
					<tfoot>
						<tr>
							<td align="center" colspan="2">
								<h:commandButton actionListener="#{ metodologiaAvaliacaoEad.novoItem }" value="Novo Item"/>
							</td>
						</tr>
					</tfoot>
				</table>				
			</td>
		</tr>
		<tfoot>
			<tr>
				<td align="center" colspan="2">
					<h:commandButton id="voltar" value="<< Voltar" action="#{metodologiaAvaliacaoEad.voltar}">
						<f:setPropertyActionListener target="#{metodologiaAvaliacaoEad.operacaoVoltar}" value="1" /> 
					</h:commandButton>
					
					<h:commandButton value="Cancelar" action="#{metodologiaAvaliacaoEad.cancelar}" onclick="#{confirm}" immediate="true"/>
					
					<h:commandButton id="cadastrar" value="Próximo >>" action="#{metodologiaAvaliacaoEad.submeterItens}"/>
				</td>
			</tr>
		</tfoot>
		</table>
					
	</h:form>
	
	<br />
	<center><img src="/shared/img/required.gif" style="vertical-align: middle;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
	<br />

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
